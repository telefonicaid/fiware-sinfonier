package models.responses;

import com.google.gson.JsonObject;

public enum Codes {
  CODE_200("OK", 200),
  CODE_400("NOOK", 400),
  CODE_404("Not Found", 404),
  CODE_500("NOOK", 500);

  private final String status;
  private final Integer code;
  private JsonObject data;

  Codes(String status, Integer code, JsonObject data) {
    this.status = status;
    this.code = code;
    this.data = data;
  }

  Codes(String status, Integer code) {
    this(status, code, null);
  }

  public String getStatus() {
    return status;
  }

  public Integer getCode() {
    return code;
  }

  public JsonObject getData() {
    return data;
  }

  public void setData(JsonObject data) {
    this.data = data;
  }

  public JsonObject toGSON() {
    JsonObject object = new JsonObject();

    object.addProperty("status", status);
    object.addProperty("code", code);

    if (data != null) object.add("data", data);

    return object;
  }
}
