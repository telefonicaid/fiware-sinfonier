package models.topology.json;

import com.google.gson.*;
import com.mongodb.util.JSON;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.topology.*;
import models.topology.json.serializers.ModuleSerializer;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static models.SinfonierConstants.Module.*;

public class Builder {

  private final static String KEY_DESCRIPTION = "description";
  private final static String KEY_PROP = "properties";
  private final static String KEY_CONFIG = "builderConfig";
  private final static String KEY_BOLTS = "bolts";
  private final static String KEY_SPOUTS = "spouts";
  private final static String KEY_DRAINS = "drains";
  private final static String KEY_PARALLELISM = "parallelism";

  private final static String CLASS_SPOUT = "com.sinfonier.spouts";
  private final static String CLASS_BOLT = "com.sinfonier.bolts";
  private final static String CLASS_DRAIN = "com.sinfonier.drains";

  private List<Module> modules;
  private Map<String, String> properties;

  public Builder() {
    modules = new ArrayList<Module>();
    properties = new TreeMap<String, String>();
  }

  public Builder(Topology topology) throws SinfonierException {
    this();

    if (topology == null) {
      throw new SinfonierException(SinfonierError.TOPOLOGY_BUILDER_TYPE);
    }

    TopologyConfig config = topology.getConfig();
    generateModules(config.getModules());
    generateWires(config.getWires());

    for (String key : config.getProperties().keySet()) {
      if (!key.equals(KEY_DESCRIPTION)) {
        properties.put(key, config.getProperties().get(key));
      }
    }
  }

  public JsonElement getJson() {
    JsonParser parser = new JsonParser();
    JsonObject object = new JsonObject();
    JsonArray spouts = new JsonArray();
    JsonArray bolts = new JsonArray();
    JsonArray drains = new JsonArray();
    JsonObject builderConfig = new JsonObject();
    Gson gson = new GsonBuilder().registerTypeAdapter(Module.class, new ModuleSerializer()).create();

    JsonObject properties = parser.parse(JSON.serialize(this.properties)).getAsJsonObject();

    for (Module module : modules) {
      if (module instanceof Spout) {
        spouts.add(parser.parse(gson.toJson(module)));
      }

      if (module instanceof Bolt) {
        bolts.add(parser.parse(gson.toJson(module)));
      }

      if (module instanceof Drain) {
        drains.add(parser.parse(gson.toJson(module)));
      }
    }

    builderConfig.add(KEY_SPOUTS, spouts);
    builderConfig.add(KEY_BOLTS, bolts);
    builderConfig.add(KEY_DRAINS, drains);

    object.add(KEY_PROP, properties);
    object.add(KEY_CONFIG, builderConfig);

    return object;
  }

  private void generateWires(List<Wire> wires) {
    for (Wire wire : wires) {
      Integer source = wire.getSource().getId();
      Integer target = wire.getTarget().getId();
      addSource(modules.get(source), modules.get(target));
    }
  }

  private void addSource(Module src, Module target) {
    List<Source> sources = src.getSources();
    sources.add(new Source(target.getAbstractionId()));
    src.setSources(sources);
  }

  private void generateModules(List<TopologyModule> modules) {
    for (TopologyModule module : modules) {
      Integer parallelism = getParallelism(module);
      String abstractId = generateAbstractId(UUID.randomUUID(), module.getName());

      if (module.getType().equals(TYPE_SPOUT)) {
        String className = generateClassName(TYPE_SPOUT, module.getName());
        Spout spout = new Spout(className, abstractId, parallelism, new ArrayList<Source>(), module.getValues());

        this.modules.add(spout);
        continue;
      }

      if (module.getType().equals(TYPE_BOLT)) {
        String className = generateClassName(TYPE_BOLT, module.getName());
        Bolt bolt = new Bolt(className, abstractId, parallelism, new ArrayList<Source>(), module.getValues());

        this.modules.add(bolt);
        continue;
      }

      if (module.getType().equals(TYPE_DRAIN)) {
        String className = generateClassName(TYPE_DRAIN, module.getName());
        Drain drain = new Drain(className, abstractId, parallelism, new ArrayList<Source>(), module.getValues());

        this.modules.add(drain);
      }
    }
  }

  private String generateClassName(String type, String name) {
    if (type.equals(TYPE_SPOUT)) {
      return CLASS_SPOUT + "." + StringUtils.capitalize(name);
    }

    if (type.equals(TYPE_BOLT)) {
      return CLASS_BOLT + "." + StringUtils.capitalize(name);
    }

    return CLASS_DRAIN + "." + StringUtils.capitalize(name);
  }

  private String generateAbstractId(UUID uuid, String name) {
    return name.toLowerCase() + "_" + uuid.toString();
  }

  private Integer getParallelism(TopologyModule module) {
    Map values = module.getValues();

    if (values != null && values.keySet().contains(KEY_PARALLELISM)) {
      return Integer.parseInt(values.get(KEY_PARALLELISM).toString(), 10);
    }

    return 1;
  }
}
