package models.module;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import exceptions.SinfonierException;
import models.Model;
import models.user.Rating;
import models.user.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Ratings extends Model implements Iterable<Rating> {
  private List<Rating> ratings;

  public Ratings(List<Rating> ratings) {
    this.ratings = ratings;
  }

  public Ratings() {
    this(new ArrayList<Rating>());
  }

  public Ratings(BasicDBList o) throws SinfonierException {
    this();

    if (o != null) {
      for (Object o1 : o) {
        add(new Rating(((DBObject) o1)));
      }
    }
  }

  public int size() {
    return ratings.size();
  }

  public boolean hasVoted(User user) {
    return ratings.contains(new Rating(user));
  }

  public void add(Rating rating) {
    ratings.add(rating);
  }

  public List<Rating> getRatings() {
    return ratings;
  }

  public void setRatings(List<Rating> ratings) {
    this.ratings = ratings;
  }

  public DBObject toDBObject() {
    BasicDBList obj = new BasicDBList();

    for (Rating rating : ratings) {
      obj.add(rating.toDBObject());
    }

    return obj;
  }

  @Override
  public Iterator<Rating> iterator() {
    return ratings.iterator();
  }
}
