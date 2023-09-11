package comp3350.group9.theauctionhouse.integrationtests.business.auctioning;


import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.business.auctioning.AuctionInfo;
import comp3350.group9.theauctionhouse.business.auctioning.AuctionTagDTO;
import comp3350.group9.theauctionhouse.business.auctioning.CreateAuction;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.ProductQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.exception.auction.DuplicateAuction;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.unittests.business.auctioning.TestAuction;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class CreateAuctionTests {


    private QueryEngine queryEngine;

    private UserQueriable userQueriable;

    private AuctionQueriable auctionQueriable;

    private ProductQueriable productQueriable;

    private CreateAuction createAuction;
    private User user;
    private Product product;
    private Product product2;
    @Before
    @Test
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        queryEngine = HSQLDBFactory.get();

        user = new User("1", "JohnDoe", "john@myumanitoba.ca", "12345678");
        userQueriable = queryEngine.users();
        auctionQueriable = queryEngine.auctions();
        productQueriable = queryEngine.products();

        UserAccountManager profile = UserAccountManager.of();

        try {
            profile.registerUser(user.username(), user.email(), user.password());
        } catch (UserRegistrationException e) {
            fail("Profile should be created!");
        }
        try {
            profile.loginUser(user.username(), user.password());
        } catch (UserLoginException e) {
            fail("Should be able to login!");
        }

        createAuction = new CreateAuction(queryEngine, profile);

        product = new Product("1","test product");
        product2 = new Product("2","test product");
        productQueriable.addProduct(product);
        productQueriable.addProduct(product2);
    }

    @Test
    public void testShouldCreateAuction() throws CreateAuctionException {
        createAuction.invoke(TestAuction.of(product).toAuctionInfo(),product);
    }

    @Test(expected = DuplicateAuction.class)
    public void testShouldThrowOnDuplicateAuctionTitle() throws CreateAuctionException {
        createAuction.invoke(TestAuction.of(product).toAuctionInfo(),product);
        createAuction.invoke(TestAuction.of(product2).toAuctionInfo(),product2);
        fail("Expected DuplicateAuction, but none was thrown.");
    }

    @Test
    public void testShouldThrowWhenTitleHasOnlySpaces(){
        assertThrows(
                "Empty title should throw CreateAuctionException but none was thrown",
                CreateAuctionException.class, () ->
                        createAuction.invoke(TestAuction.of(product).title("  ").toAuctionInfo(),product)
        );
    }

    @Test
    public void testShouldThrowWhenMinBidLessThanZero() {
        assertThrows(
                "Negative bid should throw CreateAuctionException but none was thrown",
                CreateAuctionException.class,
                () -> createAuction.invoke(TestAuction.of(product).minBid(-100.0).toAuctionInfo(),product)
        );
    }

    @Test
    public void testShouldThrowWhenMinBidEqualToZero() {
        assertThrows(
                "Bid of 0 should throw CreateAuctionException but none was thrown",
                CreateAuctionException.class,
                () -> createAuction.invoke(TestAuction.of(product).minBid(0).toAuctionInfo(),product)
        );
    }

    @Test
    public void testShouldThrowWhenExpiryIsNow() {
        assertThrows(
                "Today as expiry should throw CreateAuctionException but none was thrown",
                CreateAuctionException.class,
                () -> createAuction.invoke(TestAuction.of(product).expiry(DateTime.now()).toAuctionInfo(),product)
        );
    }

    @Test
    public void testShouldThrowWhenExpiryIsBeforeNow() {
        assertThrows(
                "Yesterday as expiry should throw CreateAuctionException but none was thrown",
                CreateAuctionException.class,
                () -> createAuction.invoke(TestAuction.of(product).expiry(DateTime.now().addDays(-1)).toAuctionInfo(),product)
        );
    }

    @Test(expected = InvalidParameterException.class)
    public void testShouldThrowWithInvalidTags() throws CreateAuctionException {
        List<AuctionTagDTO> tags = new ArrayList<AuctionTagDTO>() {{
            add(new AuctionTagDTO("1", "Pen"));
        }};

        AuctionInfo info = AuctionInfo.builder()
                .title("Test Title")
                .minBid(20)
                .expiry(DateTime.now().addDays(365))
                .seller(user)
                .tags(tags)
                .build();

        createAuction.invoke(info,product);
        fail("Expected InvalidParameterException, but none was thrown");
    }
}
