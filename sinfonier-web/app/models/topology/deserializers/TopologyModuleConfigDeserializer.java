package models.topology.deserializers;

import com.google.gson.*;
import models.topology.TopologyModuleConfig;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;

public class TopologyModuleConfigDeserializer implements JsonDeserializer<TopologyModuleConfig> {
  @Override
  public TopologyModuleConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
    final JsonObject jsonObject = jsonElement.getAsJsonObject();
    final String xtype = jsonObject.get("xtype").getAsString();

    Map<String, String> values;

    if (jsonObject.get("position").isJsonArray()) {
      values = new TreeMap<String, String>();
      Integer cont = 0;
      for (JsonElement position : jsonObject.get("position").getAsJsonArray()) {
        values.put(cont.toString(), position.getAsString());
        cont++;
      }
    } else {
      values = ((Map<String, String>) new Gson().fromJson(jsonObject.get("position").getAsString(), Map.class));
    }

    Integer top = null, right = null, bottom = null, left = null;

    if (values.keySet().contains("top") || values.keySet().contains("right") || values.keySet().contains("bottom") || values.keySet().contains("left")) {
      for (String s : values.keySet()) {
        if (s.equals("top")) {
          top = Integer.parseInt(values.get(s), 10);
        }

        if (s.equals("right")) {
          right = Integer.parseInt(values.get(s), 10);
        }

        if (s.equals("bottom")) {
          bottom = Integer.parseInt(values.get(s), 10);
        }

        if (s.equals("left")) {
          left = Integer.parseInt(values.get(s), 10);
        }
      }
    } else {
      String[] positions = values.values().toArray(new String[values.values().size()]);
      left = Integer.parseInt(positions[0], 10);
      top = Integer.parseInt(positions[1], 10);
    }
    
    ArrayList<Integer> position = new ArrayList<Integer>();
    position.addAll(Arrays.asList(left, top));
    if (right != null)
      position.add(right);
    if (bottom != null)
      position.add(bottom);
    return new TopologyModuleConfig(position, xtype);
  }
}
