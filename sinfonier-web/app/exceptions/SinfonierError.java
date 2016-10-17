package exceptions;

import play.i18n.Messages;

public enum SinfonierError {
  INVALID_CONSTRUCTION(1, "Error trying to build a object"),
  INVALID_RESPONSE(2, "Something was wrong trying to build the response"),
  INVALID_REQUEST(3, "Invalid request"),
  INVALID_FILE_PATH(4, "We can not find the path"),
  INVALID_VALIDATION(5, "We got a exception trying to validate a param"),
  PARSE_EXTRA_PARAMS_EXCEPTION(6, "We have got an exception trying to parse the extra params"),

  MODULE_DUPLICATE(1001, "The module name is already used"),
  MODULE_INVALID(1002, "The module no exists or we can't find it."),
  MODULE_INVALID_CONSTRUCTION(1003, "Error trying to build a module"),
  MODULE_VERSION_NO_DEFINED(1004, "Any module version has been defined yet."),

  MODULE_LIMIT_PENDING(1004, "The user reached the limit of pending modules"),

  TOPOLOGY_DUPLICATE(2001, "The topology name is already used"),
  TOPOLOGY_INVALID_CONSTRUCTION(2003, "Error trying to build a topology"),

  TOPOLOGY_BUILDER_TYPE(2101, "The topology or some property required is not found it."), 
  
  PASSWORD_CONSTRAINS(3001, "The password doesn't meet the security policy.");

  private static final String DEFAULT_KEY_MSG = "Error.500.msg";

  private final int code;
  private final String msg;

  SinfonierError(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return msg;
  }

  public String getMessagei18n(Object... args) {
    String key = "Error." + code + ".msg";
    String msg = Messages.get(key, args);

    if (msg == null || msg.equals(key)) {
      return Messages.get(DEFAULT_KEY_MSG);
    }

    return msg;
  }
}
