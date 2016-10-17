package models.module;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.Model;
import play.Logger;

import java.util.ArrayList;
import java.util.List;

public class Versions extends Model {
  private List<Version> versions;

  public Versions(List<Version> versions) {
    this.versions = versions;
  }

  public Versions() {
    this(new ArrayList<Version>());
  }

  public Versions(BasicDBList o) throws SinfonierException {
    this();
    if (o != null) {
      for (Object o1 : o) {
        add(new Version(((DBObject) o1)));
      }
    }
  }

  public List<Version> getVersions() {
    return versions;
  }

  public void setVersions(List<Version> versions) {
    this.versions = versions;
  }

  public void add(Version version) {
    versions.add(version);
  }
  
  public int size() {
    return versions.size();
  }
  
  public Version getItem(int versionCode) {
    int i;
    Version v = null;
    
    for (i=0; i < versions.size(); i++) {
      if (versions.get(i).getVersionCode() == versionCode) {
        v = versions.get(i);
      }
    }
    
    return v;
  }

  public boolean remove(Version version) {
    return versions.remove(version);
  }

  public boolean isEmpty() {
    return versions.isEmpty();
  }

  public Version getLastVersion() throws SinfonierException {
    try {
      return versions.get(versions.size() - 1);
    } catch (IndexOutOfBoundsException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_VERSION_NO_DEFINED, e);
    }
  }
  
  public boolean contains(Version version) {
    return this.getItem(version.getVersionCode()) != null;
  }

  public DBObject toDBObject() {
    BasicDBList obj = new BasicDBList();

    for (Version version : versions) {
      obj.add(version.toDBObject());
    }

    return obj;
  }
}
