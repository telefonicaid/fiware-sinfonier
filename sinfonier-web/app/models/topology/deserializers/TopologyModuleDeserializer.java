package models.topology.deserializers;

import com.google.gson.*;
import models.topology.*;

import java.lang.reflect.Type;
import java.util.Map;

public class TopologyModuleDeserializer implements JsonDeserializer<TopologyModule> {
  @Override
  public TopologyModule deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(TopologyModuleConfig.class, new TopologyModuleConfigDeserializer());

    Gson gson = gsonBuilder.create();

    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    final String name = jsonObject.get("name").getAsString();

    final String moduleId, moduleVersionId;

    // This will be null in modules defined by code.
    if (jsonObject.get("module_id") != null && !jsonObject.get("module_id").isJsonNull()) {
      moduleId = jsonObject.get("module_id").getAsString();
    } else {
      moduleId = null;
    }

    // This will be null in modules defined by code.
    if (jsonObject.get("module_version_id") != null && !jsonObject.get("module_version_id").isJsonNull()) {
      moduleVersionId = jsonObject.get("module_version_id").getAsString();
    } else {
      moduleVersionId = null;
    }

    final String moduleType = jsonObject.get("type").getAsString();
    final String moduleLanguage = jsonObject.get("language").getAsString();
    final Integer parallelisms = jsonObject.get("parallelism").getAsInt();

    final JsonElement versionCodeElement = jsonObject.get("versionCode");
    int moduleVersionCode = 0;
    if (versionCodeElement != null) {
      moduleVersionCode = new Integer(versionCodeElement.getAsString());
    }
    final Map<String, Object> valuesMap = gson.fromJson(jsonObject.get("value"), Map.class);

    for (String s : valuesMap.keySet()) {
      if (valuesMap.get(s) instanceof Double) {
        Double value = (Double) valuesMap.get(s);
        if (value == Math.rint(value)) {
          valuesMap.put(s, value.intValue());
        }
      }
    }

    final TopologyModuleConfig config = gson.fromJson(jsonObject.get("config"), TopologyModuleConfig.class);

    return new TopologyModule(moduleId, moduleVersionId, name, moduleType, moduleLanguage, moduleVersionCode, parallelisms, valuesMap, config);
  }
}
