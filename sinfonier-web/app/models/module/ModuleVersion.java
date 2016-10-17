package models.module;

import static models.SinfonierConstants.ModuleVersion.*;
import static models.SinfonierConstants.ModelCollection.FIELD_ID;
import static models.SinfonierConstants.Module.FIELD_ICON;
import static models.SinfonierConstants.Module.FIELD_AUTHOR_ID;
import static models.SinfonierConstants.Module.FIELD_AVERAGE_RATE;
import static models.SinfonierConstants.Module.FIELD_CATEGORY;
import static models.SinfonierConstants.Module.FIELD_VERSIONS;
import static models.SinfonierConstants.Module.FIELD_COMPLAINS;
import static models.SinfonierConstants.Module.FIELD_RATINGS;
import static models.SinfonierConstants.Module.FIELD_TOPOLOGIES_COUNT;
import static models.SinfonierConstants.Module.PATH_TO_SAVE;
import static models.SinfonierConstants.Module.STATUS_DEV;
import static models.SinfonierConstants.Module.STATUS_DELETED;
import static models.SinfonierConstants.Module.STATUS_PENDING;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.ModelCollection;
import models.factory.MongoFactory;
import models.module.validations.CodeSourceCheck;
import models.module.validations.CodeSourceUrlCheck;
import models.user.MyTool;
import models.user.User;
import models.utils.Mongo2gson;
import play.Logger;
import play.data.binding.NoBinding;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.data.validation.Valid;

public class ModuleVersion extends ModelCollection {

  protected static String collectionName = COLLECTION_NAME;

  private String versionTag;
  private int versionCode;
  private String status;

  @Required(message = "validation.required.module.sourceType")
  private String sourceType;

  @CheckWith(CodeSourceCheck.class)
  private String sourceCode;

  @CheckWith(CodeSourceUrlCheck.class)
  private String sourceCodeURL;

  @Valid
  private Fields fields;

  @Valid
  private Libraries libraries;

  private String description;
  private TickTuple tickTuple;
  private Date createdAt;
  private Date updatedAt;
  private Boolean singleton;
  private int topologiesCount;
  private MyTools myTools;
  private String buildStatus;

  @NoBinding
  private Container container;

  public ModuleVersion() {
    this.setTickTuple(null);
    this.setSingleton(false);
    this.setSourceType(SOURCE_TYPE_GIST);
    this.setStatus(STATUS_DEV);
  }

