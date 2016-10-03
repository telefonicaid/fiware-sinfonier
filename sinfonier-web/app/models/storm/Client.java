package models.storm;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import exceptions.SinfonierError;
import exceptions.SinfonierException;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import play.Logger;
import play.Play;
import play.libs.WS;

public class Client {
  private static Client instance;
  private static final String HOSTNAME = Play.configuration.getProperty("storm.host");
  private static final String PORT = Play.configuration.getProperty("storm.port");
  private static final String BASE_URL = HOSTNAME + ":" + PORT;
  private static final int MAX_WORDS_PER_LINE = 12;

  private enum Methods {POST, GET, DELETE}

  private Client() {
  }

  public boolean isOk() {
    WS.WSRequest request = WS.url(BASE_URL + "/");
    JsonObject res = null;

    try {
      res = doRequest(request, Methods.POST);
    } catch (SinfonierException e) {
      Logger.error(e.getMessage());
    }

    if (res != null && res.get("code").getAsInt() == 200) {
      return true;
    } else {
      return false;
    }
  }

  public JsonObject uploadModule(String id, String versionId) throws SinfonierException {
    String path = Play.configuration.getProperty("storm.route.modules.upload");
    WS.WSRequest request = WS.url(BASE_URL + buildPathById(buildPathByVersionId(path, versionId), id));

    JsonObject res = doRequest(request, Methods.POST);

    if (res != null) {
      return res.getAsJsonObject();
    } else {
      throw new SinfonierException(SinfonierError.INVALID_REQUEST);
    }
  }

  public JsonObject validateModule(String id, String versionId) throws SinfonierException {
    String path = Play.configuration.getProperty("storm.route.modules.validate");
    WS.WSRequest request = WS.url(BASE_URL + buildPathById(buildPathByVersionId(path, versionId), id));

    JsonObject res = doRequest(request, Methods.POST);

    if (res != null) {
      return res.getAsJsonObject();
    } else {
      throw new SinfonierException(SinfonierError.INVALID_REQUEST);
    }
  }

  public String getTopologyLog(String id) throws SinfonierException {
    String path = Play.configuration.getProperty("storm.route.topologies.log");
    WS.WSRequest request = WS.url(BASE_URL + buildPathById(path, id));

    JsonObject res = doRequest(request, Methods.GET);
    try {
      return sanitise(res.get("data").getAsJsonObject().get("log").getAsString());
    } catch (RuntimeException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_RESPONSE);
    }
  }

  public JsonObject topologyLaunch(String id) throws SinfonierException {
    String path = Play.configuration.getProperty("storm.route.topologies.launch");
    WS.WSRequest request = WS.url(BASE_URL + buildPathById(path, id));

    JsonObject res = doRequest(request, Methods.POST);
    if (res != null) {
      return res.getAsJsonObject();
    } else {
      throw new SinfonierException(SinfonierError.INVALID_REQUEST);
    }
  }

  public JsonObject topologyStop(String id) throws SinfonierException {
    String path = Play.configuration.getProperty("storm.route.topologies.stop");
    WS.WSRequest request = WS.url(BASE_URL + buildPathById(path, id));

    JsonObject res = doRequest(request, Methods.POST);
    if (res != null) {
      return res.getAsJsonObject();
    } else {
      throw new SinfonierException(SinfonierError.INVALID_REQUEST);
    }
  }

  private JsonObject doRequest(WS.WSRequest request, Methods method) throws SinfonierException {
    WS.HttpResponse response;

    try {
      if (method == Methods.GET) {
        response = request.get();
      } else if (method == Methods.POST) {
        response = request.post();
      } else {
        throw new SinfonierException(SinfonierError.INVALID_REQUEST);
      }
    } catch (RuntimeException e) {
      Logger.error(e.getLocalizedMessage());
      throw new SinfonierException(SinfonierError.INVALID_REQUEST);
    }

    try {
      return response.getJson().getAsJsonObject();
    } catch (JsonSyntaxException e) {
      Logger.error(e.getMessage());
      throw new SinfonierException(SinfonierError.INVALID_RESPONSE);
    }
  }

  private static String buildPathByVersionId(String url, String versionId) {
    if (url != null) {
      return url.replace("{version_code}", versionId);
    }

    return null;
  }

  private static String buildPathById(String url, String id) {
    if (url != null) {
      return url.replace("{id}", id);
    }

    return null;
  }

  private String sanitise(String str) {
    if (str == null)
      return "";

    String _str = Jsoup.clean(str, Whitelist.basic());
    String separator = System.getProperty("line.separator");

    if ((!_str.contains("\n") || !_str.contains(separator)) && StringUtils.split(_str).length > MAX_WORDS_PER_LINE) {
      String[] words = StringUtils.split(_str);
      int counterPerLine = 0;
      _str = "";

      for (int i = 0; i < words.length; i++) {
        if (counterPerLine > MAX_WORDS_PER_LINE) {
          _str += separator;
          counterPerLine = 0;
        }

        _str += words[i] + " ";
        counterPerLine++;

        if (i == words.length - 1) {
          _str += separator;
          break;
        }
      }
    }

    return _str;
  }

  public static synchronized Client getInstance() {
    if (instance == null) {
      instance = new Client();
    }

    return instance;
  }
}
