package models.topology.json.serializers;

import static models.SinfonierConstants.Topology.FIELD_CONFIG;
import static models.SinfonierConstants.Topology.FIELD_DESCRIPTION;
import static models.SinfonierConstants.Topology.FIELD_NAME;
import static models.SinfonierConstants.TopologyConfig.FIELD_STORM_PROPERTIES;
import static models.SinfonierConstants.TopologyConfig.FIELD_TOPOLOGY_PROPERTIES;
import static models.SinfonierConstants.TopologyConfig.FIELD_WIRES;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mongodb.util.JSON;

import exceptions.SinfonierException;
import groovy.json.JsonException;
import models.module.Module;
import models.module.ModuleVersion;
import models.topology.Topology;
import models.topology.TopologyConfig;
import models.topology.TopologyModule;
import models.topology.Wire;

public class FlowSerializer implements JsonSerializer<Topology> {

  private static final String FIELD_ID = "id";
  private static final String FIELD_TYPE = "type";
  private static final String FIELD_LABEL = "label";
  private static final String FIELD_TOPOLOGY_REF = "z";
  private static final String FIELD_POSITION_X = "x";
  private static final String FIELD_POSITION_Y = "y";
  private static final String FIELD_WIRES = "wires";

  private static final String TYPE_TAB = "tab";
  
  private static final String[] OUTPUT_TERMINALS = {"out", "yes", "no"};

  @Override
  public JsonArray serialize(Topology topology, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    JsonParser parser = new JsonParser();

    object.addProperty(FIELD_ID, topology.getId());
    object.addProperty(FIELD_TYPE, TYPE_TAB);
    object.addProperty(FIELD_LABEL, topology.getName());
    object.addProperty(FIELD_DESCRIPTION, topology.getDescription());

    JsonObject jsonConfig = new JsonObject();
    TopologyConfig config = topology.getConfig();

    object.add(FIELD_CONFIG, jsonConfig);
    Gson gson = new Gson();
    jsonConfig.add(FIELD_STORM_PROPERTIES, parser.parse(gson.toJson(config.getStormProperties())).getAsJsonObject());
    jsonConfig.add(FIELD_TOPOLOGY_PROPERTIES,
        parser.parse(gson.toJson(config.getTopologyProperties())).getAsJsonObject());

    JsonArray modules = new JsonArray();
    modules.add(object);
    int modulePosition = 0;
    for (TopologyModule topologyModule : config.getModules()) {
      JsonObject jsonModule = new JsonObject();
      //TODO Every Node-RED node must have an unique global ID, for all flows 
      jsonModule.addProperty(FIELD_ID, 
          getUniqueNodeId(topologyModule, modulePosition, topology.getId()));
      
      //Attributes not necessary on Node-RED
      jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_VERSION_CODE,
          topologyModule.getVersionCode());
      jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_LANGUAGE, topologyModule.getLanguage());
      jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_PARALLELISMS,topologyModule.getParallelism());
      
      String versionTag = getVersionTag(topologyModule);
      jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_TYPE, topologyModule.getName() +
          (versionTag != null ? " (" + versionTag + ")" : ""));
      
      jsonModule.addProperty(FIELD_TOPOLOGY_REF, topology.getId());
      jsonModule.addProperty(FIELD_NAME, topologyModule.getName());
      jsonModule.addProperty(FIELD_POSITION_X, topologyModule.getConfig().getPosition().get(0));
      jsonModule.addProperty(FIELD_POSITION_Y, topologyModule.getConfig().getPosition().get(1));
      jsonModule.add(FIELD_WIRES, parser.parse(gson.toJson(referencedModulesByOutputWires(config, modulePosition, topology.getId()))).getAsJsonArray());

      //Add value attributes at module/node level
      //TODO When we flat values at module level, we must be careful with reserved names
      Iterator entries = topologyModule.getValues().entrySet().iterator();
      while (entries.hasNext()) {
        Entry thisEntry = (Entry) entries.next();
        Object key = thisEntry.getKey();
        Object value = thisEntry.getValue();
        jsonModule.add(key.toString(), parser.parse(gson.toJson(value)));
      }

      modules.add(jsonModule);
      modulePosition = modulePosition + 1;
    }

    return modules;
  }
  
  private String getUniqueNodeId(TopologyModule topologyModule, int modulePosition, String topologyId) {
    String uniqueNodeId = topologyId;
    if (topologyModule.getModuleId() != null) {
      uniqueNodeId = uniqueNodeId + "." + topologyModule.getModuleId() + "." + topologyModule.getModuleVersionId() + "." + Integer.toString(modulePosition);
    } else {
      uniqueNodeId = uniqueNodeId + "." + topologyModule.getName() + "." + Integer.toString(modulePosition);
    }
    return uniqueNodeId;
  }
  
  private List<List<String>> referencedModulesByOutputWires(TopologyConfig config, int modulePosition, String topologyId) {
    ArrayList<Wire> wiresFromModule = new ArrayList<Wire>();
    for (Wire wire : config.getWires()) {
      if ((wire.getSource().getId() == modulePosition && Arrays.asList(OUTPUT_TERMINALS).contains(wire.getSource().getTerminal())) ||
          (wire.getTarget().getId() == modulePosition && Arrays.asList(OUTPUT_TERMINALS).contains(wire.getTarget().getTerminal()))) {
        wiresFromModule.add(wire);
      }
    }
    Map<String, List<String>> groupedWires = new HashMap<String, List<String>>();
    for (Wire wire: wiresFromModule) {
      String terminal = (wire.getSource().getId() == modulePosition ? wire.getSource().getTerminal() : wire.getTarget().getTerminal());
      if (groupedWires.get(terminal) == null) {
        groupedWires.put(terminal, new ArrayList<String>());
      }
      String reference = getUniqueNodeId(config.getModules().get((wire.getSource().getId() == modulePosition ? wire.getTarget().getId() : wire.getSource().getId())), 
          (wire.getSource().getId() == modulePosition ? wire.getTarget().getId() : wire.getSource().getId()), 
          topologyId);
      groupedWires.get(terminal).add(reference);
    }
    return new ArrayList(groupedWires.values());
  }
  
  private String getVersionTag(TopologyModule topologyModule) {
    String versionTag = null;
    try {
      if (topologyModule.getModuleId() != null && topologyModule.getModuleVersionId() != null) {
        ModuleVersion moduleVersion = ModuleVersion.findById(topologyModule.getModuleVersionId());
        versionTag = moduleVersion.getVersionTag();
      }
    } catch (SinfonierException e) {
      throw new JsonException(e.getMessage(), e.getCause());
    }
    return versionTag;
  }
}