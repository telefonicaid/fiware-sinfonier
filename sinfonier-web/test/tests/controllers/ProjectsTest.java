package tests.controllers;


import exceptions.SinfonierException;
import models.SinfonierConstants;
import models.topology.Topology;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.*;
import play.mvc.Http;
import tests.BaseTestFunctional;
import tests.TestData;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;


public class ProjectsTest extends BaseTestFunctional {

  private static Http.Request request;

  @BeforeClass
  public static void beforeClass() throws Exception {
    doMongoReset(TestData.USERS_COLLECTION, TestData.USERS_JSON_FILE);
    doMongoReset(TestData.MODULES_COLLECTION, TestData.MODULES_JSON_FILE);
    doMongoReset(TestData.MODULE_VERSIONS_COLLECTION, TestData.MODULE_VERSIONS_JSON_FILE);
    doMongoReset(TestData.TOPOLOGIES_COLLECTION, TestData.TOPOLOGIES_JSON_FILE);
    doMongoReset(TestData.PROJECTS_COLLECTION, TestData.PROJECTS_JSON_FILE);
  }

  @Before
  public void setUp() throws IOException, ParserConfigurationException {
    request = doLogin("test@test.com", "test");
  }

  @After
  public void tearDown() throws IOException, ParserConfigurationException {
    doLogout();
    request = null;
  }
  
  @AfterClass
  public static void afterClass() throws Exception {
    doMongoDrop(TestData.USERS_COLLECTION);
    doMongoDrop(TestData.TOPOLOGIES_COLLECTION);
    doMongoDrop(TestData.MODULE_VERSIONS_COLLECTION);
    doMongoDrop(TestData.MODULES_COLLECTION);
    doMongoDrop(TestData.PROJECTS_COLLECTION);
  }


  @Test
  public void index_01() {
    Http.Response response = GET(request, "/projects");
    assertIsOk(response);
    //assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element projects = doc.getElementById("projects");
    Elements projectElements = projects.getElementsByClass("project");
    assertTrue(projectElements.size() == 5);
  }

  
  @Test
  public void project_01() {
    Http.Response response = GET(request, "/projects/LaCosaNoVa");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }
  
  @Test
  public void project_02() {
    Http.Response response = GET(request, "/projects/Unexistent");
    assertIsNotFound(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }
  
  @Test
  public void search_01() {
    Http.Response response = GET(request, "/projects/search?search.name=Nuevo&search.owner=");
    assertIsOk(response);
    Document doc = Jsoup.parse(response.out.toString());
    Element projects = doc.getElementById("projects");
    Elements projectElements = projects.getElementsByClass("project");
    assertTrue(projectElements.size() == 1);
  }
  @Test
  public void search_02() {
    Http.Response response = GET(request, "/projects/search?search.name=Unexistent&search.owner=");
    assertIsOk(response);
    Document doc = Jsoup.parse(response.out.toString());
    Element projects = doc.getElementById("projects");
    Elements projectElements = projects.getElementsByClass("project");
    assertTrue(projectElements.size() == 0);
  }
  
  @Test
  public void search_03() {
    Http.Response response = GET(request, "/projects/search?search.name=&search.owner=test1@test.com");
    assertIsOk(response);
    Document doc = Jsoup.parse(response.out.toString());
    Element projects = doc.getElementById("projects");
    Elements projectElements = projects.getElementsByClass("project");
    assertTrue(projectElements.size() == 2);
  }
  
  @Test
  public void search_04() {
    Http.Response response = GET(request, "/projects/search?search.name=Unexistent&search.owner=unexistent");
    assertIsOk(response);
    Document doc = Jsoup.parse(response.out.toString());
    Element projects = doc.getElementById("projects");
    Elements projectElements = projects.getElementsByClass("project");
    assertTrue(projectElements.size() == 0);
  }


}
