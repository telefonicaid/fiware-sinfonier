package models.module;

import models.Model;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import play.Logger;
import play.data.validation.Required;
import play.data.validation.URL;
import static models.SinfonierConstants.ModuleLibrary.*;

public class Library extends Model {

  @Required(message = "validation.required.library.name")
  private String name;

  @Required(message = "validation.required.library.url")
  @URL
  private String url;

  public Library(String name, String url) {
    this.name = name;
    this.url = url;
  }

  public Library(DBObject o) throws SinfonierException {
    try {
      name = o.get(FIELD_NAME).toString();
      url = o.get(FIELD_URL).toString();
    } catch (Exception e) {
      name = null;
      url = null;
      Logger.error("Exception constructor Library(DBObject) > " + e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public DBObject toDBObject() {
    DBObject out = new BasicDBObject();

    if (name != null && url != null) {
      out.put(FIELD_NAME, this.name);
      out.put(FIELD_URL, this.url);
    }

    return out;
  }
}
