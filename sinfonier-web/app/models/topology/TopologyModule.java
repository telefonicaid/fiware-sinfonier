package models.topology;

import com.google.gson.annotations.SerializedName;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import static models.SinfonierConstants.ModuleVersion.FIELD_VERSION_CODE;
import static models.SinfonierConstants.TopologyModule.*;

import java.util.Map;

public class TopologyModule {
  private static final int DEFAULT_PARALLELISMS_VALUE = 1;

  private String name;
  private String type;
  private String language;
  private int versionCode;
  private Integer parallelism;

  @SerializedName(FIELD_MODULE_ID_ANNOTATION)
  private String moduleId;

  @SerializedName(FIELD_MODULE_VERSION_ID_ANNOTATION)
  private String moduleVersionId;

  @SerializedName(FIELD_VALUES_ANNOTATION)
  private Map values;
  private TopologyModuleConfig config;

  public TopologyModule(String moduleId, String moduleVersionId, String name, String type, String language, int versionCode, Integer parallelism, Map values, TopologyModuleConfig config) {
    this.name = name;
    this.moduleId = moduleId;
    this.moduleVersionId = moduleVersionId;
    this.type = type;
    this.language = language;
    this.versionCode = versionCode;
    this.values = values;
    this.config = config;
    this.parallelism = parallelism != null ? parallelism : DEFAULT_PARALLELISMS_VALUE;
  }

  public TopologyModule(DBObject o) {
    if (o != null) {
      name = ((String) o.get(FIELD_NAME));

      if (o.get(FIELD_MODULE_ID_ANNOTATION) != null) {
        moduleId = o.get(FIELD_MODULE_ID_ANNOTATION).toString();
      } else {
        moduleId = null;
      }

      if (o.get(FIELD_MODULE_VERSION_ID_ANNOTATION) != null) {
        moduleVersionId = o.get(FIELD_MODULE_VERSION_ID_ANNOTATION).toString();
      } else {
        moduleVersionId = null;
      }

      if (o.get(FIELD_TYPE) != null) {
        type = o.get(FIELD_TYPE).toString();
      } else {
        type = "";
      }

      if (o.get(FIELD_LANGUAGE) != null) {
        language = o.get(FIELD_LANGUAGE).toString();
      } else {
        language = "";
      }

      if (o.get(FIELD_VERSION_CODE) != null) {
        versionCode = new Integer(o.get(FIELD_VERSION_CODE).toString());
      }

      if (o.get(FIELD_PARALLELISMS) != null) {
        parallelism = ((Integer) o.get(FIELD_PARALLELISMS));
      } else {
        parallelism = DEFAULT_PARALLELISMS_VALUE;
      }

      config = new TopologyModuleConfig(((DBObject) o.get(FIELD_CONFIG)));
      values = ((DBObject) o.get(FIELD_VALUES)).toMap();
    }
  }

  public DBObject toDBObject() {
    DBObject object = new BasicDBObject();

    if (name != null) {
      object.put(FIELD_NAME, name);
      object.put(FIELD_MODULE_ID_ANNOTATION, moduleId);
      object.put(FIELD_MODULE_VERSION_ID_ANNOTATION, moduleVersionId);
      object.put(FIELD_TYPE, type);
      object.put(FIELD_LANGUAGE, language);
      object.put(FIELD_PARALLELISMS, parallelism);

      if (this.versionCode > 0) {
        object.put(FIELD_VERSION_CODE, this.versionCode);
      }

      object.put(FIELD_CONFIG, config.toDBObject());
      object.put(FIELD_VALUES, values);
    }

    return object;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public int getVersionCode() {
    return versionCode;
  }

  public void setVersionCode(int versionCode) {
    this.versionCode = versionCode;
  }

  public Map getValues() {
    return values;
  }

  public void setValues(Map values) {
    this.values = values;
  }

  public TopologyModuleConfig getConfig() {
    return config;
  }

  public void setConfig(TopologyModuleConfig config) {
    this.config = config;
  }

  public Integer getParallelism() {
    return parallelism;
  }

  public void setParallelism(Integer parallelism) {
    this.parallelism = parallelism;
  }

  public String getModuleId() {
    return moduleId;
  }

  public void setModuleId(String moduleId) {
    this.moduleId = moduleId;
  }

  public String getModuleVersionId() {
    return moduleVersionId;
  }

  public void setModuleVersionId(String moduleVersionId) {
    this.moduleVersionId = moduleVersionId;
  }

  @Override
  public String toString() {
    return toDBObject().toString();
  }

  @Override
  public boolean equals(Object obj) {
    TopologyModule topologyModule = (TopologyModule) obj;
    if (this.getName().equals(topologyModule.getName()))
      return true;
    return false;
  }
}
