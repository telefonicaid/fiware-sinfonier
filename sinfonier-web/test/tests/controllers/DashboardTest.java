package tests.controllers;


import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.mvc.Http;
import tests.BaseTestFunctional;
import tests.TestData;

public class DashboardTest extends BaseTestFunctional{

  private static Http.Request request;

  @BeforeClass
  public static void setUp() throws Exception {
    doMongoImport(TestData.USERS_COLLECTION, TestData.USERS_JSON_FILE);
    doMongoImport(TestData.MODULE_VERSIONS_COLLECTION, TestData.MODULE_VERSIONS_JSON_FILE);
    doMongoImport(TestData.MODULES_COLLECTION, TestData.MODULES_JSON_FILE);
    request = doLogin("test@test.com", "test");
  }

  @Test
  public void index() {
    Http.Response response = GET(request, "/dashboard");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }


  @AfterClass
  public static void tearDown() throws Exception {
    doLogout();
    doMongoDrop(TestData.USERS_COLLECTION);
    doMongoDrop(TestData.MODULES_COLLECTION);
    doMongoDrop(TestData.MODULE_VERSIONS_COLLECTION);
  }
}
