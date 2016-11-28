package models.project;

import java.util.ArrayList;
import java.util.List;

public class ProjectsContainer {

  private List<Project> projects;
  private int countBeforeLimit;

  public ProjectsContainer() {
    projects = new ArrayList<Project>();
    countBeforeLimit = 0;
  }

  public ProjectsContainer(List<Project> projects, int countBeforeLimit) {
    this.projects = projects;
    this.countBeforeLimit = countBeforeLimit;
  }

  
  public int getCount() {
    return projects.size();
  }

  public int getCountBeforeLimit() {
    return countBeforeLimit;
  }

  public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public void setCountBeforeLimit(int countBeforeLimit) {
    this.countBeforeLimit = countBeforeLimit;
  }
}
