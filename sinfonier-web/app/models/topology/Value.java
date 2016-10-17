package models.topology;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import static models.SinfonierConstants.ModuleField.*;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class Value {

  public static Boolean isSimpleOrWrapper(Object o) {
    return (o != void.class) || o == Double.class || o == Float.class || o == Long.class ||
        o == Integer.class || o == Short.class || o == Character.class || o == Byte.class ||
        o == Boolean.class || o == String.class;
  }

  protected String type;
  protected String key;
  protected transient Object value;

  public Value() {
  }

  public Value(String type, String key, Object value) {
    this.type = type;
    this.key = key;
    this.value = value;
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (key == null) return object;

    if (type.equals(TYPE_LIST)) {
      BasicDBList valueList = new BasicDBList();
      List<String> list;

      try {
        list = (List<String>) value;
      } catch (Exception ignored) {
        Logger.error("Error trying to cast to List in Value.toDBObject method. " + ignored.getMessage());
        list = new ArrayList<String>();
      }

      for (String s : list) {
        valueList.add(s);
      }

      object.put(key, valueList);

    } else if (Value.isSimpleOrWrapper((value))) {
      object.put(key, value.toString());
    } else {
      object.put(key, "");
    }

    return object;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
