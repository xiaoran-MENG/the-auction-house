package comp3350.group9.theauctionhouse.integrationtests.business.account;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.domain.TrustFactor;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;


public class TrustFactorTests {
    User userToRate;

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

    }

    @Test
    public void testNewTrustFactor() {
        TrustFactor trust = new TrustFactor("1",userToRate.id());

        assertTrue(trust.getAverageRating() == 0);
        assertTrue(trust.getReviewAmount() == 0);
    }

    @Test
    public void testTrustFactorAverages() {
        TrustFactor trust = new TrustFactor("1",userToRate.id());
        trust.addRating(2);

        assertTrue(trust.getAverageRating() == 2);
        assertTrue(trust.getReviewAmount() == 1);

        trust.addRating(4);

        assertTrue(trust.getAverageRating() == 3);
        assertTrue(trust.getReviewAmount() == 2);
    }


    @Test
    public void testTrustFactorRatings() {
        TrustFactor trust = new TrustFactor("1",userToRate.id());
        trust.addRating(2);
        trust.addRating(5);

        final ArrayList<TrustFactor.Rating> ratings = trust.getPastRatings();
        assertTrue(ratings.get(0).getRating() == 2);
        assertTrue(ratings.get(1).getRating() == 5);
    }

    @Test
    public void testTrustFactorRating() {
        TrustFactor trust = new TrustFactor("1",userToRate.id());
        trust.addRating(-100);

        assertTrue(trust.getAverageRating() == 1);
        assertTrue(trust.getReviewAmount() == 1);

        trust.addRating(-100);

        assertTrue(trust.getAverageRating() == 1);
        assertTrue(trust.getReviewAmount() == 2);
    }
}
