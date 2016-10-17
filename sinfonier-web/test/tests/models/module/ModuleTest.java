package tests.models.module;

import static models.SinfonierConstants.ModelCollection.FIELD_ID;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.factory.DarwinFactory;
import models.factory.MongoFactory;
import models.module.Complains;
import models.module.Module;
import models.module.ModuleSearch;
import models.module.ModuleVersion;
import models.module.Ratings;
import models.module.Version;
import models.module.Versions;
import models.user.Inappropriate;
import models.user.Rating;
import models.user.User;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import play.test.PlayJUnitRunner;
import tests.BaseTest;
import tests.TestData;
import tests.TestMock;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import exceptions.SinfonierException;

@RunWith(PlayJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModuleTest extends BaseTest {
  
  private static final String AUTHOR_ID = "user@domain.com";
  private static final double AVERAGE_RATE = 4.2;
  private static final String CATEGORY = "Spouts";
  private static final String USER_EMAIL = "test1@test.com";
  private static final String COMPLAINS_DATE = "2016-08-26 08:59:21";
  private static final String COMPLAINS_COMMENT = "Comment inappropriate";
  private static final String CREATED_AT = "2016-08-25 10:59:41";
  private static final String PATH_ICON = new File("/public/images/modules/icons/TestSave.png").getPath();
  private static final String LANGUAGE = "java";
  private static final String NAME = "TestSave";
  private static final int RATING_VOTE = 3;
  private static final String RATING_COMMENT = "Comment rating";
  private static final String STATUS = "developing";
  private static final String STATUS_CALCULATE = "private";
  private static final int TOPOLOGIES_COUNT = 4;
  private static final String TYPE = "spout";
  private static final String UPDATE_AT = "2016-08-25 15:31:14";
  private static final String VERSION_ID1 = "57ad72bde1c0801a58324247";
  private static final String VERSION_ID2 = "57ad7aaae1c0801a5832424f";
  
  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @BeforeClass
  public static void setUp() throws Exception {
    doMongoImport(TestData.USERS_COLLECTION, TestData.USERS_JSON_FILE);
    doMongoImport(TestData.MODULE_VERSIONS_COLLECTION, TestData.MODULE_VERSIONS_JSON_FILE);
    doMongoImport(TestData.MODULES_COLLECTION, TestData.MODULES_JSON_FILE);
  }
  
  @Test
  public void test01NewModuleWithDBObject() throws SinfonierException {
    // Prepare inputs
    DBCollection collection = MongoFactory.getDB().getCollection(TestData.MODULES_COLLECTION);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId("57ad73e8e1c0801a5832424b"));
    DBObject dbObject = collection.findOne(query);
    
    // Run method
    Module result = new Module(dbObject);
    
    // Check result
    assertNotNull(result);
    assertEquals(3.5, result.getAverageRate(), TestData.OFFSET_DOUBLE);
    assertEquals("test1@test.com", result.getAuthor().getId());
    assertEquals("test1@test.com", result.getAuthorId());
    assertEquals("Bolts", result.getCategory());
    assertEquals(0, result.getComplains().size());
    assertEquals("2016-08-12 08:59:52", formatter.format(result.getCreatedAt()));
    assertNull(result.getIcon());
    assertEquals("java", result.getLanguage());
    assertEquals("MyBolt", result.getName());
    assertEquals(2, result.getRatings().size());
    assertEquals("published", result.getStatus());
    assertEquals(9, result.getTopologiesCount());
    assertEquals("bolt", result.getType());
    assertEquals("2016-08-12 09:01:51", formatter.format(result.getUpdatedAt()));
    assertEquals(1, result.getVersions().size());
  }
  
  @Test
  public void test02NewModule() throws SinfonierException {
    // Run method
    Module result = new Module();
    
    // Check result
    assertNotNull(result);
    assertNull(result.getName());
    assertEquals("spout", result.getType());
    assertEquals("Spouts", result.getCategory());
    assertEquals("java", result.getLanguage());
    assertEquals("developing", result.getStatus());
    assertNotNull(result.getVersions());
  }
  
  @Test
  public void test03GetModulesUser1() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test1@test.com", false);
    
    // Run method
    List<Module> result = Module.getModules(mockUser, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(4, result.size());
  }
  
  @Test
  public void test04GetModulesUser2() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test1@test.com", true);
    
    // Run method
    List<Module> result = Module.getModules(mockUser, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(6, result.size());
  }
  
  @Test
  public void test05GetModulesUserAndSharing1() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test1@test.com", false);
    
    // Run method
    List<Module> result = Module.getModules(mockUser, false, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(3, result.size());
  }
  
  @Test
  public void test06GetModulesUserAndSharing2() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test1@test.com", false);
    
    // Run method
    List<Module> result = Module.getModules(mockUser, true, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(4, result.size());
  }
  
  @Test
  public void test07GetModulesUserAndStatus() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test@test.com", false);
    
    // Run method
    List<Module> result = Module.getModules(mockUser, "private", null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(1, result.size());
  }
  
  @Test
  public void test08GetModulesUserAndStatus2() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test@test.com", false);
    
    // Run method
    List<Module> result = Module.getModules(mockUser, "published", null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(0, result.size());
  }
  
  @Test
  public void test09LastModuleVersion() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyDrain");
    
    // Run method
    ModuleVersion result = obj.lastModuleVersion();
    
    // Check result
    assertNotNull(result);
    assertEquals("2.0", result.getVersionTag());
    assertEquals(2, result.getVersionCode());
    assertEquals("published", result.getStatus());
  }
  
  @Test
  public void test10GetModuleVersion() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyDrain");
    
    // Run method
    ModuleVersion result = obj.getModuleVersion(1);
    
    // Check result
    assertNotNull(result);
    assertEquals("1.0", result.getVersionTag());
    assertEquals(1, result.getVersionCode());
    assertEquals("published", result.getStatus());
  }
  
  @Test
  public void test11GetNextVersionCode() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyDrain");
    
    // Run method
    int result = obj.getNextVersionCode();
    
    // Check result
    assertEquals(3, result);
  }
  
  @Test
  public void test12CountByUser() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test@test.com", false);
    
    // Run method
    long result = Module.countByUser(mockUser);
    
    // Check result
    assertEquals(1, result);
  }
  
  @Test
  public void test13FindByName() throws SinfonierException {
    // Run method
    Module result = Module.findByName("ModuloFirefox");
    
    // Check result
    assertNotNull(result);
    assertEquals("ModuloFirefox", result.getName());
    assertEquals("spout", result.getType());
    assertEquals("python", result.getLanguage());
  }
  
  @Test
  public void test14FindByIdObjectId() throws SinfonierException {
    // Run method
    Module result = Module.findById(new ObjectId("57b40a65b902ae2bb5bd704f"));
    
    // Check result
    assertNotNull(result);
    assertEquals("ModuloFirefox", result.getName());
    assertEquals("spout", result.getType());
    assertEquals("python", result.getLanguage());
  }
  
  @Test
  public void test15FindByIdString() throws SinfonierException {
    // Run method
    Module result = Module.findById("57b40a65b902ae2bb5bd704f");
    
    // Check result
    assertNotNull(result);
    assertEquals("ModuloFirefox", result.getName());
    assertEquals("spout", result.getType());
    assertEquals("python", result.getLanguage());
  }
  
  @Test
  public void test16Find1() throws SinfonierException {
    User user = DarwinFactory.getInstance().loadUser("test1@test.com");
    // Prepare inputs
    ModuleSearch search = new ModuleSearch();
    search.setType("drain");
    
    // Run method
    List<Module> result = Module.find(search, user, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(1, result.size());
  }
  
  @Test
  public void test17Find2() throws SinfonierException {
    User user = DarwinFactory.getInstance().loadUser("test1@test.com");
    // Prepare inputs
    ModuleSearch search = new ModuleSearch();
    search.setName("MyDrain");
    search.setType("drain");
    search.setOwner("test1");
    
    // Run method
    List<Module> result = Module.find(search, user, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(1, result.size());
  }
  
  @Test
  public void test18Find3() throws SinfonierException {
    User user = DarwinFactory.getInstance().loadUser("test1@test.com");
    // Prepare inputs
    ModuleSearch search = new ModuleSearch();
    search.setName("My");
    
    // Run method
    List<Module> result = Module.find(search, user, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(3, result.size());
  }
  
  @Test
  public void test19Find4() throws SinfonierException {
    User user = DarwinFactory.getInstance().loadUser("test@test.com");
    // Prepare inputs
    ModuleSearch search = new ModuleSearch();
    search.setOwner("test.com");
    
    // Run method
    List<Module> result = Module.find(search, user, null).getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(4, result.size());
  }
  
  @Test
  public void test20GetTopModules() throws SinfonierException {
    // Run method
    List<Module> result = Module.getTopModules().getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("MyBolt", result.get(0).getName());
    assertEquals("MyDrain", result.get(1).getName());
  }
  
  @Test
  public void test21GetTopUsed() throws SinfonierException {
    // Run method
    List<Module> result = Module.getTopUsed().getModules();
    
    // Check result
    assertNotNull(result);
    assertEquals(4, result.size());
    assertEquals("MyDrain", result.get(0).getName());
    assertEquals("MyBolt", result.get(1).getName());
    assertEquals("MySpout", result.get(2).getName());
    assertEquals("ModuloFirefox", result.get(3).getName());
  }

  @Test
  public void test22Save() throws SinfonierException, ParseException {    
    // Prepare inputs
    Module obj = new Module();
    obj.setAuthorId(AUTHOR_ID);
    obj.setAverageRate(AVERAGE_RATE);
    obj.setCategory(CATEGORY);
    
    Complains complains = new Complains();    
    User mockUser = TestMock.mockUser(USER_EMAIL, false);
    complains.add(new Inappropriate(mockUser, formatter.parse(COMPLAINS_DATE), COMPLAINS_COMMENT));
    obj.setComplains(complains);
    obj.setCreatedAt(formatter.parse(CREATED_AT));
    obj.setIcon(new File("same/path.png"));
    obj.setLanguage(LANGUAGE);
    obj.setName(NAME);
    
    Ratings ratings = new Ratings();
    ratings.add(new Rating(mockUser, RATING_VOTE, RATING_COMMENT));
    obj.setRatings(ratings);
    obj.setStatus(STATUS);
    obj.setTopologiesCount(TOPOLOGIES_COUNT);
    obj.setType(TYPE);
    obj.setUpdatedAt(formatter.parse(UPDATE_AT));
    
    Versions versions = new Versions();
    ModuleVersion moduleVersion1 = ModuleVersion.findById(VERSION_ID1);
    Version version1 = new Version(moduleVersion1);
    version1.setVersionTag(moduleVersion1.getVersionTag());
    versions.add(version1);
    
    ModuleVersion moduleVersion2 = ModuleVersion.findById(VERSION_ID2);
    Version version2 = new Version(moduleVersion2);
    versions.add(version2);
    obj.setVersions(versions);
    
    // Run method
    obj.save();
    
    // Check result
    Module result = Module.findById(obj.getId());
    assertNotNull(result);
    assertEquals(AUTHOR_ID, result.getAuthorId());
    assertEquals(AVERAGE_RATE, result.getAverageRate(), TestData.OFFSET_DOUBLE);
    assertEquals(CATEGORY, result.getCategory());
    
    Inappropriate resultComplains = result.getComplains().getComplains().get(0);
    assertEquals(USER_EMAIL, resultComplains.getUserId());
    assertEquals(COMPLAINS_DATE, formatter.format(resultComplains.getTimestamp()));
    assertEquals(COMPLAINS_COMMENT, resultComplains.getComment());
    
    assertEquals(CREATED_AT, formatter.format(result.getCreatedAt()));
    assertEquals(PATH_ICON, result.getIcon().getPath());
    assertEquals(LANGUAGE, result.getLanguage());
    assertEquals(NAME, result.getName());
    
    Rating resultRating = result.getRatings().getRatings().get(0);
    assertEquals(USER_EMAIL, resultRating.getUserId());
    assertEquals(RATING_VOTE, resultRating.getRate());
    assertEquals(RATING_COMMENT, resultRating.getComment());
    
    assertEquals(STATUS_CALCULATE, result.getStatus());
    assertEquals(TOPOLOGIES_COUNT, result.getTopologiesCount());
    assertEquals(TYPE, result.getType());
    assertEquals(UPDATE_AT, formatter.format(result.getUpdatedAt()));
    
    Version resultVersion1 = result.getVersions().getItem(1);
    assertEquals(1, resultVersion1.getVersionCode());
    assertEquals(VERSION_ID1, resultVersion1.getModuleVersionId());
    Version resultVersion2 = result.getVersions().getVersions().get(1);
    assertEquals(3, resultVersion2.getVersionCode());
    assertEquals(VERSION_ID2, resultVersion2.getModuleVersionId());
  }
  
  @Test
  public void test23Remove() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findById("57ce8820ba787b7edd7c8cab");
    
    // Run method
    obj.remove();
    
    // Check result
    Module result = Module.findById("57ce8820ba787b7edd7c8cab");
    assertNull(result);
  }
  
  @Test
  public void test24UpdateTopologiesCount() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName(NAME);
    obj.setTopologiesCount(12);
    double averageRate = obj.getAverageRate();
    obj.setAverageRate(4.9);
    
    // Run method
    obj.updateTopologiesCount();
    
    // Check result
    Module result = Module.findByName(NAME);
    assertNotNull(result);
    assertEquals(12, result.getTopologiesCount());
    assertEquals(averageRate, result.getAverageRate(), TestData.OFFSET_DOUBLE);
  }
  
  @Test
  public void test25HasWritePermission1() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("test1@test.com", false);

    // Run method
    boolean result = obj.hasWritePermission(mockUser);
    
    // Check result
    assertTrue(result);
  }
  
  @Test
  public void test25HasWritePermission2() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("admin@domain.es", true);

    // Run method
    boolean result = obj.hasWritePermission(mockUser);
    
    // Check result
    assertTrue(result);
  }
  
  @Test
  public void test25HasWritePermission3() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("other@domain.es", false);

    // Run method
    boolean result = obj.hasWritePermission(mockUser);
    
    // Check result
    assertFalse(result);
  }
  
  @Test
  public void test26HasReadPermission1() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("test1@test.com", false);

    // Run method
    boolean result = obj.hasReadPermission(obj.getModuleVersion(1), mockUser);
    
    // Check result
    assertTrue(result);
  }
  
  @Test
  public void test26HasReadPermission2() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("admin@domain.es", true);

    // Run method
    boolean result = obj.hasReadPermission(obj.getModuleVersion(1), mockUser);
    
    // Check result
    assertTrue(result);
  }
  
  @Test
  public void test26HasReadPermission3() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("other@domain.es", false);

    // Run method
    boolean result = obj.hasReadPermission(obj.getModuleVersion(1), mockUser);
    
    // Check result
    assertTrue(result);
  }
  
  @Test
  public void test26HasReadPermission4() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MySpout");
    User mockUser = TestMock.mockUser("other@domain.es", false);

    // Run method
    boolean result = obj.hasReadPermission(obj.getModuleVersion(3), mockUser);
    
    // Check result
    assertFalse(result);
  }
  
  @Test
  public void test27AddRate1() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("other@domain.es", false);
    Rating rating = new Rating(mockUser, 3, RATING_COMMENT);
    
    // Run method
    obj.addRate(rating);
    
    // Check result
    Module result = Module.findByName("MyBolt");
    assertNotNull(result);
    assertEquals(3, result.getRatings().size());
    assertEquals(3.3333, result.getAverageRate(), TestData.OFFSET_DOUBLE);
    assertEquals("other@domain.es", result.getRatings().getRatings().get(2).getUserId());
    assertEquals(RATING_COMMENT, result.getRatings().getRatings().get(2).getComment());
    assertEquals(3, result.getRatings().getRatings().get(2).getRate());
  }
  
  @Test
  public void test27AddRate2() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyDrain");
    User mockUser = TestMock.mockUser("other@domain.es", false);
    Rating rating = new Rating(mockUser, 4, RATING_COMMENT);
    
    // Run method
    obj.addRate(rating);
    
    // Check result
    Module result = Module.findByName("MyDrain");
    assertNotNull(result);
    assertEquals(1, result.getRatings().size());
    assertEquals(4, result.getAverageRate(), TestData.OFFSET_DOUBLE);
    assertEquals("other@domain.es", result.getRatings().getRatings().get(0).getUserId());
    assertEquals(RATING_COMMENT, result.getRatings().getRatings().get(0).getComment());
    assertEquals(4, result.getRatings().getRatings().get(0).getRate());
  }
  
  @Test
  public void test28AddComplain1() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("ModuloFirefox");
    User mockUser = TestMock.mockUser("other@domain.es", false);
    Inappropriate inappropriate = new Inappropriate(mockUser, COMPLAINS_COMMENT);
    
    // Run method
    obj.addComplain(inappropriate);
    
    // Check result
    Module result = Module.findByName("ModuloFirefox");
    assertNotNull(result);
    assertEquals(5, result.getComplains().size());
    assertEquals("other@domain.es", result.getComplains().getComplains().get(4).getUserId());
    assertEquals(COMPLAINS_COMMENT, result.getComplains().getComplains().get(4).getComment());
  }
  
  @Test
  public void test28AddComplain2() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("other@domain.es", false);
    Inappropriate inappropriate = new Inappropriate(mockUser, COMPLAINS_COMMENT);
    
    // Run method
    obj.addComplain(inappropriate);
    
    // Check result
    Module result = Module.findByName("MyBolt");
    assertNotNull(result);
    assertEquals(1, result.getComplains().size());
    assertEquals("other@domain.es", result.getComplains().getComplains().get(0).getUserId());
    assertEquals(COMPLAINS_COMMENT, result.getComplains().getComplains().get(0).getComment());
  }
  
  @Test
  public void test29IsOwner1() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("other@domain.es", false);
    
    // Run method
    boolean result = obj.isOwner(mockUser);
    
    // Check result
    assertFalse(result);
  }
  
  @Test
  public void test29IsOwner2() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    User mockUser = TestMock.mockUser("test1@test.com", false);
    
    // Run method
    boolean result = obj.isOwner(mockUser);
    
    // Check result
    assertTrue(result);
  }
  
  @Test
  public void test29IsOwner3() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    
    // Run method
    boolean result = obj.isOwner(null);
    
    // Check result
    assertFalse(result);
  }
  
  @Test
  public void test30ToDBObject() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    
    // Run method
    DBObject result = obj.toDBObject();
    
    // Check result
    assertNotNull(result);
    assertEquals(new ObjectId("57ad73e8e1c0801a5832424b"), result.get(FIELD_ID));
    assertEquals("MyBolt", result.get(FIELD_NAME));
    assertEquals("bolt", result.get(FIELD_TYPE));
    assertEquals("Bolts", result.get(FIELD_CATEGORY));
    assertEquals("published", result.get(FIELD_STATUS));
    assertEquals("java", result.get(FIELD_LANGUAGE));
    assertEquals("2016-08-12 08:59:52", formatter.format(result.get(FIELD_CREATED)));
    assertEquals("2016-08-12 09:01:51", formatter.format(result.get(FIELD_UPDATED)));
    assertEquals("test1@test.com", result.get(FIELD_AUTHOR_ID));
    // Calculated in a previous test
    assertEquals(3.3333, (Double) result.get(FIELD_AVERAGE_RATE), TestData.OFFSET_DOUBLE);
    assertEquals(9, result.get(FIELD_TOPOLOGIES_COUNT));
    assertEquals("", result.get(FIELD_ICON));
    assertEquals(3, ((BasicDBList) result.get(FIELD_RATINGS)).size());
    // Added in a previous test
    assertEquals(1, ((BasicDBList) result.get(FIELD_COMPLAINS)).size());
    assertEquals(1, ((BasicDBList) result.get(FIELD_VERSIONS)).size());
  }
  
  @Test
  public void test31GetAuthor() throws SinfonierException {
    // Prepare inputs
    Module obj = Module.findByName("MyBolt");
    
    // Run method
    User result = obj.getAuthor();
    
    // Check result
    assertNotNull(result);
    assertEquals("test1@test.com", result.getId());
  }
  
  @Test
  public void test32GetCategoryFromType() throws SinfonierException {
    // Run method
    String result = Module.getCategoryFromType("test");
    
    // Check result
    assertNotNull(result);
    assertEquals("Tests", result);
  }
  
  @Test
  public void test33GetTypeFromCategory() throws SinfonierException {
    // Run method
    String result = Module.getTypeFromCategory("Tests");
    
    // Check result
    assertNotNull(result);
    assertEquals("test", result);
  }
  
  @AfterClass
  public static void tearDown() throws Exception {
    doMongoDrop(TestData.USERS_COLLECTION);
    doMongoDrop(TestData.MODULES_COLLECTION);
    doMongoDrop(TestData.MODULE_VERSIONS_COLLECTION);
  }
}
