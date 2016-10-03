package controllers;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.module.Module;
import models.responses.Codes;
import models.storm.ParamsValidator;
import models.topology.Topology;
import models.topology.TopologiesContainer;
import models.topology.deserializers.TopologyDeserializer;
import models.storm.Client;
import org.bson.types.ObjectId;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Util;


public class Topologies extends WebSecurityController {
  public static final String INDEX_MODE_MY_TOPOLOGIES = "myTopologies";
  public static final String FLASH_KEY_LAUNCHING_ERROR = "topology_error_launching";
  public static final String FLASH_KEY_STOPPING_ERROR = "topology_error_stopping";

  private static Client client = Client.getInstance();

  @Before(unless = {"index", "topology", "search", "log"})
  static void hasWritePermission() throws SinfonierException {
    String id = request.params.get("id");
    if (id != null) {
      Topology topology = Topology.findById(id);
      if (topology == null || !topology.hasWritePermission(getCurrentUser()))
        forbidden();
    } else if (!request.actionMethod.equals("save")) {
      forbidden();
    }
  }

  public static void index(String indexMode, String template, int page) throws SinfonierException {
    if (request.isAjax()) {
      TopologiesContainer topologiesContainer = Topology.getTopologies(getCurrentUser(), false, false, page);
      List<Topology> topologies = topologiesContainer.getTopologies();
      if (template != null) {
        Topology templateTopology = Topology.getAsTemplate(Topology.findById(template), getCurrentUser());
        topologies.add(templateTopology);
      }
      renderJSON(new Gson().toJson(topologies));
    } else {
      TopologiesContainer topologiesContainer;

      if (indexMode != null && indexMode.equals(INDEX_MODE_MY_TOPOLOGIES)) {
        topologiesContainer = Topology.getTopologies(getCurrentUser(), false, true, page);
      } else {
        topologiesContainer = Topology.getTopologies(getCurrentUser(), page);
      }
      List<Topology> topologies = topologiesContainer.getTopologies();
      int totalTopologies = topologiesContainer.getCountBeforeLimit();
      render(topologies,page,totalTopologies);
    }
  }

  public static void save() throws SinfonierException {
    try {
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Topology.class, new TopologyDeserializer());
      Gson gson = gsonBuilder.create();
  
      ParamsValidator validator = ParamsValidator.getInstance();
      Topology topology = gson.fromJson(request.params.get("body"), Topology.class);
      topology.setAuthorId(getCurrentUser().getId());
  
      if (validator.validate(topology.getConfig())) {
        String topologyId = topology.save();
        renderJSON(new Gson().toJson(Topology.findById(topologyId)));
      } else {
        Codes c410 = Codes.CODE_400;
        JsonObject data = new JsonObject();
        data.addProperty("message", Messages.get("validation.topology.params"));
        c410.setData(data);
        response.status = c410.getCode();
        renderJSON(c410.toGSON());
      }
    } catch (SinfonierException se) {
      if (se.getError().getCode() == SinfonierError.TOPOLOGY_DUPLICATE.getCode()) {
        Codes c410 = Codes.CODE_400;
        JsonObject data = new JsonObject();
        data.addProperty("message", se.getMessage());
        c410.setData(data);
        response.status = c410.getCode();
        renderJSON(c410.toGSON());
      } else {
        throw se;
      }
    }
  }

  public static void topology(String name) throws SinfonierException {
    Topology topology = Topology.findByName(name);

    if (topology == null) {
      Logger.error("We can't found the topology with name: " + name);
      notFound();
    } else {
      render(topology);
    }
  }

  public static void remove(String id) throws SinfonierException {
    Topology topology = Topology.findById(new ObjectId(id));

    if (topology != null) {
      topology.remove();
      index(null, null, 1);
    } else {
      Logger.error("We can't found the topology with id: " + id);
      notFound();
    }
  }

  public static void launch(String id) throws SinfonierException {
    Topology topology = Topology.findById(new ObjectId(id));

    if (topology != null) {
      JsonObject res = client.topologyLaunch(topology.getId());

      if (request.isAjax() && res.get("code") != null && res.get("code").getAsInt() == 200) {
        Codes c200 = Codes.CODE_200;
        JsonObject data = new JsonObject();
        data.addProperty("name", topology.getName());
        c200.setData(data);
        renderJSON(c200.toGSON());
      } else if (request.isAjax() && (res.get("code") == null || res.get("code").getAsInt() != 200)) {
        Codes c400 = Codes.CODE_400;
        JsonObject data = new JsonObject();
        data.addProperty("message", Messages.get("Topologies.error.launching"));
        c400.setData(data);
        response.status = c400.getCode();
        renderJSON(c400.toGSON());
      } else {
        if (res.get("code") == null || res.get("code").getAsInt() != 200) {
          flash.put(FLASH_KEY_LAUNCHING_ERROR, Messages.get("Topologies.error.launching"));
        }

        topology(topology.getName());
      }
    } else {
      Logger.error("We can't found the topology with id: " + id);
      notFound();
    }
  }

  public static void stop(String id) throws SinfonierException {
    Topology topology = Topology.findById(new ObjectId(id));

    if (topology != null) {
      JsonObject res = client.topologyStop(topology.getId());

      if (res.get("code") == null || res.get("code").getAsInt() != 200) {
        flash.put(FLASH_KEY_STOPPING_ERROR, Messages.get("Topologies.error.stopping"));
      }

      topology(topology.getName());
    } else {
      Logger.error("We can't found the topology with id: " + id);
      notFound();
    }
  }

  public static void search(String status, String query, Date updated, int page) throws SinfonierException {
    TopologiesContainer topologiesContainer = Topology.findByStatusOrNameOrAuthorOrUpdatedDate(status, query, updated, getCurrentUser(), page);
    flash("searching", true);
    params.flash();
    List<Topology> topologies = topologiesContainer.getTopologies();
    int totalTopologies = topologiesContainer.getCountBeforeLimit();
    render("Topologies/index.html", topologies, page, totalTopologies);
  }

  public static void log(@Required String id) {
    try {
      Codes code200 = Codes.CODE_200;
      JsonObject data = new JsonObject();
      data.addProperty("msg", client.getTopologyLog(id));
      code200.setData(data);

      renderJSON(Codes.CODE_200.toGSON());
    } catch (SinfonierException e) {
      Logger.error(e.getMessage());
      response.status = Codes.CODE_500.getCode();
      renderJSON(Codes.CODE_500.toGSON());
    }

  }

  public static void publish(@Required String id) throws SinfonierException {
    checkAuthenticity();
    Topology topology = Topology.findById(id);

    if (topology == null) {
      Logger.error("We can\'t find the topology with id: " + id);
      notFound();
    } else {
      topology.setSharing(true);
      topology.save();
      topology(topology.getName());
    }
  }

  public static void privatize(@Required String id) throws SinfonierException {
    checkAuthenticity();
    Topology topology = Topology.findById(id);

    if (topology == null) {
      Logger.error("We can\'t find the topology with id: " + id);
      notFound();
    } else {
      topology.setSharing(false);
      topology.save();
      topology(topology.getName());
    }
  }

  @Util
  @Catch(value = SinfonierException.class, priority = 1)
  public static void catchSinfonierExceptions(SinfonierException e) {
    Logger.error(e, e.getMessage());
    SinfonierError error = e.getError();
    Object[] args = e.getArgs();
    render("errors/error.html", error, args);
  }
}
