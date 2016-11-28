package controllers;

import play.Logger;
import play.data.validation.Required;
import play.data.validation.Validation;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import exceptions.SinfonierException;
import models.drawer.Drawer;
import models.drawer.DrawerModule;
import models.module.Module;
import models.module.ModuleVersion;
import models.topology.json.serializers.DrawerModuleSerializer;

public class NodeRedEditor extends WebSecurityController {

  public static void index() throws SinfonierException {
    render();
  }
  
  public static void nodes() throws SinfonierException {
    if (request.isAjax()) {
      Drawer drawer = new Drawer(getCurrentUser());
      List<DrawerModule> drawerModules = drawer.getModules();
      
      if (request.headers.get("accept").values.get(0).equals("application/json")) {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DrawerModule.class, new DrawerModuleSerializer());
        for (Class cls : DrawerModule.instantiatedDerivedTypes) {
          builder.registerTypeAdapter(cls, new DrawerModuleSerializer());
        }
        
        Gson gson = builder.create();
        
        renderJSON(gson.toJson(drawerModules));
      } else {
        render(drawerModules);
      }
    }
  }

}
