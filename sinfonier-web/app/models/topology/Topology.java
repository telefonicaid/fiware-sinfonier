package models.topology;

import static models.SinfonierConstants.Topology.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import models.factory.DarwinFactory;
import models.factory.MongoFactory;
import models.module.Module;
import models.module.ModuleVersion;
import models.module.ModulesContainer;
import models.user.User;

import org.bson.types.ObjectId;

import play.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import exceptions.SinfonierError;
import exceptions.SinfonierException;

import utils.Utils;

public class Topology implements Cloneable {

  private String id;
  private String name;
  private Date createdAt;
  private Date updatedAt;
  private User author;
  private String authorId;
  private String status;
  private Boolean sharing;
  private String description;
  private TopologyConfig config;
  private ObjectId templateId;

  public static TopologiesContainer findByStatusOrNameOrAuthorOrUpdatedDate(String status, String q, Date updated, User user, Integer page) throws SinfonierException {
    DBObject query = new BasicDBObject();
    BasicDBList andQuery = new BasicDBList();


    if (!user.isAdminUser()) {
      andQuery.add(new BasicDBObject(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED)));
      BasicDBList orQuery = new BasicDBList();
      orQuery.add(new BasicDBObject(FIELD_SHARING, true));
      orQuery.add(new BasicDBObject(FIELD_AUTHOR_ID, user.getId()));
      andQuery.add(new BasicDBObject("$or", orQuery));
    }

    if (status != null && status.length() > 0) {
      andQuery.add(new BasicDBObject(FIELD_STATUS, status));
    }

    if (q != null && q.length() > 0) {
      BasicDBList orQuery = new BasicDBList();
      orQuery.add(new BasicDBObject(FIELD_NAME, Pattern.compile(q, Pattern.CASE_INSENSITIVE)));
      orQuery.add(new BasicDBObject(FIELD_AUTHOR_ID, Pattern.compile(q, Pattern.CASE_INSENSITIVE)));
      orQuery.add(new BasicDBObject(FIELD_AUTHOR_ID, new BasicDBObject("$in", Utils.getUsersEmailsByName(q))));
      andQuery.add(new BasicDBObject("$or", orQuery));
    }

    if (updated != null) {
      andQuery.add(new BasicDBObject(FIELD_UPDATED, new BasicDBObject("$gt", updated)));
    }

    if (andQuery.size() > 0) {
      query.put("$and", andQuery);
    }

