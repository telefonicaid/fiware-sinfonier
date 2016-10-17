package tests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import models.user.User;

public class TestMock {

  public static User mockUser(String id, boolean isAdmin) {
    User mockUser = mock(User.class);
    when(mockUser.getId()).thenReturn(id);
    when(mockUser.isAdminUser()).thenReturn(isAdmin);
    
    return mockUser;
  }
}
