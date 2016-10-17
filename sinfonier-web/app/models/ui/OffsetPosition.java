package models.ui;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import static models.SinfonierConstants.OffsetPosition.*;

public class OffsetPosition {
  private Integer top;
  private Integer right;
  private Integer bottom;
  private Integer left;

  public OffsetPosition(Integer top, Integer right, Integer bottom, Integer left) {
    this.top = top;
    this.right = right;
    this.bottom = bottom;
    this.left = left;
  }

  public OffsetPosition(DBObject o) {
    if (o != null && o.get(FIELD_TOP) != null) {
      top = ((Integer) o.get(FIELD_TOP));
    }

    if (o != null && o.get(FIELD_RIGHT) != null) {
      right = ((Integer) o.get(FIELD_RIGHT));
    }

    if (o != null && o.get(FIELD_LEFT) != null) {
      left = ((Integer) o.get(FIELD_LEFT));
    }

    if (o != null && o.get(FIELD_BOTTOM) != null) {
      bottom = ((Integer) o.get(FIELD_BOTTOM));
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if(top != null) object.put(FIELD_TOP, top);
    if(right != null) object.put(FIELD_RIGHT, right);
    if(bottom != null) object.put(FIELD_BOTTOM, bottom);
    if(left != null) object.put(FIELD_LEFT, left);

    return object;
  }

  public Integer getTop() {
    return top;
  }

  public void setTop(Integer top) {
    this.top = top;
  }

  public Integer getRight() {
    return right;
  }

  public void setRight(Integer right) {
    this.right = right;
  }

  public Integer getLeft() {
    return left;
  }

  public void setLeft(Integer left) {
    this.left = left;
  }

  public Integer getBottom() {
    return bottom;
  }

  public void setBottom(Integer bottom) {
    this.bottom = bottom;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }
}
