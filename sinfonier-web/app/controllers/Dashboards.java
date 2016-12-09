package controllers;

import exceptions.SinfonierException;
import models.dashboard.Dashboard;

public class Dashboards extends BaseController {

  public static void index() throws SinfonierException {
    Dashboard dashboard = new Dashboard(getCurrentUser());
    render(dashboard);
  }
}
