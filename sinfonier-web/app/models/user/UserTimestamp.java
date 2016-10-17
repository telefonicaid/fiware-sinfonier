package models.user;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import static models.SinfonierConstants.UserTimestamp.*;

import java.util.Date;

public abstract class UserTimestamp {

  protected String userId;
  protected Date timestamp;

  public UserTimestamp(User user) {
    this(user, new Date());
  }
  
  public UserTimestamp(User user, Date timestamp) {
    this(user.getId(), timestamp);
  }
  
  public UserTimestamp(String userId, Date timestamp) {
    this.userId = userId;
    this.timestamp = timestamp;
  }

  public UserTimestamp(DBObject o) throws SinfonierException {
    try {
      this.userId = o.get(FIELD_USER_ID).toString();
      this.timestamp = ((Date) o.get(FIELD_TIMESTAMP));
    } catch (Exception e) {
      userId = null;
      timestamp = null;
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }

  }

  public DBObject toDBObject() {
    DBObject out = new BasicDBObject();

    if (userId != null && timestamp != null) {
      out.put(FIELD_USER_ID, this.userId);
      out.put(FIELD_TIMESTAMP, this.timestamp);
    }

    return out;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
  
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserTimestamp userTimestamp = (UserTimestamp) o;

    return userId.equals(userTimestamp.getUserId());
  }
  
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }


  public Date getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }
}
