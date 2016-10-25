package models.validators;

import com.google.gson.*;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.storm.ParamValues;
import models.storm.Params;
import models.topology.TopologyConfig;
import play.Logger;
import play.Play;

import java.io.*;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static models.SinfonierConstants.Drawer.IS_ACTIVE_EXTRA_PARAMS;
import static models.SinfonierConstants.TopologyConfig.FIELD_EXTRA_CONFIGURATION;
import static models.SinfonierConstants.TopologyConfig.FIELD_TOPOLOGY_PROPERTIES;

public class ParamsValidator {
  private static final String KEY_CONFIG_FILE_PATH = "storm.options.file";
  private static ParamsValidator ourInstance;
  private static Params params;

  public static ParamsValidator getInstance() throws SinfonierException {
    if (ourInstance == null) {
      ourInstance = new ParamsValidator();
    }
    return ourInstance;
  }

  public static Params getParams() {
    return params;
  }
  
  private ParamsValidator() throws SinfonierException {
    String path = Play.configuration.getProperty(KEY_CONFIG_FILE_PATH);

    if (path == null || path.trim().length() == 0) {
      throw new SinfonierException(SinfonierError.INVALID_FILE_PATH);
    }

    String basePath = Play.applicationPath.getAbsolutePath();
    JsonParser parser = new JsonParser();

    try {
      JsonObject object = ((JsonObject) parser.parse(new FileReader(basePath + "/" + path)));
      params = new Params(object);

    } catch (FileNotFoundException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_FILE_PATH, e);
    } catch (JsonIOException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_FILE_PATH, e);
    } catch (JsonSyntaxException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_CONSTRUCTION, e);
    }
  }

  public boolean hasParams(String key) {
    return params.hasParam(key);
  }

  public boolean validate(String key, Object value) throws SinfonierException {
    if (!hasParams(key)) {
      return false;
    }

    try {
      String str = ((String) value).trim();
      ParamValues origin = getOrigin(key);

      if (origin.getType().equals("integer")) {
        return validateInteger(origin, str);
      } else if (origin.getType().equals("string")) {
        return validateString(str);
      } else if (origin.getType().equals("boolean")) {
        return validateBoolean(str);
      } else {
        return false;
      }
    } catch (Exception e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_VALIDATION, e);
    }
  }

  public boolean validate(Map<String, Object> values) throws SinfonierException {
    for (String key : values.keySet()) {
      if (!validate(key, values.get(key))) {
        return false;
      }
    }

    return true;
  }

  public boolean validate(TopologyConfig config, boolean validateTopologyConfig) throws SinfonierException {
    Map properties = config.getStormProperties();
    Map params = new TreeMap();
    if (IS_ACTIVE_EXTRA_PARAMS && properties.containsKey(FIELD_EXTRA_CONFIGURATION)) {
      try {
        Map extraConfig = parseExtraConfig(((String) properties.get(FIELD_EXTRA_CONFIGURATION)));   
        params.putAll(extraConfig);
        config.getStormProperties().putAll(extraConfig);
      } catch (SinfonierException e) {
        Logger.error(e.getMessage());
        return false;
      }
    }

    for (Object o : properties.keySet()) {
      if (((String) properties.get(o)).trim().length() > 0 && !o.equals(FIELD_EXTRA_CONFIGURATION)) {
        params.put(o, properties.get(o));
      }
    }
    
    boolean validStormProperties = validate(((Map<String, Object>) params)); 
    
    //TopologyProperties validation
    if (validStormProperties && validateTopologyConfig && config.getTopologyProperties() != null) {
      Map topologyProperties = config.getTopologyProperties();
      Map<String, Object> topologyParams = new TreeMap<String, Object>();
      try {
        Map topologyConfig = parseExtraConfig(((String) topologyProperties.get(FIELD_TOPOLOGY_PROPERTIES)));   
        topologyParams.putAll(topologyConfig);
        config.getTopologyProperties().putAll(topologyConfig);
      } catch (SinfonierException e) {
        Logger.error(e.getMessage());
        return false;
      }
      for (String key : topologyParams.keySet()) {
        if (!validateTopologyConfKey(key) || !validateTopologyConfValue((String)topologyParams.get(key))) {
          return false;
        }
      }
    }
    
    return validStormProperties;
  }

  private ParamValues getOrigin(String keyParam) {
    return params.getParams().get(keyParam);
  }

  private boolean validateInteger(ParamValues origin, String value) {
    Integer v;

    try {
      v = Integer.parseInt(value, 10);
    } catch (NumberFormatException e) {
      Logger.error(e.getMessage());
      return false;
    }

    return v >= origin.getMinRange() && v <= origin.getMaxRange();
  }

  private boolean validateString(String value) {
    Pattern pattern = Pattern.compile("\\w+");
    Matcher matcher = pattern.matcher(value);
    return value.length() > 0 && matcher.matches();
  }

  private boolean validateTopologyConfKey(String value) {
    Pattern pattern = Pattern.compile("^([a-zA-Z0-9]+)*$");
    Matcher matcher = pattern.matcher(value);
    return value.length() > 0 && matcher.matches();
  }
  
  private boolean validateTopologyConfValue(String value) {
    Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.:/@]+){1,25}+([,][\\s]*([a-zA-Z0-9_\\-\\.:/@]+){1,25}+)*$");
    Matcher matcher = pattern.matcher(value);
    return value.length() > 0 && matcher.matches();
  }
  
  private boolean validateBoolean(String value) {
    return value.equals("true") || value.equals("false");
  }

  private Map parseExtraConfig(String config) throws SinfonierException {
    Map<String, Object> map = new TreeMap<String, Object>();

    if (config == null) {
      throw new SinfonierException(SinfonierError.PARSE_EXTRA_PARAMS_EXCEPTION);
    } else {
      config = config.trim();
    }

    if (config.length() == 0) {
      return map;
    }

    String[] rows = config.split("\n");
    for (String row : rows) {
      if (row.trim().length() == 0) {
        continue;
      }

      String[] values = row.split("=");

      if (values.length != 2 || values[0].trim().length() == 0 || values[1].trim().length() == 0) {
        throw new SinfonierException(SinfonierError.PARSE_EXTRA_PARAMS_EXCEPTION);
      }
      map.put(values[0], values[1]);
    }

    return map;
  }

  public static void reset() throws SinfonierException {
    ourInstance = new ParamsValidator();
  }
}
