package controllers;

import com.google.gson.Gson;

import com.google.gson.JsonObject;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.module.Container;
import models.module.Module;
import models.module.ModulesContainer;
import models.module.ModuleSearch;
import models.module.ModuleVersion;
import models.module.Version;
import models.module.Versions;
import models.storm.Client;
import models.user.Inappropriate;
import models.user.MyTool;
import models.user.Rating;
import models.user.User;
import notifiers.SinfonierMailer;
import play.Logger;
import play.data.validation.*;
import play.i18n.Messages;
import play.mvc.Catch;
import play.mvc.Util;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;


import static models.SinfonierConstants.Module.STATUS_PUBLISHED;
import static models.SinfonierConstants.Module.STATUS_DEV;
import static models.SinfonierConstants.Module.STATUS_DELETED;
import static models.SinfonierConstants.Module.STATUS_PENDING;
import static models.SinfonierConstants.Module.STATUS_PRIVATE;
import static models.SinfonierConstants.Module.STATUS_PREDEFINED;
import static models.SinfonierConstants.Module.LIMIT_COMPLAINS_NOTIFY_ADMIN;
import static models.SinfonierConstants.ModuleVersion.LIMIT_PENDING_MODULE_VERSIONS;

public class Modules extends WebSecurityController {

  public static final String INDEX_MODE_TOP_MODULES = "topModules";
  public static final String INDEX_MODE_MY_MODULES = "myModules";
  public static final String FLASH_KEY_BACKEND_LOG = "backend_log";
  public static final String FLASH_KEY_WARNING = "warning";
  public static final String[] AVOIDED_PAGINATION_PARAMS = {"page", "body"};
  private static final int FIRST_VERSION_CODE = 1;
  private static Client client = Client.getInstance();

  public static void index(String indexMode, int page) throws SinfonierException {
    ModulesContainer modulesContainer;
    if (indexMode != null && indexMode.equals(INDEX_MODE_MY_MODULES)) {
      modulesContainer = Module.getModules(getCurrentUser(), false, page);
    } else if (indexMode != null && indexMode.equals(INDEX_MODE_TOP_MODULES)) {
      modulesContainer = Module.getTopModules();
    } else {
      modulesContainer = Module.getModules(getCurrentUser(), page);
    }
    List<Module> modules = modulesContainer.getModules();
    int totalModules = modulesContainer.getCountBeforeLimit();
    render(modules, page, totalModules);
  }

  public static void search(ModuleSearch search, int page) throws SinfonierException {
    ModulesContainer modulesContainer = Module.find(search, getCurrentUser(), page);
    flash("searching", true);
    params.flash();
    List<Module> modules = modulesContainer.getModules();
    int totalModules = modulesContainer.getCountBeforeLimit();
    render("Modules/index.html", modules, search, page, totalModules);
  }

  public static void user(String id) throws SinfonierException {
    ModulesContainer modulesContainer = Module.getModules(getCurrentUser(), false, null);
    List<Module> modules = modulesContainer.getModules();
    int totalModules = modulesContainer.getCountBeforeLimit();
    render("Modules/index.html", modules, totalModules);
  }

  public static void module(String name, Integer versionCode) throws SinfonierException {
    Module module = Module.findByName(name);
    User user = getCurrentUser();

    if (module == null) {
      Logger.error("We can't found the module with name: " + name);
      notFound();
    } else {
      ModuleVersion moduleVersion;
      if (versionCode == null) {
        moduleVersion = module.lastModuleVersion();
      } else {
        moduleVersion = module.getModuleVersion(versionCode);
      }

      // Check permissions to view version
      if (moduleVersion == null || !module.hasReadPermission(moduleVersion, user)) {
        moduleNotFoundError("We can't found the module with versionCode: " + versionCode);
      }
      render(module, moduleVersion);
    }
  }

  public static void add() {
    Module module = new Module();
    ModuleVersion moduleVersion = new ModuleVersion();
    moduleVersion.setVersionCode(FIRST_VERSION_CODE);
    render(module, moduleVersion);
  }

