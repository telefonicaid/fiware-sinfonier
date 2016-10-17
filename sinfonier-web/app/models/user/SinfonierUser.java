package models.user;

import java.util.TimeZone;

import models.Constants;
import play.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static models.SinfonierConstants.SinfonierUser.*;

public class SinfonierUser extends MongoUser {
  
  protected String twitter;
  protected String organization;
  protected String timeZoneID;
  protected String web;

  public SinfonierUser(String name, String email, String password) {
    super(name, email, password);
  }
  
  public SinfonierUser(DBObject o) {
    super(o);
    this.twitter = (String) o.get(FIELD_TWITTER);
    this.organization = (String) o.get(FIELD_ORGANIZATION);
    this.timeZoneID = (String) o.get(FIELD_TIME_ZONE);
    this.web = (String) o.get(FIELD_WEB);
  }

  protected BasicDBObject beforeGetAsDBObject(BasicDBObject mongoUserParams) {
    mongoUserParams.append(FIELD_TWITTER, this.twitter);
    mongoUserParams.append(FIELD_ORGANIZATION, this.organization);
    mongoUserParams.append(FIELD_TIME_ZONE, this.timeZoneID);
    mongoUserParams.append(FIELD_WEB, this.web);
    return mongoUserParams;
  }

  public String getTwitter() {
    return twitter;
  }

  public void setTwitter(String twitter) {
    this.twitter = twitter;
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getTimeZoneID() {
    return timeZoneID;
  }

  public void setTimeZoneID(String timeZoneID) {
    this.timeZoneID = timeZoneID;
  }

  public String getWeb() {
    return web;
  }

  public void setWeb(String web) {
    this.web = web;
  }
  
  @Override
  public boolean isAdminUser() {
    boolean isAdmin = super.isAdminUser();
    if (!isAdmin) {
      Logger.debug("Checking superadmins not in auto_admin_users configuration list");
      isAdmin = (this.getRoleId() != null ? this.getRoleId().equals(Constants.UserRole.SUPER_ADMIN) : false); 
    }
    return isAdmin;
  }
  
}
