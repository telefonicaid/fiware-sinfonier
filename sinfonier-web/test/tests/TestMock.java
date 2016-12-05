package tests;

import static org.mockito.Mockito.mock;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import controllers.Topologies;
import exceptions.SinfonierException;
import models.storm.Client;
import models.user.User;

public class TestMock {

  public static User mockUser(String id, boolean isAdmin) {
    User mockUser = mock(User.class);
    when(mockUser.getId()).thenReturn(id);
    when(mockUser.isAdminUser()).thenReturn(isAdmin);
    
    return mockUser;
  }
  
  public static Client mockTopologiesClient() {
    Client mockClient = mock(Client.class);
    JsonParser parser = new JsonParser();
    try {
      FileReader reader = new FileReader(TestData.TOPOLOGY_INFO_JSON_FILE);
      JsonElement jsonElement = parser.parse(reader);

      when(mockClient.getTopologyInfo(any(String.class))).thenReturn(jsonElement.getAsJsonObject());
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } 
    return mockClient;
  }

}
