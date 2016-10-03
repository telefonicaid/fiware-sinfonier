package tests.models.module;

import static models.SinfonierConstants.ModelCollection.FIELD_ID;
import static models.SinfonierConstants.Module.FIELD_RATINGS;
import static models.SinfonierConstants.Module.FIELD_TOPOLOGIES_COUNT;
import static models.SinfonierConstants.ModuleVersion.FIELD_BUILD_STATUS;
import static models.SinfonierConstants.ModuleVersion.FIELD_CONTAINER;
import static models.SinfonierConstants.ModuleVersion.FIELD_CREATED;
import static models.SinfonierConstants.ModuleVersion.FIELD_DESCRIPTION;
import static models.SinfonierConstants.ModuleVersion.FIELD_FIELDS;
import static models.SinfonierConstants.ModuleVersion.FIELD_LIBRARIES;
import static models.SinfonierConstants.ModuleVersion.FIELD_MY_TOOLS;
import static models.SinfonierConstants.ModuleVersion.FIELD_SINGLETON;
import static models.SinfonierConstants.ModuleVersion.FIELD_SOURCE_CODE;
import static models.SinfonierConstants.ModuleVersion.FIELD_SOURCE_CODE_URL;
import static models.SinfonierConstants.ModuleVersion.FIELD_SOURCE_TYPE;
import static models.SinfonierConstants.ModuleVersion.FIELD_STATUS;
import static models.SinfonierConstants.ModuleVersion.FIELD_TICK_TUPLE;
import static models.SinfonierConstants.ModuleVersion.FIELD_UPDATED;
import static models.SinfonierConstants.ModuleVersion.FIELD_VERSION_CODE;
import static models.SinfonierConstants.ModuleVersion.FIELD_VERSION_TAG;
import static models.SinfonierConstants.ModuleContainer.FIELD_XTYPE;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import models.factory.MongoFactory;
import models.module.Container;
import models.module.ElementType;
import models.module.ElementTypeField;
import models.module.ElementTypeFields;
import models.module.Field;
import models.module.Fields;
import models.module.Libraries;
import models.module.Library;
import models.module.Module;
import models.module.ModuleVersion;
import models.module.MyTools;
import models.module.TickTuple;
import models.user.MyTool;
import models.user.User;

import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import exceptions.SinfonierException;
import play.test.PlayJUnitRunner;
import tests.BaseTest;
import tests.TestData;
import tests.TestMock;

