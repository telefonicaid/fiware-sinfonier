package models.module;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import exceptions.SinfonierException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Model;
import play.data.validation.Valid;

public class Libraries extends Model implements Iterable<Library> {
  @Valid
  private List<Library> libraries;

  public Libraries(List<Library> libraries) {
    this.libraries = libraries;
  }

  public Libraries() {
    this(new ArrayList<Library>());
  }

  public Libraries(BasicDBList o) throws SinfonierException {
    this();

    if (o != null) {
      for (Object o1 : o) {
        add(new Library(((DBObject) o1)));
      }
    }
  }

  public void add(Library library) {
    libraries.add(library);
  }
  
  public int size() {
    return libraries.size();
  }

  @Override
  public Iterator<Library> iterator() {
    return libraries.iterator();
  }

  public List<Library> getLibraries() {
    return libraries;
  }

  public void setLibraries(List<Library> libraries) {
    this.libraries = libraries;
  }

  public DBObject toDBObject() {
    BasicDBList obj = new BasicDBList();

    for (Library library : libraries) {
      obj.add(library.toDBObject());
    }

    return obj;
  }
}
