package models.module.validations;

import play.Play;
import play.data.validation.Check;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;


public class IconCheck extends Check {
  public static final long MAX_SIZE = Long.parseLong(Play.configuration.getProperty("max_imagen_size", "1000000"));

  @Override
  public boolean isSatisfied(Object module, Object icon) {
    // Because the icon is not a mandatory field.
    if (icon == null) {
      return true;
    }

    String contentType = new MimetypesFileTypeMap().getContentType(((File) icon));
    String type = contentType.split("/")[0];

    if (!type.equals("image")) {
      setMessage("validation.icon.invalidFormat");
      return false;
    }

    if (((File) icon).length() > MAX_SIZE) {
      setMessage("validation.icon.exceedsSize");
      return false;
    }

    return true;
  }
}
