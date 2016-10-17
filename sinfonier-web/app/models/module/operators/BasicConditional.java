package models.module.operators;

import java.util.ArrayList;
import java.util.Arrays;

import models.drawer.DrawerModule;
import models.module.Module;
import models.module.Container;
import models.module.Fields;
import models.module.Field;
import models.module.ModuleVersion;
import static models.SinfonierConstants.Module.*;

public class BasicConditional extends DrawerModule {
  
  private static final String CATEGORY = "Operators";
  private static final String TYPE = "operator";
  private static final String FIELD1_NAME = "field";
  private static final String FIELD1_TYPE = "string";
  private static final String FIELD1_LABEL = "Field";
  private static final String FIELD2_NAME = "operator";
  private static final String FIELD2_TYPE = "select";
  private static final String FIELD2_LABEL = "Operator";
  
  public BasicConditional() {
    super();
    this.module.setCategory(CATEGORY);
    this.module.setType(TYPE);
    this.module.setStatus(STATUS_PUBLISHED);
    this.version.setStatus(STATUS_PUBLISHED);
    
    Field fieldField = new Field(FIELD1_NAME,FIELD1_TYPE,FIELD1_LABEL,false,false,null,null);
    Field fieldOperator = new Field(FIELD2_NAME,FIELD2_TYPE,FIELD2_LABEL,false,false,null,null);
    ArrayList<String> choices = new ArrayList(Arrays.asList(">",">=","<","<=","==","!=","RegexExpression"));
    fieldOperator.setChoices(choices);
    Fields fields = new Fields();
    fields.add(fieldField);
    fields.add(fieldOperator);
    this.version.setFields(fields);
  }
}
