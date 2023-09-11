package comp3350.group9.theauctionhouse.unittests.domain.account;

import org.junit.Test;

import static org.junit.Assert.*;

import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.core.domain.User;

public class UserTests {
    @Test
    public void testValidAccountCreation() throws UserRegistrationException {
        User newAccount = User.tryCreateAccount(
                "tester",
                "uofmemail@myumanitoba.ca",
                "password"
        );
    }

    @Test
    public void testInvalidUsernameAccountCreation() {
        assertThrows(UserRegistrationException.class, () -> {
            User u = User.tryCreateAccount(
                    "1",
                    "uofmemail@myumanitoba.ca",
                    "password"
            );
        });
    }

    @Test
    public void testInvalidEmailAccountCreation() {
        assertThrows(UserRegistrationException.class, () -> {
            User u = User.tryCreateAccount(
                    "tester",
                    "gmailemail@gmail.ca",
                    "password"
            );
        });
    }

    @Test
    public void testInvalidPasswordAccountCreation() {
        assertThrows(UserRegistrationException.class, () -> {
            User u = User.tryCreateAccount(
                    "tester",
                    "uofmemail@myumanitoba.ca",
                    "123456"
            );
        });
    }

    @Test
    public void testValidLogin() throws UserRegistrationException {
        User newAccount = User.tryCreateAccount(
                "tester",
                "uofmemail@myumanitoba.ca",
                "password"
        );

        assertTrue(newAccount.tryLogin("password"));
    }

    @Test
    public void testNullLogin() throws UserRegistrationException {
        User newAccount = User.tryCreateAccount(
                "tester",
                "uofmemail@myumanitoba.ca",
                "password"
        );

        assertFalse(newAccount.tryLogin(null));
    }

    @Test
    public void testWrongLogin() throws UserRegistrationException {
        User newAccount = User.tryCreateAccount(
                "tester",
                "uofmemail@myumanitoba.ca",
                "password"
        );

        assertFalse(newAccount.tryLogin("notThePassword"));
    }
}
