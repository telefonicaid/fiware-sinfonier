package models.module.utils;

import models.drawer.DrawerModule;
import models.module.Module;
import models.module.Container;
import models.module.Fields;
import models.module.Field;

import static models.SinfonierConstants.Module.*;

public class GlobalVariable extends DrawerModule {

  private static final String NAME = "Global Variable";
  private static final String CATEGORY = "Utils";
  private static final String TYPE = "variable";
  private static final String DESCRIPTION = "Global Variable assignable to fields";
  private static final String FIELD_NAME = "value";
  private static final String FIELD_TYPE = "string";
  private static final String FIELD_LABEL = "value";
  private static final String ICON_TYPE = "globe";

  public GlobalVariable() {
    super();
    this.module.setName(NAME);
    this.module.setCategory(CATEGORY);
    this.version.setSingleton(true);
    this.module.setType(TYPE);
    this.version.setDescription(DESCRIPTION);
    this.module.setStatus(STATUS_PUBLISHED);
    this.version.setStatus(STATUS_PUBLISHED);

    Field field = new Field(FIELD_NAME, FIELD_TYPE, FIELD_LABEL, false, false, null, null);
    Fields fields = new Fields();
    fields.add(field);
    this.version.setFields(fields);

    Container container = new Container(this.module, this.version);
    container.setIcon(Container.ICONS_PATH + ICON_TYPE + Container.ICONS_EXTENSION);

    this.version.setContainer(container);
  }
}
