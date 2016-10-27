package models.topology;

import com.google.gson.annotations.SerializedName;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static models.SinfonierConstants.TopologyConfig.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TopologyConfig {
  private List<Wire> wires;
  private List<TopologyModule> modules;
  @SerializedName("properties")
  private Map<String, String> stormProperties;
  private Map<String, String> topologyProperties;

  public TopologyConfig() {
    this.wires = new ArrayList<Wire>();
    this.modules = new ArrayList<TopologyModule>();
    this.stormProperties = new TreeMap<String, String>();
    this.topologyProperties = new TreeMap<String, String>();
  }

  public TopologyConfig(List<Wire> wires, List<TopologyModule> modules, Map<String, String> stormProperties, Map<String, String> topologyProperties) {
    this.wires = wires;
    this.modules = modules;
    this.stormProperties = stormProperties;
    this.topologyProperties = topologyProperties;
  }

  public TopologyConfig(DBObject o) {
    this();

    if (o != null && o.get(FIELD_WIRES) != null) {
      BasicDBList dbList = ((BasicDBList) o.get(FIELD_WIRES));
      for (Object w : dbList) {
        wires.add(new Wire(((DBObject) w)));
      }
    }

    if (o != null && o.get(FIELD_MODULES) != null) {
      BasicDBList dbList = ((BasicDBList) o.get(FIELD_MODULES));
      for (Object m : dbList) {
        modules.add(new TopologyModule(((DBObject) m)));
      }
    }

    if (o != null && o.get(FIELD_STORM_PROPERTIES) != null) {
      stormProperties.putAll(((DBObject) o.get(FIELD_STORM_PROPERTIES)).toMap());
    }
    
    if (o != null && o.get(FIELD_TOPOLOGY_PROPERTIES) != null) {
      topologyProperties.putAll(((DBObject) o.get(FIELD_TOPOLOGY_PROPERTIES)).toMap());
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();
    BasicDBList wiresDbList = new BasicDBList();
    BasicDBList modulesDbList = new BasicDBList();
    DBObject stormPropertiesDbObject = new BasicDBObject();
    DBObject topologyPropertiesDbObject = new BasicDBObject();

    if (wires != null) {
      for (Wire w : wires) {
        wiresDbList.add(w.toDBObject());
      }
    }

    if (modules != null) {
      for (TopologyModule m : modules) {
        modulesDbList.add(m.toDBObject());
      }
    }

    if (stormProperties != null) {
      for (String key : stormProperties.keySet()) {
        if (stormProperties.get(key).trim().length() > 0) {
          stormPropertiesDbObject.put(key, stormProperties.get(key));
        }
      }
    }

    if (topologyProperties != null) {
      for (String key : topologyProperties.keySet()) {
        if (topologyProperties.get(key).trim().length() > 0) {
          topologyPropertiesDbObject.put(key, topologyProperties.get(key));
        }
      }
    }
    
    object.put(FIELD_WIRES, wiresDbList);
    object.put(FIELD_MODULES, modulesDbList);
    object.put(FIELD_STORM_PROPERTIES, stormPropertiesDbObject);
    object.put(FIELD_TOPOLOGY_PROPERTIES, topologyPropertiesDbObject);

    return object;
  }

  public List<Wire> getWires() {
    return wires;
  }

  public void setWires(List<Wire> wires) {
    this.wires = wires;
  }

  public List<TopologyModule> getModules() {
    return modules;
  }

  public void setModules(List<TopologyModule> modules) {
    this.modules = modules;
  }

  public Map<String, String> getStormProperties() {
    return stormProperties;
  }
  
  public void setStormProperties(Map<String, String> stormProperties) {
    this.stormProperties = stormProperties;
  }

  public Map<String, String> getTopologyProperties() {
    return topologyProperties;
  }

  public Map<String, String> getTopologyPropertiesToExport() {
    Map<String, String> exportProperties = new TreeMap<String, String>();;
    String topologyPropsStr = "";
    for (String key : topologyProperties.keySet()) {
      if (!key.equals(FIELD_TOPOLOGY_PROPERTIES)) {
        topologyPropsStr = topologyPropsStr.concat(key).concat("=\n");
      }
    }
    if (topologyPropsStr.length() > 0)
      topologyPropsStr = topologyPropsStr.substring(0, topologyPropsStr.length() - 1);
    exportProperties.put(FIELD_TOPOLOGY_PROPERTIES, topologyPropsStr);
    return exportProperties;
  }
  
  public void setTopologyProperties(Map<String, String> topologyProperties) {
    this.topologyProperties = topologyProperties;
  }
  
  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
