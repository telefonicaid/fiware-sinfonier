package models.module;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Model;
import exceptions.SinfonierException;
import play.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class ElementTypeFields extends Model implements Iterable<ElementTypeField> {

  private List<ElementTypeField> fields;

  public ElementTypeFields(List<ElementTypeField> fields) {
    this.fields = fields;
  }

  public ElementTypeFields() {
    this(new ArrayList<ElementTypeField>());
  }

  public ElementTypeFields(BasicDBList o) throws SinfonierException {
    this();

    if (o != null) {
      for (Object o1 : o) {
        add(new ElementTypeField(((DBObject) o1)));
      }
    }
  }

  public void add(ElementTypeField field) {
    fields.add(field);
  }

  public int size() {
    return fields.size();
  }

  public List<ElementTypeField> getFields() {
    return fields;
  }

  public void setFields(List<ElementTypeField> fields) {
    this.fields = fields;
  }

  @Override
  public Iterator<ElementTypeField> iterator() {
    return fields.iterator();
  }

  public DBObject toDBObject() {
    BasicDBList obj = new BasicDBList();

    for (ElementTypeField element : fields) {
      obj.add(element.toDBObject());
    }

    return obj;
  }
}
