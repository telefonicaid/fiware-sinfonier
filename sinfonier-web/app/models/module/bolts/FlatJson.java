package models.module.bolts;

import models.drawer.DrawerModule;
import models.module.Module;
import models.module.Container;
import models.module.Fields;
import models.module.Field;
import static models.SinfonierConstants.Module.*;

public class FlatJson extends DrawerModule {
  
  private static final String NAME = "FlatJson";
  private static final String DESCRIPTION = "Flat a Json using the separator";
  private static final String CATEGORY = "Bolts";
  private static final String TYPE = "bolt";
  private static final String FIELD1_NAME = "separator";
  private static final String FIELD1_TYPE = "string";
  private static final String FIELD1_LABEL = "Separator";
  private static final String ICON_TYPE = "bolt";
  
  public FlatJson() {
    super();
    this.module.setName(NAME);
    this.version.setDescription(DESCRIPTION);
    this.module.setCategory(CATEGORY);
    this.module.setType(TYPE);
    this.module.setStatus(STATUS_PUBLISHED);
    this.version.setStatus(STATUS_PUBLISHED);
    
    Field fieldSeparator = new Field(FIELD1_NAME,FIELD1_TYPE,FIELD1_LABEL,false,false,null,null);

    Fields fields = new Fields();
    fields.add(fieldSeparator);
    this.version.setFields(fields);
    
    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);
    
    this.version.setContainer(container);
  }
}
