package comp3350.group9.theauctionhouse.unittests.business.bidding;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.List;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.bidding.PlaceBid;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.BidQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.exception.auction.PlaceBidException;

@RunWith(MockitoJUnitRunner.Silent.class)

public class PlaceBidTests {
    @Mock
    private QueryEngine queryEngine;

    @Mock
    private BidQueriable bidQueriable;

    @Mock
    private AuctionQueriable auctionQueriable;
    private User user;

    private PlaceBid placeBid;

    private List<Auction> auctions = new ArrayList<Auction>();

    @Before
    public void init() throws CreateAuctionException {
        auctions.add(Auction.builder().id("1").seller(new User("5", "John Doe", "john@myumanitoba.ca", "12345678")).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
        auctions.add(Auction.builder().id("2").seller(new User("6", "Jane Doe", "jane@myumanitoba.ca", "12345678")).title("Test Title").minBid(25).expiry(DateTime.now().addDays(365)).build());

        when(queryEngine.auctions()).thenReturn(auctionQueriable);
        when(queryEngine.bids()).thenReturn(bidQueriable);

        auctions.forEach(x ->
            when(queryEngine.auctions().findById(x.id()))
                .thenReturn(auctions.get(Integer.parseInt(x.id()) - 1)));

        user = new User("1", "John Doe", "john@myumanitoba.ca", "12345678");
        UserQueriable userQueriable = mock(UserQueriable.class);
        when(queryEngine.users()).thenReturn(userQueriable);
        when(queryEngine.users().findById(user.id())).thenReturn(user);
        UserAccountManager profile = UserAccountManager.of(queryEngine, user.id());

        placeBid = PlaceBid.of(queryEngine, profile);
    }

    @Test
    public void testShouldPlaceBid() throws PlaceBidException {
        when(bidQueriable.add(any(Bid.class))).thenReturn(true);
        when(auctionQueriable.update(anyString(), any(Auction.class))).thenReturn(true);
        when(bidQueriable.findByUserIdANDAuctionId(anyString(),anyString())).thenReturn(null);
        assertTrue(placeBid.on("1").with(20).invoke());
        verify(bidQueriable).add(any(Bid.class));
        verify(bidQueriable,never()).update(anyString(), any(Bid.class));
        verify(auctionQueriable).update(anyString(), any(Auction.class));
    }

    @Test
    public void testShouldUpdateOldBid() throws CreateAuctionException, PlaceBidException {
        Auction auction = Auction.builder().id("1").seller(user).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build();
        Bid bid = new Bid("5",user,auction,20, Bid.Status.PENDING);

        when(bidQueriable.findByUserIdANDAuctionId(user.id(),"1")).thenReturn(bid);
        when(bidQueriable.update("5", bid)).thenReturn(true);
        when(auctionQueriable.update(anyString(), any(Auction.class))).thenReturn(true);

        assertTrue(placeBid.on("1").with(25).invoke());

        verify(bidQueriable).update("5", bid);
        verify(auctionQueriable).update(anyString(), any(Auction.class));
        verify(bidQueriable,never()).add(any(Bid.class));
    }

    @Test(expected = PlaceBidException.class)
    public void testShouldFailPlaceBid() throws PlaceBidException {
        when(bidQueriable.add(any(Bid.class))).thenReturn(false);
        when(auctionQueriable.update(anyString(), any(Auction.class))).thenReturn(true);
        placeBid.on("1").with(15).invoke();
        verify(bidQueriable).add(any(Bid.class));
        verify(auctionQueriable, times(0)).update(anyString(), any(Auction.class));
        fail("Expected PlaceBidException, but none was thrown. 15 less than minimum bid of 20");
    }

    @Test(expected = PlaceBidException.class)
    public void testShouldThrowOnAuctionNotFound() throws PlaceBidException {
        when(bidQueriable.add(any(Bid.class))).thenReturn(true);
        when(auctionQueriable.update(anyString(), any(Auction.class))).thenReturn(true);
        when(auctionQueriable.findById(anyString())).thenReturn(null);
        placeBid.on("1").with(25).invoke();
        fail("Expected PlaceBidException, but none was thrown.");
    }

    @Test(expected = PlaceBidException.class)
    public void testShouldThrowWhenPlacingBidOnOwnAuction() throws CreateAuctionException, PlaceBidException {
        when(bidQueriable.add(any(Bid.class))).thenReturn(true);
        when(auctionQueriable.update(anyString(), any(Auction.class))).thenReturn(true);
        Auction auction = Auction.builder().id("1").seller(user).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build();
        when(auctionQueriable.findById(anyString())).thenReturn(auction);
        placeBid.on("1").with(25).invoke();
        fail("Expected PlaceBidException, but none was thrown.");
    }
}
