package controllers;

import com.google.gson.Gson;
import models.factory.DarwinFactory;
import models.user.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import play.Logger;
import play.Play;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.security.SecureRandom;

public class OAuthController extends DarwinController {

    public static final String URL_BASE = Play.configuration.getProperty("application.baseUrl");
    public static final String URL_CALLBACK = Play.configuration.getProperty("oauth.redirectUrl");
    public static final String OAUTH_SERVER = Play.configuration.getProperty("oauth.server");
    public static final String CLIENT_ID = Play.configuration.getProperty("oauth.clientID");
    public static final String CLIENT_SECRET = Play.configuration.getProperty("oauth.clientSecret");
    public static final String RESOURCE_USER = Play.configuration.getProperty("oauth.resource.user");

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.!$";
    static SecureRandom rnd = new SecureRandom();

    public static void authenticate(String code) throws Throwable {
        Logger.debug("authenticate");
        // Send access token request
        OAuthClientRequest request = OAuthClientRequest
                .tokenLocation(OAUTH_SERVER + Play.configuration.getProperty("oauth.token"))
                .setRedirectURI(URL_BASE + URL_CALLBACK)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setCode(code)
                .buildBodyMessage();

        String base64 = Base64.encodeBase64String((CLIENT_ID + ":" + CLIENT_SECRET).getBytes());
        request.addHeader("Authorization", "Basic " + base64);

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
        OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(request);

        // Send user data request
        String accessToken = oAuthResponse.getAccessToken();

        URL url = new URL(OAUTH_SERVER + RESOURCE_USER + "?access_token=" + accessToken);
        InputStream input = url.openStream();
        Reader reader = new InputStreamReader(input, "UTF-8");
        OAuthUserModel user  = new Gson().fromJson(reader, OAuthUserModel.class);
        authenticateDarwin(user);
    }

    private static void authenticateDarwin(OAuthUserModel username) throws Throwable {
        // Check tokens
        User user = DarwinFactory.getInstance().loadUser(username.getEmail());
        if (user == null) {
            user = DarwinFactory.getInstance().buildUser(username.getDisplayName(), username.getEmail(), randomString(10));
            user.setActive(true);
            user.save();
        }

        SecurityOAuth security = new SecurityOAuth();
        security.authenticateUserWithOAuth(user);
        flash.error("secure.error");
        params.flash();
        redirect("Secure.login");
    }

    public static void redirect() throws OAuthSystemException {
        Logger.debug("redirect");
        OAuthClientRequest request = OAuthClientRequest
                .authorizationLocation(OAUTH_SERVER + Play.configuration.getProperty("oauth.authorize"))
                .setResponseType("code")
                .setClientId(CLIENT_ID)
                .setRedirectURI(URL_BASE + URL_CALLBACK)
                .buildQueryMessage();

        redirect(request.getLocationUri());

    }

    private static String randomString(int len) {
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
}
