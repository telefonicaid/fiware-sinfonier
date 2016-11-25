package models.topology.json.serializers;

import com.google.gson.*;
import com.mongodb.util.JSON;
import models.drawer.DrawerModule;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static models.SinfonierConstants.Module.FIELD_NAME;

public class DrawerModuleSerializer implements JsonSerializer<DrawerModule> {

  private static final String FIELD_ID = "id";
  private static final String FIELD_TYPES = "types";
  private static final String FIELD_ENABLED = "enabled";
  private static final String FIELD_LOCAL = "local";
  private static final String FIELD_MODULE = "module";
  private static final String FIELD_VERSION = "version";

  @Override
  public JsonElement serialize(DrawerModule module, Type type, JsonSerializationContext jsonSerializationContext) {
    JsonObject object = new JsonObject();
    JsonParser parser = new JsonParser();
    Gson gson = new Gson();

    String versionTag = module.getVersion().getVersionTag();
    object.addProperty(FIELD_ID, module.getModule().getType() + "/" + module.getModule().getName() + (versionTag != null ? " (" + versionTag + ")" : ""));
    object.addProperty(FIELD_NAME, module.getModule().getName() + (versionTag != null ? " (" + versionTag + ")" : ""));
    List<String> types = new ArrayList<String>();
    //types.add(module.getModule().getType());
    types.add(module.getModule().getName() + (versionTag != null ? " (" + versionTag + ")" : ""));
    object.add(FIELD_TYPES, parser.parse(gson.toJson(types)).getAsJsonArray());
    
    object.addProperty(FIELD_ENABLED, true);
    object.addProperty(FIELD_LOCAL, false);
    object.addProperty(FIELD_MODULE, module.getModule().getCategory());
    object.addProperty(FIELD_VERSION, module.getVersion().getVersionTag());

    return object;
  }
}
