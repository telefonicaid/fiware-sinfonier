package models.module;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import exceptions.SinfonierException;
import models.Model;
import models.user.Inappropriate;
import models.user.Rating;
import models.user.User;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class Complains extends Model {
  private List<Inappropriate> complains;

  public Complains(List<Inappropriate> complains) {
    this.complains = complains;
  }

  public Complains() {
    this(new ArrayList<Inappropriate>());
  }

  public Complains(BasicDBList o) throws SinfonierException {
    this();
    if (o != null) {
      for (Object o1 : o) {
        add(new Inappropriate(((DBObject) o1)));
      }
    }
  }
  
  public int size() {
    return complains.size();
  }

  public List<Inappropriate> getComplains() {
    return complains;
  }

  public void setComplains(List<Inappropriate> complains) {
    this.complains = complains;
  }

  public void add(Inappropriate inappropriate) {
    complains.add(inappropriate);
  }
  
  public boolean hasComplained(User user) {
    return complains.contains(new Inappropriate(user));
  }

  public DBObject toDBObject() {
    BasicDBList obj = new BasicDBList();

    for (Inappropriate complain : complains) {
      obj.add(complain.toDBObject());
    }

    return obj;
  }
}
