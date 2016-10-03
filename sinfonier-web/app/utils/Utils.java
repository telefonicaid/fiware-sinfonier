package utils;

import static models.SinfonierConstants.SinfonierUser.FORMAT_TIME_ZONE;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import models.Constants;
import models.factory.DarwinFactory;
import models.finder.UserFinder;
import models.user.User;
import models.user.UsersContainer;

public class Utils {

  public static String displayTimeZone(String tzID, String lang) {
    String result = "";
    
    if (tzID != null) {
      TimeZone tz = TimeZone.getTimeZone(tzID);
      if (tz != null) {
        long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset())
                                    - TimeUnit.HOURS.toMinutes(hours);
        // avoid -4:-30 issue
        minutes = Math.abs(minutes);
  
        String strLong = tz.getDisplayName(false, TimeZone.LONG, Locale.forLanguageTag(lang));
        if (hours > 0) {
          result = String.format(FORMAT_TIME_ZONE, hours, minutes, tz.getID()+" - "+strLong);
        } else {
          result = String.format(FORMAT_TIME_ZONE, hours, minutes, tz.getID()+" - "+strLong);
        }
      }
    }

    return result;
  }
  
  public static List<String> getUsersEmailsByName(String name) {
    //Search users with name
    UserFinder userFinder = new UserFinder();
    userFinder.setField(Constants.User.FIELD_NAME);
    userFinder.setValue(name);
    Map<String, Object> usersQuery = userFinder.getQuery();
    UsersContainer usersContainer = DarwinFactory.getInstance().retrieveUsers(usersQuery, 0, 0);
    List<User> users = usersContainer.getUsers();
    List<String> emails = new ArrayList<String>();
    for(User user : users){
      emails.add(user.getEmail());
    }
    return emails;
  }
}
