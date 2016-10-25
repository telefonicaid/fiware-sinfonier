package models.topology.deserializers;

import com.google.gson.*;
import models.topology.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bson.types.ObjectId;

import static models.SinfonierConstants.Topology.*;
import static models.SinfonierConstants.TopologyConfig.FIELD_STORM_PROPERTIES;
import static models.SinfonierConstants.TopologyConfig.FIELD_TOPOLOGY_PROPERTIES;

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

    Map<String, String> properties = null;
    Map<String, String> topologyProperties = null;
    //Deserializing from editor
    if (configObject.get("properties") != null) {
      properties = gson.fromJson(configObject.get("properties").toString(), Map.class);
  
      if (properties.keySet().contains("name")) properties.remove("name");
      if (properties.keySet().contains("description")) properties.remove("description");
      
      //Topology Configuration
      if (properties.keySet().contains(FIELD_TOPOLOGY_PROPERTIES)) {
        topologyProperties = new TreeMap<String, String>();
        topologyProperties.put(FIELD_TOPOLOGY_PROPERTIES, properties.get(FIELD_TOPOLOGY_PROPERTIES));
        properties.remove(FIELD_TOPOLOGY_PROPERTIES);    
      }
    //Deserializing from exported topology  
    } else {
      if (configObject.get(FIELD_STORM_PROPERTIES) != null)
        properties = gson.fromJson(configObject.get(FIELD_STORM_PROPERTIES).toString(), Map.class);
      if (configObject.get(FIELD_TOPOLOGY_PROPERTIES) != null)
        topologyProperties = gson.fromJson(configObject.get(FIELD_TOPOLOGY_PROPERTIES).toString(), Map.class);
    }

    final JsonElement templateIdElement = topologyObject.get("template_id");
    String templateId = null;
    if (templateIdElement != null) {
      templateId = templateIdElement.getAsString();
    }

    final JsonElement sharingElement = topologyObject.get("sharing");
    Boolean isSharing = SHARING_DEFAULT_VALUE;
    if (sharingElement != null) {
      isSharing = sharingElement.getAsBoolean();
    }

    Topology topology = new Topology(name, null, isSharing, description,
        new TopologyConfig(wireList, moduleList, properties, topologyProperties));

    if (id != null) {
      topology.setId(id);
    }

    if (templateId != null) {
      topology.setTemplateId(new ObjectId(templateId));
    }

    return topology;
  }
}
