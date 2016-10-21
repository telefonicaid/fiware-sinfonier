package tests.controllers;


import org.junit.*;
import play.mvc.Http;
import play.test.CleanTest;
import tests.BaseTestFunctional;
import tests.TestData;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class DrawersTest extends BaseTestFunctional {

  private static Http.Request request;

  @BeforeClass
  public static void beforeClass() throws Exception {
    doMongoImport(TestData.USERS_COLLECTION, TestData.USERS_JSON_FILE);
    doMongoImport(TestData.MODULE_VERSIONS_COLLECTION, TestData.MODULE_VERSIONS_JSON_FILE);
    doMongoImport(TestData.MODULES_COLLECTION, TestData.MODULES_JSON_FILE);
  }

  @Before
  public void setUp() throws IOException, ParserConfigurationException {
    request = doLogin("test@test.com", "test");
  }

  @Test
  public void index() {
    Http.Response response = GET(request, "/drawer");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void module_01() {
    Http.Response response = GET(request, "/drawer/modules/MyBolt/versions/1/json");
    assertIsOk(response);
    assertContentType("application/json", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void module_02() {
    Http.Response response = GET(request, "/drawer/modules/MyBolttt/versions/1/json");
    assertIsNotFound(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }


  @Test
  public void module_03() {
    Http.Response response = GET(request, "/drawer/modules/MyBolt/versions/10/json");
    assertIsNotFound(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @After
  public void tearDown() throws IOException, ParserConfigurationException {
    doLogout();
    request = null;
  }

  @AfterClass
  public static void afterClass() throws Exception {
    doMongoDrop(TestData.USERS_COLLECTION);
    doMongoDrop(TestData.MODULES_COLLECTION);
    doMongoDrop(TestData.MODULE_VERSIONS_COLLECTION);
  }
}
