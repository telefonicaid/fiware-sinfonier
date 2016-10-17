package controllers;


import models.factory.MongoFactory;
import models.storm.Client;
import play.Logger;

public class Diagnosis extends WebController {

  private static Client client = Client.getInstance();

  public static void index() {
    boolean apiStatus = client.isOk();
    boolean dbStatus = false;

    try {
      dbStatus = MongoFactory.getDB().getStats().ok();
    } catch (Exception e) {
      Logger.debug(e.getMessage());
      dbStatus = false;
    }

    boolean playStatus = apiStatus && dbStatus;

    render(apiStatus, dbStatus, playStatus);
  }
}
