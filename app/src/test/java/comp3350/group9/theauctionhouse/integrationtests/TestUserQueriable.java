package comp3350.group9.theauctionhouse.integrationtests;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;

public class TestUserQueriable {
    UserQueriable userPersistence;

    @Before
    public void setup(){
        userPersistence = HSQLDBFactory.get().users();
    }

    @Test
    public void testInsert(){
        try {
            assertTrue(userPersistence.add(User.tryCreateAccount("username1","email@myumanitoba.ca","password")));
            User u = userPersistence.findByUsername("username1");
            assertEquals("username1",u.username());
            userPersistence.deleteByUsername("username1"); //clean up
        } catch (UserRegistrationException e) {
            fail("Failed to create user: "+ e.getMessage());
        }
    }
}
