package controllers;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import models.factory.DarwinFactory;
import models.token.Token;
import models.token.TokenTypeBase;
import models.user.User;
import net.sf.oval.constraint.Email;
import notifiers.DarwinMailer;
import play.Logger;
import play.data.validation.Required;
import play.exceptions.MailException;
import play.i18n.Lang;
import play.i18n.Messages;

public class PublicContentSinfonier extends PublicContentBase {

  public static void processRequestPasswordResetSinfonier(@Required(message = "Public.processRequestPasswordReset.validation.enterAccount")
      @Email(message = "Public.processRequestPasswordReset.validation.invalidFormat") String email) {

    String actualLang = null;
    checkAuthenticity();
    if (validation.hasErrors()) {
      params.flash();
      validation.keep();
      requestPasswordReset();
    } else {
      User user = DarwinFactory.getInstance().loadUser(email);
      actualLang = Lang.get();
      Lang.set(user.getPreferredLang());
    }
    doProcessRequestPasswordReset(email, actualLang);
  }
  
  protected static void doProcessRequestPasswordReset(String email, String actualLang){
    try {
        User user = DarwinFactory.getInstance().loadUser(email);
        if (user != null) {
            Token token = DarwinFactory.getInstance().buildToken(email, TokenTypeBase.RESET_PASSWORD);
            token.save();
            DarwinMailer.passwordReset(user, token.getToken());
        }
    } catch (MailException e) {
        Logger.info("Error sending email notification");
    }
    if (actualLang != null) {
      Lang.set(actualLang);
    }
    renderArgs.put("email", (StringUtils.isEmpty(email)) ? Messages.get("Public.activate.yourEmail") : StringEscapeUtils.escapeHtml(email));
    render("PublicContentBase/processRequestPasswordReset.html");
}
}
