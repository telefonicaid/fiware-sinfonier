package controllers;

import java.util.List;

import org.bson.types.ObjectId;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import exceptions.SinfonierError;
import exceptions.SinfonierException;
import models.project.Project;
import models.project.ProjectSearch;
import models.project.ProjectsContainer;
import models.responses.Codes;
import models.user.User;
import play.Logger;
import play.Play;
import play.mvc.Before;

public class Projects extends BaseController {
  public static final String INDEX_MODE_MY_PROJECTS = "myProjects";
  public static final String FLASH_KEY_BACKEND_LOG = "backend_log";
  public static final String FLASH_KEY_BACKEND_TRACE = "backend_trace";
  public static final String FLASH_KEY_WARNING = "warning";
  public static final String[] AVOIDED_PAGINATION_PARAMS = {"page", "body"};

	
	@Before(unless = { "index", "search", "show", "delete","add", "edit", "deactivate" })
	static void hasWritePermission() throws SinfonierException {
		String id = request.params.get("id");
		if (id != null) {
			Project project = Project.findById(id);
			if (project == null || !project.hasWritePermission(getCurrentUser())) {
				Logger.error("No write permissions: " + request.url);
				forbidden();
			}
		} else if (!request.actionMethod.equals("save")) {
			forbidden();
		}
	}
  
	@Before
	static void projectsEnabled() throws SinfonierException {
		if (!"true".equals(Play.configuration.get("projects"))) {
			notFound();
		}
	}
	
	public static void index(String indexMode, int page) throws SinfonierException {
		if (request.isAjax()) {
			ProjectsContainer projectsContainer = Project.getProjects(getCurrentUser(), false, page);
			List<Project> projects = projectsContainer.getProjects();

			renderJSON(new Gson().toJson(projects));
		} else {
			ProjectsContainer projectsContainer = Project.getProjects(getCurrentUser(), page);

			List<Project> projects = projectsContainer.getProjects();
			int totalProjects = projectsContainer.getCountBeforeLimit();
			render(projects, page, totalProjects);
		}
	}
	
  public static void search(ProjectSearch search, int page) throws SinfonierException {
  	ProjectsContainer projectsContainer = Project.find(search, getCurrentUser(), page);
    flash("searching", true);
    params.flash();
    List<Project> projects = projectsContainer.getProjects();
    int totalProjects = projectsContainer.getCountBeforeLimit();
    render("Projects/index.html", projects, search, page, totalProjects);
  }

  public static void edit(String name) throws SinfonierException {
    Project project = Project.findByName(name);
    User user = getCurrentUser();

    if (project == null || !project.hasWritePermission(user)) {
      Logger.error("We can't found the project with name: " + name);
      notFound();
    } else {
      

      render("Projects/edit.html", project);
    }
  }

  public static void save(Project project) throws SinfonierException {
    try {
      project.setAuthorId(getCurrentUser().getId());
      project.save();

      index(INDEX_MODE_MY_PROJECTS, 1);

    } catch (SinfonierException se) {
      if (se.getError().getCode() == SinfonierError.TOPOLOGY_DUPLICATE.getCode()) {
        Codes c410 = Codes.CODE_400;
        JsonObject data = new JsonObject();
        data.addProperty("message", se.getMessage());
        c410.setData(data);
        response.status = c410.getCode();
        renderJSON(c410.toGSON());
      } else {
        throw se;
      }
    }
  }

	public static void show(String name) throws SinfonierException {
		Project project = Project.findByName(name);

		if (project == null) {
			Logger.error("We can't found the project with name: " + name);
			notFound();
		} else {
			render(project);
		}
	}
	
	public static void add() throws SinfonierException {
		
		Project project = new Project();
		
		render(project);
	}


	public static void delete(String id) throws SinfonierException {
		Project project = Project.findById(new ObjectId(id));

		if (project != null) {
			project.remove();
			if (request.isAjax()) {
				Codes c200 = Codes.CODE_200;
				JsonObject data = new JsonObject();
				data.addProperty("name", project.getName());
				c200.setData(data);
				renderJSON(c200.toGSON());
			} else {
				index(null, 1);
			}
		} else {
			Logger.error("We can't found the topology with id: " + id);
			notFound();
		}
	}

	public static void activate(String id) throws SinfonierException {
		
		Project project = Project.findById(new ObjectId(id));

		if (project != null) {
			setCurrentProject(project);
			if (request.isAjax()) {
				Codes c200 = Codes.CODE_200;
				JsonObject data = new JsonObject();
				data.addProperty("name", project.getName());
				c200.setData(data);
				renderJSON(c200.toGSON());
			} else {
				index(null, 1);
			}
		} else {
			Logger.error("We can't found the project with id: " + id);
			notFound();
		}

	}
	
	public static void deactivate() throws SinfonierException {
		
		setCurrentProject(null);
		index(null, 1);

	}



}
