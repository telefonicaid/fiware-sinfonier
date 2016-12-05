package tests.models.project;

import static models.SinfonierConstants.Project.FIELD_ID;
import static models.SinfonierConstants.Project.STATUS_ACTIVE;
import static models.SinfonierConstants.Project.STATUS_DELETED;
import static tests.TestData.PROJECTS_COLLECTION;
import static tests.TestData.PROJECTS_JSON_FILE;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import exceptions.SinfonierException;
import models.factory.MongoFactory;
import models.project.Project;
import models.project.ProjectSearch;
import models.topology.Topology;
import models.user.User;
import play.test.PlayJUnitRunner;
import tests.BaseTest;
import tests.TestMock;

@RunWith(PlayJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectTests extends BaseTest {
  private static final String ID_OWNER = "test1@test.com";
  private static final String ID_NON_ADMIN = "other_strange_but_normal_user@test.com";
  private static final String ID_NON_OWNER_OR_ADMIN = "other_strange_but_normal_user_without_nothing@test.com";

  private static final Integer PROJECTS_SIZE_FOR_OWNER = 2;

  private static final String T_NAME = "MyTestProject";
  private static final String T_DESCRIPTION = "MyTestProject description in a test";

  private static User adminUser, normalUser, lostUser;

  @BeforeClass
  public static void runBeforeClass() throws Exception {
    doMongoImport(PROJECTS_COLLECTION, PROJECTS_JSON_FILE);
    initUserMock();
  }

  public static void initUserMock() {
    adminUser = TestMock.mockUser(ID_OWNER, true);
    normalUser = TestMock.mockUser(ID_NON_ADMIN, false);
    lostUser = TestMock.mockUser(ID_NON_OWNER_OR_ADMIN, false);
  }


  @Test
  public void newProjectWithDBObjectTest() throws SinfonierException {
    String id = "5820674a96e0398127d6723d";
    DBCollection collection = MongoFactory.getDB().getCollection(PROJECTS_COLLECTION);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(id));
    DBObject dbObject = collection.findOne(query);

    assert dbObject != null;

    Project project = new Project(dbObject);
    
    assertNotNull(project);
    assertEquals(project.getId(), id);
    assertEquals(project.getAuthorId(), ID_OWNER);
    assertEquals(project.getStatus(), STATUS_ACTIVE);
  }



  @Test
  public void findByNameAndAuthorTest() throws SinfonierException {
    ProjectSearch search = new ProjectSearch();
    search.setName("Ola");
    search.setOwner("test1@test.com");

    List<Project> listProjects = Project.find(search,adminUser,Integer.valueOf(0)).getProjects();

     assertTrue(listProjects.size() == 1);
  }

  @Test
  public void getProjectsTest() throws SinfonierException {
    List<Project> list = Project.getProjects(adminUser,false, null).getProjects();
    assertTrue(list.size() == 5);
  }

  @Test
  public void countByUserTest() throws SinfonierException {
    assertTrue(Project.countByUser(adminUser) == PROJECTS_SIZE_FOR_OWNER);
  }


  @Test
  public void getProjectByNameTest() throws SinfonierException {
    String name = "LaCosaNoVa";
    Project project = Project.findByName(name);
    assertTrue(project != null && project.getName().equals(name));
  }

  @Test
  public void getProjectByIdTest() throws SinfonierException {
    String id = "5824905e96e07dadead5a9ba";
    Project project = Project.findById(id);

    assert project != null;

    assertEquals(project.getId(), id);
    assertEquals(project.getAuthorId(), "test@test.com");
    assertEquals(project.getStatus(), STATUS_ACTIVE);
  }


  @Test
  public void isOwnerTest() throws SinfonierException {
    Project project = Project.findById("5820674a96e0398127d6723d");
    assert project != null;
    assertTrue("Should be true", project.isOwner(adminUser));
    assertFalse("Should be false", project.isOwner(lostUser));
  }


  @Test
  public void saveTest() throws SinfonierException {
    Project project = new Project(T_NAME, normalUser, T_DESCRIPTION, new ArrayList<String>());

    assertTrue("Should be true", project.getName().equals(T_NAME));
    assertTrue("Should be true", project.hasWritePermission(normalUser));

    String id = project.save();
    assertNotNull("new id", id);
  }


  @Test
  public void removeTest() throws SinfonierException {
    Project project = Project.findById("5824905e96e07dadead5a9ba");
    assert project != null;

    project.remove();
    project = Project.findById("5824905e96e07dadead5a9ba");

    assert project != null;
    assertTrue("Should be true", project.getStatus().equals(STATUS_DELETED));
  }


  @AfterClass
  public static void runAfterClass() throws Exception {
    doMongoDrop(PROJECTS_COLLECTION);
  }
}
