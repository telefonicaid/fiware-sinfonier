package models.module.validations;

import models.module.Field;
import play.data.validation.Check;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FieldNameCheck extends Check {

  @Override
  public boolean isSatisfied(Object o, Object name) {
    Field field;
    Pattern p = Pattern.compile("\\w+");

    if (o instanceof Field) {
      field = ((Field) o);
    } else {
      return false;
    }

    if (field == null || field.getName() == null || field.getName().length() == 0) {
      setMessage("validation.required.field.name");
      return false;
    }

    Matcher matcher = p.matcher(field.getName());
    if (!matcher.matches()) {
      setMessage("validation.match.field.name");
      return false;
    }

    return true;
  }
}
