package controllers;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import exceptions.SinfonierException;
import models.drawer.Drawer;
import models.drawer.DrawerModule;
import models.module.Module;
import models.module.ModuleVersion;

public class NodeRedEditor extends BaseController {

  public static void index() throws SinfonierException {
    render();
  }

}
