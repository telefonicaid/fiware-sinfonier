package controllers;

import play.Logger;
import play.data.validation.Equals;
import play.data.validation.Required;
import play.data.validation.URL;
import play.data.validation.Validation;
import play.i18n.Lang;
import play.mvc.Catch;
import play.mvc.Util;
import models.exception.PasswordConstraintViolationException;
import models.factory.DarwinFactory;
import models.user.User;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.user.SinfonierUser;
import models.validation.Password;
import play.templates.JavaExtensions;

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
                          String timeZone, @URL(message = "validation.url.invalid") String web) {
    checkAuthenticity();
    User current = getCurrentUser();
    User user = DarwinFactory.getInstance().loadUser(email);

    if (user != null && !Validation.hasErrors()) {
      if (current.getEmail().equals(email) || current.isAdminUser()) {
        Logger.debug("Edit profile " + email + ", name=" + name + ", preferredLang=" + preferredLang);
        user.setName(name);
        user.setPreferredLang(preferredLang);

        // Save SinfonierUser fields
        SinfonierUser sinfonierUser = (SinfonierUser) user.getImplementation();
        sinfonierUser.setTwitter(twitter);
        sinfonierUser.setOrganization(organization);
        sinfonierUser.setTimeZoneID(timeZone);
        sinfonierUser.setWeb(web != null ? web: "");
        user.save();
        
        //Change web language if user has changed preferred language
        changeWebLangIfUserLangIsChanged(current, email, preferredLang);

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
                                    @Password @Equals(value = "newPassword2", message = "validation.match.profile.password")
                                        String newPassword1,
                                    @Required(message = "validation.required.profile.password")
                                        String newPassword2,
                                    @Required(message = "validation.required.profile.oldPassword") String oldPassword)
      throws SinfonierException, PasswordConstraintViolationException {
    checkAuthenticity();
    User user = DarwinFactory.getInstance().loadUser(email);

    if (user == null || getCurrentUser() == null) {
      notFound();
    }

    if (Validation.hasErrors()) {
      for (play.data.validation.Error error : Validation.errors()) {
        Logger.error(error.message());
      }
      params.flash();
      // TODO: Change redirect when Bug #19469 is fixed in Darwin library.
      //showUserProfile(email);
      index(user);
    }

    if (!getCurrentUser().isAdminUser() && !getCurrentUser().getEmail().equals(email)) {
        forbidden();
    }


    if (!user.authenticate(oldPassword)) {
      validation.equals(true, false).message("Public.passwordReset.validation.invalidOldPassword");
    }

    if (validation.hasErrors()) {
      validation.keep();
      index(user);
    }

    user.changePassword(newPassword1);
    user.save();
    flash.clear();

    // TODO: Change render when Bug #19153 is fixed in Darwin library.
    //showUserProfile(email);
    index(user);
  }

  @Util
  @Catch(value = SinfonierException.class, priority = 1)
  public static void catchSinfonierExceptions(SinfonierException e) {
    Logger.error(e, e.getMessage());
    SinfonierError error = e.getError();
    Object[] args = e.getArgs();
    render("errors/error.html", error, args);
  }
  
  private static void changeWebLangIfUserLangIsChanged(User currentUser, String email, String preferredLang) {
    if (currentUser.getEmail().equals(email) && preferredLang != null && !preferredLang.equals(currentUser.getPreferredLang())) {
      Lang.change(preferredLang);
      session.put(LANGUAGE_SESSION_KEY, preferredLang);
    }
  }
}
