package models.module;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.Model;
import models.SinfonierConstants;
import models.ui.Attributes;
import models.ui.OffsetPosition;
import play.Logger;

import org.apache.commons.lang.StringUtils;

import static models.SinfonierConstants.ModuleContainer.*;

import java.util.ArrayList;
import java.util.List;

public class Container extends Model {
  private static final String SINFONIER_PACKAGE = "com.sinfonier.";
  public static final String ICONS_PATH = "/public/images/icons/";
  public static final String ICONS_EXTENSION = ".png";

  private String type;
  private String xType;
  private String icon;
  private List<Terminal> terminals;
  private Attributes attributes;
  private List<Field> fields;
  private TickTuple tickTuple;
  private Boolean singleton;
  private String description;
  private String language;
  private int versionCode;

  public Container(DBObject o, ModuleVersion moduleVersion) throws SinfonierException {
    if (o != null && o.get(FIELD_TYPE) != null) {
      type = o.get(FIELD_TYPE).toString();
    }

    if (o != null && o.get(FIELD_XTYPE) != null) {
      xType = o.get(FIELD_XTYPE).toString();
    }

    if (o != null && o.get(FIELD_ICON) != null) {
      icon = o.get(FIELD_ICON).toString();
    }

    if (o != null && o.get(FIELD_TERMINALS) != null) {
      BasicDBList dbList = ((BasicDBList) o.get(FIELD_TERMINALS));
      terminals = new ArrayList<Terminal>();

      for (Object t : dbList) {
        terminals.add(new Terminal((DBObject) t));
      }
    }

    if (o != null && o.get(FIELD_ATTR) != null) {
      attributes = new Attributes(((DBObject) o.get(FIELD_ATTR)));
    }

    if (moduleVersion != null && moduleVersion.getFields() != null) {
      fields = moduleVersion.getFields().getFields();
    }

    if(moduleVersion != null && moduleVersion.getTickTuple() != null) {
     tickTuple = moduleVersion.getTickTuple();
    }

    singleton = moduleVersion != null ? moduleVersion.isSingleton() : false;
    versionCode = moduleVersion.getVersionCode();
    description = moduleVersion.getDescription();
  }

  public Container(Module module, ModuleVersion version) {
    if (version.hasFields() && module.getType().equals(SinfonierConstants.Module.TYPE_COMMENT)) {
      xType = SinfonierConstants.UI.X_TYPE_TEXTAREA;
    } else {
      xType = SinfonierConstants.UI.X_TYPE_FORM;
    }

    terminals = new ArrayList<Terminal>(createDefaultTerminals(module));
    type = module.getType();
    icon = ICONS_PATH + type + ICONS_EXTENSION;
    attributes = new Attributes(StringUtils.defaultIfBlank(module.getName(), ",").toLowerCase(), this.getClassName(module));
    singleton = version.isSingleton();
    description = version.getDescription();
    language = module.getLanguage();
    versionCode = version.getVersionCode();

    if (version.getFields() != null) {
      fields = version.getFields().getFields();
    }

    if (version.getTickTuple() != null) {
      tickTuple = version.getTickTuple();
    }
  }

  private String getClassName(Module module) {
    String[] words = module.getName().split(" ");
    for (int i = 0; i < words.length; i++) {
      words[i] = StringUtils.capitalize(words[i]);
    }
    return SINFONIER_PACKAGE + module.getType() + "s." + StringUtils.join(words);
  }

  private List<Terminal> createDefaultTerminals(Module module) {
    List<Terminal> terminals = new ArrayList<Terminal>();
    List<Integer> inputDirections = new ArrayList<Integer>();
    List<Integer> outputDirections = new ArrayList<Integer>();
    OffsetPosition position;
    Integer offset = SinfonierConstants.Terminal.OFFSET_WITH_FIELDS;

    if (module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_DRAIN) ||
        module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_BOLT) ||
        module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_OPERATOR)) {
      position = new OffsetPosition(offset, null, null, SinfonierConstants.OffsetPosition.DEFAULT_LEFT);

      inputDirections.add(0);
      inputDirections.add(-1);
      terminals.add(new TerminalIn(inputDirections, position, Module.getMaxWires()));
    }

    if (module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_BOLT) ||
        module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_SPOUT) ||
        module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_VARIABLE)) {
      position = new OffsetPosition(null, null, offset, SinfonierConstants.OffsetPosition.DEFAULT_LEFT);

      outputDirections.add(0);
      outputDirections.add(1);
      if (module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_VARIABLE)) {
        terminals.add(new TerminalGlobal(outputDirections, position, Module.getMaxWires()));
      } else {
        terminals.add(new TerminalOut(outputDirections, position, Module.getMaxWires()));
      }
    }

    if (module.getType().equalsIgnoreCase(SinfonierConstants.Module.TYPE_OPERATOR)) {
      outputDirections.add(0);
      outputDirections.add(1);

      position = new OffsetPosition(null, null, offset, SinfonierConstants.OffsetPosition.YES_LEFT);
      terminals.add(new TerminalYes(outputDirections, position, Module.getMaxWires()));

      OffsetPosition positionNo = new OffsetPosition(null, null, offset, SinfonierConstants.OffsetPosition.NO_LEFT);
      terminals.add(new TerminalNo(outputDirections, positionNo, Module.getMaxWires()));

    }

    return terminals;
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (xType != null && type != null && attributes != null) {
      object.put(FIELD_XTYPE, xType);
      object.put(FIELD_TYPE, type);
      object.put(FIELD_ICON, icon);
      object.put(FIELD_ATTR, attributes.toDBObject());
    }

    if (terminals != null) {
      BasicDBList dbList = new BasicDBList();
      for (Terminal t : terminals) {
        dbList.add(t.toDBObject());
      }

      object.put(FIELD_TERMINALS, dbList);
    }

    return object;
  }

  public DBObject memoryToDBObject() {
    DBObject dbObj = this.toDBObject();
    BasicDBList dbList = new BasicDBList();

    if (fields != null) {
      for (Field field : fields) {
        dbList.add(field.toDBObject());
      }
    }

    if (tickTuple != null) {
      dbList.add(tickTuple.toDBObject());
    }

    dbObj.put(FIELD_FIELDS, dbList);
    dbObj.put(FIELD_SINGLETON, singleton);
    dbObj.put(FIELD_DESCRIPTION, description);
    dbObj.put(FIELD_LANGUAGE, language);

    if (versionCode > 0) {
      dbObj.put(FIELD_VERSION_CODE, versionCode);
    }

    return dbObj;
  }

  @Override
  public String toString() {
    return memoryToDBObject().toString();

  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getXType() {
    return xType;
  }

  public void setXType(String xType) {
    this.xType = xType;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public List<Terminal> getTerminals() {
    return terminals;
  }

  public void setTerminals(List<Terminal> terminals) {
    this.terminals = terminals;
  }

  public Attributes getAttributes() {
    return attributes;
  }

  public void setAttributes(Attributes attributes) {
    this.attributes = attributes;
  }

  public Boolean getSingleton() {
    return singleton;
  }

  public void setSingleton(Boolean singleton) {
    this.singleton = singleton;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public List<Field> getFields() {
    return fields;
  }

  public void setFields(List<Field> fields) {
    this.fields = fields;
  }

  public TickTuple getTickTuple() {
    return tickTuple;
  }

  public void setTickTuple(TickTuple tickTuple) {
    this.tickTuple = tickTuple;
  }
}
