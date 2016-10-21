package tests.controllers;


import org.junit.*;
import play.mvc.Http;
import tests.BaseTestFunctional;
import tests.TestData;

public class ProfileSinfonierTest extends BaseTestFunctional {

  private static Http.Request request;

  @BeforeClass
  public static void beforeClass() throws Exception {
    doMongoImport(TestData.USERS_COLLECTION, TestData.USERS_JSON_FILE);
    doMongoImport(TestData.MODULE_VERSIONS_COLLECTION, TestData.MODULE_VERSIONS_JSON_FILE);
    doMongoImport(TestData.MODULES_COLLECTION, TestData.MODULES_JSON_FILE);
  }

  @Test
  public void index() {
    assertTrue(true);
  }

  @AfterClass
  public static void afterClass() throws Exception {
    doMongoDrop(TestData.USERS_COLLECTION);
    doMongoDrop(TestData.MODULES_COLLECTION);
    doMongoDrop(TestData.MODULE_VERSIONS_COLLECTION);
  }
}
