package models.drawer;

import static models.SinfonierConstants.ModuleVersion.FIELD_CONTAINER;
import static models.SinfonierConstants.Module.FIELD_NAME;
import static models.SinfonierConstants.Module.FIELD_TYPE;
import static models.SinfonierConstants.Module.FIELD_LANGUAGE;
import static models.SinfonierConstants.Module.FIELD_CATEGORY;
import static models.SinfonierConstants.Module.FIELD_AUTHOR_ID;

import play.Logger;

import com.mongodb.DBObject;

import models.Model;
import models.module.Container;
import models.module.Module;
import models.module.ModuleVersion;

public class DrawerModule {

  public static final String FIELD_LABEL = "label";
  public static final String FIELD_MODULE_NAME = "name";
  public static final String FIELD_MODULE_ID = "module_id";
  public static final String FIELD_VERSION_ID = "module_version_id";

  protected Module module;
  protected ModuleVersion version;

  public DrawerModule() {
    this.module = new Module();
    this.version = new ModuleVersion();
  }

  public DrawerModule(Module module, ModuleVersion version) {
    this.module = module;
    this.version = version;
  }

  @Override
  public String toString() {
    DBObject dbObj = version.toDBObject();

    dbObj.put(FIELD_TYPE, module.getType());
    dbObj.put(FIELD_LANGUAGE, module.getLanguage());
    dbObj.put(FIELD_CATEGORY, module.getCategory());
    dbObj.put(FIELD_AUTHOR_ID, module.getAuthorId());

    dbObj.removeField(FIELD_CONTAINER);
    Container container = version.getContainer();
    container.setLanguage(module.getLanguage());

    DBObject containerDbObject = container.memoryToDBObject();
    containerDbObject.put(FIELD_MODULE_ID, module.getId());
    containerDbObject.put(FIELD_VERSION_ID, version.getId());
    containerDbObject.put(FIELD_MODULE_NAME, module.getName());

    if (version.getVersionTag() != null) {
      containerDbObject.put(FIELD_LABEL, module.getName() + " (" + version.getVersionTag() + ")");
    } else {
      containerDbObject.put(FIELD_LABEL, module.getName());
    }

    dbObj.put(FIELD_CONTAINER, containerDbObject);

    return dbObj.toString();
  }

  public Module getModule() {
    return module;
  }

  public void setModule(Module module) {
    this.module = module;
  }

  public ModuleVersion getVersion() {
    return version;
  }

  public void setVersion(ModuleVersion version) {
    this.version = version;
  }
}
