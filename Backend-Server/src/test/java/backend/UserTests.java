package backend;

import backend.token.TokenService;
import backend.user.User;
import backend.user.UserController;
import backend.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserTests {

	@Mock
	private UserService userService;

	@Mock
	private TokenService tokenService;

	@InjectMocks
	private UserController userController;

	@Test
	public void testGetUsers() {
		// Arrange
		User user = new User("testuser", "test@test.com", "testing");


		// Assert
		Assertions.assertEquals("success", userController.registerUser(user));
//		Assertions.assertEquals(user, userService.registerUser(user));
	}

}