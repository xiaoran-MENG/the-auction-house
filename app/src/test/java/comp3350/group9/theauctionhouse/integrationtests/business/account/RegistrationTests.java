package comp3350.group9.theauctionhouse.integrationtests.business.account;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class RegistrationTests {
    private UserAccountManager userManager;
    private User user;

    @Before
    @Test
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }
        user = new User("1", "JohnDoe", "john@myumanitoba.ca", "12345678");
        userManager = UserAccountManager.of();
    }

    @Test
    public void testShouldRegister() throws UserRegistrationException {
        userManager.registerUser("JaneDoe", "jane@myumanitoba.ca", "123456789");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithInvalidUsername() throws UserRegistrationException {
        userManager.registerUser("1", "jane@myumanitoba.com", "12345678");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithInvalidEmail() throws UserRegistrationException {
        userManager.registerUser("JaneDoe", "jane@gmail.com", "12345678");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithDuplicateEmail() throws UserRegistrationException {
        userManager.registerUser(user.username(), user.email(), user.password());
        userManager.registerUser("NewUser", user.email(), user.password());
        fail("Expected UserRegistrationException, but none was thrown");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithDuplicateUsername() throws UserRegistrationException {
        userManager.registerUser(user.username(), user.email(), user.password());
        userManager.registerUser(user.username(), "testing@myumanitoba.ca", user.password());
        fail("Expected UserRegistrationException, but none was thrown");
    }

}
