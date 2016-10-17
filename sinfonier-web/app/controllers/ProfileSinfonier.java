package controllers;

import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import play.Logger;
import play.data.validation.CheckWith;
import play.data.validation.Equals;
import play.data.validation.Required;
import play.data.validation.Validation;
import play.i18n.Messages;
import play.mvc.Catch;
import play.mvc.Util;
import models.exception.PasswordConstraintViolationException;
import models.factory.DarwinFactory;
import models.module.validations.NameCheck;
import models.user.User;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.user.SinfonierUser;
import models.validation.Password;
import static models.SinfonierConstants.SinfonierUser.FORMAT_TIME_ZONE;

public class ProfileSinfonier extends Profile {

  public static void edit(String email) {
    User current = getCurrentUser();
    
    if (current.getEmail().equals(email) || current.isAdminUser()) {
      User user = DarwinFactory.getInstance().loadUser(email);
      if (user != null) {
        render("Profile/edit.html", user);
      } else {
        notFound();
      }
    } else {
      forbidden();
    }
  }
  
  public static void save(@Required(message = "validation.requiere.email") String email, 
                          @Required(message = "validation.requiere.name") String name, 
                          @Required String preferredLang, String twitter, String organization, 
      String timeZone, String web) {
    checkAuthenticity();
    User current = getCurrentUser();
    User user = DarwinFactory.getInstance().loadUser(email);
    
    if (user != null && !Validation.hasErrors()) {
      if (current.getEmail().equals(email) || current.isAdminUser()) {
        Logger.debug("Edit profile "+email+", name="+name+", preferredLang="+preferredLang);
        user.setName(name);
        user.setPreferredLang(preferredLang);
        
        // Save SinfonierUser fields
        SinfonierUser sinfonierUser = (SinfonierUser) user.getImplementation();
        sinfonierUser.setTwitter(twitter);
        sinfonierUser.setOrganization(organization);
        sinfonierUser.setTimeZoneID(timeZone);
        sinfonierUser.setWeb(web);
        user.save();
        
        // TODO: Change render when Bug #19153 is fixed in Darwin library. 
        //showUserProfile(email);
        render("Profile/index.html", user);
      } else {
        forbidden();
      }
    } else if (Validation.hasErrors()) {
      params.flash();
      render("Profile/edit.html", user); 
    } else {
      notFound();
    }
  }
  
  public static void changePassword(String email,
                                    @Required(message = "validation.required.profile.password")
                                    @Password @Equals(value="newPassword2", message = "validation.match.profile.password") 
                                    String newPassword1,
                                    @Required(message = "validation.required.profile.password")
                                    String newPassword2) 
      throws SinfonierException {
    checkAuthenticity();    
    User current = getCurrentUser();
    User user = DarwinFactory.getInstance().loadUser(email);
    
    if (user != null) {
      if (!Validation.hasErrors()) {
        if (current.getEmail().equals(email) || current.isAdminUser()) {
          Logger.debug("Change password "+email);
          try {
            user.changePassword(newPassword1);
          } catch (PasswordConstraintViolationException e) {
            throw new SinfonierException(SinfonierError.PASSWORD_CONSTRAINS, e);
          }
          user.save();
          
          // TODO: Change render when Bug #19153 is fixed in Darwin library. 
          //showUserProfile(email);
          render("Profile/index.html", user);
        } else {
          forbidden();
        }
      } else {
        for (play.data.validation.Error error : Validation.errors()) {
          Logger.error(error.message());
        }
    
        params.flash();
        // TODO: Change redirect when Bug #19469 is fixed in Darwin library. 
        //showUserProfile(email);
        render("Profile/index.html", user);
      }
    } else {
      notFound();
    }
  }
  
  @Util
  @Catch(value = SinfonierException.class, priority = 1)
  public static void catchSinfonierExceptions(SinfonierException e) {
    Logger.error(e, e.getMessage());
    SinfonierError error = e.getError();
    Object[] args = e.getArgs();
    render("errors/error.html", error, args);
  }
}
