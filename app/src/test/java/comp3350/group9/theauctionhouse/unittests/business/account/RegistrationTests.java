package comp3350.group9.theauctionhouse.unittests.business.account;

import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.User;

@RunWith(MockitoJUnitRunner.Silent.class)

public class RegistrationTests {
    @Mock
    private QueryEngine queryEngine;
    @Mock
    private UserQueriable userQueriable;
    private UserAccountManager profile;

    private User user;

    @Before
    public void init() {
        user = new User("1", "JohnDoe", "john@myumanitoba.ca", "12345678");
        when(queryEngine.users()).thenReturn(userQueriable);
        profile = new UserAccountManager(queryEngine);
    }

    @Test
    public void testShouldRegister() throws UserRegistrationException {
        when(userQueriable.add(any(User.class))).thenReturn(true);
        when(userQueriable.findByUsername("JaneDoe")).thenReturn(null);
        when(userQueriable.findByEmail("jane@myumanitoba.ca")).thenReturn(null);
        profile.registerUser("JaneDoe", "jane@myumanitoba.ca", "123456789");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithInvalidUsername() throws UserRegistrationException {
        when(userQueriable.findByUsername(anyString())).thenReturn(null);
        when(userQueriable.findByEmail(anyString())).thenReturn(null);
        profile.registerUser("1", "jane@myumanitoba.com", "12345678");
        fail("Expected UserRegistrationException, but none was thrown");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithInvalidEmail() throws UserRegistrationException {
        when(userQueriable.findByUsername(anyString())).thenReturn(null);
        when(userQueriable.findByEmail(anyString())).thenReturn(null);
        profile.registerUser("JaneDoe", "jane@gmail.com", "12345678");
        fail("Expected UserRegistrationException, but none was thrown");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithDuplicateEmailAndNewUsername() throws UserRegistrationException {
        String username = "JaneDoe";
        when(userQueriable.findByUsername(user.username())).thenReturn(null);
        when(userQueriable.findByEmail(user.email())).thenReturn(user);
        profile.registerUser(username, user.email(), user.password());
        fail("Expected UserRegistrationException, but none was thrown");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithDuplicateUsernameAndNewEmail() throws UserRegistrationException {
        String email = "jane@myumanitoba";
        when(userQueriable.findByUsername(user.username())).thenReturn(user);
        when(userQueriable.findByEmail(email)).thenReturn(null);
        profile.registerUser(user.username(), email, user.password());
        fail("Expected UserRegistrationException, but none was thrown");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithDuplicateEmail() throws UserRegistrationException {
        when(userQueriable.findByEmail(user.email())).thenReturn(user);
        profile.registerUser(user.username(), user.email(), user.password());
        fail("Expected UserRegistrationException, but none was thrown");
    }

    @Test(expected = UserRegistrationException.class)
    public void testShouldThrowAndFailToRegisterWithDuplicateUsername() throws UserRegistrationException {
        when(userQueriable.findByUsername(user.username())).thenReturn(user);
        profile.registerUser(user.username(), user.email(), user.password());
        fail("Expected UserRegistrationException, but none was thrown");
    }

}
