package comp3350.group9.theauctionhouse.unittests.business.catalog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.NoSuchElementException;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.catalog.SellerCatalog;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;

@RunWith(MockitoJUnitRunner.Silent.class)

public class UpdateAuctionTests {
    @Mock
    private QueryEngine queryEngine;
    private AuctionQueriable auctionQueriable;

    private SellerCatalog catalog;

    @Before
    public void init() {
        User user = new User("1", "John Doe", "john@myumanitoba.ca", "12345678");
        UserQueriable userQueriable = mock(UserQueriable.class);
        auctionQueriable = mock(AuctionQueriable.class);
        when(queryEngine.auctions()).thenReturn(auctionQueriable);
        when(queryEngine.users()).thenReturn(userQueriable);
        when(queryEngine.users().findById(user.id())).thenReturn(user);
        UserAccountManager profile = UserAccountManager.of(queryEngine, user.id());
        catalog = new SellerCatalog(queryEngine, profile);
    }

    @Test
    public void testShouldUpdateAuction() throws CreateAuctionException {
        String id = "1";
        Auction auction = Auction.builder().id(id).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build();
        when(auctionQueriable.update(id, auction)).thenReturn(true);
        when(auctionQueriable.findById(id)).thenReturn(auction);
        catalog.put(id, auction);
    }

    @Test
    public void testShouldThrowAndFailToUpdateAuctionNotFound() throws CreateAuctionException {
        String id = "-1";
        Auction auction = Auction.builder().id(id).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build();
        when(auctionQueriable.findById(id)).thenReturn(null);
        assertThrows(NoSuchElementException.class, () -> catalog.put(id, auction));
    }
}
