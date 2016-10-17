package models.topology.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public abstract class Module {

  @SerializedName("class")
  private String clazz;
  private String abstractionId;
  private Integer parallelism;
  private List<Source> sources;
  private Map<String, Object> params;

  public Module(String clazz, String abstractionId, Integer parallelism, List<Source> sources, Map<String, Object> params) {
    this.clazz = clazz;
    this.abstractionId = abstractionId;
    this.parallelism = parallelism;
    this.sources = sources;
    this.params = params;
  }

  public List<Source> getSources() {
    return sources;
  }

  public void setSources(List<Source> sources) {
    this.sources = sources;
  }

  public String getClazz() {
    return clazz;
  }

  public void setClazz(String clazz) {
    this.clazz = clazz;
  }

  public String getAbstractionId() {
    return abstractionId;
  }

  public void setAbstractionId(String abstractionId) {
    this.abstractionId = abstractionId;
  }

  public Integer getParallelism() {
    return parallelism;
  }

  public void setParallelism(Integer parallelism) {
    this.parallelism = parallelism;
  }

  public Map<String, Object> getParams() {
    return params;
  }

  public void setParams(Map<String, Object> params) {
    this.params = params;
  }
}
