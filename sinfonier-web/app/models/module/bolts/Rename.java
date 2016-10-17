package models.module.bolts;

import models.drawer.DrawerModule;
import models.module.Module;
import models.module.Container;
import models.module.Fields;
import models.module.Field;
import static models.SinfonierConstants.Module.*;

public class Rename extends DrawerModule {
  
  private static final String NAME = "Rename";
  private static final String DESCRIPTION = "Renames the field specified in \"Field\" by the literal written in \"Replace\"";
  private static final String CATEGORY = "Bolts";
  private static final String TYPE = "bolt";
  private static final String FIELD1_NAME = "find";
  private static final String FIELD1_TYPE = "string";
  private static final String FIELD1_LABEL = "Find";
  private static final String FIELD2_NAME = "replace";
  private static final String FIELD2_TYPE = "string";
  private static final String FIELD2_LABEL = "Replace";
  private static final String ICON_TYPE = "bolt";
  
  public Rename() {
    super();
    this.module.setName(NAME);
    this.version.setDescription(DESCRIPTION);
    this.module.setCategory(CATEGORY);
    this.module.setType(TYPE);
    this.module.setStatus(STATUS_PUBLISHED);
    this.version.setStatus(STATUS_PUBLISHED);
    
    Field fieldFind = new Field(FIELD1_NAME,FIELD1_TYPE,FIELD1_LABEL,false,false,null,null);
    Field fieldReplace = new Field(FIELD2_NAME,FIELD2_TYPE,FIELD2_LABEL,false,false,null,null);

    Fields fields = new Fields();
    fields.add(fieldFind);
    fields.add(fieldReplace);
    this.version.setFields(fields);
    
    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);
    
    this.version.setContainer(container);
  }
}
