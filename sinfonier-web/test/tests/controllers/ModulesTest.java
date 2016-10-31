package tests.controllers;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.*;
import play.mvc.Http;
import tests.BaseTestFunctional;
import tests.TestData;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ModulesTest extends BaseTestFunctional {

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
  public void index_01() {
    Http.Response response = GET(request, "/modules");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void index_02() {
    Http.Response response = GET(request, "/modules/20");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element modules = doc.getElementById("modules");
    Element row = modules.getElementsByClass("row").first();
    for (Element element : row.children()) {
      assertTrue("Number of children should be 0.", element.children().size() == 0);
    }
  }

  @Test
  public void index_03() {
    Http.Response response = GET(request, "/modules/0?indexMode=myModules");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element modules = doc.getElementById("modules");
    Element row = modules.getElementsByClass("row").first();
    Integer count = 0;
    for (Element element : row.children()) {
      count += element.children().size();
    }
    assertTrue("Count should be 1.", count == 1);
  }

  @Test
  public void index_04() {
    Http.Response response = GET(request, "/modules/search?search.name=ModuloSpout&search.owner=&search.type=");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element modules = doc.getElementById("modules");
    Element row = modules.getElementsByClass("row").first();
    Integer count = 0;
    for (Element element : row.children()) {
      count += element.children().size();
    }
    assertTrue("Count should be 1.", count == 1);
  }

  @Test
  public void index_05() {
    Http.Response response = GET(request, "/modules/search?search.name=ModuloSpoutt&search.owner=Smith&search.type=");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element modules = doc.getElementById("modules");
    Element row = modules.getElementsByClass("row").first();
    Integer count = 0;
    for (Element element : row.children()) {
      count += element.children().size();
    }
    assertTrue("Count should be 0.", count == 0);
  }

  @Test
  public void index_06() {
    Http.Response response = GET(request, "/modules/ModuloSpout/versions/1/edit");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void index_07() {
    Http.Response response = GET(request, "/modules/ModuloSpout/versions/1");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void index_08() {
    Http.Response response = GET(request, "/modules/ModuloSpout");
    assertIsOk(response);
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
