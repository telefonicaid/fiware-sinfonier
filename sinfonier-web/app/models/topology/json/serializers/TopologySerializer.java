package models.topology.json.serializers;

import static models.SinfonierConstants.Topology.FIELD_CONFIG;
import static models.SinfonierConstants.Topology.FIELD_DESCRIPTION;
import static models.SinfonierConstants.Topology.FIELD_NAME;
import static models.SinfonierConstants.TopologyConfig.FIELD_PROPERTIES;
import static models.SinfonierConstants.TopologyConfig.FIELD_WIRES;

import java.lang.reflect.Type;

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

public class TopologySerializer implements JsonSerializer<Topology> {

  private static final String FIELD_MODULE = "module";

	@Override
	public JsonElement serialize(Topology topology, Type type, JsonSerializationContext jsonSerializationContext) {
		JsonObject object = new JsonObject();
		JsonParser parser = new JsonParser();

		object.addProperty(FIELD_DESCRIPTION, topology.getDescription());
		object.addProperty(FIELD_NAME, topology.getName());

		JsonObject jsonConfig = new JsonObject();
		TopologyConfig config = topology.getConfig();

		object.add(FIELD_CONFIG, jsonConfig);
		Gson gson = new Gson();
		jsonConfig.add(FIELD_WIRES, parser.parse(gson.toJson(config.getWires())).getAsJsonArray());
		jsonConfig.add(FIELD_PROPERTIES, parser.parse(gson.toJson(config.getProperties())).getAsJsonObject());

		JsonArray modules = new JsonArray();
		for (TopologyModule topologyModule : config.getModules()) {
			JsonObject jsonModule = new JsonObject();
			jsonModule.addProperty(FIELD_NAME, topologyModule.getName());
			jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_VERSION_CODE,
			    topologyModule.getVersionCode());
			jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_TYPE, topologyModule.getType());
			jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_LANGUAGE, topologyModule.getLanguage());
			jsonModule.addProperty(models.SinfonierConstants.TopologyModule.FIELD_PARALLELISMS,topologyModule.getParallelism());
			jsonModule.add(models.SinfonierConstants.TopologyModule.FIELD_VALUES,
			    parser.parse(gson.toJson(topologyModule.getValues())).getAsJsonObject());
			jsonModule.add(models.SinfonierConstants.TopologyModule.FIELD_CONFIG,
			    parser.parse(gson.toJson(topologyModule.getConfig())).getAsJsonObject());
			
			try {
				if (topologyModule.getModuleId() != null) {
					Module module = Module.findById(topologyModule.getModuleId());
					ModuleVersion moduleVersion = ModuleVersion.findById(topologyModule.getModuleVersionId());
					jsonModule.add(FIELD_MODULE, moduleVersion.toJson(module));
					jsonModule.addProperty(models.SinfonierConstants.Version.FIELD_VERSION_TAG, moduleVersion.getVersionTag());
				}
			} catch (SinfonierException e) {
				throw new JsonException(e.getMessage(), e.getCause());
			}
			modules.add(jsonModule);
		}

		jsonConfig.add(models.SinfonierConstants.TopologyConfig.FIELD_MODULES, modules);

		return object;
	}
}