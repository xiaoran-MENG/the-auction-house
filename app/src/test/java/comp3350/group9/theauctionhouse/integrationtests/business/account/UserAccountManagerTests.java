package comp3350.group9.theauctionhouse.integrationtests.business.account;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class UserAccountManagerTests {
    private UserAccountManager userAccountManager;

    @Before
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        userAccountManager = UserAccountManager.of();
    }

    @Test
    public void testValidAccountRegistration() {
        registerUser("Username507", "uofmtester@myumanitoba.ca", "Password");
    }

    @Test
    public void testInvalidUsernameAccountRegistration() {
        assertThrows(UserRegistrationException.class, () -> userAccountManager.registerUser(
                "1",
                "uofm@myumanitoba.ca",
                "Password"
        ));
    }

    @Test
    public void testInvalidEmailAccountRegistration() {
        assertThrows(UserRegistrationException.class, () -> userAccountManager.registerUser(
                "Username131231",
                "gmail@gmail.ca",
                "Password"
        ));
    }

    @Test
    public void testInvalidPasswordAccountRegistration() {
        assertThrows(UserRegistrationException.class, () -> userAccountManager.registerUser(
                "Username123123",
                "uofm@myumanitoba.ca",
                "123456"
        ));
    }

    @Test
    public void testInvalidDoubleUsernameAccountRegistration() {
        registerUser("Username", "uofm@myumanitoba.ca", "Password");
        assertThrows(UserRegistrationException.class, () -> userAccountManager.registerUser(
                "Username",
                "other@myumanitoba.ca",
                "Password"
        ));
    }

    @Test
    public void testInvalidDoubleEmailAccountRegistration() {
        registerUser("UsernameTest", "uofm@myumanitoba.ca", "Password");
        assertThrows(UserRegistrationException.class, () -> userAccountManager.registerUser(
                "UsernameTest",
                "uofm@myumanitoba.ca",
                "Password"
        ));
    }

    @Test
    public void testValidLogin() throws UserLoginException {
        registerUser("Username","UserLoginException@myumanitoba.ca","Password");
        User u = userAccountManager.loginUser("Username", "Password");
    }

    @Test
    public void testInvalidUsernameLogin() {
        assertThrows(UserLoginException.class, () -> userAccountManager.loginUser(
                "notInTheDatabase",
                "Password"));
    }

    @Test
    public void testInvalidPasswordLogin() {
        assertThrows(UserLoginException.class, () -> userAccountManager.loginUser(
                "Username",
                "123456"));
    }

    @Test
    public void testValidGetUserAccountByEmail(){
        User foundAccount = userAccountManager.getUserByEmail("user@myumanitoba.ca");
    }

    @Test
    public void testInvalidGetUserAccountByEmail(){
        assertNull(userAccountManager.getUserByEmail("notuser@myumanitoba.ca"));
    }

    @Test
    public void testValidGetUserAccountByUsername(){
        User foundAccount = userAccountManager.getUserByUsername("Username");
    }

    @Test
    public void testInvalidGetUserAccountByUsername() {
        assertNull(userAccountManager.getUserByUsername("NotAUsername"));
    }

    private void registerUser(String username, String email, String password) {
        try {
            userAccountManager.registerUser(
                    username,
                    email,
                    password
            );
        } catch (UserRegistrationException e) {
            fail("Failed to register User." + e.getMessage());
        }
    }
}
