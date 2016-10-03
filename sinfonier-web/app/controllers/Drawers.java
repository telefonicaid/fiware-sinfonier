package controllers;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;
import exceptions.SinfonierException;
import models.drawer.Drawer;
import models.drawer.DrawerModule;
import models.module.Module;
import models.module.ModuleVersion;

public class Drawers extends WebSecurityController {

  public static void index() throws SinfonierException {
    Drawer drawer = new Drawer(getCurrentUser());
    render(drawer);
  }
  
  public static void module(@Required(message = "validation.required.drawer.name") String name, 
                            @Required(message = "validation.required.drawer.versionCode")Integer versionCode) 
                                throws SinfonierException {
    
    if (!Validation.hasErrors()) {
      Module module = Module.findByName(name);
  
      if (module != null) {
        ModuleVersion moduleVersion;
        if (versionCode != null) {
          moduleVersion = module.getModuleVersion(versionCode);
          if (moduleVersion != null) {
            // Generate JSON
            DrawerModule json = new DrawerModule(module, moduleVersion);
            renderJSON(json.toString());
          }
        }
  
        notFound();
      } else {
        notFound();
      }
    } else {
      Logger.error("Missing some params: name:" + name + ", versionCode:" + versionCode);
      error();
    }
  }
}