  public ModuleVersion(DBObject o) throws SinfonierException {
    try {
      id = o.get(FIELD_ID).toString();
      versionTag = (String) o.get(FIELD_VERSION_TAG);
      versionCode = new Integer(o.get(FIELD_VERSION_CODE).toString());
      status = ((String) o.get(FIELD_STATUS));
      sourceType = ((String) o.get(FIELD_SOURCE_TYPE));
      sourceCode = ((String) o.get(FIELD_SOURCE_CODE));
      sourceCodeURL = ((String) o.get(FIELD_SOURCE_CODE_URL));
      description = ((String) o.get(FIELD_DESCRIPTION));
      createdAt = (Date) o.get(FIELD_CREATED);
      updatedAt = (Date) o.get(FIELD_UPDATED);
      singleton = o.get(FIELD_SINGLETON) != null ? (Boolean) o.get(FIELD_SINGLETON) : false;
      topologiesCount = o.get(FIELD_TOPOLOGIES_COUNT) != null ? (Integer) o.get(FIELD_TOPOLOGIES_COUNT) : 0;
      buildStatus = ((String) o.get(FIELD_BUILD_STATUS));

      // Create recursive object
      if (o.get(FIELD_TICK_TUPLE) != null) {
        tickTuple = new TickTuple((DBObject) o.get(FIELD_TICK_TUPLE));
      }
      fields = new Fields(((BasicDBList) o.get(FIELD_FIELDS)));
      libraries = new Libraries(((BasicDBList) o.get(FIELD_LIBRARIES)));
      myTools = new MyTools(((BasicDBList) o.get(FIELD_MY_TOOLS)));
      container = new Container(((DBObject) o.get(FIELD_CONTAINER)), this);
    } catch (Exception e) {
      Logger.error("Exception constructor ModuleVersion(DBObject) > " + e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }
  }

  public DBObject toDBObject() {
    DBObject obj = new BasicDBObject();
    obj.put(FIELD_ID, this.id == null ? null : new ObjectId(this.id));
    obj.put(FIELD_VERSION_TAG, this.versionTag);
    obj.put(FIELD_VERSION_CODE, this.versionCode);
    obj.put(FIELD_STATUS, this.status);
    obj.put(FIELD_SOURCE_TYPE, this.sourceType);
    obj.put(FIELD_SOURCE_CODE, this.sourceCode);
    obj.put(FIELD_SOURCE_CODE_URL, this.sourceCodeURL);
    obj.put(FIELD_DESCRIPTION, this.description);
    obj.put(FIELD_CREATED, this.createdAt);
    obj.put(FIELD_UPDATED, this.updatedAt);
    obj.put(FIELD_SINGLETON, this.singleton);
    obj.put(FIELD_TOPOLOGIES_COUNT, this.getTopologiesCount());

    // TickTuple
    if (tickTuple == null || tickTuple.getLabel() == null) {
      obj.put(FIELD_TICK_TUPLE, null);
    } else {
      obj.put(FIELD_TICK_TUPLE, this.tickTuple.toDBObject());
    }

    if (fields != null) {
      obj.put(FIELD_FIELDS, fields.toDBObject());
    }

    if (libraries != null) {
      obj.put(FIELD_LIBRARIES, libraries.toDBObject());
    }

    if (myTools != null) {
      obj.put(FIELD_MY_TOOLS, myTools.toDBObject());
    }

    if (container != null) {
      obj.put(FIELD_CONTAINER, container.toDBObject());
    }

    if (buildStatus != null) {
      obj.put(FIELD_BUILD_STATUS, buildStatus);
    }

    return obj;
  }

  public static long countByUserAndStatus(User user, String status) throws SinfonierException {
    List<ObjectId> versionIds = new ArrayList<ObjectId>();
    ModulesContainer myModules = Module.getModules(user, false, null);
    for (int i = 0; i < myModules.getModules().size(); i++) {
      List<Version> versions = myModules.getModules().get(i).getVersions().getVersions();
      for (int j = 0; j < versions.size(); j++) {
        versionIds.add(new ObjectId(versions.get(j).getModuleVersionId()));
      }
    }

    return ModuleVersion.countByStatus(versionIds, status);
  }

  private static long countByStatus(List<ObjectId> versionIds, String status) throws SinfonierException {
    long modules;

    BasicDBList list = new BasicDBList();
    list.add(new BasicDBObject(FIELD_STATUS, status));
    list.add(new BasicDBObject(FIELD_ID, new BasicDBObject("$in", versionIds)));
    modules = count(new BasicDBObject("$and", list));

    return modules;
  }

  private static long count(DBObject query) {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    return collection.count(query);
  }

  public static ModuleVersion findById(ObjectId id) throws SinfonierException {
    DBObject query = new BasicDBObject(FIELD_ID, id);
    return findOne(query);
  }

  public static ModuleVersion findById(String id) throws SinfonierException {
    if (ObjectId.isValid(id)) {
      DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(id));
      return findOne(query);
    } else {
      return null;
    }
  }

  private static ModuleVersion findOne(DBObject query) throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    DBObject module = collection.findOne(query);

    if (module != null) {
      return new ModuleVersion(module);
    }

