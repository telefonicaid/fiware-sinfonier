package models.module;

import static models.SinfonierConstants.ModelCollection.FIELD_ID;
import static models.SinfonierConstants.Module.COLLECTION_NAME;
import static models.SinfonierConstants.Module.FIELD_AUTHOR_ID;
import static models.SinfonierConstants.Module.FIELD_AVERAGE_RATE;
import static models.SinfonierConstants.Module.FIELD_CATEGORY;
import static models.SinfonierConstants.Module.FIELD_COMPLAINS;
import static models.SinfonierConstants.Module.FIELD_CREATED;
import static models.SinfonierConstants.Module.FIELD_ICON;
import static models.SinfonierConstants.Module.FIELD_LANGUAGE;
import static models.SinfonierConstants.Module.FIELD_NAME;
import static models.SinfonierConstants.Module.FIELD_RATINGS;
import static models.SinfonierConstants.Module.FIELD_STATUS;
import static models.SinfonierConstants.Module.FIELD_TOPOLOGIES_COUNT;
import static models.SinfonierConstants.Module.FIELD_TYPE;
import static models.SinfonierConstants.Module.FIELD_UPDATED;
import static models.SinfonierConstants.Module.FIELD_VERSIONS;
import static models.SinfonierConstants.Module.LANG_JAVA;
import static models.SinfonierConstants.Module.LIMIT_TOP_MODULES;
import static models.SinfonierConstants.Module.MAX_PARALLELISM;
import static models.SinfonierConstants.Module.MAX_WIRES;
import static models.SinfonierConstants.Module.PATH_TO_SAVE_ICONS;
import static models.SinfonierConstants.Module.STATUS_DELETED;
import static models.SinfonierConstants.Module.STATUS_DEV;
import static models.SinfonierConstants.Module.STATUS_PENDING;
import static models.SinfonierConstants.Module.STATUS_PRIVATE;
import static models.SinfonierConstants.Module.STATUS_PUBLISHED;
import static models.SinfonierConstants.Module.STATUS_PREDEFINED;
import static models.SinfonierConstants.Module.TYPE_SPOUT;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import models.Constants;
import models.ModelCollection;
import models.factory.DarwinFactory;
import models.factory.MongoFactory;
import models.module.validations.NameCheck;
import models.user.Inappropriate;
import models.user.Rating;
import models.user.User;
import models.user.UsersContainer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.WordUtils;
import org.bson.types.ObjectId;

import play.Logger;
import play.Play;
import play.data.validation.CheckWith;
import play.data.validation.Required;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

import exceptions.SinfonierError;
import exceptions.SinfonierException;

import utils.Utils;

@SuppressWarnings("deprecation")
public class Module extends ModelCollection {

  private static final String[] PREDEFINED_MODULES = {"Filter", "FlatJson", "Rename", "Trim"};
  private static final String PREDEFINED_DOCUMENTS_PATH = "db/documents/predefinedModules";
  protected static String collectionName = COLLECTION_NAME;

  private String status;
  
  @CheckWith(NameCheck.class)
  private String name;

  @Required(message = "validation.required.module.type")
  private String type;
  private String category;

  @Required(message = "validation.required.module.language")
  private String language;
  private File icon;
  
  private Versions versions;

  private Date createdAt;
  private Date updatedAt;
  private User author;
  private String authorId;
  private Ratings ratings;
  private double averageRate;
  private int topologiesCount;
  private Complains complains;

