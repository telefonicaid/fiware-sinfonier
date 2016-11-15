package models.project;

import static models.SinfonierConstants.ModelCollection.FIELD_ID;
import static models.SinfonierConstants.Module.FIELD_AUTHOR_ID;
import static models.SinfonierConstants.Module.FIELD_NAME;
import static models.SinfonierConstants.Module.FIELD_STATUS;
import static models.SinfonierConstants.Module.STATUS_DELETED;
import static models.SinfonierConstants.Module.STATUS_PREDEFINED;
import static models.SinfonierConstants.Module.STATUS_PUBLISHED;
import static models.SinfonierConstants.Project.COLLECTION_NAME;
import static models.SinfonierConstants.Project.FIELD_CREATED;
import static models.SinfonierConstants.Project.FIELD_DESCRIPTION;
import static models.SinfonierConstants.Project.FIELD_SHARING;
import static models.SinfonierConstants.Project.FIELD_TOPOLOGY_IDS;
import static models.SinfonierConstants.Project.FIELD_UPDATED;
import static models.SinfonierConstants.Project.PROJECT_MAX_RESULTS_PAGE;
import static models.SinfonierConstants.Project.STATUS_ACTIVE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.factory.DarwinFactory;
import models.factory.MongoFactory;
import models.user.User;
import play.Logger;
import utils.Utils;

public class Project implements Cloneable {

  private String id;
  private String name;
  private Date createdAt;
  private Date updatedAt;
  private User author;
  private String authorId;
  private String status;
  private String description;
  private List<String> topologyIds;

  public Project() {
    List<String> ids = new ArrayList<String>();
    this.setStatus(STATUS_ACTIVE);
    this.setTopologyIds(ids);
  }

  public List<String> getTopologyIds() {
    return topologyIds;
  }

  public void setTopologyIds(List<String> topologyIds) {
    this.topologyIds = topologyIds;
  }

  public static ProjectsContainer getProjects(User user, Integer page) throws SinfonierException {
    return getProjects(user,  true, page);
  }

  public static ProjectsContainer getProjects(User user,  boolean usePagination,
      Integer page) throws SinfonierException {
    ProjectsContainer projects;

    DBObject sortByName = new BasicDBObject(FIELD_NAME, 1);
    BasicDBObject not_deleted = new BasicDBObject(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED));

    BasicDBList notDeletedAndAuthor = new BasicDBList();
    notDeletedAndAuthor.add(not_deleted);
    notDeletedAndAuthor.add(new BasicDBObject(FIELD_AUTHOR_ID, user.getId()));

    if (user.isAdminUser() ) {
      projects = find(null, sortByName, usePagination, page);
    } else {
      projects = find(new BasicDBObject("$and", notDeletedAndAuthor), sortByName, usePagination, page);
    }

