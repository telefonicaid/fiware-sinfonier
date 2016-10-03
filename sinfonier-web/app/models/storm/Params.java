package models.storm;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import exceptions.SinfonierException;
import models.Model;

import java.util.Map;
import java.util.TreeMap;

public class Params extends Model {
  private Map<String, ParamValues> params;

  public Params(JsonObject object) throws SinfonierException {
    this(new TreeMap<String, ParamValues>());

    if (object != null) {
      for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
        params.put(entry.getKey(), new ParamValues(((JsonObject) entry.getValue())));
      }
    }
  }

  public Params(Map<String, ParamValues> params) {
    this.params = params;
  }

  public boolean hasParam(String keyParam) {
    return params.containsKey(keyParam);
  }

  @Override
  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (params == null) {
      return object;
    }

    for (String key : params.keySet()) {
      object.put(key, params.get(key).toDBObject());
    }

    return object;
  }

  public Map<String, ParamValues> getParams() {
    return params;
  }

  public void setParams(Map<String, ParamValues> params) {
    this.params = params;
  }
}
