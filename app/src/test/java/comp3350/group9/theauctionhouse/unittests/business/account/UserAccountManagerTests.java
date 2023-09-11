package comp3350.group9.theauctionhouse.unittests.business.account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.User;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserAccountManagerTests {
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
    public void testShouldGetUserByEmail() {
        when(queryEngine.users().findByEmail(user.email())).thenReturn(user);
        assertNotNull(profile.getUserByEmail(user.email()));
        verify(userQueriable).findByEmail(user.email());
    }

    @Test
    public void testShouldFailToGetUserByEmailNotFound() {
        String email = "notfound@myumanitoba.ca";
        when(userQueriable.findByEmail(email)).thenReturn(null);
        assertNull(profile.getUserByEmail(email));
        verify(userQueriable).findByEmail(email);
    }

    @Test
    public void testShouldGetAccountByUsername() {
        when(userQueriable.findByUsername(user.username())).thenReturn(user);
        User result = profile.getUserByUsername(user.username());
        assertEquals(user.username(), result.username());
        verify(userQueriable).findByUsername(user.username());
    }

    @Test
    public void testShouldFailToGetUserAccountByUsernameNotFound() {
        assertNull(profile.getUserByUsername("XXX"));
        verify(userQueriable).findByUsername("XXX");
    }
}
