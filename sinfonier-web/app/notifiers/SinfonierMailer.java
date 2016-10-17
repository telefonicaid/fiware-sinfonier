package notifiers;

import models.Config;
import models.factory.DarwinFactory;
import models.factory.MongoFactory;
import models.module.Module;
import models.module.ModuleVersion;
import models.notifiers.TranslatedTemplatePathResolver;
import models.user.Inappropriate;
import models.user.User;
import models.user.UsersContainer;
import play.Play;
import play.i18n.Lang;
import play.i18n.Messages;
import play.mvc.Scope;

import java.util.ArrayList;
import java.util.List;

public class SinfonierMailer extends DarwinMailer {

  protected static final String DEFAULT_LANG = "en";
  protected static final String MAIL_FOLDER = "SinfonierMailer";
  protected static final String ADMIN_EMAILS = "admin.mails";
  protected static final String SUBJECT_REVIEW_CODE = "Mailer.subject.reviewModule";
  protected static final String SUBJECT_COMPLAIN_MODULE = "Mailer.subject.complainModule";
  protected static final String SUBJECT_NOTIFY_COMPLAIN_MODULE_ADMIN = "Mailer.subject.nofityComplainModuleAdmin";

  public static void reviewModule(Module module, ModuleVersion version) {
    List<String> emails = getEmails();
    configureEmailSettings(emails, SUBJECT_REVIEW_CODE);
    String baseUrl = getBaseUrl();
    String appName = Config.getApplicationName();
    Scope.RenderArgs renderArgs = Scope.RenderArgs.current();
    send(getTemplatePath("recheckCode", Lang.get()), renderArgs, appName, baseUrl, module, version);
  }

  public static void complainModule(Module module, Inappropriate inappropriate) {
    String emails = module.getAuthor().getEmail();
    String actualLang = Lang.get();
    Lang.set(module.getAuthor().getPreferredLang());
    configureEmailSettings(emails, SUBJECT_COMPLAIN_MODULE);
    String baseUrl = getBaseUrl();
    String appName = Config.getApplicationName();
    Scope.RenderArgs renderArgs = Scope.RenderArgs.current();
    send(getTemplatePath("complainModule", module.getAuthor().getPreferredLang()), renderArgs, appName, baseUrl,
        module, inappropriate);
    Lang.set(actualLang);
  }

  public static void notifyComplainModuleAdmin(Module module, Inappropriate inappropriate) {
    List<String> emails = getEmails();
    configureEmailSettings(emails, SUBJECT_NOTIFY_COMPLAIN_MODULE_ADMIN);
    String baseUrl = getBaseUrl();
    String appName = Config.getApplicationName();
    Scope.RenderArgs renderArgs = Scope.RenderArgs.current();
    send(getTemplatePath("notifyComplainModuleAdmin", Lang.get()), renderArgs, appName, baseUrl, module,
        inappropriate);
  }

  protected static String getTemplatePath(String template, String lang) {
    if (lang == null) {
      lang = DEFAULT_LANG;
    }

    TranslatedTemplatePathResolver pathResolver = new TranslatedTemplatePathResolver(MAIL_FOLDER, lang);
    return pathResolver.getEmailContent(template);
  }

  private static void configureEmailSettings(List<String> recipients, String subject) {
    String from = getNameSender() + "<" + getEmailSender() + ">";
    setCharset(UTF_8);
    setFrom(from);
    setSubject(Messages.get(subject));
    addRecipient(recipients.toArray());
  }

  private static List<String> getEmails() {
    String emails = Play.configuration.getProperty(ADMIN_EMAILS);
    List<String> emailsList = new ArrayList<String>();

    if (emails == null || emails.trim().length() == 0) {
      UsersContainer usersContainer = DarwinFactory.getInstance().retrieveUsers("role", "SuperAdmin", 0, 0);
      List<User> users = usersContainer.getUsers();

      for (User user : users) {
        emailsList.add(user.getEmail());
      }

    } else {
      String[] emailsArray;
      if (emails.contains(",")) {
        emailsArray = emails.split(",");
      } else {
        emailsArray = new String[]{emails.trim()};
      }

      for (String email : emailsArray) {
        if (email != null && email.trim().length() > 0) {
          emailsList.add(email.trim());
        }
      }
    }

    return emailsList;
  }
}
