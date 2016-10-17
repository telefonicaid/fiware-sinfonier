package models.topology.json;

import java.util.List;
import java.util.Map;

public class Spout extends Module {

  public Spout(String clazz, String abstractionId, Integer parallelism, List<Source> sources, Map<String, Object> params) {
    super(clazz, abstractionId, parallelism, sources, params);
  }

}
