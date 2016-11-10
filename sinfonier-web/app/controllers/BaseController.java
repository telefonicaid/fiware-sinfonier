package controllers;

import com.google.gson.Gson;

import exceptions.SinfonierException;
import models.project.Project;
import models.user.User;
import play.mvc.Before;

public class BaseController extends WebSecurityController {

  @Before
  private static void setUpCurrentProject() {
      getCurrentProject();
  }

	public static Project getCurrentProject() {

		if (renderArgs.get("currentProject") == null)  {
			String strProject = session.get("currentProject");
			if (strProject != null)
				renderArgs.put("currentProject", Project.fromJson(strProject));
  }

	  if (renderArgs.get("currentProject") != null) {
	      return (Project) renderArgs.get("currentProject");
	  }else{
	      return null;
	  }
		

	}
	
	
	protected static void setCurrentProject(Project project)
	{
		if (project == null) {
			session.remove("currentProject");
		}	else {
			String strProject =  project.toJson();
			session.put("currentProject", strProject);
		}		
	}

}
