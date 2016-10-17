package models.module;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import exceptions.SinfonierException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Model;
import play.data.validation.Valid;

public class Fields extends Model implements Iterable<Field> {
  @Valid
  private List<Field> fields;

  public Fields(List<Field> fields) {
    this.fields = fields;
  }

  public Fields() {
    this(new ArrayList<Field>());
  }

  public Fields(BasicDBList o) throws SinfonierException {
    this();

    if (o != null) {
      for (Object o1 : o) {
        add(new Field(((DBObject) o1)));
      }
    }
  }

  public void add(Field field) {
    fields.add(field);
  }

  public int size() {
    return fields.size();
  }

  @Override
  public Iterator<Field> iterator() {
    return fields.iterator();
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

  public DBObject toDBObject() {
    BasicDBList obj = new BasicDBList();

    for (Field field : fields) {
      obj.add(field.toDBObject());
    }

    return obj;
  }
}
