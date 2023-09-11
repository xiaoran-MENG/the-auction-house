package comp3350.group9.theauctionhouse.unittests.business.catalog;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.catalog.SellerCatalog;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Entity;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;

@RunWith(MockitoJUnitRunner.Silent.class)

public class SellerCatalogTests {
    @Mock
    private QueryEngine queryEngine;

    private SellerCatalog catalog;

    private AuctionQueriable auctionQueriable;
    private User user;

    @Before
    public void init() {
        auctionQueriable = mock(AuctionQueriable.class);
        when(queryEngine.auctions()).thenReturn(auctionQueriable);

        user = new User("1", "John Doe", "john@myumanitoba.ca", "12345678");
        UserQueriable userQueriable = mock(UserQueriable.class);
        when(queryEngine.users()).thenReturn(userQueriable);
        when(queryEngine.users().findById(user.id())).thenReturn(user);
        UserAccountManager profile = UserAccountManager.of(queryEngine, user.id());

        catalog = new SellerCatalog(queryEngine, profile);
    }

    @Test
    public void testShouldGetAllAuctionsForSeller() throws CreateAuctionException {
        User otherUser = new User("2", "Jane Doe", "jane@myumanitoba.ca", "12345678");
        List<Auction> list = new ArrayList<Auction>() {{
            add(Auction.builder().id("1").seller(user).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
            add(Auction.builder().id("2").seller(otherUser).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
        }};

        when(auctionQueriable.findAll()).thenReturn(list);

        Set<String> ids = list.stream()
                .filter(x -> x.seller().id().equals("1"))
                .map(Entity::id)
                .collect(Collectors.toSet());

        catalog.getAll().then(x -> assertTrue(ids.contains(x.id())));
        verify(auctionQueriable).findAll();
    }
}
