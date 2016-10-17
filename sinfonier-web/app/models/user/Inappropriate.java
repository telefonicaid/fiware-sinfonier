package models.user;

import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;

import java.util.Date;

import models.SinfonierConstants;
import static models.SinfonierConstants.Inappropriate.*;

public class Inappropriate extends UserTimestamp {
  
  private String comment;
  
  public Inappropriate(User user) {
    super(user);
  }
  
  public Inappropriate(User user, String comment) {
    super(user);
    this.comment = comment;
  }
  
  public Inappropriate(User user, Date timestamp, String comment) {
    super(user, timestamp);
    this.comment = comment;
  }

  public Inappropriate(DBObject o) throws SinfonierException {
    super(o);
    

    try {
      this.comment = o.get(FIELD_COMMENT).toString();
    } catch (Exception e) {
      userId = null;
      timestamp = null;
      comment = null;
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }
  }
  
  @Override
  public DBObject toDBObject() {
    DBObject out = super.toDBObject();

    out.put(SinfonierConstants.Rating.FIELD_COMMENT, this.comment);

    return out;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
