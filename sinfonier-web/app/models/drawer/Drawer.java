package models.drawer;

import exceptions.SinfonierException;
import models.module.Module;
import models.module.utils.*;
import models.module.operators.*;
import models.module.bolts.*;
import models.module.ModuleVersion;
import models.module.ModulesContainer;
import models.module.Version;
import models.user.User;
import static models.SinfonierConstants.Module.STATUS_PUBLISHED;
import static models.SinfonierConstants.Module.STATUS_PRIVATE;

import java.util.ArrayList;
import java.util.List;

public class Drawer {
  private List<DrawerModule> drawerModules;

  public Drawer(User user) throws SinfonierException {
    int i, j;
    this.drawerModules = new ArrayList<DrawerModule>();
    
    // Get user modules
    ModulesContainer modules = Module.getModules(user, true, null);
    for (i=0; i < modules.getModules().size(); i++) {
      Module module = modules.getModules().get(i);
      List<Version> versions = module.getVersions().getVersions();
      
      // Search versions published
      for (j=0; j < versions.size(); j++) {
        ModuleVersion version = versions.get(j).getModuleVersion();
        
        if (module.isOwner(user) && (version.getStatus().equals(STATUS_PUBLISHED) 
            || version.getStatus().equals(STATUS_PRIVATE))) {
          // Own modules
          DrawerModule item = new DrawerModule(module, version);
          this.drawerModules.add(item);
        } else if (!module.isOwner(user) && version.getMyTools().hasAdded(user)) {
          // My tools modules
          DrawerModule item = new DrawerModule(module, version);
          this.drawerModules.add(item);
        } 
      }
    }
    
    // Static modules
    GlobalVariable globalVariable = new GlobalVariable();
    this.drawerModules.add(globalVariable);
    Comment comment = new Comment();
    this.drawerModules.add(comment);
    addOperatorsToModules();
    addPredefinedBoltsToModules();
  }

  public List<DrawerModule> getModules() {
    return drawerModules;
  }
  
  private void addOperatorsToModules() {
    Conditional conditional = new Conditional();
    drawerModules.add(conditional);
    ConditionalFields conditionalFields = new ConditionalFields();
    drawerModules.add(conditionalFields);
    Exists exists = new Exists();
    drawerModules.add(exists);
  }
  
  private void addPredefinedBoltsToModules() {
    Filter filter = new Filter();
    drawerModules.add(filter);
    FlatJson flatJson = new FlatJson();
    drawerModules.add(flatJson);
    Rename rename = new Rename();
    drawerModules.add(rename);
    Trim trim = new Trim();
    drawerModules.add(trim);
  }
}
