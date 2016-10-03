package models.module.validations;

import models.module.ModuleVersion;
import play.data.validation.Check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeSourceUrlCheck extends Check {

  @Override
  public boolean isSatisfied(Object module, Object url) {
    ModuleVersion m;

    if (module instanceof ModuleVersion) {
      m = (ModuleVersion) module;
    } else {
      return false;
    }

    String _url = url.toString();

    if (m.isFromGist() && (_url == null || _url.length() == 0 || !isValidUrl(_url))) {
      setMessage("validation.required.sourceCodeUrl");
      return false;
    }

    return true;
  }


  private boolean isValidUrl(String url) {
    Pattern pattern = Pattern.compile("^https?:\\/\\/gist\\.github\\.com\\/\\w+\\/\\w+$");
    Matcher matcher = pattern.matcher(url);

    return matcher.lookingAt();
  }
}