  public static void edit(String name, Integer versionCode) throws SinfonierException {
    Module module = Module.findByName(name);
    User user = getCurrentUser();

    if (module == null || !module.hasWritePermission(user)) {
      Logger.error("We can't found the module with name: " + name);
      notFound();
    } else {
      ModuleVersion moduleVersion;
      if (versionCode == null) {
        moduleVersion = module.lastModuleVersion();
      } else {
        moduleVersion = module.getModuleVersion(versionCode);
      }

      if (moduleVersion == null) {
        moduleNotFoundError("We can't found the versionCode: " + versionCode);
      }

      if (!moduleVersion.getStatus().equals(STATUS_DEV)) {
        moduleVersion.setId(null);
        moduleVersion.setVersionCode(module.getNextVersionCode());
        moduleVersion.setVersionTag(null);
        moduleVersion.setStatus(STATUS_DEV);
        Versions versions = module.getVersions();
        if (versions.getLastVersion().getVersionCode() != versionCode && versions.getLastVersion().getVersionTag() == null &&
            module.getModuleVersion(versions.getLastVersion().getVersionCode()).getStatus().equals(STATUS_DEV)) {
          flash.put(FLASH_KEY_WARNING, Messages.get("Modules.form.warning.versions.moreDeveloping"));
        }
      }

      render("Modules/add.html", module, moduleVersion);
    }
  }

  public static void save(@Valid Module module, @Valid ModuleVersion version) throws SinfonierException {
    checkAuthenticity();

    if (Validation.hasErrors()) {
      for (play.data.validation.Error error : Validation.errors()) {
        Logger.error(error.message());
      }
      params.flash();
      ModuleVersion moduleVersion = version;
      render("Modules/add.html", module, moduleVersion);
    } else {
      if (module.getId() == null) {
        module.setTopologiesCount(0);
        module.setCreatedAt(new Date());
        module.setUpdatedAt(new Date());
        module.setAuthorId(getCurrentUser().getId());
        module.setVersions(new Versions());
      } else {
        Module tmp = Module.findById(module.getId());
        if (tmp != null)
          module.setVersions(tmp.getVersions());
      }
      
      if (module.getVersions().getVersions().size() > 0) {
        List<Version> versions = module.getVersions().getVersions();
        List<String> versionTagList = new ArrayList<String>();
        for (Version tmpVersion : versions) {
          versionTagList.add(tmpVersion.getModuleVersion().getVersionTag());
        }
        if (versionTagList.contains(version.getVersionTag())) {
          Logger.debug("Version tag already used");
          flash.put(FLASH_KEY_BACKEND_LOG, Messages.get("Modules.form.err.versionTagUsed"));
          ModuleVersion moduleVersion = version;
          render("Modules/add.html", module, moduleVersion);
        } else {
          saveModuleAndVersion(module, version);
        }
      } else {
        saveModuleAndVersion(module, version);
      }
    }
  }

  private static void saveModuleAndVersion(Module module, ModuleVersion version) throws SinfonierException {
    // Save module version
    if (version.getId() == null) {
      version.setTopologiesCount(0);
      version.setCreatedAt(new Date());
      version.setUpdatedAt(new Date());

      if (version.getTickTuple() == null || version.getTickTuple().getLabel() == null) {
        version.setTickTuple(null);
      }
    }

    version.setContainer(new Container(module, version));
    version.setSingleton(!version.isSingleton());
    version.save();

    // Save version reference
    Version v = new Version(version);
    if (!module.getVersions().contains(v)) {
      module.getVersions().add(v);
    }

    module.save();
    index(INDEX_MODE_MY_MODULES,1);
  }
  
  public static void remove(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);
    User user = getCurrentUser();

