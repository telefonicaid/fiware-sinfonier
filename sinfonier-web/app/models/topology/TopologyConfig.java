package models.topology;

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
  private Map<String, String> properties;

  public TopologyConfig() {
    this.wires = new ArrayList<Wire>();
    this.modules = new ArrayList<TopologyModule>();
    this.properties = new TreeMap<String, String>();
  }

  public TopologyConfig(List<Wire> wires, List<TopologyModule> modules, Map<String, String> properties) {
    this.wires = wires;
    this.modules = modules;
    this.properties = properties;
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

    if (o != null && o.get(FIELD_PROPERTIES) != null) {
      properties.putAll(((DBObject) o.get(FIELD_PROPERTIES)).toMap());
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();
    BasicDBList wiresDbList = new BasicDBList();
    BasicDBList modulesDbList = new BasicDBList();
    DBObject propertiesDbObject = new BasicDBObject();

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

    if (properties != null) {
      for (String key : properties.keySet()) {
        if (properties.get(key).trim().length() > 0) {
          propertiesDbObject.put(key, properties.get(key));
        }
      }
    }

    object.put(FIELD_WIRES, wiresDbList);
    object.put(FIELD_MODULES, modulesDbList);
    object.put(FIELD_PROPERTIES, propertiesDbObject);

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

  public Map<String, String> getProperties() {
    return properties;
  }

  public void setProperties(Map<String, String> properties) {
    this.properties = properties;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
