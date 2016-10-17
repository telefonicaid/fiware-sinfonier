package models.module.operators;

import models.module.Module;
import models.module.Container;
import models.module.Fields;
import models.module.Field;

import static models.SinfonierConstants.Module.*;

public class ConditionalFields extends BasicConditional {
  
  private static final String NAME = "ConditionalFields";
  private static final String DESCRIPTION = "Conditional operation between two fields";
  private static final String FIELD3_NAME = "field2";
  private static final String FIELD3_TYPE = "string";
  private static final String FIELD3_LABEL = "Field";
  private static final String ICON_TYPE = "operator";
  
  public ConditionalFields() {
    super();
    this.module.setName(NAME);
    this.version.setDescription(DESCRIPTION);
    
    Field fieldValue = new Field(FIELD3_NAME,FIELD3_TYPE,FIELD3_LABEL,false,false,null,null);
    this.version.getFields().add(fieldValue);;
    
    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);
    
    this.version.setContainer(container);
  }
}