    return null;
  }

  public void save() throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);

    if (this.id == null) {
      Logger.info("Adding a new module version");
      DBObject dbObj = this.toDBObject();
      collection.save(dbObj);
      this.id = dbObj.get(FIELD_ID).toString();
    } else {
      Logger.info("Editing module version id:" + this.getId().toString());
      DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(this.id));
      DBObject toSet = this.toDBObject();

      // Remove fields's unmodified
      toSet.removeField(FIELD_ID);
      toSet.removeField(FIELD_CREATED);
      toSet.removeField(FIELD_TOPOLOGIES_COUNT);
      toSet.removeField(FIELD_BUILD_STATUS);

      // Update field's update
      toSet.put(FIELD_UPDATED, new Date());

      collection.update(query, new BasicDBObject("$set", toSet), true, false);
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

  public void recheck() throws SinfonierException {
    setStatus(STATUS_PENDING);
    save();
  }

  public File exportAsJson(Module module) {
    DBObject obj = module.toDBObject();
    obj.putAll(this.toDBObject());

    obj.removeField(FIELD_ID);
    obj.removeField(FIELD_ICON);
    obj.removeField(FIELD_STATUS);
    obj.removeField(FIELD_AUTHOR_ID);
    obj.removeField(FIELD_CREATED);
    obj.removeField(FIELD_UPDATED);
    obj.removeField(FIELD_RATINGS);
    obj.removeField(FIELD_AVERAGE_RATE);
    obj.removeField(FIELD_TOPOLOGIES_COUNT);
    obj.removeField(FIELD_MY_TOOLS);
    obj.removeField(FIELD_COMPLAINS);
    obj.removeField(FIELD_CONTAINER);
    obj.removeField(FIELD_CATEGORY);
    obj.removeField(FIELD_VERSIONS);

    JsonObject res = Mongo2gson.getAsJsonObject(obj);
    File dir = FileUtils.getFile(PATH_TO_SAVE);

    if (!dir.exists()) {
      dir.mkdirs();
    }

    String fileName = module.getName() + ".json";
    File file = FileUtils.getFile(dir, fileName);

    try {
      FileUtils.writeByteArrayToFile(file, res.toString().getBytes("UTF-8"));
    } catch (IOException e) {
      Logger.error(e.getMessage());
    }

    return file;
  }

  public void addToMyTools(MyTool tool) {
    DBObject toSet = new BasicDBObject();
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));

    myTools.add(tool);
    toSet.put(FIELD_MY_TOOLS, myTools.toDBObject());
    collection.update(query, new BasicDBObject("$set", toSet), true, false);
  }

  public void removeToMyTools(MyTool tool) {
    DBObject toSet = new BasicDBObject();
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));

    myTools.remove(tool);
    toSet.put(FIELD_MY_TOOLS, myTools.toDBObject());
    collection.update(query, new BasicDBObject("$set", toSet), true, false);
  }

  public boolean isFromGist() {
    return sourceType.equalsIgnoreCase(SOURCE_TYPE_GIST);
  }

  public boolean hasFields() {
    return fields != null && fields.size() > 0;
  }

  public boolean hasLibraries() {
    return libraries != null && libraries.size() > 0;
  }

  public String getVersionTag() {
    return versionTag;
  }

  public void setVersionTag(String versionTag) {
    this.versionTag = versionTag;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public String getSourceCode() {
    if (sourceCode != null) {
      return sourceCode.replace("\n", "\\n").replace("\r", "\\r");
    } else {
      return null;
    }
  }

  public String getSourceCodeBase64() {
    if (sourceCode != null) {
      byte[] base64 = Base64.encodeBase64(sourceCode.getBytes());
      return new String(base64);
    } else {
      return null;
    }
  }
  
  public void setSourceCode(String sourceCode) {
    this.sourceCode = sourceCode;
  }

  public String getSourceCodeURL() {
    return sourceCodeURL;
  }

  public void setSourceCodeURL(String sourceCodeURL) {
    this.sourceCodeURL = sourceCodeURL;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TickTuple getTickTuple() {
    return tickTuple;
  }

  public void setTickTuple(TickTuple tickTuple) {
    this.tickTuple = tickTuple;
  }

  public Fields getFields() {
    return fields;
  }

  public void setFields(Fields fields) {
    this.fields = fields;
  }

  public Libraries getLibraries() {
    return libraries;
  }

  public void setLibraries(Libraries libraries) {
    this.libraries = libraries;
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

  public Boolean isSingleton() {
    return singleton;
  }

  public void setSingleton(Boolean singleton) {
    this.singleton = singleton;
  }

  public int getTopologiesCount() {
    return topologiesCount;
  }

  public void setTopologiesCount(int topologiesCount) {
    this.topologiesCount = topologiesCount;
  }

  public MyTools getMyTools() {
    return myTools;
  }

  public void setMyTools(MyTools myTools) {
    this.myTools = myTools;
  }

  public Container getContainer() {
    return container;
  }

  public void setContainer(Container container) {
    this.container = container;
  }

  public String getBuildStatus() {
    return buildStatus;
  }
}
