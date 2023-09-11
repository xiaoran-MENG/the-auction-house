package comp3350.group9.theauctionhouse.unittests.business.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.Catalog;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.AuctionTag;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;

@RunWith(MockitoJUnitRunner.Silent.class)

public class CatalogTests {

    @Mock
    private QueryEngine queryEngine;
    @InjectMocks
    private Catalog catalog;

    @Mock
    private AuctionQueriable auctionQueriable;

    @Before
    public void init() {
        when(queryEngine.auctions()).thenReturn(auctionQueriable);
    }

    @Test
    public void testShouldGetAllAuctions() throws CreateAuctionException {
        List<Auction> list = new ArrayList<Auction>() {{
           add(Auction.builder().id("1").title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
           add(Auction.builder().id("2").title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
        }};
        Set<String> ids = new HashSet<String>() {{
            add("1");
            add("2");
        }};
        when(auctionQueriable.findAll()).thenReturn(list);
        catalog.getAll().then(x -> assertTrue(ids.contains(x.id())));
        verify(auctionQueriable).findAll();
    }

    @Test
    public void testShouldGetAuctionById() throws CreateAuctionException {
        String id = "1";
        Auction auction = Auction.builder()
                .id(id)
                .title("Test Title")
                .minBid(20)
                .expiry(DateTime.now().addDays(365))
                .build();
        when(auctionQueriable.findById(id)).thenReturn(auction);
        catalog.getById(id).then(x -> assertEquals(id, x.id));
        verify(auctionQueriable).findById(id);
    }

    @Test
    public void testShouldGetRecentAuctionsBySellerId() throws CreateAuctionException {
        String sellerId = "1";
        int limit = 2;
        User seller = new User("1", "John Doe","john@myumanitoba.ca", "123456789");
        List<Auction> list = new ArrayList<Auction>() {{
            add(Auction.builder().id("1").seller(seller).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
            add(Auction.builder().id("2").seller(seller).title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).build());
        }};
        Set<String> ids = new HashSet<String>() {{
            add("1");
            add("2");
        }};
        when(auctionQueriable.findBySellerIdOrderedByDateCreated(sellerId, limit)).thenReturn(list);
        catalog.getRecentAuctionsForSeller(sellerId, limit).then(x -> {
            assertTrue(ids.contains(x.id()));
            assertEquals(sellerId, x.seller.id());
        });
        verify(auctionQueriable).findBySellerIdOrderedByDateCreated(sellerId, limit);
    }

    @Test
    public void testShouldGetAuctionsByTags() throws CreateAuctionException {
        String pencils = "Pencils", pens = "Pens";

        Set<String> expected = new HashSet<String>() {{
           add(pencils);
           add(pens);
        }};

        List<AuctionTag> auctionTags = new ArrayList<AuctionTag>() {{
            add(AuctionTag.builder().id("1").auctionId("1").tag(pencils).build());
            add(AuctionTag.builder().id("2").auctionId("1").tag(pens).build());
        }};

        List<AuctionTag> otherAuctionTags = new ArrayList<AuctionTag>() {{
            add(AuctionTag.builder().id("1").auctionId("2").tag(pencils).build());
            add(AuctionTag.builder().id("2").auctionId("2").tag(pens).build());
        }};

        List<Auction> list = new ArrayList<Auction>() {{
            add(Auction.builder().id("1").title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).tags(auctionTags).build());
            add(Auction.builder().id("2").title("Test Title").minBid(20).expiry(DateTime.now().addDays(365)).tags(otherAuctionTags).build());
        }};

        List<String> tags = Flux.of(auctionTags).map(AuctionTag::tag).get();

        when(auctionQueriable.findByTags(tags)).thenReturn(list);

        List<AuctionCatalogDTO> result = catalog.getByTags(tags).get();

        Flux.of(result).by(x -> x.id.equals("1")).one().then(x ->
            Flux.of(x.tags()).map(AuctionTag::tag).then(t ->
                assertTrue(expected.contains(t))));

        Flux.of(result).by(x -> x.id.equals("2")).one().then(x ->
            Flux.of(x.tags()).map(AuctionTag::tag).then(t ->
                assertTrue(expected.contains(t))));

        verify(auctionQueriable).findByTags(tags);
    }

    @Test
    public void testShouldGetNoAuctionsByInvalidTags() {
        List<String> tags = new ArrayList<String>() {{
            add("Pencil");
            add("Pen");
        }};
        when(auctionQueriable.findByTags(tags)).thenReturn(new ArrayList<>());
        assertEquals(0, catalog.getByTags(tags).get().size());
        verify(auctionQueriable).findByTags(tags);
    }

}
