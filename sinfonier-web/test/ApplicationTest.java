import org.junit.*;
import play.test.*;
import play.mvc.*;
import play.mvc.Http.*;
import models.user.User;

public class ApplicationTest extends FunctionalTest {
  
  @Test
  public void testThatLoginPageWorks() {
    Response response = GET("/login");
    assertIsOk(response);
    assertContentType("text/html", response);
    assertCharset(play.Play.defaultWebEncoding, response);
  }
    
}