package exceptions;

import play.i18n.Messages;

public class SinfonierException extends Exception {

  private SinfonierError error;
  private Object[] args;

  public SinfonierException(SinfonierError error, Object... args) {
    super("[" + error.getCode() + "] " + error.getMessage());
    this.error = error;
    this.args = args;
  }

  public SinfonierException(SinfonierError error, Throwable e, Object... args) {
    super("[" + error.getCode() + "] " + error.getMessage(), e);
    this.error = error;
    this.args = args;
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
}
