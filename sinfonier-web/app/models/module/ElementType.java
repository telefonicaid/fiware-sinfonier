package models.module;

import static models.SinfonierConstants.ElementType.FIELD_NAME;
import static models.SinfonierConstants.ElementType.FIELD_TYPE;
import static models.SinfonierConstants.ElementType.FIELD_FIELDS;
import static models.SinfonierConstants.ElementType.FIELD_SEPARATORS;
import static models.SinfonierConstants.ElementType.FIELD_LABEL;

import java.util.List;

import models.Model;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import play.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ElementType extends Model {

  private String name;
  private String type;
  private ElementTypeFields fields;
  private List<Object> separators;
  private String label;

  public ElementType(String name, String type, ElementTypeFields fields, List<Object> separators) {
    this.name = name;
    this.type = type;
    this.fields = fields;
    this.separators = separators;
  }

  public ElementType(DBObject o) throws SinfonierException {
    try {
      name = o.get(FIELD_NAME).toString();
      type = o.get(FIELD_TYPE).toString();
      fields = new ElementTypeFields((BasicDBList) o.get(FIELD_FIELDS));
      separators = (List<Object>) o.get(FIELD_SEPARATORS);
    } catch (Exception e) {
      name = type = null;
      fields = null;
      separators = null;
      Logger.error("An exception in ElementType's constructor > " + e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ElementTypeFields getFields() {
    return fields;
  }

  public void setFields(ElementTypeFields fields) {
    this.fields = fields;
  }

  public List<Object> getSeparators() {
    return separators;
  }

  public void setSeparators(List<Object> separators) {
    this.separators = separators;
  }
  
  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public DBObject toDBObject() {
    DBObject out = new BasicDBObject();
    if (name != null && type != null && fields != null && separators != null) {
      out.put(FIELD_NAME, this.name);
      out.put(FIELD_TYPE, this.type);
      out.put(FIELD_FIELDS, this.fields.toDBObject());
      out.put(FIELD_SEPARATORS, this.separators);
    }
    if (label != null) {
      out.put(FIELD_LABEL, this.getLabel());
    }
    return out;
  }
}
