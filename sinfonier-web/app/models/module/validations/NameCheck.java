package models.module.validations;

import models.SinfonierConstants;
import models.module.Module;
import play.data.validation.Check;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameCheck extends Check {

  @Override
  public boolean isSatisfied(Object module, Object name) {
    Module m;
    Pattern p = Pattern.compile("(([A-Z]+[a-z0-9]*)+)");
    List<String> blackList = new ArrayList<String>(Arrays.asList(SinfonierConstants.Module.BLACKLIST.split(",")));

    if (module instanceof Module) {
      m = ((Module) module);
    } else {
      return false;
    }

    if (m.getName() == null || m.getName().length() == 0) {
      setMessage("validation.required.module.name");
      return false;
    }

    Matcher matcher = p.matcher(m.getName());
    if (!matcher.matches()) {
      setMessage("validation.match.module.name");
      return false;
    }

    if (blackList.contains(m.getName().trim())) {
      setMessage("validation.not.allow.module.name");
      return false;
    }


    return true;
  }
}
