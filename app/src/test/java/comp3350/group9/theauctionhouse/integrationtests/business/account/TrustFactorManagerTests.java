package comp3350.group9.theauctionhouse.integrationtests.business.account;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import comp3350.group9.theauctionhouse.business.accounts.TrustFactorManager;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class TrustFactorManagerTests {
    User userToRate;
    TrustFactorManager trustFactorManager;
    TrustFactor userToRateTrust;

    @Before
    @Test
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        UserAccountManager userManager = UserAccountManager.of();

        try {
            userManager.registerUser("ValidUsername", "validemail@myumanitoba.ca", "ValidPassword1");
        } catch (UserRegistrationException e) {
            assertTrue(false);
        }

        userToRate = UserAccountManager.of().getUserByUsername("ValidUsername");
        trustFactorManager = TrustFactorManager.get();
        userToRateTrust = new TrustFactor("1", userToRate.id());
    }

    @Test
    public void testValidRate() {
        userToRateTrust.addRating(5);

        assert(trustFactorManager.addRating(userToRateTrust));
    }


    @Test
    public void testMatchingRating() {
        userToRateTrust.addRating(3);
        trustFactorManager.addRating(userToRateTrust);

        assert(userToRateTrust.getAverageRating() == trustFactorManager.getByUserId(userToRate.id()).getAverageRating());
    }
}