package controllers;

import models.user.User;

public class SecurityOAuth extends Security {

    static void authenticateUserWithOAuth(User user) {
        if (user != null) {
            boolean authenticationOk = authenticateWithLatch(user);
            if (authenticationOk) {
                String username = user.getId();
                session.put("username", username);
                boolean renewCookie = isCookieExpired();
                markSessionAsValid(renewCookie);
                onAuthenticated();
                redirect((String)DarwinHooks.AppHooks.invoke("onUrlAfterLogin"));
            }
        }
    }
}
