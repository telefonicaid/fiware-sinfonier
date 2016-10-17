package models.ui;


import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import play.Logger;

import static models.SinfonierConstants.DdConfig.*;

import java.util.ArrayList;
import java.util.List;

public class DdConfig {
  private String type;
  private List<String> allowedTypes;

  public DdConfig(String type, List<String> allowedTypes) {
    this.type = type;
    this.allowedTypes = allowedTypes;
  }

  public DdConfig(DBObject o) {
    try {
      allowedTypes = new ArrayList<String>();
      type = o.get(FIELD_TYPE).toString();
      BasicDBList dbList = ((BasicDBList) o.get(FIELD_ALLOWED));

      for (Object type : dbList) {
        allowedTypes.add(type.toString());
      }

    } catch (Exception e) {
      type = null;
      Logger.error("DbConfig exception in constructor");
    }

  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (type != null) {
      object.put(FIELD_TYPE, type);
      BasicDBList dbList = new BasicDBList();

      for (String s : allowedTypes) {
        dbList.add(s);
      }

      object.put(FIELD_ALLOWED, dbList);
    }

    return object;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getAllowedTypes() {
    return allowedTypes;
  }

  public void setAllowedTypes(List<String> allowedTypes) {
    this.allowedTypes = allowedTypes;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
