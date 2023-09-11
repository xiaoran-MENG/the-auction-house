package comp3350.group9.theauctionhouse.integrationtests.business.auctioning;


import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.business.auctioning.AuctionCompletionService;
import comp3350.group9.theauctionhouse.business.auctioning.CreateAuction;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.BidQueriable;
import comp3350.group9.theauctionhouse.core.application.ProductQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CannotCompleteAuctionException;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.exception.auction.PlaceBidException;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.unittests.business.auctioning.TestAuction;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class AuctionCompletionServiceTests {
    private QueryEngine queryEngine;

    private AuctionQueriable auctionQueriable;
    private ProductQueriable productQueriable;
    private BidQueriable bidQueriable;

    private AuctionCompletionService service;
    private User authUser;
    private Auction defaultAuction;
    private CreateAuction createAuction;
    private User bidder1;
    private User bidder2;

    private Product product;
    private Product product2;
    @Before
    @Test
    public void setup() throws CreateAuctionException {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        queryEngine = HSQLDBFactory.get();

        auctionQueriable = queryEngine.auctions();
        productQueriable = queryEngine.products();
        bidQueriable = queryEngine.bids();

        UserQueriable userQueriable = queryEngine.users();

        UserAccountManager profile = UserAccountManager.of();

        authUser = new User("1", "JohnDoes", "john@myumanitoba.ca", "12345678");
        bidder1 = new User("2", "JanesDoes", "jane@myumanitoba.ca", "12345678");
        bidder2 = new User("3", "jonnyDoes", "jones@myumanitoba.ca", "12345678");

        try {
            profile.registerUser(authUser.username(), authUser.email(), authUser.password());
            profile.registerUser(bidder1.username(), bidder1.email(), bidder1.password());
            profile.registerUser(bidder2.username(), bidder2.email(), bidder2.password());
        } catch (UserRegistrationException e) {
            fail("Profile should be created!");
        }

        try {
            profile.loginUser(authUser.username(), authUser.password());
        } catch (UserLoginException e) {
            fail("Profile should be created!");
        }

        createAuction = new CreateAuction(queryEngine, profile);

        defaultAuction = Auction.builder()
                .id("1")
                .seller(authUser)
                .expiry(DateTime.now().addDays(10))
                .minBid(19)
                .title("title1")
                .build();

        product = new Product("1","test product");
        product2 = new Product("2","test product");
        productQueriable.addProduct(product);
        productQueriable.addProduct(product2);

        createAuction.invoke(TestAuction.of(product).toAuctionInfo(), product);

        service = new AuctionCompletionService(queryEngine, profile);
    }

    @Test
    public void testCompleteAuctionPickBid() throws CannotCompleteAuctionException, PlaceBidException {
        defaultAuction.addBid(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));
        defaultAuction.addBid(new Bid("2",bidder2,defaultAuction,22, Bid.Status.PENDING));

        bidQueriable.add(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));
        bidQueriable.add(new Bid("2",bidder2,defaultAuction,22, Bid.Status.PENDING));

        service.completeAuction("1","1");
    }

    @Test
    public void testCompleteAuctionNoSuchBid() throws PlaceBidException {
        defaultAuction.addBid(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));
        defaultAuction.addBid(new Bid("2",bidder2,defaultAuction,22, Bid.Status.PENDING));

        bidQueriable.add(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));
        bidQueriable.add(new Bid("2",bidder2,defaultAuction,22, Bid.Status.PENDING));

        assertThrows("No such bid, should throw CannotCompleteException",
                CannotCompleteAuctionException.class,
                () -> service.completeAuction("1","3"));
    }

    @Test
    public void testAlreadyCompletedAuction() throws PlaceBidException, CannotCompleteAuctionException {
        defaultAuction.addBid(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));
        defaultAuction.addBid(new Bid("2",bidder2,defaultAuction,22, Bid.Status.PENDING));

        bidQueriable.add(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));
        bidQueriable.add(new Bid("2",bidder2,defaultAuction,22, Bid.Status.PENDING));

        service.completeAuction("1","1");

        assertThrows("Auction already complete, should throw CannotCompleteException",
                CannotCompleteAuctionException.class,
                () -> service.completeAuction("1","1"));
    }
}
