package models.user;

import com.mongodb.DBObject;

import exceptions.SinfonierException;

import java.util.Date;

public class MyTool extends UserTimestamp {

  public MyTool(User user) {
    super(user);
  }

  public MyTool(User user, Date timestamp) {
    super(user, timestamp);
  }

  public MyTool(DBObject o) throws SinfonierException {
    super(o);
  }
}