    return find(query, page);
  }
  
  public static TopologiesContainer getTopologies(User user, Integer page) throws SinfonierException {
    return getTopologies(user, true, true, page);
  }

  public static TopologiesContainer getTopologies(User user, boolean includeExternalSharing, boolean usePagination, Integer page) throws SinfonierException {
    TopologiesContainer topologies;

    DBObject sortByName = new BasicDBObject(FIELD_NAME, 1);
    BasicDBObject not_deleted = new BasicDBObject(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED));

    BasicDBList notDeletedAndAuthor = new BasicDBList();
    notDeletedAndAuthor.add(not_deleted);
    notDeletedAndAuthor.add(new BasicDBObject(FIELD_AUTHOR_ID, user.getId()));


    if (user.isAdminUser() && includeExternalSharing) {
      topologies = find(null, sortByName, usePagination, page);
    } else if (includeExternalSharing) {
      BasicDBList notDeletedAndSharing = new BasicDBList();
      notDeletedAndSharing.add(not_deleted);
      notDeletedAndSharing.add(new BasicDBObject(FIELD_SHARING, true));

      BasicDBList list = new BasicDBList();
      list.add(new BasicDBObject("$and", notDeletedAndSharing));
      list.add(new BasicDBObject("$and", notDeletedAndAuthor));

      topologies = find(new BasicDBObject("$or", list), sortByName, usePagination, page);
    } else {
      topologies = find(new BasicDBObject("$and", notDeletedAndAuthor), sortByName, usePagination, page);
    }

    return topologies;
  }

  public static Topology findByName(String name) throws SinfonierException {
    DBObject query = new BasicDBObject(FIELD_NAME, name);
    return findOne(query);
  }

  public static Topology findById(ObjectId id) throws SinfonierException {
    DBObject query = new BasicDBObject(FIELD_ID, id);
    return findOne(query);
  }

  public static Topology findById(String id) throws SinfonierException {
    id = id.replace("\"", "");
    if (ObjectId.isValid(id)) {
      DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(id));
      return findOne(query);
    } else {
      return null;
    }
  }

  public static long countByUser(User user) {
    DBObject query = new BasicDBObject();

    query.put(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED));
    query.put(FIELD_AUTHOR_ID, user.getId());

    return count(query);
  }

  public static long getRunningByUser(User user) {
    DBObject query = new BasicDBObject();

    query.put(FIELD_STATUS, STATUS_RUNNING);
    query.put(FIELD_AUTHOR_ID, user.getId());

    return count(query);
  }

  public static Topology getAsTemplate(Topology topology, User user) throws SinfonierException {
    Topology templateTopology;
    try {
      templateTopology = (Topology) topology.clone();
    } catch (Exception ex) {
      throw new SinfonierException(SinfonierError.TOPOLOGY_INVALID_CONSTRUCTION, ex);
    }
    templateTopology.setName(TEMPLATE_NAME);
    Map<String, String> properties = templateTopology.getConfig().getProperties();
    properties.put("templateid", topology.getId());
    templateTopology.getConfig().setProperties(properties);
    if (!templateTopology.isOwner(user) && templateTopology.getConfig().getModules() != null) {
      for (TopologyModule m : templateTopology.getConfig().getModules()) {
        m.setValues(null);
      }
    }
    return templateTopology;
  }

  public Topology(String name, User author, Boolean sharing, String description, TopologyConfig config) {
    this.name = name;
    this.author = author;
    if (author != null) this.authorId = author.getId();
    this.sharing = sharing;
    this.description = description;
    this.config = config;
    this.createdAt = new Date();
    this.updatedAt = new Date();
    this.status = STATUS_ACTIVE;
  }

  public Topology(DBObject o) throws SinfonierException {
    try {
      id = o.get(FIELD_ID).toString();
      name = o.get(FIELD_NAME).toString();
      status = o.get(FIELD_STATUS).toString();
      sharing = ((Boolean) o.get(FIELD_SHARING));
      description = o.get(FIELD_DESCRIPTION).toString();
      authorId = o.get(FIELD_AUTHOR_ID).toString();
      createdAt = ((Date) o.get(FIELD_CREATED));
      updatedAt = ((Date) o.get(FIELD_UPDATED));
      config = new TopologyConfig(((DBObject) o.get(FIELD_CONFIG)));
    } catch (Exception e) {
      name = null;
      Logger.error("Exception in Topology construct trying to build an object from DBObject. " + e.getMessage());
      throw new SinfonierException(SinfonierError.TOPOLOGY_INVALID_CONSTRUCTION, e);
    }
  }

  public String save() throws SinfonierException {
    try {
      DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
  
      if (id == null) {
        Logger.info("New topology");
        DBObject dbObj = this.toDBObject();
        updateUsedModulesCount(true);
        collection.save(dbObj);
        this.id = dbObj.get(FIELD_ID).toString();
      } else {
        Logger.info("Editing topology id:" + this.getId());
        DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(this.getId()));
        DBObject toSet = this.toDBObject();
  
        // Remove fields's unmodified
        toSet.removeField(FIELD_ID);
        toSet.removeField(FIELD_CREATED);
        toSet.removeField(FIELD_AUTHOR_ID);
  
        // Update field's update
        toSet.put(FIELD_UPDATED, new Date());
  
        updateUsedModulesCount(false);
        collection.update(query, new BasicDBObject("$set", toSet), true, false);
      }
      return this.getId();
    } catch (MongoException.DuplicateKey e) {
      Logger.error(e, e.getMessage());
      throw new SinfonierException(SinfonierError.TOPOLOGY_DUPLICATE, e, this.name);
    }
  }

  public void remove() {
    DBObject toSet = new BasicDBObject(FIELD_STATUS, STATUS_DELETED);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));
    DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
    collection.update(query, new BasicDBObject("$set", toSet));
  }

  public Boolean isRunning() {
    return status.equals(STATUS_RUNNING);
  }

  public Boolean isOwner(User user) {
    if (user == null) {
      return false;
    } else {
      return authorId.equals(user.getId());
    }
  }

  public boolean hasWritePermission(User user) {
    if (this.isOwner(user) || user.isAdminUser()) {
      return true; // Owner or Admin
    } else {
      return false;
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (name != null && id != null) {
      object.put(FIELD_ID, this.id == null ? null : new ObjectId(this.id));
    }

    if (name != null && description != null) {
      object.put(FIELD_DESCRIPTION, description);
    } else {
      object.put(FIELD_DESCRIPTION, "");
    }

    if (name != null) {
      object.put(FIELD_NAME, name);
      object.put(FIELD_STATUS, status);
      object.put(FIELD_CREATED, createdAt);
      object.put(FIELD_UPDATED, updatedAt);
      object.put(FIELD_SHARING, sharing);
      object.put(FIELD_AUTHOR_ID, authorId);
      object.put(FIELD_CONFIG, config.toDBObject());
    }
    if (templateId != null) {
      object.put(FIELD_TEMPLATE_ID, templateId);
    }

    return object;
  }

  private void updateUsedModulesCount(Boolean isNewTopology) throws SinfonierException {
    List<TopologyModule> newUsedModules;
    List<TopologyModule> removedModules;
    if (isNewTopology) {
      newUsedModules = this.getConfig().getModules();
      removedModules = null;
      Logger.debug("Add " + newUsedModules.size() + " modules");
    } else {
      Topology originalTopology = Topology.findById(this.getId());
      List<TopologyModule> originalModules = originalTopology.getConfig().getModules();
      newUsedModules = new ArrayList<TopologyModule>(this.getConfig().getModules());
      removedModules = new ArrayList<TopologyModule>(originalModules);

      for (TopologyModule m : originalModules) {
        newUsedModules.remove(m);
      }
      for (TopologyModule m : this.getConfig().getModules()) {
        removedModules.remove(m);
      }
      Logger.debug("Add " + newUsedModules.size() + " modules and remove " + removedModules.size() + " modules");
    }

    if (newUsedModules != null) {
      for (TopologyModule m : newUsedModules) {
        if (m.getVersionCode() > 0) {
          String moduleName = m.getName();
          increaseOrReduceModuleUsage(moduleName, m.getVersionCode(), true);
        }
      }
    }
    if (removedModules != null) {
      for (TopologyModule m : removedModules) {
        if (m.getVersionCode() > 0) {
          String moduleName = m.getName();
          increaseOrReduceModuleUsage(moduleName, m.getVersionCode(), false);
        }
      }
    }
  }

  private void increaseOrReduceModuleUsage(String moduleName, int versionCode, boolean increase) throws SinfonierException {
    Module module = Module.findByName(moduleName);
    if (increase) {
      module.setTopologiesCount(module.getTopologiesCount() + 1);
    } else {
      module.setTopologiesCount(module.getTopologiesCount() - 1);
    }
    module.updateTopologiesCount();

    ModuleVersion moduleVersion = module.getModuleVersion(versionCode);
    if (increase) {
      moduleVersion.setTopologiesCount(moduleVersion.getTopologiesCount() + 1);
    } else {
      moduleVersion.setTopologiesCount(moduleVersion.getTopologiesCount() - 1);
    }
    moduleVersion.updateTopologiesCount();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public void setUpdated(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public User getAuthor() {
    if (this.author == null && this.authorId != null) {
      this.author = DarwinFactory.getInstance().loadUser(this.authorId);
    }

    return author;
  }

  public String getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Boolean getSharing() {
    return sharing;
  }

  public void setSharing(Boolean sharing) {
    this.sharing = sharing;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public TopologyConfig getConfig() {
    return config;
  }

  public void setConfig(TopologyConfig config) {
    this.config = config;
  }

  public ObjectId getTemplateId() {
    return templateId;
  }

  public void setTemplateId(ObjectId templateId) {
    this.templateId = templateId;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }

  private static Topology findOne(DBObject query) throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
    DBObject topology = collection.findOne(query);

    if (topology != null) {
      return new Topology(topology);
    }
    return null;
  }

  private static TopologiesContainer find(DBObject query, Integer page) throws SinfonierException {
    return find(query, null, TOPOLOGY_MAX_RESULTS_PAGE, page);
  }

  private static TopologiesContainer find(DBObject query, DBObject sortBy, boolean usePagination, Integer page) throws SinfonierException {
    if (usePagination)
      return find(query, sortBy, TOPOLOGY_MAX_RESULTS_PAGE, page);
    else
      return find(query, sortBy, null, page);
  }

  private static TopologiesContainer find(DBObject query, DBObject orderBy, Integer limit, Integer page) throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
    List<Topology> list = new ArrayList<Topology>();
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
    
    int totalTopologies = cursor.count();

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
      list.add(new Topology(dbObject));
    }

    return new TopologiesContainer(list, totalTopologies);
  }

  private static long count(DBObject query) {
    DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
    return collection.count(query);
  }

  private static String getCollectionName() {
    return COLLECTION_NAME;
  }
}