    return projects;
  }

  public static Project findByName(String name) throws SinfonierException {
    DBObject query = new BasicDBObject(FIELD_NAME, name);
    return findOne(query);
  }

  public static Project findById(ObjectId id) throws SinfonierException {
    DBObject query = new BasicDBObject(FIELD_ID, id);
    return findOne(query);
  }

  public static Project findById(String id) throws SinfonierException {
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

  public static long getActiveByUser(User user) {
    DBObject query = new BasicDBObject();

    query.put(FIELD_STATUS, STATUS_ACTIVE);
    query.put(FIELD_AUTHOR_ID, user.getId());

    return count(query);
  }

  public Project(String name, User author, Boolean sharing, String description, List<String> topologyIds) {
    this.name = name;
    this.author = author;
    if (author != null)
      this.authorId = author.getId();
    this.description = description;
    this.topologyIds = topologyIds;
    this.createdAt = new Date();
    this.updatedAt = new Date();
    this.status = STATUS_ACTIVE;
  }

  public Project(DBObject o) throws SinfonierException {
    try {
      id = o.get(FIELD_ID).toString();
      name = o.get(FIELD_NAME).toString();
      status = o.get(FIELD_STATUS).toString();
      description = o.get(FIELD_DESCRIPTION).toString();
      authorId = o.get(FIELD_AUTHOR_ID).toString();
      createdAt = ((Date) o.get(FIELD_CREATED));
      updatedAt = ((Date) o.get(FIELD_UPDATED));
      List<ObjectId> objIds = (List<ObjectId>) o.get(FIELD_TOPOLOGY_IDS);
      topologyIds = new ArrayList<String>(objIds.size());
      for (ObjectId id : objIds) {
        topologyIds.add(id.toString());
      }

    } catch (Exception e) {
      name = null;
      Logger.error("Exception in Project construct trying to build an object from DBObject. " + e.getMessage());
      throw new SinfonierException(SinfonierError.PROJECT_INVALID_CONSTRUCTION, e);
    }
  }

  public String save() throws SinfonierException {
    try {
      DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());

      if (id == null) {
        Logger.info("New Project");
        Date now = new Date();
        this.setCreatedAt(now);
        this.setUpdated(now);
        DBObject dbObj = this.toDBObject();
        collection.save(dbObj);
        this.id = dbObj.get(FIELD_ID).toString();
      } else {
        Logger.info("Editing project id:" + this.getId());
        
        //Recover previous project information not shared in form
        Project previousProjectInfo = findById(this.getId());
        this.setTopologyIds(previousProjectInfo.getTopologyIds());
        
        DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(this.getId()));
        DBObject toSet = this.toDBObject();

        this.setUpdated(new Date());
        // Remove fields's unmodified
        toSet.removeField(FIELD_ID);
        toSet.removeField(FIELD_CREATED);
        toSet.removeField(FIELD_AUTHOR_ID);

        // Update field's update
        toSet.put(FIELD_UPDATED, new Date());

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

  public Boolean isActive() {
    return status.equals(STATUS_ACTIVE);
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
      object.put(FIELD_AUTHOR_ID, authorId);
      List<ObjectId> objIds = new ArrayList<ObjectId>(topologyIds.size()); 
      for (String id : topologyIds) {
        objIds.add(new ObjectId(id));
      }
      object.put(FIELD_TOPOLOGY_IDS, objIds);
    }

    return object;
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
  
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }

  private static Project findOne(DBObject query) throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
    DBObject project = collection.findOne(query);

    if (project != null) {
      return new Project(project);
    }
    return null;
  }

  private static ProjectsContainer find(DBObject query, Integer page) throws SinfonierException {
    return find(query, null, PROJECT_MAX_RESULTS_PAGE, page);
  }

  public static ProjectsContainer find(ProjectSearch search, User user, Integer page) throws SinfonierException {
    BasicDBList query = new BasicDBList();

    query.add(new BasicDBObject(FIELD_STATUS, Pattern.compile(".*", Pattern.DOTALL)));
    if (!user.isAdminUser()) {
      BasicDBList notDeletedAndAuthor = new BasicDBList();
      notDeletedAndAuthor.add(new BasicDBObject(FIELD_STATUS, new BasicDBObject("$ne", STATUS_DELETED)));
      notDeletedAndAuthor.add(new BasicDBObject(FIELD_AUTHOR_ID, user.getId()));

      BasicDBList list = new BasicDBList();
      list.add(new BasicDBObject(FIELD_STATUS,
          new BasicDBObject("$in", new ArrayList(Arrays.asList(STATUS_PUBLISHED, STATUS_PREDEFINED)))));
      list.add(new BasicDBObject("$and", notDeletedAndAuthor));
      query.add(new BasicDBObject("$or", list));
    }

    if (search.getName() != null && search.getName().length() > 0) {
      query.add(new BasicDBObject(FIELD_NAME, Pattern.compile(search.getName(), Pattern.CASE_INSENSITIVE)));
    }

    if (search.getOwner() != null && search.getOwner().length() > 0) {
      BasicDBList list = new BasicDBList();
      list.add(new BasicDBObject(FIELD_AUTHOR_ID, Pattern.compile(search.getOwner(), Pattern.CASE_INSENSITIVE)));
      list.add(
          new BasicDBObject(FIELD_AUTHOR_ID, new BasicDBObject("$in", Utils.getUsersEmailsByName(search.getOwner()))));
      query.add(new BasicDBObject("$or", list));
    }

    return find(new BasicDBObject("$and", query), page);
  }

  private static ProjectsContainer find(DBObject query, DBObject sortBy, boolean usePagination, Integer page)
      throws SinfonierException {
    if (usePagination)
      return find(query, sortBy, PROJECT_MAX_RESULTS_PAGE, page);
    else
      return find(query, sortBy, null, page);
  }

  private static ProjectsContainer find(DBObject query, DBObject orderBy, Integer limit, Integer page)
      throws SinfonierException {
    DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
    List<Project> list = new ArrayList<Project>();
    DBCursor cursor;

    if (limit != null) {
      page = (page != null && page > 0) ? ((page - 1) * limit) : 0;
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
      list.add(new Project(dbObject));
    }

    return new ProjectsContainer(list, totalTopologies);
  }

  private static long count(DBObject query) {
    DBCollection collection = MongoFactory.getDB().getCollection(getCollectionName());
    return collection.count(query);
  }

  private static String getCollectionName() {
    return COLLECTION_NAME;
  }

  public static Project fromJson(String json) {
    Gson gson = new Gson();
    return gson.fromJson(json, Project.class);
  }

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }

  public boolean hasTopologyId(String topologyId) {
    ObjectId id = new ObjectId(topologyId);
    return this.topologyIds.contains(id);
  }

  public void addTopology(String topologyId) {
    ObjectId id = new ObjectId(topologyId);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(getId()));
    DBObject toPush = new BasicDBObject(FIELD_TOPOLOGY_IDS, id);
    DBCollection collection = MongoFactory.getDB().getCollection(COLLECTION_NAME);
    collection.update(query, new BasicDBObject("$push", toPush));
    topologyIds.add(topologyId);
  }

  public ObjectId[] getArrTopologyIds() {
    ObjectId[] ids = new ObjectId[topologyIds.size()];

    for (int i = 0; i < topologyIds.size(); i++) {
      ids[i] = new ObjectId(topologyIds.get(i));
    }

    return ids;
  }
}
