package models.topology;

import java.util.ArrayList;
import java.util.List;

public class TopologiesContainer {

  private List<Topology> topologies;
  private int countBeforeLimit;

  public TopologiesContainer() {
    topologies = new ArrayList<Topology>();
    countBeforeLimit = 0;
  }

  public TopologiesContainer(List<Topology> topologies, int countBeforeLimit) {
    this.topologies = topologies;
    this.countBeforeLimit = countBeforeLimit;
  }

  public List<Topology> getTopologies() {
    return topologies;
  }

  public int getCount() {
    return topologies.size();
  }

  public int getCountBeforeLimit() {
    return countBeforeLimit;
  }

  public void setTopologies(List<Topology> topologies) {
    this.topologies = topologies;
  }

  public void setCountBeforeLimit(int countBeforeLimit) {
    this.countBeforeLimit = countBeforeLimit;
  }
}
