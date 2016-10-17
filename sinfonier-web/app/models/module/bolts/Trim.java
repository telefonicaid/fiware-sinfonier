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

public class Trim extends DrawerModule {
  
  private static final String NAME = "Trim";
  private static final String DESCRIPTION = "El objetivo de este [bolt](http://storm.incubator.apache.org/documentation/Concepts.html) es podar los elementos de la entidad para obtener en la salida del mismo una entidad con los elementos indicados. La filosofia puede ser seleccionar los que quieres eliminar (Delete), seleccionar solo los que quieres que continuen (Allow) o combinar ambas opciones";
  private static final String CATEGORY = "Bolts";
  private static final String TYPE = "bolt";
  private static final String FIELD1_NAME = "#";
  private static final String FIELD1_TYPE = "combine";
  private static final String FIELD1_LABEL = "";
  private static final String FIELD1_ETFIELD_NAME = "action";
  private static final String FIELD1_ETFIELD_TYPE = "select";
  private static final String FIELD1_SEPARATOR = "&nbsp; the following fields";
  private static final String FIELD2_NAME = "fields";
  private static final String FIELD2_TYPE = "list";
  private static final String FIELD2_LABEL = "Fields";
  private static final String FIELD2_ET_NAME = "field";
  private static final String FIELD2_ET_TYPE = "string";
  private static final String FIELD2_ET_LABEL = "Field";
  private static final String ICON_TYPE = "bolt";
  
  public Trim() {
    super();
    this.module.setName(NAME);
    this.version.setDescription(DESCRIPTION);
    this.module.setCategory(CATEGORY);
    this.module.setType(TYPE);
    this.module.setStatus(STATUS_PUBLISHED);
    this.version.setStatus(STATUS_PUBLISHED);
    
    
    ElementTypeFields etFields = new ElementTypeFields();
    ElementTypeField f1 = new ElementTypeField(FIELD1_ETFIELD_TYPE, FIELD1_ETFIELD_NAME, " ");
    f1.setChoices(new ArrayList(Arrays.asList("Allows","Deletes")));
    etFields.add(f1);
    List<Object> separators = new ArrayList<Object>();
    separators.add(false);
    separators.add(FIELD1_SEPARATOR);
    
    Field fieldDefinition = new Field(FIELD1_NAME,FIELD1_TYPE,FIELD1_LABEL,false,false,null,null);
    fieldDefinition.setFields(etFields);
    fieldDefinition.setSeparators(separators);
    
    ElementType elementType2 = new ElementType(FIELD2_ET_NAME, FIELD2_ET_TYPE, null, null);
    elementType2.setLabel(FIELD2_ET_LABEL);
    Field fieldFields = new Field(FIELD2_NAME,FIELD2_TYPE,FIELD2_LABEL,false,false,null,elementType2);
    
    Fields fields = new Fields();
    fields.add(fieldDefinition);
    fields.add(fieldFields);
    this.version.setFields(fields);
    
    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);
    
    this.version.setContainer(container);
  }
}
