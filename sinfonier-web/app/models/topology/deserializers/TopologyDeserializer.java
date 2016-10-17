package models.topology.deserializers;

import com.google.gson.*;
import models.topology.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import static models.SinfonierConstants.Topology.*;

public class TopologyDeserializer implements JsonDeserializer<Topology> {

  @Override
  public Topology deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(TopologyModule.class, new TopologyModuleDeserializer());
    gsonBuilder.registerTypeAdapter(Wire.class, new WireDeserializer());
    Gson gson = gsonBuilder.create();

    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    final JsonObject topologyObject = jsonObject.get("topology").getAsJsonObject();
    final JsonElement idElement = topologyObject.get("id");

    String id = null;
    if (idElement != null) {
      id = idElement.getAsString();
    }

    final String name = topologyObject.get("name").getAsString();
    final String description = topologyObject.get("description").getAsString();
    final JsonObject configObject = topologyObject.get("config").getAsJsonObject();
    final JsonArray modulesElements = configObject.get("modules").getAsJsonArray();
    final List<TopologyModule> moduleList = new ArrayList<TopologyModule>();

    for (JsonElement element : modulesElements) {
      moduleList.add(gson.fromJson(element.getAsJsonObject(), TopologyModule.class));
    }

    final JsonArray wiresElements = configObject.get("wires").getAsJsonArray();
    final List<Wire> wireList = new ArrayList<Wire>();
    for (JsonElement element : wiresElements) {
      wireList.add(gson.fromJson(element, Wire.class));
    }

    final Map<String, String> properties = gson.fromJson(configObject.get("properties").toString(), Map.class);

    if (properties.keySet().contains("name")) properties.remove("name");
    if (properties.keySet().contains("description")) properties.remove("description");

    final JsonElement templateIdElement = topologyObject.get("template_id");
    String templateId = null;
    if (templateIdElement != null) {
      templateId = templateIdElement.toString();
    }

    final JsonElement sharingElement = topologyObject.get("sharing");
    Boolean isSharing = SHARING_DEFAULT_VALUE;
    if (sharingElement != null) {
      isSharing = sharingElement.getAsBoolean();
    }

    Topology topology = new Topology(name, null, isSharing, description,
        new TopologyConfig(wireList, moduleList, properties));

    if (id != null) {
      topology.setId(id);
    }

    if (templateId != null) {
      topology.setTemplateId(new ObjectId(templateId));
    }

    return topology;
  }
}
