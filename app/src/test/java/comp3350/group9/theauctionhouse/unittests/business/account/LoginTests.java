package comp3350.group9.theauctionhouse.unittests.business.account;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.User;

@RunWith(MockitoJUnitRunner.Silent.class)

public class LoginTests {
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
    public void testShouldLogin() throws UserLoginException {
        when(userQueriable.findByUsername(user.username())).thenReturn(user);
        profile.loginUser(user.username(), user.password());
        verify(userQueriable).findByUsername(user.username());
    }

    @Test(expected = UserLoginException.class)
    public void testShouldThrowAndFailToLoginWithUsernameNotFound() throws UserLoginException {
        String username = "user not found";
        when(userQueriable.findByUsername(username)).thenReturn(null);
        profile.loginUser(username,"123456");
        verify(userQueriable).findByUsername(username);
        fail("Expected UserLoginException, but none was thrown");
    }

    @Test(expected = UserLoginException.class)
    public void testShouldThrowAndFailToLoginWithInvalidPassword() throws UserLoginException {
        when(userQueriable.findByUsername(user.username())).thenReturn(user);
        profile.loginUser(user.username(),"");
        verify(userQueriable).findByUsername(user.username());
        fail("Expected UserLoginException, but none was thrown");
    }
}
