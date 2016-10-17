package tests;

import models.factory.MongoFactory;

import org.junit.Ignore;

import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;

import play.test.UnitTest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.util.JSON;

@Ignore public class BaseTest extends UnitTest {

  protected static void doMongoImport(String collectionName, String jsonFilePath) throws UnknownHostException, IOException {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    
    JsonParser parser = new JsonParser();
    FileReader reader = new FileReader(jsonFilePath);
    JsonElement jsonElement = parser.parse(reader);

    BasicDBList dbList = (BasicDBList) JSON.parse(jsonElement.toString());

    int i;
    for (i=0; i < dbList.size(); i++) {
      collection.insert((DBObject) dbList.get(i));
    }
  }
  
  protected static void doMongoDrop(String collectionName) {
    DBCollection collection = MongoFactory.getDB().getCollection(collectionName);
    
    collection.drop();
  }
}
