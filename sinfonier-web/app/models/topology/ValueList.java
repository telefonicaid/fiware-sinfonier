package models.topology;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import static models.SinfonierConstants.ModuleField.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ValueList extends Value {
  private List<String> value;

  public ValueList(String type, String key, List<String> value) {
    super(type, key, value);
  }

  public ValueList(DBObject o) {
    if (o != null && o.keySet().size() == 1) {
      type = TYPE_LIST;
      value = new ArrayList<String>();
      Set<String> keys = o.keySet();

      for (String key : keys) {
        this.key = key;
        BasicDBList values = ((BasicDBList) o.get(key));

        for (Object v : values) {
          value.add(v.toString());
        }
      }
    }
  }

  public ValueList(String key, List<String> values) {
    this(TYPE_LIST, key, values);
  }

  @Override
  public List<String> getValue() {
    return value;
  }

  public void setValue(List<String> value) {
    this.value = value;
  }
}