  public Module(DBObject o) throws SinfonierException {
    try {
      this.id = o.get(FIELD_ID).toString();
      this.status = o.get(FIELD_STATUS).toString();
      this.name = o.get(FIELD_NAME).toString();
      this.type = (o.get(FIELD_TYPE) != null ? o.get(FIELD_TYPE).toString() : Module
          .getTypeFromCategory(o.get(FIELD_CATEGORY).toString()));
      this.category = (o.get(FIELD_CATEGORY) != null ? o.get(FIELD_CATEGORY).toString()
          : Module.getCategoryFromType(this.type));
      this.language = o.get(FIELD_LANGUAGE).toString();
      this.createdAt = (Date) o.get(FIELD_CREATED);
      this.updatedAt = (Date) o.get(FIELD_UPDATED);
      this.authorId = o.get(FIELD_AUTHOR_ID).toString();
      this.averageRate = o.get(FIELD_AVERAGE_RATE) != null ? new Double(o.get(FIELD_AVERAGE_RATE).toString()) : 0;
      this.topologiesCount = o.get(FIELD_TOPOLOGIES_COUNT) != null ? (Integer) o.get(FIELD_TOPOLOGIES_COUNT) : 0;
      
      // Create recursive object
      ratings = new Ratings(((BasicDBList) o.get(FIELD_RATINGS)));
      complains = new Complains(((BasicDBList) o.get(FIELD_COMPLAINS)));
      versions = new Versions((BasicDBList) o.get(FIELD_VERSIONS));
  
      if (o.get(FIELD_ICON) != null && o.get(FIELD_ICON).toString().length() > 0) {
        icon = FileUtils.getFile(o.get(FIELD_ICON).toString());
      }
    } catch (Exception e) {
      Logger.error("Exception constructor Module(DBObject) > " + e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }
  }

  public Module() {
    this.setType(TYPE_SPOUT);
    this.setCategory(Module.getCategoryFromType(this.getType()));
    this.setLanguage(LANG_JAVA);
    this.setStatus(STATUS_DEV);
    Versions versions = new Versions();
    this.setVersions(versions);
  }

  public static ModulesContainer getModules(User user, Integer page) throws SinfonierException {
    return getModules(user, true, page);
  }

  public static ModulesContainer getModules(User user, boolean includeExternalSharing, Integer page) throws SinfonierException {
    ModulesContainer modules;

    BasicDBList notDeletedAndAuthor = new BasicDBList();
    notDeletedAndAuthor.add(new BasicDBObject(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED)));
    notDeletedAndAuthor.add(new BasicDBObject(FIELD_AUTHOR_ID, user.getId()));

    DBObject sortByName = new BasicDBObject(FIELD_NAME, 1);

    if (user.isAdminUser() && includeExternalSharing) {
      modules = find(page);
    } else if (includeExternalSharing) {
      BasicDBList list = new BasicDBList();
      list.add(new BasicDBObject(FIELD_STATUS, new BasicDBObject("$in", new ArrayList(Arrays.asList(STATUS_PUBLISHED,STATUS_PREDEFINED)))));
      list.add(new BasicDBObject("$and", notDeletedAndAuthor));
      modules = find(new BasicDBObject("$or", list), sortByName, page);
    } else {
      modules = find(new BasicDBObject("$and", notDeletedAndAuthor), sortByName, page);
    }

    return modules;
  }
  
  public static ModulesContainer getModules(User user, String status, Integer page) throws SinfonierException {
    ModulesContainer modules;
    
    BasicDBList statusAndAuthor = new BasicDBList();
    statusAndAuthor.add(new BasicDBObject(FIELD_STATUS, status));
    statusAndAuthor.add(new BasicDBObject(FIELD_AUTHOR_ID, user.getId()));
    
    modules = find(new BasicDBObject("$and", statusAndAuthor), page);
    return modules;
  }
  
  public ModuleVersion lastModuleVersion() throws SinfonierException {
    int i;
    Version v = null;
    List<Version> arrayVersions = this.versions.getVersions();
    
    // Search last tag version
    for (i=arrayVersions.size()-1; i >= 0 && v == null ; i--) {
      if (arrayVersions.get(i).getVersionTag() != null) {
        v = arrayVersions.get(i);
      }
    }
    
    // If not last deploying version
    if (v == null) {
      v = arrayVersions.get(arrayVersions.size()-1);
    }
    
    return v.getModuleVersion();
  }
  
  public ModuleVersion getModuleVersion(int versionCode) throws SinfonierException {
    Version v = this.versions.getItem(versionCode);
    if (v != null) {
      return v.getModuleVersion();
    } else {
      return null;
    }
  }
  