@RunWith(PlayJUnitRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ModuleVersionTest extends BaseTest {
  
  private static final String CREATED_AT = "2016-08-29 10:56:42";
  private static final String DESCRIPTION = "Description";
  private static final String FIELD_NAME1 = "field1";
  private static final String FIELD_TYPE1 = "list";
  private static final String FIELD_LABEL1 = "Field 1";
  private static final Boolean FIELD_REQUIRED1 = false;
  private static final Boolean FIELD_WIRABLE1 = false;
  private static final String FIELD_ELEMENT_TYPE_ENUM1 = null;
  private static final String FIELD_ET_NAME = "keyValue";
  private static final String FIELD_ET_TYPE = "combine";
  private static final String FIELD_NAME2 = "field2";
  private static final String FIELD_TYPE2 = "number";
  private static final String FIELD_LABEL2 = "Field 2";
  private static final Boolean FIELD_REQUIRED2 = true;
  private static final Boolean FIELD_WIRABLE2 = true;
  private static final String FIELD_ELEMENT_TYPE_ENUM2 = null;
  private static final String LIBRARY_NAME1 = "lib1";
  private static final String LIBRARY_URL1 = "http://maven.org/loquesea";
  private static final String MYTOOL_USER1 = "user@domain.es";
  private static final String MYTOOL_DATE1 = "2016-08-30 13:06:41";
  private static final Boolean SINGLETON = false;
  private static final String SOURCE_CODE = "dfjakdlfñjakldñfjadñlfajl";
  private static final String SOURCE_CODE_URL = null;
  private static final String SOURCE_TYPE = "template";
  private static final String STATUS = "developing";
  private static final String TICKTUPLE_NAME = "TickTuple";
  private static final String TICKTUPLE_TYPE = "integer";
  private static final String TICKTUPLE_LABEL = "label";
  private static final boolean TICKTUPLE_REQUIRED = true;
  private static final boolean TICKTUPLE_WIRABLE = false;
  private static final int TOPOLOGIES_COUNT = 10;
  private static final String UPDATE_AT = "2016-08-30 12:15:11";
  private static final int VERSION_CODE = 2;
  private static final String VERSION_TAG = "1.1";
  private static final String JSON_PATH = new File("tmp/module/ModuloSpout.json").getPath();

  private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @BeforeClass
  public static void setUp() throws Exception {
    doMongoImport(TestData.MODULE_VERSIONS_COLLECTION, TestData.MODULE_VERSIONS_JSON_FILE);
    doMongoImport(TestData.MODULES_COLLECTION, TestData.MODULES_JSON_FILE);
  }
  
  @Test
  public void test01NewModule() throws SinfonierException {
    // Run method
    ModuleVersion result = new ModuleVersion();
    
    // Check result
    assertNotNull(result);
    assertNull(result.getTickTuple());
    assertEquals(false, result.isSingleton());
    assertEquals("gist", result.getSourceType());
    assertEquals("developing", result.getStatus());
  }
  
  @Test
  public void test02NewModuleWithDBObject() throws SinfonierException {
    // Prepare inputs
    DBCollection collection = MongoFactory.getDB().getCollection(TestData.MODULE_VERSIONS_COLLECTION);
    DBObject query = new BasicDBObject(FIELD_ID, new ObjectId("57ad72bde1c0801a58324247"));
    DBObject dbObject = collection.findOne(query);
    
    // Run method
    ModuleVersion result = new ModuleVersion(dbObject);
    
    // Check result
    assertNotNull(result);
    assertEquals("success", result.getBuildStatus());
    assertEquals("WireIt.FormContainer", result.getContainer().getXType());
    assertEquals("2016-08-12 08:54:53", formatter.format(result.getCreatedAt()));
    assertEquals("Descripción del módulo MySpout. Versión 1.0", result.getDescription());
    assertEquals(0, result.getFields().size());
    assertEquals(0, result.getLibraries().size());
    assertEquals(0, result.getMyTools().size());
    assertTrue(result.getSourceCode().startsWith("/**The MIT License (MIT) "));
    assertEquals("", result.getSourceCodeURL());
    assertEquals("template", result.getSourceType());
    assertEquals("private", result.getStatus());
    assertNull(result.getTickTuple());
    assertEquals(1, result.getTopologiesCount());
    assertEquals("2016-08-12 08:56:43", formatter.format(result.getUpdatedAt()));
    assertEquals(1, result.getVersionCode());
    assertEquals("1.0", result.getVersionTag());
  }
  
  @Test
  public void test03ToDBObject() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad72bde1c0801a58324247");
    
    // Run method
    DBObject result = obj.toDBObject();
    
    // Check result
    assertNotNull(result);
    assertEquals(new ObjectId("57ad72bde1c0801a58324247"), result.get(FIELD_ID));
    assertEquals("1.0", result.get(FIELD_VERSION_TAG));
    assertEquals(1, result.get(FIELD_VERSION_CODE));
    assertEquals("private", result.get(FIELD_STATUS));
    assertEquals("template", result.get(FIELD_SOURCE_TYPE));
    assertTrue(((String) result.get(FIELD_SOURCE_CODE)).startsWith("/**The MIT License (MIT) "));
    assertEquals("", result.get(FIELD_SOURCE_CODE_URL));
    assertEquals("Descripción del módulo MySpout. Versión 1.0", result.get(FIELD_DESCRIPTION));
    assertEquals("2016-08-12 08:54:53", formatter.format(result.get(FIELD_CREATED)));
    assertEquals("2016-08-12 08:56:43", formatter.format(result.get(FIELD_UPDATED)));
    assertEquals(false, result.get(FIELD_SINGLETON));
    assertEquals(1, result.get(FIELD_TOPOLOGIES_COUNT));
    assertNull(result.get(FIELD_TICK_TUPLE));
    assertEquals(0, ((BasicDBList) result.get(FIELD_FIELDS)).size());
    assertEquals(0, ((BasicDBList) result.get(FIELD_LIBRARIES)).size());
    assertEquals(0, ((BasicDBList) result.get(FIELD_MY_TOOLS)).size());
    assertEquals("WireIt.FormContainer", ((DBObject) result.get(FIELD_CONTAINER)).get(FIELD_XTYPE));
    assertEquals("success", result.get(FIELD_BUILD_STATUS));
  }
  
  @Test
  public void test04CountByUserAndStatus1() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test1@test.com", false);
    
    // Run method
    long result = ModuleVersion.countByUserAndStatus(mockUser, "private");
    
    // Check result
    assertEquals(1, result); 
  }
  
  @Test
  public void test04CountByUserAndStatus2() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test1@test.com", false);
    
    // Run method
    long result = ModuleVersion.countByUserAndStatus(mockUser, "published");
    
    // Check result
    assertEquals(4, result); 
  }
  
  @Test
  public void test04CountByUserAndStatus3() throws SinfonierException {
    // Prepare inputs
    User mockUser = TestMock.mockUser("test1@test.com", false);
    
    // Run method
    long result = ModuleVersion.countByUserAndStatus(mockUser, "developing");
    
    // Check result
    assertEquals(1, result); 
  }
  
  @Test
  public void test05FindById1() throws SinfonierException {
    // Run method
    ModuleVersion result = ModuleVersion.findById("57ad7aaae1c0801a5832424f");
    
    // Check result
    assertNotNull(result);
    assertEquals("Descripción del módulo MySpout. Corrección de errores y cambio de los campos. Versión 1.2", 
        result.getDescription());
    assertEquals("developing", result.getStatus());
    assertEquals(3, result.getVersionCode());
    assertEquals("1.2", result.getVersionTag());
  }
  
  @Test
  public void test05FindById2() throws SinfonierException {
    // Run method
    ModuleVersion result = ModuleVersion.findById(new ObjectId("57ad7aaae1c0801a5832424f"));
    
    // Check result
    assertNotNull(result);
    assertEquals("Descripción del módulo MySpout. Corrección de errores y cambio de los campos. Versión 1.2", 
        result.getDescription());
    assertEquals("developing", result.getStatus());
    assertEquals(3, result.getVersionCode());
    assertEquals("1.2", result.getVersionTag());
  }
  
  @Test
  public void test06Save() throws SinfonierException, ParseException {
    // Prepare inputs
    Module module = Module.findByName("MyBolt");
    ModuleVersion obj = new ModuleVersion();
    obj.setCreatedAt(formatter.parse(CREATED_AT));
    obj.setDescription(DESCRIPTION);
    
    Fields fields = new Fields();
    ElementTypeFields etFields = new ElementTypeFields();
    ElementTypeField f1 = new ElementTypeField("string", "key", "Key");
    etFields.add(f1);
    ElementTypeField f2 = new ElementTypeField("string", "value", "Value");
    etFields.add(f2);
    List<Object> separators = new ArrayList<Object>();
    separators.add(false);
    separators.add(false);
    separators.add(false);
    ElementType elementType1 = new ElementType(FIELD_ET_NAME, FIELD_ET_TYPE, etFields , separators );
    Field field1 = new Field(FIELD_NAME1, FIELD_TYPE1, FIELD_LABEL1, FIELD_REQUIRED1, FIELD_WIRABLE1, 
        FIELD_ELEMENT_TYPE_ENUM1, elementType1 );
    fields.add(field1);
    Field field2 = new Field(FIELD_NAME2, FIELD_TYPE2, FIELD_LABEL2, FIELD_REQUIRED2, FIELD_WIRABLE2, 
        FIELD_ELEMENT_TYPE_ENUM2, null);
    fields.add(field2);
    obj.setFields(fields);
    
    Libraries libraries = new Libraries();
    Library library1 = new Library(LIBRARY_NAME1, LIBRARY_URL1);
    libraries.add(library1);
    obj.setLibraries(libraries);
    
    MyTools myTools = new MyTools();
    User mockUser = TestMock.mockUser(MYTOOL_USER1, false);
    MyTool myTool1 = new MyTool(mockUser, formatter.parse(MYTOOL_DATE1));
    myTools.add(myTool1);
    obj.setMyTools(myTools);
    
    obj.setSingleton(SINGLETON);
    obj.setSourceCode(SOURCE_CODE);
    obj.setSourceCodeURL(SOURCE_CODE_URL);
    obj.setSourceType(SOURCE_TYPE);
    obj.setStatus(STATUS);
    
    TickTuple tickTuple = new TickTuple(TICKTUPLE_TYPE, TICKTUPLE_LABEL, TICKTUPLE_REQUIRED, TICKTUPLE_WIRABLE);
    obj.setTickTuple(tickTuple);
    
    obj.setTopologiesCount(TOPOLOGIES_COUNT);
    obj.setUpdatedAt(formatter.parse(UPDATE_AT));
    obj.setVersionCode(VERSION_CODE);
    obj.setVersionTag(VERSION_TAG);
    
    Container container = new Container(module, obj);
    obj.setContainer(container);
    
    // Run method
    obj.save();
    
    // Check result
    ModuleVersion result = ModuleVersion.findById(obj.getId());
    assertNotNull(result);
    assertNull(result.getBuildStatus());
    assertEquals("WireIt.FormContainer", result.getContainer().getXType());
    assertEquals(CREATED_AT, formatter.format(result.getCreatedAt()));
    assertEquals(DESCRIPTION, result.getDescription());
    
    assertEquals(FIELD_NAME1, result.getFields().getFields().get(0).getName());
    assertEquals(FIELD_TYPE1, result.getFields().getFields().get(0).getType());
    assertEquals(FIELD_LABEL1, result.getFields().getFields().get(0).getLabel());
    assertEquals(FIELD_REQUIRED1, result.getFields().getFields().get(0).isRequired());
    assertEquals(FIELD_WIRABLE1, result.getFields().getFields().get(0).isWirable());
    assertEquals(FIELD_ELEMENT_TYPE_ENUM1, result.getFields().getFields().get(0).getElementTypeEnum());
    assertEquals(FIELD_ET_NAME, result.getFields().getFields().get(0).getElementType().getName());
    assertEquals(FIELD_ET_TYPE, result.getFields().getFields().get(0).getElementType().getType());
    assertEquals(FIELD_NAME2, result.getFields().getFields().get(1).getName());
    assertEquals(FIELD_TYPE2, result.getFields().getFields().get(1).getType());
    assertEquals(FIELD_LABEL2, result.getFields().getFields().get(1).getLabel());
    assertEquals(FIELD_REQUIRED2, result.getFields().getFields().get(1).isRequired());
    assertEquals(FIELD_WIRABLE2, result.getFields().getFields().get(1).isWirable());
    assertEquals(FIELD_ELEMENT_TYPE_ENUM2, result.getFields().getFields().get(1).getElementTypeEnum());
    assertNull(result.getFields().getFields().get(1).getElementType());
    
    assertEquals(LIBRARY_NAME1, result.getLibraries().getLibraries().get(0).getName());
    assertEquals(LIBRARY_URL1, result.getLibraries().getLibraries().get(0).getUrl());
    
    assertEquals(MYTOOL_USER1, result.getMyTools().getMyTools().get(0).getUserId());
    assertEquals(MYTOOL_DATE1, formatter.format(result.getMyTools().getMyTools().get(0).getTimestamp()));
    
    assertEquals(SINGLETON, result.isSingleton());
    assertEquals(SOURCE_CODE, result.getSourceCode());
    assertEquals(SOURCE_CODE_URL, result.getSourceCodeURL());
    assertEquals(SOURCE_TYPE, result.getSourceType());
    assertEquals(STATUS, result.getStatus());
    
    assertEquals(TICKTUPLE_NAME, result.getTickTuple().getName());
    assertEquals(TICKTUPLE_TYPE, result.getTickTuple().getType());
    assertEquals(TICKTUPLE_LABEL, result.getTickTuple().getLabel());
    assertEquals(TICKTUPLE_REQUIRED, result.getTickTuple().isRequired());
    assertEquals(TICKTUPLE_WIRABLE, result.getTickTuple().isWirable());
    
    assertEquals(TOPOLOGIES_COUNT, result.getTopologiesCount());
    assertEquals(UPDATE_AT, formatter.format(result.getUpdatedAt()));
    assertEquals(VERSION_CODE, result.getVersionCode());
    assertEquals(VERSION_TAG, result.getVersionTag());    
  }
  
  @Test
  public void test07Remove() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ce8820ba787b7edd7c8caa");
    
    // Run method
    obj.remove();
    
    // Check result
    ModuleVersion result = ModuleVersion.findById("57ce8820ba787b7edd7c8caa");
    assertNull(result);
  }
  
  @Test
  public void test08UpdateTopologiesCount() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad835fe1c0801a58324252");
    obj.setTopologiesCount(21);
    
    // Run method
    obj.updateTopologiesCount();
    
    // Check result
    ModuleVersion result = ModuleVersion.findById("57ad835fe1c0801a58324252");
    assertNotNull(result);
    assertEquals(21, result.getTopologiesCount());
  }
  
  @Test
  public void test09Recheck() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad835fe1c0801a58324252");
    
    // Run method
    obj.recheck();
    
    // Check result
    ModuleVersion result = ModuleVersion.findById("57ad835fe1c0801a58324252");
    assertNotNull(result);
    assertEquals("pending", result.getStatus());
  }
  
  @Test
  public void test10ExportAsJson() throws SinfonierException {
    // Prepare inputs
    Module module = Module.findByName("ModuloSpout");
    ModuleVersion obj = ModuleVersion.findById("57ad835fe1c0801a58324252");
    
    // Run method
    File result = obj.exportAsJson(module);
    
    // Check result
    assertNotNull(result);
    assertEquals(JSON_PATH, result.getPath());
  }
  
  @Test
  public void test11AddToMyTools() throws SinfonierException, ParseException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad835fe1c0801a58324252");
    User mockUser = TestMock.mockUser("user@domain.es", false);
    MyTool tool = new MyTool(mockUser, formatter.parse("2016-07-15 08:53:21"));
    
    // Run method
    obj.addToMyTools(tool);
    
    // Check result
    ModuleVersion result = ModuleVersion.findById("57ad835fe1c0801a58324252");
    assertNotNull(result);
    assertEquals(1, result.getMyTools().size());
    assertEquals("user@domain.es", result.getMyTools().getMyTools().get(0).getUserId());
    assertEquals("2016-07-15 08:53:21", formatter.format(result.getMyTools().getMyTools().get(0).getTimestamp()));
  }
  
  @Test
  public void test12RemoveToMyTools() throws SinfonierException, ParseException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad835fe1c0801a58324252");
    User mockUser = TestMock.mockUser("user@domain.es", false);
    MyTool tool = new MyTool(mockUser, formatter.parse("2016-07-15 08:53:21"));
    
    // Run method
    obj.removeToMyTools(tool);
    
    // Check result
    ModuleVersion result = ModuleVersion.findById("57ad835fe1c0801a58324252");
    assertNotNull(result);
    assertEquals(0, result.getMyTools().size());
  }
  
  @Test
  public void test13IsFromGist1() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad752ae1c0801a5832424c");
    
    // Run method
    boolean result = obj.isFromGist();
    
    // Check result
    assertTrue(result);
  }
  
  @Test
  public void test13IsFromGist2() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad7aaae1c0801a5832424f");
    
    // Run method
    boolean result = obj.isFromGist();
    
    // Check result
    assertFalse(result);
  }
  
  @Test
  public void test14hasFields1() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57ad7aaae1c0801a5832424f");
    
    // Run method
    boolean result = obj.hasFields();
    
    // Check result
    assertTrue(result); 
  }
  
  @Test
  public void test14hasFields2() throws SinfonierException {
    // Prepare inputs
    ModuleVersion obj = ModuleVersion.findById("57b40a65b902ae2bb5bd704e");
    
    // Run method
    boolean result = obj.hasFields();
    
    // Check result
    assertFalse(result); 
  }
  
  @AfterClass
  public static void tearDown() throws Exception {
    doMongoDrop(TestData.MODULES_COLLECTION);
    doMongoDrop(TestData.MODULE_VERSIONS_COLLECTION);
  }
}
