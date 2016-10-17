package models.module;

import models.Model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import play.Logger;
import play.data.binding.NoBinding;
import play.data.validation.Required;
import static models.SinfonierConstants.TickTuple.*;

public class TickTuple extends Model {
  private static final String NAME = "TickTuple";
  private static final String TYPE = "integer";

  @NoBinding
  private String name = NAME;

  @NoBinding
  private String type = TYPE;

  @Required(message = "validation.required.ticktuple.label")
  private String label;

  @Required(message = "validation.required.ticktuple.required")
  private boolean required;

  @Required(message = "validation.required.ticktuple.wirable")
  private boolean wirable;

  public TickTuple(String type, String label, boolean required, boolean wirable) {
    this.name = NAME;
    this.type = type;
    this.label = label;
    this.required = required;
    this.wirable = wirable;
  }

  public TickTuple(DBObject o) throws SinfonierException {
    try {
      this.name = o.get(FIELD_NAME).toString();
      this.type = o.get(FIELD_TYPE).toString();
      this.label = o.get(FIELD_LABEL).toString();
      this.required = (Boolean) o.get(FIELD_REQUIRED);
      this.wirable = (Boolean) o.get(FIELD_WIRABLE);
    } catch (Exception e) {
      name = type = label = null;
      required = wirable = false;
      Logger.error("" + e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }

  }

  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    if (label != null && !label.isEmpty()) {
      this.label = label.trim();
    } else {
      this.label = null;
    }
  }

  public boolean isRequired() {
    return required;
  }

  public boolean isWirable() {
    return wirable;
  }

  public void setWirable(boolean wirable) {
    this.wirable = wirable;
  }

  public void setRequired(boolean required) {
    this.required = required;
  }

  public DBObject toDBObject() {
    DBObject out = new BasicDBObject();

    if (label != null) {
      out.put(FIELD_NAME, NAME);
      out.put(FIELD_TYPE, TYPE);
      out.put(FIELD_LABEL, this.label);
      out.put(FIELD_REQUIRED, this.required);
      out.put(FIELD_WIRABLE, this.wirable);
    }

    return out;
  }
}