    if (module != null && module.hasWritePermission(user)) {
      ModuleVersion moduleVersion;

      if (versionCode != null) {
        moduleVersion = module.getModuleVersion(versionCode);

        if (moduleVersion != null) {
          if (moduleVersion.getStatus().equals(STATUS_DEV)) {
            // Remove module version
            moduleVersion.remove();
            Versions versions = module.getVersions();
            versions.remove(versions.getItem(versionCode));

            if (versions.isEmpty()) {
              // Remove module entity
              module.remove();
              index(INDEX_MODE_MY_MODULES,1);
            } else {
              module.setVersions(versions);
              module.save();
              module(module.getName(), versions.getLastVersion().getVersionCode());
            }
          } else {
            forbidden(Messages.get("Modules.form.err.remove", moduleVersion.getStatus()));
          }
        }
      }

      moduleNotFoundError("We can't found the module with versionCode: " + versionCode);
    } else {
      moduleNotFoundError("Impossible to remove it because we can't find the id: ", id);
    }
  }

  public static void export(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);

    if (module != null) {
      ModuleVersion moduleVersion;
      if (versionCode != null) {
        moduleVersion = module.getModuleVersion(versionCode);
        if (moduleVersion != null) {
          // Generate JSON
          String fileName = module.getName() + ".json";
          renderBinary(moduleVersion.exportAsJson(module), fileName);
        }
      }

      moduleNotFoundError("We can't found the module with versionCode: " + versionCode);
    } else {
      moduleNotFoundError("Impossible to export it because we can't find the id: ", id);
    }
  }

  public static void vote(@Required(message = "validation.required.module.rate")
                          @Min(value = 0, message = "validation.minValue.rating.rate")
                          @Max(value = 5, message = "validation.maxValue.rating.rate") Integer rate,
                          String msg,
                          @Required(message = "validation.required.module.id") String module_name)
      throws SinfonierException {

    checkAuthenticity();
    if (!Validation.hasErrors()) {
      Module module = Module.findByName(module_name);

      if (module != null) {
        Rating rating = new Rating(getCurrentUser(), rate, msg);
        module.addRate(rating);
        module(module_name, null);
      } else {
        moduleNotFoundError("Impossible to rate it because we can't found the name: ", module_name);
      }
    } else {
      Logger.error("Missing some params: name:" + module_name + ", rate:" + rate + ", msg:" + msg);
      params.flash();
      module(module_name, null);
    }
  }

  public static void addToMyTools(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);

    if (module != null && !module.isOwner(getCurrentUser()) && !module.getStatus().equals(STATUS_PREDEFINED)) {
      ModuleVersion version = module.getModuleVersion(versionCode);
      if (version != null) {
        // Add module version to my tools
        MyTool tool = new MyTool(getCurrentUser());
        version.addToMyTools(tool);
        index(null,1);
      } else {
        moduleNotFoundError("Impossible to add to my tools because we can't found the versionCode: ",
            versionCode.toString());
      }
    } else {
      moduleNotFoundError("Impossible to add to my tools because we can't found the id: ", id);
    }
  }

  public static void removeToMyTools(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);

    if (module != null && !module.isOwner(getCurrentUser())) {
      ModuleVersion version = module.getModuleVersion(versionCode);
      if (version != null) {
        // Remove module version to my tools
        MyTool tool = new MyTool(getCurrentUser());
        version.removeToMyTools(tool);
        index(null, 1);
      } else {
        moduleNotFoundError("Impossible to remove to my tools because we can't found the versionCode: ",
            versionCode.toString());
      }
    } else {
      moduleNotFoundError("Impossible to remove to my tools because we can't found the id: ", id);
    }
  }

  public static void complain(@Required(message = "validation.required.complain.msg") String msg,
                              @Required(message = "validation.required.module.id") String module_name)
      throws SinfonierException {
    checkAuthenticity();
    if (!Validation.hasErrors()) {
      Module module = Module.findByName(module_name);

      if (module != null && !module.isOwner(getCurrentUser()) && !module.getStatus().equals(STATUS_PREDEFINED)) {
        Inappropriate complain = new Inappropriate(getCurrentUser(), msg);
        module.addComplain(complain);

        if (module.getComplains().getComplains().size() > LIMIT_COMPLAINS_NOTIFY_ADMIN) {
          SinfonierMailer.notifyComplainModuleAdmin(module, complain);
        }
        SinfonierMailer.complainModule(module, complain);
        module(module_name, null);
      } else {
        moduleNotFoundError("Impossible to complain it because we can't found the name: ", module_name);
      }
    } else {
      Logger.error("Missing some params: name:" + module_name + ", msg:" + msg);
      params.flash();
      module(module_name, null);
    }
  }

  public static void recheck(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);

    if (module != null) {
      ModuleVersion moduleVersion;
      if (versionCode != null) {
        moduleVersion = module.getModuleVersion(versionCode);
        if (moduleVersion != null) {
          // Check waiting validations
          long versions = ModuleVersion.countByUserAndStatus(getCurrentUser(), STATUS_PENDING);

          if (versions < LIMIT_PENDING_MODULE_VERSIONS) {
            JsonObject res = client.uploadModule(module.getId(), moduleVersion.getId());
            String log = null;

            if (res.get("code") != null && res.get("code").getAsInt() == 200) {
              // Recheck module version
              moduleVersion.recheck();
              module.save();
              SinfonierMailer.reviewModule(module, moduleVersion);
            } else {
              log = Messages.get("Modules.recheck.error");
              flash.put(FLASH_KEY_BACKEND_LOG, log);              
            }

            module(module.getName(), moduleVersion.getVersionCode());
          } else {
            throw new SinfonierException(SinfonierError.MODULE_LIMIT_PENDING);
          }
        }
      }

      moduleNotFoundError("We can't found the module with versionCode: " + versionCode);
    } else {
      moduleNotFoundError("Impossible set it to recheck because we can't found the id: ", id);
    }
  }

  public static void validate(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);

    if (module != null) {
      ModuleVersion moduleVersion;
      if (versionCode != null) {
        moduleVersion = module.getModuleVersion(versionCode);
        if (moduleVersion != null) {
          JsonObject res = client.validateModule(module.getId(), moduleVersion.getId());
          String log = null;

          if (res.get("code") != null && res.get("code").getAsInt() == 200) {
            // Validate module version
            moduleVersion.setStatus(STATUS_PRIVATE);
            moduleVersion.save();

            // Save new version with tag
            Version v = module.getVersions().getItem(moduleVersion.getVersionCode());
            v.setVersionTag(moduleVersion.getVersionTag());
            v.setIsVisible(false);
            module.save();
          } else {
            log = Messages.get("Modules.validate.error");
            flash.put(FLASH_KEY_BACKEND_LOG, log);
          }

          module(module.getName(), versionCode);
        }
      }

      moduleNotFoundError("We can't found the module with versionCode: " + versionCode);
    } else {
      moduleNotFoundError("Impossible set it to recheck because we can't found the id: ", id);
    }
  }

  public static void publish(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);

    if (module != null) {
      ModuleVersion moduleVersion;
      if (versionCode != null) {
        moduleVersion = module.getModuleVersion(versionCode);
        if (moduleVersion != null) {
          // Change status of version
          moduleVersion.setStatus(STATUS_PUBLISHED);
          moduleVersion.save();

          Version v = module.getVersions().getItem(versionCode);
          v.setIsVisible(true);
          module.save();
          module(module.getName(), versionCode);
        }
      }

      moduleNotFoundError("We can't found the module with versionCode: " + versionCode);
    } else {
      moduleNotFoundError("Impossible set it to published because we can't found the id: ", id);
    }
  }

  public static void privatize(String id, Integer versionCode) throws SinfonierException {
    checkAuthenticity();
    Module module = Module.findById(id);

    if (module != null) {
      ModuleVersion moduleVersion;
      if (versionCode != null) {
        moduleVersion = module.getModuleVersion(versionCode);
        if (moduleVersion != null) {
          // Change status of version
          moduleVersion.setStatus(STATUS_PRIVATE);
          moduleVersion.save();

          Version v = module.getVersions().getItem(versionCode);
          v.setIsVisible(false);
          module.save();
          module(module.getName(), versionCode);
        }
      }

      moduleNotFoundError("We can't found the module with versionCode: " + versionCode);
    } else {
      moduleNotFoundError("Impossible set it to privatize because we can't found the id: ", id);
    }
  }

  @Check("ADMIN")
  public static void loadPredefined() throws SinfonierException {
    Module.loadPredefinedModules();
    index(null,1);
  }

  private static void moduleNotFoundError(String message, String id) {
    moduleNotFoundError(message + id);
  }

  private static void moduleNotFoundError(String message) {
    Logger.error(message);
    notFound();
  }

  @Util
  @Catch(value = SinfonierException.class, priority = 1)
  public static void catchSinfonierExceptions(SinfonierException e) {
    Logger.error(e, e.getMessage());
    SinfonierError error = e.getError();
    Object[] args = e.getArgs();
    render("errors/error.html", error, args);
  }
}
