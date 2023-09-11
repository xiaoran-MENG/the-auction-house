package comp3350.group9.theauctionhouse.unittests.business.auctioning;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import comp3350.group9.theauctionhouse.business.auctioning.AuctionCompletionService;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
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
import comp3350.group9.theauctionhouse.exception.auction.CannotCompleteAuctionException;

@RunWith(MockitoJUnitRunner.Silent.class)

public class AuctionCompletionServiceTests {
    @Mock
    private QueryEngine queryEngine;
    @Mock
    private AuctionQueriable auctionQueriable;
    @Mock
    private BidQueriable bidQueriable;

    private AuctionCompletionService service;
    private User authUser;
    private Auction defaultAuction;

    @Before
    public void init() throws CreateAuctionException {
        authUser = new User("1", "John Doe", "john@myumanitoba.ca", "12345678");
        UserQueriable userQueriable = mock(UserQueriable.class);
        when(queryEngine.users()).thenReturn(userQueriable);
        when(queryEngine.users().findById(authUser.id())).thenReturn(authUser);
        UserAccountManager profile = UserAccountManager.of(queryEngine, authUser.id());

        defaultAuction = Auction.builder()
                .id("1")
                .seller(authUser)
                .expiry(DateTime.now().addDays(10))
                .minBid(19)
                .title("title1")
                .build();

        when(queryEngine.auctions()).thenReturn(auctionQueriable);
        when(queryEngine.bids()).thenReturn(bidQueriable);
        when(auctionQueriable.findById("1")).thenReturn(defaultAuction);

        service = new AuctionCompletionService(queryEngine, profile);
    }

    @Test
    public void testCompleteAuctionPickBid() throws CannotCompleteAuctionException, PlaceBidException {
        when(auctionQueriable.update(eq("1"),any(Auction.class))).thenReturn(true);
        when(bidQueriable.updateCompletedAuctionBidStatus(eq("1"),eq("1"),anyString(),anyString())).thenReturn(true);

        User bidder1 = new User("2", "Jane Doe", "jane@myumanitoba.ca", "12345678");
        User bidder2 = new User("3", "jon Doe", "jon@myumanitoba.ca", "12345678");

        defaultAuction.addBid(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));
        defaultAuction.addBid(new Bid("2",bidder2,defaultAuction,22, Bid.Status.PENDING));

        service.completeAuction("1","1");
    }

    @Test
    public void testCompleteAuctionNoSuchBid() throws PlaceBidException {
        when(auctionQueriable.update(eq("1"),any(Auction.class))).thenReturn(true);
        when(bidQueriable.updateCompletedAuctionBidStatus(eq("1"),eq("3"),anyString(),anyString())).thenReturn(false);

        User bidder1 = new User("2", "Jane Doe", "jane@myumanitoba.ca", "12345678");
        defaultAuction.addBid(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));

        assertThrows("No such bid, should throw CannotCompleteException",
                CannotCompleteAuctionException.class,
                () -> service.completeAuction("1","3"));
    }

    @Test
    public void testAlreadyCompletedAuction() throws PlaceBidException {
        defaultAuction.setAsComplete();

        User bidder1 = new User("2", "Jane Doe", "jane@myumanitoba.ca", "12345678");
        defaultAuction.addBid(new Bid("1",bidder1,defaultAuction,20, Bid.Status.PENDING));

        assertThrows("Auction already complete, should throw CannotCompleteException",
                CannotCompleteAuctionException.class,
                () -> service.completeAuction("1","1"));
    }

    @Test
    public void completeOldAuctionsTest() throws CannotCompleteAuctionException {
        Auction expired1 = Auction.builder()
                .id("2")
                .seller(authUser)
                .expiry(DateTime.now().addDays(-10))
                .minBid(19)
                .title("title2")
                .forceBuild();

        Auction expired2 = Auction.builder()
                .id("3")
                .seller(authUser)
                .expiry(DateTime.now().addDays(-10))
                .minBid(19)
                .title("title2")
                .forceBuild();

        List<Auction> expired = new ArrayList<>();
        expired.add(expired1);
        expired.add(expired2);

        when(auctionQueriable.findIncompleteExpired()).thenReturn(expired);
        when(auctionQueriable.findById("2")).thenReturn(expired1);
        when(auctionQueriable.findById("3")).thenReturn(expired2);
        when(auctionQueriable.update(anyString(),any(Auction.class))).thenReturn(true);
        when(bidQueriable.updateCompletedAuctionBidStatus(anyString(),nullable(String.class),nullable(String.class),anyString())).thenReturn(true);

        service.completeOldAuctions();
    }
}