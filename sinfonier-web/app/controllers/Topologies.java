package controllers;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.module.Module;
import models.module.ModuleVersion;
import models.responses.Codes;
import models.storm.Client;
import models.topology.TopologiesContainer;
import models.topology.Topology;
import models.topology.TopologyModule;
import models.topology.deserializers.TopologyDeserializer;
import models.validators.ParamsValidator;
import models.topology.json.LogData;
import play.Logger;
import play.data.validation.Required;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Util;


public class Topologies extends WebSecurityController {
  public static final String INDEX_MODE_MY_TOPOLOGIES = "myTopologies";
  public static final String FLASH_KEY_LAUNCHING_ERROR = "topology_error_launching";
  public static final String FLASH_KEY_STOPPING_ERROR = "topology_error_stopping";
  public static final String FLASH_KEY_REMOVING_ERROR = "topology_error_removing";
  public static final String[] FLASH_KEYS = {FLASH_KEY_LAUNCHING_ERROR, FLASH_KEY_STOPPING_ERROR, FLASH_KEY_REMOVING_ERROR};

  private static Client client = Client.getInstance();

  @Before(unless = {"index", "topology", "search", "log", "importTopology","doImport"})
  static void hasWritePermission() throws SinfonierException {
    String id = request.params.get("id");
    if (id != null) {
      Topology topology = Topology.findById(id);
			if (topology == null || !topology.hasWritePermission(getCurrentUser())) {
				Logger.error("No write permissions: " + request.url);
				forbidden();
			}
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
  
      if (validator.validate(topology.getConfig(), true)) {
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
      if (!topology.isRunning()) {
        topology.remove();
        index(null, null, 1);
      } else {
        flash.put(FLASH_KEY_REMOVING_ERROR, Messages.get("Topologies.msgs.stopBeforeDelete"));
        topology(topology.getName());
      }
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

	public static void log(@Required String id, String start) {
		try {
			Codes code200 = Codes.CODE_200;
			Gson gson = new Gson();
			JsonObject data = new JsonObject();
			String logDataKey = id + "_logdata";
			boolean loadedLogData = false;
			String logData = session.get(logDataKey);
			List<LogData> logDatas;
			if (logData != null) {
				logDatas = gson.fromJson(logData, new TypeToken<ArrayList<LogData>>() {}.getType());
			} else {
				logDatas = client.getTopologyLogSizes(id);
				loadedLogData = true;
			}
			if (start != null) {
				String[] starts = start.split(",");

				for (int i = 0; i < starts.length; i++) {
					String startValue = starts[i];
					if (startValue.matches("^[\\+|\\-]?\\d+$")) {
						long lStart = Long.parseLong(startValue);
						if (lStart < 0L) {
							if (!loadedLogData) {
								logDatas = client.getTopologyLogSizes(id);
								loadedLogData = true;
							}
						} else {
							while (logDatas.size() <= i) {
								logDatas.add(new LogData(String.valueOf(i), 0L, 52100L));
							}
							logDatas.get(i).setStart(lStart);
						}
					}
				}
			}

			List<String> logs = client.getTopologyLog(id, logDatas);

			List<String> escapedLogs = new ArrayList<String>(logs.size());

			for (String log : logs) {
				escapedLogs.add(StringEscapeUtils.escapeHtml(log));
			}

			for (int i = 0; i < logs.size(); i++) {
				while (logDatas.size() <= i) {
					logDatas.add(new LogData(String.valueOf(i), 0L, 52100L));
				}
				LogData current = logDatas.get(i);
				current.setStart(current.getStart() + logs.get(i).length());
			}

			session.put(logDataKey, gson.toJson(logDatas));

			JsonElement el = gson.toJsonTree(escapedLogs, new TypeToken<List<String>>() {}.getType());
			data.add("msg", el);
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
  
  public static void export(@Required String id) throws SinfonierException, UnsupportedEncodingException {
    checkAuthenticity();
    Topology topology = Topology.findById(id);

    if (topology == null) {
      Logger.error("We can\'t find the topology with id: " + id);
      notFound();
    } else {
    	Codes c200 = Codes.CODE_200;
    	JsonObject data = new JsonObject();
    	JsonElement jelement = new JsonParser().parse(topology.export());
      JsonObject  jobject = jelement.getAsJsonObject();
      data.add("topology", jobject);
      c200.setData(data);

      renderBinary(new ByteArrayInputStream(jobject.toString().getBytes("UTF-8")),topology.getName()+".json");
    }
  }
  
  public static void importTopology() throws SinfonierException {
    render();
  }
	
  public static void doImport() throws SinfonierException {
    try {
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.registerTypeAdapter(Topology.class, new TopologyDeserializer());
      Gson gson = gsonBuilder.create();
      String body = request.params.get("body");
      ParamsValidator validator = ParamsValidator.getInstance();
      Topology topology = gson.fromJson(body, Topology.class);
      topology.setAuthorId(getCurrentUser().getId());
      Topology old = Topology.findByName(topology.getName());
      if (old != null) {
      	throw new SinfonierException(SinfonierError.TOPOLOGY_DUPLICATE);
      }
      JsonElement root = new JsonParser().parse(body);
      JsonObject jTopology = root.getAsJsonObject().get("topology").getAsJsonObject();
      JsonArray jModules = jTopology.getAsJsonObject().get("config").getAsJsonObject().get("modules").getAsJsonArray();
      for (JsonElement jTopologyModule: jModules)
      {
        ModuleVersion moduleVersion = TopologyModule.checkTopologyModule(jTopologyModule.getAsJsonObject());

        Integer version = jTopologyModule.getAsJsonObject().get("versionCode").getAsInt();
        if (version != 0) {
          JsonObject jModule = jTopologyModule.getAsJsonObject().get("module").getAsJsonObject();
          String moduleName = jModule.get("name").getAsString();
          for (TopologyModule topologyModule : topology.getConfig().getModules()) {

            if (topologyModule.getName().equals(moduleName) && topologyModule.getVersionCode() == version) {
              Module module = Module.findByName(moduleName);
              topologyModule.setModuleVersionId(moduleVersion.getId());
              topologyModule.setModuleId(module.getId());
            }
          }
        }
      }

      if (validator.validate(topology.getConfig(), false)) {
        String topologyId = topology.save();
        renderJSON(new Gson().toJson(Topology.findById(topologyId)));
      } else {
        Codes c400 = Codes.CODE_400;
        c400.setMessageData(Messages.get("validation.topology.params"));
        response.status = c400.getCode();
        renderJSON(c400.toGSON());
      }
    } catch (SinfonierException se) {
      Codes c400 = Codes.CODE_400;
      c400.setMessageData(se.getMessage());
      response.status = c400.getCode();
      renderJSON(c400.toGSON());
    }
  }

}
