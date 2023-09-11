package comp3350.group9.theauctionhouse.integrationtests.persistance.account;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;

import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class TestUserPersistence {
    @Before
    @Test
    public void setup(){
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testInsert(){
        QueryEngine database = HSQLDBFactory.get();
        try {
            database.users().add(User.tryCreateAccount("username1","email@myumanitoba.ca","password"));
            User u = database.users().findById("1");
            assertEquals("username1",u.username());
        } catch (UserRegistrationException e) {
            fail("Failed to create user: "+ e.getMessage());
        }
    }

    @After
    @Test
    public void cleanup(){
        try {
            IntegrationTesting.tearDown();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
