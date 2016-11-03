package exceptions;

import notifiers.SinfonierMailer;

import java.util.Arrays;
import play.Play;

public class SinfonierException extends Exception {

  private SinfonierError error;
  private Object[] args;
  private static final Boolean isActiveAdminNotifications = Boolean.parseBoolean(Play.configuration.getProperty("admin.exceptions.notify", "true"));

  public SinfonierException(SinfonierError error, Object... args) {
    super("[" + error.getCode() + "] " + error.getMessage());
    this.error = error;
    this.args = args;

    if (isActiveAdminNotifications) {
      sendNotification();
    }
  }

  public SinfonierException(SinfonierError error, Throwable e, Object... args) {
    super("[" + error.getCode() + "] " + error.getMessage(), e);
    this.error = error;
    this.args = args;

    if (isActiveAdminNotifications) {
      sendNotification();
    }
  }

  public SinfonierException(SinfonierError error, Boolean notify, Object... args) {
    super("[" + error.getCode() + "] " + error.getMessage());
    this.error = error;
    this.args = args;

    if (notify) {
      sendNotification();
    }
  }

  public SinfonierError getError() {
    return error;
  }

  public Object[] getArgs() {
    return args;
  }

  public void setArgs(Object[] args) {
    this.args = args;
  }

  private void sendNotification() {
    SinfonierMailer.notifySinfonierExceptionAdmin(this);
  }

  public String toHtmlString() {
    return "<pre>SinfonierException<br/>&emsp;" +
        "error => (" + error.getCode() + ", " + error.getMessage() + ")<br>&emsp;" +
        "args => " + Arrays.toString(args) + "</pre>";
  }
}
