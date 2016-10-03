package models.dashboard;

import exceptions.SinfonierException;
import models.topology.Topology;
import models.module.Module;
import models.user.User;

import java.util.List;

public class Dashboard {
  private long modulesCount;
  private long topologiesCount;
  private long runningTopologiesCount;
  private List<Module> topRateModules;
  private List<Module> topUsedModules;

  public Dashboard(User user) throws SinfonierException {
    topRateModules = Module.getTopModules().getModules();
    topUsedModules = Module.getTopUsed().getModules();

    modulesCount = Module.countByUser(user);
    topologiesCount = Topology.countByUser(user);
    runningTopologiesCount = Topology.getRunningByUser(user);
  }

  public long getModulesCount() {
    return modulesCount;
  }

  public long getTopologiesCount() {
    return topologiesCount;
  }

  public long getRunningTopologiesCount() {
    return runningTopologiesCount;
  }

  public List<Module> getTopRateModules() {
    return topRateModules;
  }

  public List<Module> getTopUsedModules() {
    return topUsedModules;
  }
}
