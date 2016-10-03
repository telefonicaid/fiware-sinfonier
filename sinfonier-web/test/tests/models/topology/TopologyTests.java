package tests.models.topology;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import exceptions.SinfonierException;
import models.topology.Topology;

import models.topology.TopologyConfig;
import models.factory.MongoFactory;
import models.user.User;
import org.bson.types.ObjectId;
import org.junit.*;

import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import play.test.PlayJUnitRunner;
import tests.BaseTest;
import tests.TestMock;

import static models.SinfonierConstants.Topology.*;
import static tests.TestData.TOPOLOGIES_COLLECTION;
import static tests.TestData.TOPOLOGIES_JSON_FILE;

import java.util.List;

@RunWith(PlayJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TopologyTests extends BaseTest {
  private static final String ID_OWNER = "test1@test.com";
  private static final String ID_NON_ADMIN = "other_strange_but_normal_user@test.com";
  private static final String ID_NON_OWNER_OR_ADMIN = "other_strange_but_normal_user_without_nothing@test.com";

  private static final Integer TOPOLOGIES_SIZE_FOR_OWNER = 1;

  private static final String T_NAME = "MyTestTopology";
  private static final String T_DESCRIPTION = "MyTestTopology description in a test";
  private static final Boolean T_SHARING = false;
  private TopologyConfig T_CONFIG = new TopologyConfig();

  private static User adminUser, normalUser, lostUser;

  @BeforeClass
  public static void runBeforeClass() throws Exception {
    doMongoImport(TOPOLOGIES_COLLECTION, TOPOLOGIES_JSON_FILE);
    initUserMock();
  }

  public static void initUserMock() {
    adminUser = TestMock.mockUser(ID_OWNER, true);
    normalUser = TestMock.mockUser(ID_NON_ADMIN, false);
    lostUser = TestMock.mockUser(ID_NON_OWNER_OR_ADMIN, false);
  }


  @Test
  public void newTopologyWithDBObjectTest() throws SinfonierException {
    String id = "57becd7cd5c4fa520ae593ae";
    DBCollection collection = MongoFactory.getDB().getCollection(TOPOLOGIES_COLLECTION);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId(id));
    DBObject dbObject = collection.findOne(query);

    assert dbObject != null;

    Topology topology = new Topology(dbObject);

    assertNotNull(topology);
    assertEquals(topology.getId(), id);
    assertEquals(topology.getAuthorId(), ID_OWNER);
    assertEquals(topology.getStatus(), STATUS_ACTIVE);
  }


  @Test
  public void newTopologyTest() throws SinfonierException {
    Topology topology = new Topology(T_NAME, normalUser, T_SHARING, T_DESCRIPTION, T_CONFIG);

    assertNotNull(topology);
    assertEquals(topology.getName(), T_NAME);
    assertEquals(topology.getSharing(), T_SHARING);
    assertEquals(topology.getSharing(), T_SHARING);
    assertNotNull(topology.getConfig());
    assertEquals(topology.getAuthorId(), normalUser.getId());
  }

  @Test
  public void findByStatusOrNameOrAuthorOrUpdatedDateTest() throws SinfonierException {
    String status = STATUS_ACTIVE;
    String name = "Test";
    String author = "test_other@test.com";

    List<Topology> listByStatus = Topology.findByStatusOrNameOrAuthorOrUpdatedDate(status, null, null, adminUser, null).getTopologies();
    List<Topology> listByName = Topology.findByStatusOrNameOrAuthorOrUpdatedDate(null, name, null, adminUser, null).getTopologies();
    List<Topology> listByAuthor = Topology.findByStatusOrNameOrAuthorOrUpdatedDate(null, author, null, adminUser, null).getTopologies();

    assertTrue(listByStatus.size() == TOPOLOGIES_SIZE_FOR_OWNER && listByName.size() == TOPOLOGIES_SIZE_FOR_OWNER);
    assertTrue(listByAuthor.size() == 0);
  }

  @Test
  public void getTopologiesTest() throws SinfonierException {
    List<Topology> list = Topology.getTopologies(adminUser, null).getTopologies();
    assertTrue(list.size() == TOPOLOGIES_SIZE_FOR_OWNER);

    list = Topology.getTopologies(lostUser, false, false, null).getTopologies();
    assertTrue(list.size() == 0);
  }

  @Test
  public void countByUserTest() throws SinfonierException {
    assertTrue(Topology.countByUser(adminUser) == TOPOLOGIES_SIZE_FOR_OWNER);
  }

  @Test
  public void getRunningByUserTest() throws SinfonierException {
    assertTrue(Topology.getRunningByUser(adminUser) == 0);
  }

  @Test
  public void getAsTemplateTest() throws SinfonierException {
    Topology origin = Topology.findById(new ObjectId("57becd7cd5c4fa520ae593ae"));
    Topology template = Topology.getAsTemplate(origin, adminUser);

    assertTrue(template.getName().equals(TEMPLATE_NAME));
    assertTrue("shuold have the property tempalteid", template.getConfig().getProperties().containsKey("templateid"));
  }

  @Test
  public void getTopologyByNameTest() throws SinfonierException {
    String name = "Test";
    Topology topology = Topology.findByName(name);
    assertTrue(topology != null && topology.getName().equals(name));
  }

  @Test
  public void getTopologyByIdTest() throws SinfonierException {
    String id = "57becd7cd5c4fa520ae593ae";
    Topology topology = Topology.findById(id);

    assert topology != null;

    assertEquals(topology.getId(), id);
    assertEquals(topology.getAuthorId(), ID_OWNER);
    assertEquals(topology.getStatus(), STATUS_ACTIVE);
  }

  @Test
  public void isRunningTest() throws SinfonierException {
    Topology topology = Topology.findById("57becd7cd5c4fa520ae593ae");
    assert topology != null;
    assertFalse("Should be false", topology.isRunning());
  }

  @Test
  public void isOwnerTest() throws SinfonierException {
    Topology topology = Topology.findById("57becd7cd5c4fa520ae593ae");
    assert topology != null;
    assertTrue("Should be true", topology.isOwner(adminUser));
    assertFalse("Should be false", topology.isOwner(lostUser));
  }

  @Test
  public void hasWritePermissionTest() throws SinfonierException {
    Topology topology = Topology.findById("57becd7cd5c4fa520ae593ae");
    assert topology != null;
    assertTrue("Should be true", topology.hasWritePermission(adminUser));
    assertFalse("Should be false", topology.hasWritePermission(lostUser));
  }

  @Test
  public void saveTest() throws SinfonierException {
    Topology topology = new Topology(T_NAME, normalUser, T_SHARING, T_DESCRIPTION, T_CONFIG);

    assertTrue("Should be true", topology.getName().equals(T_NAME));
    assertTrue("Should be true", topology.hasWritePermission(normalUser));

    String id = topology.save();
    assertNotNull("new id", id);
  }


  @Test
  public void removeTest() throws SinfonierException {
    Topology topology = Topology.findById("57becd7cd5c4fa520ae593ae");
    assert topology != null;

    topology.remove();
    topology = Topology.findById("57becd7cd5c4fa520ae593ae");

    assert topology != null;
    assertTrue("Should be true", topology.getStatus().equals(STATUS_DELETED));
  }


  @AfterClass
  public static void runAfterClass() throws Exception {
    doMongoDrop(TOPOLOGIES_COLLECTION);
  }
}
