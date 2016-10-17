package models.user;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.SinfonierConstants;
import play.data.validation.Max;
import play.data.validation.Min;
import play.data.validation.Required;

import java.util.Date;

public class Rating extends UserTimestamp {

  @Required(message = "validation.required.rating.rate")
  @Min(value = 0, message = "validation.minValue.rating.rate")
  @Max(value = 5, message = "validation.maxValue.rating.rate")
  private int rate;

  private String comment;

  public Rating(User user) {
    this(user, 0, "");
  }

  public Rating(User user, int rate, String comment) {
    super(user);
    this.rate = rate;
    this.comment = comment;
  }

  public Rating(DBObject o) throws SinfonierException {
    super(o);

    try {
      this.rate = ((Integer) o.get(SinfonierConstants.Rating.FIELD_RATE));
      this.comment = o.get(SinfonierConstants.Rating.FIELD_COMMENT).toString();
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

    out.put(SinfonierConstants.Rating.FIELD_RATE, this.rate);
    out.put(SinfonierConstants.Rating.FIELD_COMMENT, this.comment);

    return out;
  }

  public int getRate() {
    return rate;
  }

  public void setRate(int rate) {
    this.rate = rate;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
