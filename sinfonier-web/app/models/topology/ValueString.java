package models.topology;

import com.mongodb.DBObject;
import static models.SinfonierConstants.ModuleField.*;

import java.util.Set;

public class ValueString extends Value {
  private String value;

  public ValueString(String type, String key, String value) {
    super(type, key, value);
  }

  public ValueString(DBObject o) {
    if (o != null && o.keySet().size() == 1) {
      type = TYPE_STRING;
      Set<String> keys = o.keySet();

      for (String key : keys) {
        this.key = key;
        value = o.get(key).toString();
      }
    }
  }

  public ValueString(String key, String value) {
    this(TYPE_STRING, key, value);
  }

  @Override
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
