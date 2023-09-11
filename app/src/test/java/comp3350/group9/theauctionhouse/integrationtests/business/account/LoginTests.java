package comp3350.group9.theauctionhouse.integrationtests.business.account;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class LoginTests {

    UserAccountManager userManager;

    @Before
    @Test
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        userManager = UserAccountManager.of();

        try {
            userManager.registerUser(
                    "Username",
                    "user@myumanitoba.ca",
                    "Password123"
            );
        } catch (UserRegistrationException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testShouldLogin() throws UserLoginException {
        userManager.loginUser("Username", "Password123");
    }

    @Test(expected = UserLoginException.class)
    public void testShouldThrowAndFailToLoginWithUsernameNotFound() throws UserLoginException {
        userManager.loginUser("Dont exist", "Fail");
    }

    @Test(expected = UserLoginException.class)
    public void testShouldThrowAndFailToLoginWithInvalidPassword() throws UserLoginException {
        userManager.loginUser("Username", "notThePassword!");
    }
}
