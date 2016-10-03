package models.module;

import static models.SinfonierConstants.Version.*;

import models.Model;
import play.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;

public class Version extends Model implements Comparable<Version> {

  private String versionTag;
  private int versionCode;
  private String moduleVersionId;
  private Boolean isDeleted;
  private Boolean isVisible;
  private ModuleVersion version;

  public Version(ModuleVersion version) {
    this.version = version;
    this.versionCode = version.getVersionCode();
    this.moduleVersionId = version.getId();
    isDeleted = false;
    isVisible = false;
  }

  public Version(DBObject o) throws SinfonierException {
    try {
      versionTag = (String) o.get(FIELD_VERSION_TAG);
      versionCode = new Integer(o.get(FIELD_VERSION_CODE).toString());
      moduleVersionId = o.get(FIELD_VERSION_ID).toString();
      isDeleted = (Boolean) o.get(FIELD_IS_DELETED);
      isVisible = (Boolean) o.get(FIELD_IS_VISIBLE);
    } catch (Exception e) {
      versionCode = -1;
      moduleVersionId = null;
      Logger.error("Exception constructor Version(DBObject) > " + e.getMessage());
      throw new SinfonierException(SinfonierError.MODULE_INVALID_CONSTRUCTION, e);
    }
  }

  public DBObject toDBObject() {
    DBObject out = new BasicDBObject();

    if (moduleVersionId != null) {
      out.put(FIELD_VERSION_TAG, this.versionTag);
      out.put(FIELD_VERSION_CODE, this.versionCode);
      out.put(FIELD_VERSION_ID, this.moduleVersionId);
      out.put(FIELD_IS_DELETED, this.isDeleted);
      out.put(FIELD_IS_VISIBLE, this.isVisible);
    }

    return out;
  }

  public String getVersionTag() {
    return versionTag;
  }

  public void setVersionTag(String versionTag) {
    this.versionTag = versionTag;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public String getModuleVersionId() {
    return moduleVersionId;
  }

  public void setModuleVersionId(String moduleVersionId) {
    this.moduleVersionId = moduleVersionId;
  }

  public ModuleVersion getModuleVersion() throws SinfonierException {
    return getModuleVersion(false);
  }

  public ModuleVersion getModuleVersion(boolean force) throws SinfonierException {
    if ((version == null && moduleVersionId != null) || (force && moduleVersionId != null)) {
      version = ModuleVersion.findById(moduleVersionId);
    }
    return version;
  }

  public Boolean getIsDeleted() {
    return isDeleted;
  }

  public void setIsDeleted(Boolean isDeleted) {
    this.isDeleted = isDeleted;
  }

  public Boolean getIsVisible() {
    return isVisible;
  }

  public void setIsVisible(Boolean isVisible) {
    this.isVisible = isVisible;
  }

  @Override
  public int compareTo(Version version) {
    if (version.getVersionCode() == getVersionCode())
      return 0;
    else if (version.getVersionCode() < getVersionCode())
      return -1;
    else
      return 1;
  }
}