  public int getNextVersionCode() {
    List<Version> arrayVersions = this.versions.getVersions();
    Version v = arrayVersions.get(arrayVersions.size()-1);
    
    return v.getVersionCode()+1;
  }

  public static long countByUser(User user) {
    DBObject query = new BasicDBObject();

    query.put(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED));
    query.put(FIELD_AUTHOR_ID, user.getId());

    return count(query);
  }

  public static Module findByName(String name) throws SinfonierException {
    DBObject query = new BasicDBObject(FIELD_NAME, name);
    return findOne(query);
  }

  public static Module findById(ObjectId id) throws SinfonierException {
    DBObject query = new BasicDBObject(FIELD_ID, id);
    return findOne(query);
  }

  public static Module findById(String id) throws SinfonierException {
    if (ObjectId.isValid(id)) {
      DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(id));
      return findOne(query);
    } else {
      return null;
    }
  }

  public static ModulesContainer find(ModuleSearch search, User user, Integer page) throws SinfonierException {
    BasicDBList query = new BasicDBList();

    query.add(new BasicDBObject(FIELD_STATUS, Pattern.compile(".*", Pattern.DOTALL)));
    if (!user.isAdminUser()) {
      BasicDBList notDeletedAndAuthor = new BasicDBList();
      notDeletedAndAuthor.add(new BasicDBObject(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED)));
      notDeletedAndAuthor.add(new BasicDBObject(FIELD_AUTHOR_ID, user.getId()));
      
      BasicDBList list = new BasicDBList();
      list.add(new BasicDBObject(FIELD_STATUS, new BasicDBObject("$in", new ArrayList(Arrays.asList(STATUS_PUBLISHED,STATUS_PREDEFINED)))));
      list.add(new BasicDBObject("$and", notDeletedAndAuthor));
      query.add(new BasicDBObject("$or", list));
    }

    if (search.getName() != null && search.getName().length() > 0) {
      query.add(new BasicDBObject(FIELD_NAME, Pattern.compile(search.getName(), Pattern.CASE_INSENSITIVE)));
    }

    if (search.getOwner() != null && search.getOwner().length() > 0) {
      BasicDBList list = new BasicDBList();
      list.add(new BasicDBObject(FIELD_AUTHOR_ID, Pattern.compile(search.getOwner(), Pattern.CASE_INSENSITIVE)));
      list.add(new BasicDBObject(FIELD_AUTHOR_ID, new BasicDBObject("$in", Utils.getUsersEmailsByName(search.getOwner()))));
      query.add(new BasicDBObject("$or", list));
    }

    if (search.getType() != null && search.getType().length() > 0) {
      query.add(new BasicDBObject(FIELD_TYPE, search.getType()));
    }

    return find(new BasicDBObject("$and", query), page);
  }
  
  public static ModulesContainer getTopModules() throws SinfonierException {
    DBObject query = new BasicDBObject();
    DBObject sortBy = new BasicDBObject();
    query.put(FIELD_STATUS, new BasicDBObject("$in", new ArrayList(Arrays.asList(STATUS_PUBLISHED,STATUS_PREDEFINED))));

    sortBy.put(FIELD_AVERAGE_RATE, -1);
    sortBy.put(FIELD_NAME, 1);
    
    ModulesContainer modules = find(query, sortBy, LIMIT_TOP_MODULES, null); 
    modules.setCountBeforeLimit(LIMIT_TOP_MODULES);
    return modules;
  }

  public static ModulesContainer getTopUsed() throws SinfonierException {
    DBObject query = new BasicDBObject();
    DBObject sortBy = new BasicDBObject();

    query.put(FIELD_STATUS, new BasicDBObject("$in", new ArrayList(Arrays.asList(STATUS_PUBLISHED,STATUS_PREDEFINED))));
    sortBy.put(FIELD_TOPOLOGIES_COUNT, -1);
    sortBy.put(FIELD_NAME, 1);

    ModulesContainer modules = find(query, sortBy, LIMIT_TOP_MODULES, null); 
    modules.setCountBeforeLimit(LIMIT_TOP_MODULES);
    return modules;
  }

  public static Integer getMaxWires() {
    return MAX_WIRES;
  }

  public static Integer getMaxParallelism() {
    return MAX_PARALLELISM;
  }
  
  private static Module findOne(DBObject query) throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    DBObject module = collection.findOne(query);

    if (module != null) {
      return new Module(module);
    }

    return null;
  }

  private static ModulesContainer find(DBObject query, DBObject orderBy, Integer limit, Integer page) throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    List<Module> list = new ArrayList<Module>();
    DBCursor cursor;
    
    if (limit != null) {
      page = (page != null && page > 0) ? ((page-1)*limit) : 0;
    } else {
      page = null;
    }

    if (query == null) {
      cursor = collection.find();
    } else {
      cursor = collection.find(query);
    }

    int totalModules = cursor.count();
    
    if (orderBy != null) {
      cursor = cursor.sort(orderBy);
    }
    
    if (page != null && page > 0) {
      cursor.skip(page);
    }
    
    if (limit != null) {
      cursor = cursor.limit(limit);
    }

    for (DBObject dbObject : cursor) {
      list.add(new Module(dbObject));
    }

    return new ModulesContainer(list, totalModules);
  }
  
  private static ModulesContainer find(DBObject query, Integer page) throws SinfonierException {
    if (page != null)
      return find(query, null, Constants.Utils.MAX_RESULTS_PAGE, page);
    else
      return find(query, null, null, page);
  }

  private static ModulesContainer find(DBObject query, DBObject sortBy, Integer page) throws SinfonierException {
    if (page != null)
      return find(query, sortBy, Constants.Utils.MAX_RESULTS_PAGE, page);
    else
      return find(query, sortBy, null, page);
  }

  private static ModulesContainer find(Integer page) throws SinfonierException {
    return find(null, page);
  }

  private static long count(DBObject query) {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    return collection.count(query);
  }

  private void updateStatusGlobal() throws SinfonierException {
    int i;
    String status = null;
    List<Version> listVersions = this.getVersions().getVersions();
    
    for (i=0; i < listVersions.size(); i++) {
      String versionStatus = listVersions.get(i).getModuleVersion(true).getStatus();
      if (versionStatus.equals(STATUS_PUBLISHED) || versionStatus.equals(STATUS_PRIVATE)) {
        status = versionStatus;
      } else if (versionStatus.equals(STATUS_PENDING) && (status == null || !status.equals(STATUS_PUBLISHED))) {
        status = versionStatus;
      } else if (versionStatus.equals(STATUS_DELETED) && (status == null || status.equals(STATUS_DELETED))) {
        status = versionStatus;
      } else if (versionStatus.equals(STATUS_DEV) && status == null) {
        status = versionStatus;
      } else if (versionStatus.equals(STATUS_PREDEFINED) && status == null) {
        status = versionStatus;
      }
    }
    
    this.status = status;
  }

  public void save() throws SinfonierException {
    try {
      DBCollection collection = MongoFactory.getDB().getCollection(collectionName);

      if (icon != null) {
        File dir = FileUtils.getFile(PATH_TO_SAVE_ICONS);

        if (!dir.exists()) {
          dir.mkdirs();
        }

        String ext = FilenameUtils.getExtension(this.icon.getName());
        File icon = FileUtils.getFile(dir, getName() + (ext.length() > 0 ? "." + ext : ""));

        try {
          FileUtils.moveFile(this.icon, icon);
        } catch (IOException e) {
          Logger.error(e.getMessage());
        } finally {
          setIcon(icon);
        }
      }
      
      // Calculate status global
      updateStatusGlobal();

      if (this.id == null) {
        Logger.info("Adding a new module");
        DBObject dbObj = this.toDBObject();
        collection.save(dbObj);
        this.id = dbObj.get(FIELD_ID).toString();
      } else {
        Logger.info("Editing module id:" + this.getId().toString());
        DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(this.id));
        DBObject toSet = this.toDBObject();

        // Remove fields's unmodified
        toSet.removeField(FIELD_ID);
        toSet.removeField(FIELD_CREATED);
        toSet.removeField(FIELD_AUTHOR_ID);
        toSet.removeField(FIELD_RATINGS);
        toSet.removeField(FIELD_AVERAGE_RATE);
        toSet.removeField(FIELD_COMPLAINS);
        toSet.removeField(FIELD_TOPOLOGIES_COUNT);

        if (icon == null) {
          toSet.removeField(FIELD_ICON);
        }

        // Update field's update
        toSet.put(FIELD_UPDATED, new Date());

        collection.update(query, new BasicDBObject("$set", toSet), true, false);
      }
    } catch (MongoException.DuplicateKey e) {
      Logger.error(e, e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_DUPLICATE, e, this.name);
    }
  }
  
  public void remove() {
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    collection.findAndRemove(query);
  }
  
  public void updateTopologiesCount() {
    DBObject toSet = new BasicDBObject(FIELD_TOPOLOGIES_COUNT, getTopologiesCount());
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    collection.update(query, new BasicDBObject("$set", toSet));
  }
  
  public boolean hasWritePermission(User user) {
    if ((this.isOwner(user) || user.isAdminUser()) && !this.getStatus().equals(STATUS_PREDEFINED)) {
      return true; // Owner or Admin
    } else {
      return false;
    }
  }
  
  public boolean hasReadPermission(ModuleVersion version, User user) {
    if (this.isOwner(user) || user.isAdminUser()) {
      return true; // Owner or Admin
    } else if (!version.getStatus().equals(STATUS_PUBLISHED) && !version.getStatus().equals(STATUS_PREDEFINED)) {
      return false; // Version not published
    } else {
      return true;
    }
  }
  
  public void addRate(Rating rating) {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));

    ratings.add(rating);

    // Update module's average
    double average = 0;
    for (Rating r : ratings) {
      average += r.getRate();
    }
    average = average / ratings.size();

    DBObject toSet = new BasicDBObject();
    toSet.put(FIELD_RATINGS, ratings.toDBObject());
    toSet.put(FIELD_AVERAGE_RATE, ((double) average));

    collection.update(query, new BasicDBObject("$set", toSet), true, false);
  }
  
  public void addComplain(Inappropriate inappropriate) {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));

    complains.add(inappropriate);

    DBObject toSet = new BasicDBObject();
    toSet.put(FIELD_COMPLAINS, complains.toDBObject());
    collection.update(query, new BasicDBObject("$set", toSet), true, false);
  }

  public boolean isOwner(User user) {
    if (user == null) {
      return false;
    } else {
      return authorId.equals(user.getId());
    }
  }

  public DBObject toDBObject() {
    DBObject obj = new BasicDBObject();
    obj.put(FIELD_ID, this.id == null ? null : new ObjectId(this.id));
    obj.put(FIELD_NAME, this.name);
    obj.put(FIELD_TYPE, this.type);
    obj.put(FIELD_CATEGORY, Module.getCategoryFromType(this.getType()));
    obj.put(FIELD_STATUS, this.status);
    obj.put(FIELD_LANGUAGE, this.language);
    obj.put(FIELD_CREATED, this.createdAt);
    obj.put(FIELD_UPDATED, this.updatedAt);
    obj.put(FIELD_AUTHOR_ID, this.authorId);
    obj.put(FIELD_AVERAGE_RATE, this.getAverageRate());
    obj.put(FIELD_TOPOLOGIES_COUNT, this.getTopologiesCount());

    // Icon
    if (icon != null) {
      obj.put(FIELD_ICON, "/" + icon.getPath());
    } else {
      obj.put(FIELD_ICON, "");
    }

    if (ratings != null) {
      obj.put(FIELD_RATINGS, ratings.toDBObject());
    }

    if (complains != null) {
      obj.put(FIELD_COMPLAINS, complains.toDBObject());
    }
    
    if (versions != null) {
      obj.put(FIELD_VERSIONS, versions.toDBObject());
    }

    return obj;
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public File getIcon() {
    return icon;
  }

  public void setIcon(File icon) {
    this.icon = icon;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
  
  public User getAuthor() {
    if (this.author == null && this.authorId != null) {
      this.author = DarwinFactory.getInstance().loadUser(this.authorId);
    }
    
    return this.author;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public double getAverageRate() {
    return averageRate;
  }

  public void setAverageRate(double averageRate) {
    this.averageRate = averageRate;
  }

  public int getTopologiesCount() {
    return topologiesCount;
  }

  public void setTopologiesCount(int topologiesCount) {
    this.topologiesCount = topologiesCount;
  }

  public Complains getComplains() {
    return complains;
  }

  public void setComplains(Complains complains) {
    if (complains == null) {
      this.complains = new Complains();
    } else {
      this.complains = complains;
    }
  }

  public Ratings getRatings() {
    return ratings;
  }

  public void setRatings(Ratings ratings) {
    if (ratings == null) {
      this.ratings = new Ratings();
    } else {
      this.ratings = ratings;
    }
  }

  public Versions getVersions() {
    return versions;
  }

  public void setVersions(Versions versions) {
    this.versions = versions;
  }
  
  public List<Version> getVisibleVersionsToUser(User user) {
    List<Version> visibleVersions = new ArrayList<Version>();
    for (Version version : this.getVersions().getVersions()) {
      if ((this.isOwner(user) && !version.getIsDeleted()) 
        || (version.getVersionTag() != null && version.getIsVisible() && !version.getIsDeleted()) 
        || user.isAdminUser()) {
        visibleVersions.add(version);
      }
    }
    return visibleVersions;
  }
  
  public static String getCategoryFromType(String type) {
    return WordUtils.capitalize(type) + "s";
  }

  /*
   * Only for modules imported from node version
   */
  public static String getTypeFromCategory(String category) {
    return WordUtils.uncapitalize(category).substring(0, category.length() - 1);
  }
  
  public static void loadPredefinedModules() throws SinfonierException {

    Logger.debug("Loading predefined modules");
    String basePath = Play.applicationPath.getAbsolutePath();
    DBCollection modulesCollection = MongoFactory.getDB().getCollection(collectionName);
    DBCollection moduleVersionsCollection = MongoFactory.getDB().getCollection(models.SinfonierConstants.ModuleVersion.COLLECTION_NAME);
    try {
      for (String moduleName : PREDEFINED_MODULES) {
        if (Module.findByName(moduleName) == null) {
          String jsonModuleFilePath = basePath + "/" + PREDEFINED_DOCUMENTS_PATH + "/" + moduleName + ".json";
          String jsonModuleVersionFilePath = basePath + "/" + PREDEFINED_DOCUMENTS_PATH + "/" + moduleName + "Version.json";
          File jsonModule = new File(jsonModuleFilePath);
          DBObject dbModule = null;
          DBObject dbModuleVersion = null;
          if (jsonModule.exists()) {
            dbModule = (DBObject) JSON.parse(FileUtils.readFileToString(new File(jsonModuleFilePath)));
            modulesCollection.insert(dbModule);
          }
          File jsonModuleVersion = new File(jsonModuleVersionFilePath);
          if (jsonModuleVersion.exists()) {
            dbModuleVersion = (DBObject) JSON.parse(FileUtils.readFileToString(new File(jsonModuleVersionFilePath)));
            moduleVersionsCollection.insert(dbModuleVersion);
          }
          if (dbModule != null && dbModuleVersion != null) {
            Module module = Module.findById(dbModule.get(FIELD_ID).toString());
            ModuleVersion moduleVersion = ModuleVersion.findById(dbModuleVersion.get(FIELD_ID).toString());
            module.setVersions(new Versions());
            // Save version reference
            Version v = new Version(moduleVersion);
            module.getVersions().add(v);
            module.save();
            Logger.debug("Predefined module " + moduleName + " correctly loaded");
          }
        } else {
          Logger.debug("Predefined module " + moduleName + " previously loaded");
        }
      }
    } catch (FileNotFoundException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_FILE_PATH, e);
    } catch (IOException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_FILE_PATH, e);
    }
  }
}
