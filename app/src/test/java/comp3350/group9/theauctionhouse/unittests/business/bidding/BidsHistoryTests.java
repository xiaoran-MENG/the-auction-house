package comp3350.group9.theauctionhouse.unittests.business.bidding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.bidding.BidsHistory;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.core.application.BidQueriable;
import comp3350.group9.theauctionhouse.core.application.Queriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.application.UserQueriable;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Bid;
import comp3350.group9.theauctionhouse.core.domain.Entity;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;

@RunWith(MockitoJUnitRunner.Silent.class)

public class BidsHistoryTests {

    @Mock
    private QueryEngine queryEngine;
    private BidsHistory bids;
    @Mock
    private BidQueriable bidQueriable;
    private User user;

    @Before
    public void init() {
        when(queryEngine.bids()).thenReturn(bidQueriable);

        user = new User("1", "John Doe", "john@myumanitoba.ca", "12345678");
        UserQueriable userQueriable = mock(UserQueriable.class);
        when(queryEngine.users()).thenReturn(userQueriable);
        when(queryEngine.users().findById(user.id())).thenReturn(user);
        UserAccountManager profile = UserAccountManager.of(queryEngine, user.id());

        bids = new BidsHistory(queryEngine, profile);
    }

    @Test
    public void testShouldGetAllBidsForSeller() throws CreateAuctionException {
        Auction auction = Auction.builder()
                .id("1")
                .title("Test Title")
                .minBid(20)
                .expiry(DateTime.now().addDays(365))
                .build();

        List<Bid> list = new ArrayList<Bid>() {{
            add(new Bid("1", user, auction, 20, Bid.Status.PENDING));
            add(new Bid("2", user, auction, 25, Bid.Status.PENDING));
            add(new Bid("3", user, auction, 25, Bid.Status.PENDING));
            add(new Bid("4", user, auction, 20, Bid.Status.PENDING));
        }};

        Set<String> ids = list.stream()
                .map(Entity::id)
                .collect(Collectors.toSet());

        when(queryEngine.bids().findAll()).thenReturn(list);

        bids.getAll().forEach(x -> {
            assertEquals(user.id(), x.user.id());
            assertTrue(ids.contains(x.id));
        });
    }

    @Test
    public void testShouldGetBidsWithAuctionId() throws CreateAuctionException {
        Auction auction1 = Auction.builder()
                .id("1")
                .title("Test Title")
                .minBid(20)
                .expiry(DateTime.now().addDays(365))
                .build();
        Auction auction2 = Auction.builder()
                .id("2")
                .title("Test Title 2")
                .minBid(25)
                .expiry(DateTime.now().addDays(365))
                .build();

        List<Bid> list = new ArrayList<Bid>() {{
            add(new Bid("1", user, auction1, 20, Bid.Status.PENDING));
            add(new Bid("2", user, auction1, 25, Bid.Status.PENDING));
            add(new Bid("3", user, auction1, 25, Bid.Status.PENDING));
            add(new Bid("4", user, auction2, 20, Bid.Status.PENDING));
        }};

        List<String> auction1_bid_ids = Arrays.asList("1","2","3");

        when(queryEngine.bids().findAll()).thenReturn(list);

        bids.getAllByAuctionId(auction1.id()).forEach(x -> {
            assertEquals(user.id(), x.user.id());
            assertTrue(auction1_bid_ids.contains(x.id));
        });
    }

}
