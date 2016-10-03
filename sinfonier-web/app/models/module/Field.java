package models.module;

import java.util.ArrayList;
import java.util.List;

import models.Model;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.module.validations.FieldNameCheck;
import play.Logger;
import play.data.validation.CheckWith;
import play.data.validation.Required;

import static models.SinfonierConstants.ElementType.FIELD_FIELDS;
import static models.SinfonierConstants.ElementType.FIELD_SEPARATORS;
import static models.SinfonierConstants.ModuleField.*;

public class Field extends Model {

  @CheckWith(FieldNameCheck.class)
  private String name;

  @Required(message = "validation.required.field.type")
  private String type;

  @Required(message = "validation.required.field.label")
  private String label;

  @Required(message = "validation.required.field.required")
  private Boolean required;

  @Required(message = "validation.required.field.wirable")
  private Boolean wirable;

  private String elementTypeEnum;
  private ElementType elementType;
  
  private List<String> choices;
  
  private ElementTypeFields fields;
  private List<Object> separators;

  public Field(String name, String type, String label, Boolean required, Boolean wirable, String elementTypeEnum, ElementType elementType) {
    this.name = name;
    this.type = type;
    this.label = label;
    this.required = required;
    this.wirable = wirable;
    this.elementTypeEnum = elementTypeEnum;
    this.elementType = elementType;
  }

  public Field(DBObject o) throws SinfonierException {
    try {
      name = o.get(FIELD_NAME).toString();
      type = o.get(FIELD_TYPE).toString();
      label = o.get(FIELD_LABEL).toString();
      required = (Boolean) o.get(FIELD_REQUIRED);
      wirable = (Boolean) o.get(FIELD_WIRABLE);
      if (o.get(FIELD_ELEMENT_TYPE) != null) {
        elementType = new ElementType((DBObject) o.get(FIELD_ELEMENT_TYPE));
      }
    } catch (Exception e) {
      name = type = label = null;
      required = wirable = false;
      elementType = null;
      Logger.error("An exception in Field's constructor > " + e.getMessage());
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

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public boolean isRequired() {
    return required;
  }
  
  public void setRequired(boolean required) {
    this.required = required;
  }

  public boolean isWirable() {
    return wirable;
  }

  public void setWirable(boolean wirable) {
    this.wirable = wirable;
  }

  public String getElementTypeEnum() {
    return elementTypeEnum;
  }

  public void setElementTypeEnum(String elementTypeEnum) {
    this.elementTypeEnum = elementTypeEnum;
  }

  public ElementType getElementType() {
    return elementType;
  }

  public void setElementType(ElementType elementType) {
    this.elementType = elementType;
  }
  
  public List<String> getChoices() {
    return choices;
  }

  public void setChoices(List<String> choices) {
    this.choices = choices;
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

  public DBObject toDBObject() {
    DBObject out = new BasicDBObject();
    if (name != null && label != null && type != null && required != null && wirable != null) {
      out.put(FIELD_NAME, this.name);
      out.put(FIELD_LABEL, this.label);
      out.put(FIELD_TYPE, this.type);
      out.put(FIELD_REQUIRED, this.required);
      out.put(FIELD_WIRABLE, this.wirable);

      List<Object> separators = new ArrayList<Object>();
      separators.add(false); separators.add(false); separators.add(false);
      if (this.elementTypeEnum != null) {
        if (this.elementTypeEnum.equals("text")) {
          // ElementType is null with 'text'
          this.elementType = null;
        } else if (this.elementTypeEnum.equals("keyValue")) {
          // Create structure for key-value
          List<ElementTypeField> elementFields = new ArrayList<ElementTypeField>();
          ElementTypeFields fields = new ElementTypeFields(elementFields);
          ElementTypeField key = new ElementTypeField("string", "key", "Key");
          elementFields.add(key);
          
          ElementTypeField value = new ElementTypeField("string", "value", "Value");;
          elementFields.add(value);
          
          this.elementType = new ElementType("keyValue", "combine", fields, separators);
        } else if (this.elementTypeEnum.equals("keyValueDefault")) {
          // Create structure for key-value-default
          List<ElementTypeField> elementFields = new ArrayList<ElementTypeField>();
          ElementTypeFields fields = new ElementTypeFields(elementFields);
          ElementTypeField key = new ElementTypeField("string", "key", "Key");
          elementFields.add(key);
          
          ElementTypeField value = new ElementTypeField("string", "value", "Value");;
          elementFields.add(value);
          
          ElementTypeField _default = new ElementTypeField("string", "default", "Default");;
          elementFields.add(_default);
          
          this.elementType = new ElementType("keyValueDefault", "combine", fields, separators);
        }
      }
      
      if (this.elementType != null) {
        out.put(FIELD_ELEMENT_TYPE, this.elementType.toDBObject());
      }
      
      if (this.choices != null) {
        BasicDBList dbList = new BasicDBList();
        for (String choice : this.choices) {
          dbList.add(choice);
        }
        out.put(FIELD_CHOICES, dbList);
      }
      
      if (this.fields != null) {  
        out.put(FIELD_FIELDS, this.fields.toDBObject());
      }
      
      if (this.separators != null) {
        out.put(FIELD_SEPARATORS, this.separators);
      }
    }

    return out;
  }
}
