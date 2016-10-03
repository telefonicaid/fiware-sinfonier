package models.module.operators;

import models.drawer.DrawerModule;
import models.module.Module;
import models.module.Container;
import models.module.Fields;
import models.module.Field;
import static models.SinfonierConstants.Module.*;

public class Exists extends DrawerModule {
  
  private static final String NAME = "Exists";
  private static final String DESCRIPTION = "Check if exists an input field";
  private static final String CATEGORY = "Operators";
  private static final String TYPE = "operator";
  private static final String FIELD_NAME = "field";
  private static final String FIELD_TYPE = "string";
  private static final String FIELD_LABEL = "Field";
  private static final String ICON_TYPE = "operator";
  
  public Exists() {
    super();
    this.module.setName(NAME);
    this.version.setDescription(DESCRIPTION);
    this.module.setCategory(CATEGORY);
    this.module.setType(TYPE);
    this.module.setStatus(STATUS_PUBLISHED);
    this.version.setStatus(STATUS_PUBLISHED);
    
    Field fieldField = new Field(FIELD_NAME,FIELD_TYPE,FIELD_LABEL,false,false,null,null);
    Fields fields = new Fields();
    fields.add(fieldField);
    this.version.setFields(fields);
    
    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);
    
    this.version.setContainer(container);
  }
}
