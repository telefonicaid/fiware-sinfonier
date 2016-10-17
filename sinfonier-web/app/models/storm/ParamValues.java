package models.storm;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParamValues extends Model {
  public static final String FIELD_TYPE = "type";
  public static final String FIELD_VALUE = "value";
  public static final String FIELD_RANGE = "range";

  private static final Integer[] INIT_RANGE = {0, 0};

  private String type;
  private String value;
  private List<Integer> range;

  public ParamValues(JsonObject object) throws SinfonierException {
    if (object == null) {
      throw new SinfonierException(SinfonierError.INVALID_CONSTRUCTION);
    }

    if (object.get(FIELD_TYPE) == null || object.get(FIELD_VALUE) == null) {
      throw new SinfonierException(SinfonierError.INVALID_CONSTRUCTION);
    } else {
      type = object.get(FIELD_TYPE).getAsString().trim().toLowerCase();
      value = object.get(FIELD_VALUE).getAsString();
      range = new ArrayList<Integer>(Arrays.asList(INIT_RANGE));

      if (object.get(FIELD_RANGE) != null && object.get(FIELD_RANGE).isJsonArray()) {
        range.set(0, object.get(FIELD_RANGE).getAsJsonArray().get(0).getAsInt());
        range.set(1, object.get(FIELD_RANGE).getAsJsonArray().get(1).getAsInt());
      }
    }

  }

  public ParamValues(String type, String value) {
    this.type = type.trim().toLowerCase();
    this.value = value;
    this.range = new ArrayList<Integer>(Arrays.asList(INIT_RANGE));
  }

  public ParamValues(String type, String value, List<Integer> range) {
    this(type, value);

    if (range != null) {
      this.range = range;
    }
  }

  public int getMinRange() {
    return range.get(0);
  }

  public int getMaxRange() {
    return range.get(1);
  }

  @Override
  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (type == null || value == null) {
      return object;
    }

    object.put(FIELD_TYPE, type);
    object.put(FIELD_VALUE, value);

    BasicDBList rangeDbList = new BasicDBList();
    rangeDbList.add(0, getMinRange());
    rangeDbList.add(1, getMaxRange());

    object.put(FIELD_RANGE, rangeDbList);

    return object;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public List<Integer> getRange() {
    return range;
  }

  public void setRange(List<Integer> range) {
    this.range = range;
  }
}
