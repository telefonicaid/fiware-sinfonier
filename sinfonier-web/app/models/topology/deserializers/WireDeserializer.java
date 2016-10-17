package models.topology.deserializers;

import com.google.gson.*;
import models.topology.*;

import java.lang.reflect.Type;

public class WireDeserializer implements JsonDeserializer<Wire> {

  @Override
  public Wire deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    JsonObject object = jsonElement.getAsJsonObject();

    final String xtype = object.get("xtype").getAsString();
    final Point source = getPoint(object.get("src").getAsJsonObject());
    final Point target = getPoint(object.get("tgt").getAsJsonObject());

    return new Wire(xtype, source, target);
  }

  private Point getPoint(JsonObject object) {
    return new Point(Integer.parseInt(object.get("moduleId").toString(), 10), object.get("terminal").getAsString());
  }
}
