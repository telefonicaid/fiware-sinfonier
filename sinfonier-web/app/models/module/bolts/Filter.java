package models.module.bolts;

import models.drawer.DrawerModule;
import models.module.Module;
import models.module.Container;
import models.module.ElementType;
import models.module.ElementTypeField;
import models.module.ElementTypeFields;
import models.module.Fields;
import models.module.Field;
import static models.SinfonierConstants.Module.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Filter extends DrawerModule {
  
  private static final String NAME = "Filter";
  private static final String DESCRIPTION = "Input parameters filtered following several rules";
  private static final String CATEGORY = "Bolts";
  private static final String TYPE = "bolt";
  private static final String FIELD1_NAME = "#";
  private static final String FIELD1_TYPE = "combine";
  private static final String FIELD1_LABEL = "";
  private static final String FIELD1_ETFIELD1_NAME = "action";
  private static final String FIELD1_ETFIELD1_TYPE = "select";
  private static final String FIELD1_ETFIELD2_NAME = "match";
  private static final String FIELD1_ETFIELD2_TYPE = "select";
  private static final String FIELD1_SEPARATOR1 = "&nbsp; items that match";
  private static final String FIELD1_SEPARATOR2 = "&nbsp; of the following";
  
  private static final String FIELD2_NAME = "conditions";
  private static final String FIELD2_TYPE = "list";
  private static final String FIELD2_LABEL = "Rules";
  private static final String FIELD2_ET_NAME = "condition";
  private static final String FIELD2_ETFIELD3_NAME = "field";
  private static final String FIELD2_ETFIELD3_TYPE = "string";
  private static final String FIELD2_ETFIELD4_NAME = "operator";
  private static final String FIELD2_ETFIELD4_TYPE = "select";
  private static final String FIELD2_ETFIELD5_NAME = "value";
  private static final String FIELD2_ETFIELD5_TYPE = "string";
  private static final String FIELD2_SEPARATOR = "&nbsp;&nbsp;";
  
  private static final String ICON_TYPE = "bolt";
  
  public Filter() {
    super();
    this.module.setName(NAME);
    this.version.setDescription(DESCRIPTION);
    this.module.setCategory(CATEGORY);
    this.module.setType(TYPE);
    this.module.setStatus(STATUS_PUBLISHED);
    this.version.setStatus(STATUS_PUBLISHED);
       
    ElementTypeFields etFields = new ElementTypeFields();
    ElementTypeField f1 = new ElementTypeField(FIELD1_ETFIELD1_TYPE, FIELD1_ETFIELD1_NAME, " ");
    f1.setChoices(new ArrayList(Arrays.asList("Permit","Block")));
    etFields.add(f1);
    ElementTypeField f2 = new ElementTypeField(FIELD1_ETFIELD2_TYPE, FIELD1_ETFIELD2_NAME, " ");
    f2.setChoices(new ArrayList(Arrays.asList("Any","All")));
    etFields.add(f2);
    List<Object> separators = new ArrayList<Object>();
    separators.add(false);
    separators.add(FIELD1_SEPARATOR1);
    separators.add(FIELD1_SEPARATOR2);
    
    Field fieldDefinition = new Field(FIELD1_NAME,FIELD1_TYPE,FIELD1_LABEL,false,false,null,null);
    fieldDefinition.setFields(etFields);
    fieldDefinition.setSeparators(separators);
    
    ElementTypeFields etFields2 = new ElementTypeFields();
    ElementTypeField f3 = new ElementTypeField(FIELD2_ETFIELD3_TYPE, FIELD2_ETFIELD3_NAME, FIELD2_ETFIELD3_NAME);
    etFields2.add(f3);
    ElementTypeField f4 = new ElementTypeField(FIELD2_ETFIELD4_TYPE, FIELD2_ETFIELD4_NAME, "");
    f4.setChoices(new ArrayList(Arrays.asList("<", "<=", ">",">=","==","!=","match","contains","not contains")));
    etFields2.add(f4);
    ElementTypeField f5 = new ElementTypeField(FIELD2_ETFIELD5_TYPE, FIELD2_ETFIELD5_NAME, FIELD2_ETFIELD5_NAME);
    etFields2.add(f5);
    List<Object> separators2 = new ArrayList<Object>();
    separators2.add(false);
    separators2.add(FIELD2_SEPARATOR);
    separators2.add(FIELD2_SEPARATOR);
    separators2.add(false);
    ElementType elementType = new ElementType(FIELD2_ET_NAME, FIELD1_TYPE, etFields2, separators2);
    Field fieldRules = new Field(FIELD2_NAME,FIELD2_TYPE,FIELD2_LABEL,false,false,null,elementType);
    
    Fields fields = new Fields();
    fields.add(fieldDefinition);
    fields.add(fieldRules);
    this.version.setFields(fields);
    
    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);
    
    this.version.setContainer(container);
  }
}
