package models.module.operators;

import models.module.Module;
import models.module.Container;
import models.module.Fields;
import models.module.Field;

import static models.SinfonierConstants.Module.*;

public class Conditional extends BasicConditional {
  
  private static final String NAME = "Conditional";
  private static final String DESCRIPTION = "Conditional operation to choose between two possible outputs";
  private static final String FIELD3_NAME = "value";
  private static final String FIELD3_TYPE = "string";
  private static final String FIELD3_LABEL = "Value";
  private static final String ICON_TYPE = "operator";
  
  public Conditional() {
    super();
    this.module.setName(NAME);
    this.version.setDescription(DESCRIPTION);
    
    Field fieldValue = new Field(FIELD3_NAME,FIELD3_TYPE,FIELD3_LABEL,false,true,null,null);
    this.version.getFields().add(fieldValue);;
    
    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);
    
    this.version.setContainer(container);
  }
}
