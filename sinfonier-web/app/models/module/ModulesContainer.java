package models.module;

import java.util.ArrayList;
import java.util.List;

public class ModulesContainer {

  private List<Module> modules;
  private int countBeforeLimit;

  public ModulesContainer() {
    modules = new ArrayList<Module>();
    countBeforeLimit = 0;
  }

  public ModulesContainer(List<Module> modules, int countBeforeLimit) {
    this.modules = modules;
    this.countBeforeLimit = countBeforeLimit;
  }

  public List<Module> getModules() {
    return modules;
  }

  public int getCount() {
    return modules.size();
  }

  public int getCountBeforeLimit() {
    return countBeforeLimit;
  }

  public void setModules(List<Module> modules) {
    this.modules = modules;
  }

  public void setCountBeforeLimit(int countBeforeLimit) {
    this.countBeforeLimit = countBeforeLimit;
  }
}
