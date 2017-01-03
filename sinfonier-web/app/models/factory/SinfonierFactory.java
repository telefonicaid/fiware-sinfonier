package models.factory;

import models.user.MongoUser;
import models.user.SinfonierUser;
import models.user.User;

import com.mongodb.DBObject;

public class SinfonierFactory extends MongoFactory {

  @Override
  public User buildUser(String name, String email, String password) {
      return new User(new SinfonierUser(name, email, password));
  }

  @Override
  protected User buildUser(DBObject user) {
      return new User(new SinfonierUser(user));
  }
}
