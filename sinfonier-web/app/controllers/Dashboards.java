package controllers;

import exceptions.SinfonierException;
import models.dashboard.Dashboard;

public class Dashboards extends WebSecurityController {

  public static void index() throws SinfonierException {
    Dashboard dashboard = new Dashboard(getCurrentUser());
    render(dashboard);
  }
}
