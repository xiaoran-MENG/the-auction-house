package comp3350.group9.theauctionhouse.integrationtests.business.bidding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import comp3350.group9.theauctionhouse.business.auctioning.AuctionInfo;
import comp3350.group9.theauctionhouse.business.auctioning.CreateAuction;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.bidding.BidsHistory;
import comp3350.group9.theauctionhouse.business.bidding.PlaceBid;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.exception.auction.PlaceBidException;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class PlaceBidTests {

    private User u1;
    private User u2;
    BidsHistory history;
    CreateAuction auction;

    @Before
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        history = BidsHistory.get();
        UserAccountManager userManager = UserAccountManager.of();

        try {
            userManager.registerUser("ValidUsername1", "validemail@myumanitoba.ca", "ValidPassword1");
            userManager.registerUser("ValidUsername2", "validemail2@myumanitoba.ca", "ValidPassword1");
        } catch (UserRegistrationException e) {
            assertTrue(false);
        }

        try {
            userManager.loginUser("ValidUsername1", "ValidPassword1");
        } catch (UserLoginException e) {
            assertTrue(false);
        }

        auction = CreateAuction.of();

        try {
            auction.invoke(AuctionInfo.builder()
                            .title("test")
                            .description("descriptions")
                            .seller(userManager.getUserByUsername("ValidUsername1"))
                            .minBid(100.0)
                            .expiry(DateTime.now().addDays(1))
                            .build(),
                    new Product("1", "Test product!")
            );
        } catch (CreateAuctionException e) {
            assertTrue(false);
        }

        try {
            auction.invoke(AuctionInfo.builder()
                    .title("test2")
                    .description("descriptions2")
                    .seller(userManager.getUserByUsername("ValidUsername2"))
                    .minBid(100.0)
                    .expiry(DateTime.now().addDays(1))
                    .build(),
                    new Product("2", "Test product2!")
            );
        } catch (CreateAuctionException e) {
            assertTrue(false);
        }
        u1 = userManager.getUserByUsername("ValidUsername1");
        u2 = userManager.getUserByUsername("ValidUsername2");
    }

    @Test
    public void testWhenPriceEqualToMinBid() {
        try {
            PlaceBid.of(HSQLDBFactory.get(), u2).on("1").with(100.0).invoke();
        } catch (PlaceBidException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testWhenPriceHigherThanMinBid() {
        try {
            PlaceBid.of(HSQLDBFactory.get(), u2).on("1").with(100.1).invoke();
        } catch (PlaceBidException e) {
            assertTrue(false);
        }
    }

    @Test
    public void testWhenPriceLowerThanMinBid() {
        assertThrows(PlaceBidException.class, () ->
                PlaceBid.of(HSQLDBFactory.get(), u2).on("1").with(99.0).invoke());
    }

    @Test
    public void testWhenPriceIsNegative() {
        assertThrows(PlaceBidException.class, () ->
                PlaceBid.of(HSQLDBFactory.get(), u2).on("1").with(-1.0).invoke());
    }

    @Test
    public void testWhenBiddingOnOwnAuction() {
        assertThrows(PlaceBidException.class, () ->
                PlaceBid.of(HSQLDBFactory.get(), u1).on("1").with(100.0).invoke());
    }

}