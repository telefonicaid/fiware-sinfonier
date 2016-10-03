package tests;

import org.junit.Ignore;
import play.test.FunctionalTest;

import java.io.IOException;
import java.net.UnknownHostException;

@Ignore public class BaseTestFunctional extends FunctionalTest {

  protected static void doMongoImport(String collectionName, String jsonFilePath) throws UnknownHostException, IOException {
    BaseTest.doMongoImport(collectionName, jsonFilePath);
  }
  
  protected static void doMongoDrop(String collectionName) {
    BaseTest.doMongoDrop(collectionName);
  }
}
