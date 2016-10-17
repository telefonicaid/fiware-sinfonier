package models.topology.json.serializers;

import com.google.gson.*;
import com.mongodb.util.JSON;
import models.topology.json.Module;

import java.lang.reflect.Type;

public class ModuleSerializer implements JsonSerializer<Module> {

  private static final String FIELD_CLASS = "class";
  private static final String FIELD_ABSTRACTION_ID = "abstractionId";
  private static final String FIELD_PARALLELISM = "parallelism";
  private static final String FIELD_PARAMS = "params";
  private static final String FIELD_SOURCES = "sources";

  @Override
  public JsonElement serialize(Module module, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    JsonParser parser = new JsonParser();

    object.add(FIELD_CLASS, new JsonPrimitive(module.getClazz()));
    object.add(FIELD_ABSTRACTION_ID, new JsonPrimitive(module.getAbstractionId()));
    object.add(FIELD_PARALLELISM, new JsonPrimitive(module.getParallelism()));

    if (module.getParams().size() > 0) {
      object.add(FIELD_PARAMS, parser.parse(JSON.serialize(module.getParams())).getAsJsonObject());
    }

    if (module.getSources().size() > 0) {
      object.add(FIELD_SOURCES, parser.parse(JSON.serialize(module.getSources())).getAsJsonObject());
    }

    return object;
  }
}
