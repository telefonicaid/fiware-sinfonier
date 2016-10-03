package models.module;

import static models.SinfonierConstants.ElementTypeField.FIELD_NAME;
import static models.SinfonierConstants.ElementTypeField.FIELD_TYPE;
import static models.SinfonierConstants.ElementTypeField.FIELD_TYPE_INVITE;
import static models.SinfonierConstants.ElementTypeField.FIELD_LABEL;
import static models.SinfonierConstants.ElementTypeField.FIELD_CHOICES;

import java.util.List;

import models.Model;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import play.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class ElementTypeField extends Model {

  private String type;
  private String name;
  private String typeInvite;
  private List<String> choices;
  private String label;

  public ElementTypeField(String type, String name, String typeInvite) {
    this.type = type;
    this.name = name;
    this.typeInvite = typeInvite;
  }

  public ElementTypeField(DBObject o) throws SinfonierException {
    try {
      name = o.get(FIELD_NAME).toString();
      type = o.get(FIELD_TYPE).toString();
      typeInvite = o.get(FIELD_TYPE_INVITE).toString();
    } catch (Exception e) {
      name = type = typeInvite = null;
      Logger.error("An exception in ElementTypeField's constructor > " + e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }

  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTypeInvite() {
    return typeInvite;
  }

  public void setTypeInvite(String typeInvite) {
    this.typeInvite = typeInvite;
  }
  
  public List<String> getChoices() {
    return choices;
  }

  public void setChoices(List<String> choices) {
    this.choices = choices;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
  
  public DBObject toDBObject() {
    DBObject out = new BasicDBObject();
    if (name != null && type != null && typeInvite != null) {
      out.put(FIELD_NAME, this.name);
      out.put(FIELD_TYPE, this.type);
      out.put(FIELD_TYPE_INVITE, this.typeInvite);
    }
    if (this.choices != null) {
      BasicDBList dbList = new BasicDBList();
      for (String choice : this.choices) {
        dbList.add(choice);
      }
      out.put(FIELD_CHOICES, dbList);
    }
    if (this.label != null) {
      out.put(FIELD_LABEL, this.label);
    }

    return out;
  }
}
