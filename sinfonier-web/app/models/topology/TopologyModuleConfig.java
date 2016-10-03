package models.topology;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static models.SinfonierConstants.ModuleConfig.*;

import java.util.ArrayList;
import java.util.List;

public class TopologyModuleConfig {
  private List<Integer> position;
  private String xType;

  public TopologyModuleConfig(List<Integer> position, String xType) {
    this.position = position;
    this.xType = xType;
  }

  public TopologyModuleConfig(DBObject o) {
    if (o != null && o.get(FIELD_XTYPE) != null) {
      xType = o.get(FIELD_XTYPE).toString();
    }

    if (o != null && o.get(FIELD_POSITION) != null) {
      BasicDBList dbList = ((BasicDBList) o.get(FIELD_POSITION));
      position = new ArrayList<Integer>();
      for (Object o1 : dbList) {
        position.add((new Double(o1.toString())).intValue());
      }
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();
    BasicDBList positionDbList = new BasicDBList();

    if (xType != null && position != null) {
      object.put(FIELD_XTYPE, xType);
      
      for (Integer pos : position) {
        positionDbList.add(pos);
      }
      
      object.put(FIELD_POSITION, positionDbList);
    }

    return object;
  }

  public List<Integer> getPosition() {
    return position;
  }

  public void setPosition(List<Integer> position) {
    this.position = position;
  }

  public String getXType() {
    return xType;
  }

  public void setXType(String xtype) {
    this.xType = xtype;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
