package tests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Ignore;
import play.mvc.Http;
import play.mvc.Scope;
import play.test.FunctionalTest;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.UnknownHostException;

@Ignore
public class BaseTestFunctional extends FunctionalTest {

  protected static Http.Cookie sessionToken;

  protected static void doMongoImport(String collectionName, String jsonFilePath) throws UnknownHostException, IOException {
    BaseTest.doMongoImport(collectionName, jsonFilePath);
  }

  protected static void doMongoDrop(String collectionName) {
    BaseTest.doMongoDrop(collectionName);
  }

  protected static Http.Request doLogin(String user, String password) throws IOException, ParserConfigurationException {
    if (sessionToken == null) {
      Http.Response login = GET(newRequest(), "/login");
      String authenticityToken = getAuthenticityToken(login);
      Http.Response auth = POST("/login?username=" + user + "&password=" + password + "&authenticityToken=" + authenticityToken);
      sessionToken = auth.cookies.get("SINFONIER_SESSION");
    }

    Http.Request r = newRequest();
    r.cookies.put(sessionToken.name, sessionToken);

    return r;
  }

  protected static void doLogout() {
    POST("/logout");
    sessionToken = null;
  }

  private static String getAuthenticityToken(Http.Response res) {
    Document doc = Jsoup.parse(res.out.toString());
    Element form = doc.getElementsByAttributeValue("action", "/login").get(0);
    Element input = form.getElementsByAttributeValue("name", "authenticityToken").get(0);
    return input.val();
  }
}
