package tests.controllers;


import java.io.IOException;
import java.lang.reflect.Field;

import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import controllers.Topologies;
import models.storm.Client;
import play.mvc.Http;
import tests.BaseTestFunctional;
import tests.TestData;
import tests.TestMock;


public class TopologiesTest extends BaseTestFunctional {

  private static Http.Request request;

  @BeforeClass
  public static void beforeClass() throws Exception {
    doMongoImport(TestData.USERS_COLLECTION, TestData.USERS_JSON_FILE);
    doMongoImport(TestData.MODULES_COLLECTION, TestData.MODULES_JSON_FILE);
    doMongoImport(TestData.MODULE_VERSIONS_COLLECTION, TestData.MODULE_VERSIONS_JSON_FILE);
    doMongoImport(TestData.TOPOLOGIES_COLLECTION, TestData.TOPOLOGIES_JSON_FILE);
    mockClient();
  }

  public static void mockClient() {
    Client client =  TestMock.mockTopologiesClient();
    if (client != null) {
      try {
        Field field = Topologies.class.getDeclaredField("client");
        field.setAccessible(true);
        field.set(null, client);
      } catch (Exception e) {
        e.printStackTrace();
      } 
    }
  }
  
  public static void unmockClient() {
    try {
      Field field = Topologies.class.getDeclaredField("client");
      field.setAccessible(true);
      field.set(null, Client.getInstance());
    } catch (Exception e) {
      e.printStackTrace();
    };
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

  @Test
  public void index_01() {
    request.headers.put("x-requested-with", new Http.Header("x-requested-with", "XMLHttpRequest"));
    Http.Response response = GET(request, "/topologies");
    assertIsOk(response);
    assertContentType("application/json", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void index_02() {
    Http.Response response = GET(request, "/topologies/0");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element topologies = doc.getElementById("topologies");
    Elements topologyElements = topologies.getElementsByClass("topology");
    assertTrue(topologyElements.size() == 3);
  }

  @Test
  public void topology_01() {
    Http.Response response = GET(request, "/topologies/TopologyTwo");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void topology_02() {
    Http.Response response = GET(request, "/topologies/TopologyTwoooo");
    assertIsNotFound(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }

  @Test
  public void search_01() {
    Http.Response response = GET(request, "/topologies/search?status=active&query=&updated=");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element topologies = doc.getElementById("topologies");
    Elements topologyElements = topologies.getElementsByClass("topology");
    assertTrue(topologyElements.size() == 3);
  }

  @Test
  public void search_02() {
    Http.Response response = GET(request, "/topologies/search?status=running&query=&updated=");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
    Document doc = Jsoup.parse(response.out.toString());
    Element topologies = doc.getElementById("topologies");
    Elements topologyElements = topologies.getElementsByClass("topology");
    assertTrue(topologyElements.size() == 0);
  }

  @Test
  public void info_01() {
    Http.Response response = GET(request, "/topologies/57becd7cd5c4fa520ae593ae/info");
    assertIsOk(response);
    assertContentType("application/json", response);
    JsonParser parser = new JsonParser();
    JsonObject json = parser.parse(response.out.toString()).getAsJsonObject();
    assertTrue("ACTIVE".equals(json.get("data").getAsJsonObject().get("status").getAsString()));
  }

  @AfterClass
  public static void afterClass() throws Exception {
    doMongoDrop(TestData.USERS_COLLECTION);
    doMongoDrop(TestData.TOPOLOGIES_COLLECTION);
    doMongoDrop(TestData.MODULE_VERSIONS_COLLECTION);
    doMongoDrop(TestData.MODULES_COLLECTION);
    unmockClient();
  }
}
