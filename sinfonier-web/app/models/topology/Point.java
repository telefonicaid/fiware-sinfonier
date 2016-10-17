package models.topology;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import static models.SinfonierConstants.Point.*;
import com.google.gson.annotations.SerializedName;

public class Point {
  @SerializedName(FIELD_ID_ANNOTATION)
  private Integer id;
  private String terminal;

  public Point(Integer id, String terminal) {
    this.id = id;
    this.terminal = terminal;
  }

  public Point(DBObject o) {
    id = ((Integer) o.get(FIELD_ID));
    terminal = o.get(FIELD_TERMINAL).toString();
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (id != null) {
      object.put(FIELD_ID, id);
      object.put(FIELD_TERMINAL, terminal);
    }

    return object;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTerminal() {
    return terminal;
  }

  public void setTerminal(String terminal) {
    this.terminal = terminal;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
