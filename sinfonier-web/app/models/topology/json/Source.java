package models.topology.json;

public class Source {
  private final static String GROUPING_TYPE = "shuffle";

  private String sourceId;
  private final String grouping = GROUPING_TYPE;

  public Source(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getGrouping() {
    return grouping;
  }
}
