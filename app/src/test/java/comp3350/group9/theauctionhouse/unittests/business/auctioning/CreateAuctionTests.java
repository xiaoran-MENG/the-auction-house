package comp3350.group9.theauctionhouse.unittests.business.auctioning;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.business.auctioning.AuctionInfo;
import comp3350.group9.theauctionhouse.business.auctioning.AuctionTagDTO;
import comp3350.group9.theauctionhouse.business.auctioning.CreateAuction;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.ProductQueriable;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.exception.auction.DuplicateAuction;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;

@RunWith(MockitoJUnitRunner.Silent.class)

public class CreateAuctionTests {
    @Mock
    private QueryEngine queryEngine;
    @Mock
    private AuctionQueriable auctionQueriable;
    @Mock
    private ProductQueriable productQueriable;

    private CreateAuction createAuction;
    private User user;
    private Product product;

    @Before
    public void init() {
        when(queryEngine.auctions()).thenReturn(auctionQueriable);
        when(queryEngine.products()).thenReturn(productQueriable);
        when(auctionQueriable.add(any(Auction.class))).thenReturn(true);

        user = new User("1", "John Doe", "john@myumanitoba.ca", "12345678");
        UserQueriable userQueriable = mock(UserQueriable.class);
        when(queryEngine.users()).thenReturn(userQueriable);
        when(queryEngine.users().findById(user.id())).thenReturn(user);
        UserAccountManager profile = UserAccountManager.of(queryEngine, user.id());

        createAuction = new CreateAuction(queryEngine, profile);

        product = new Product("1","test product");
        when(productQueriable.addProduct(any(Product.class))).thenReturn(product.id());
        when(productQueriable.findById(anyString())).thenReturn(product);
    }

    @Test
    public void testShouldCreateAuction() throws CreateAuctionException {
        when(productQueriable.addProduct(any(Product.class))).thenReturn(product.id());
        when(productQueriable.findById(anyString())).thenReturn(product);
        createAuction.invoke(TestAuction.of(product).toAuctionInfo(),product);
        verify(auctionQueriable).add(any(Auction.class));
    }

    @Test(expected = DuplicateAuction.class)
    public void testShouldThrowOnDuplicateAuctionTitle() throws CreateAuctionException {
        List<Auction> auctions = new ArrayList<Auction>() {{
            add(Auction.builder().id("1").seller(user).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
        }};
        when(auctionQueriable.findByTitle(anyString())).thenReturn(auctions);
        createAuction.invoke(TestAuction.of(product).toAuctionInfo(),product);
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