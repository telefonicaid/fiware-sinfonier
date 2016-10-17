package models.module.validations;

import models.module.Module;
import models.module.ModuleVersion;
import play.data.validation.Check;

public class CodeSourceCheck extends Check {

  @Override
  public boolean isSatisfied(Object module, Object code) {
    ModuleVersion m;

    if (module instanceof ModuleVersion) {
      m = (ModuleVersion) module;
    } else {
      return false;
    }

    String _code = code.toString();

    if (!m.isFromGist() && (_code == null || _code.length() == 0)) {
      setMessage("validation.required.sourceCode");
      return false;
    }

    return true;
  }
}
