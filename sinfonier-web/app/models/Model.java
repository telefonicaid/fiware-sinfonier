package models;

import com.mongodb.DBObject;

public abstract class Model {

  public abstract DBObject toDBObject();
  
  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
