package models.ui;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import play.Logger;

import static models.SinfonierConstants.Attributes.*;

public class Attributes {
  private String id;
  private String clazz;

  public Attributes(String id, String clazz) {
    this.id = id;
    this.clazz = clazz;
  }

  public Attributes(DBObject o) {
    try {
      id = o.get(FIELD_ID).toString();
      clazz = o.get(FIELD_CLASS).toString();

    } catch (Exception e) {
      id = null;
      Logger.error("Attributes exception in constructor");
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (id != null) {
      object.put(FIELD_ID, id);
      object.put(FIELD_CLASS, clazz);
    }
    return object;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
