package comp3350.group9.theauctionhouse.integrationtests.business.catalog;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import comp3350.group9.theauctionhouse.business.auctioning.CreateAuction;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.catalog.AuctionCatalogDTO;
import comp3350.group9.theauctionhouse.business.catalog.Catalog;
import java.util.UUID;

import comp3350.group9.theauctionhouse.business.common.functional.Flux;
import comp3350.group9.theauctionhouse.core.application.AuctionQueriable;
import comp3350.group9.theauctionhouse.core.application.ProductQueriable;
import comp3350.group9.theauctionhouse.core.application.QueryEngine;
import comp3350.group9.theauctionhouse.core.domain.Auction;
import comp3350.group9.theauctionhouse.core.domain.Product;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auction.CreateAuctionException;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.auth.UserRegistrationException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBFactory;
import comp3350.group9.theauctionhouse.unittests.business.auctioning.TestAuction;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class CatalogTests {

    private Catalog catalog;

    private QueryEngine queryEngine;

    private AuctionQueriable auctionQueriable;
    private ProductQueriable productQueriable;
    private Product product;
    private Product product2;

    @Before
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        UserAccountManager profile = UserAccountManager.of();
        User user1 = new User("1", "JohnDoe", "johnny@myumanitoba.ca", "12345678");
        User user2 = new User("2", "tester50", "john123@myumanitoba.ca", "12345678");
        User user3 = new User("3", "otherusername", "john1234@myumanitoba.ca", "12345678");

        try {
            profile.registerUser(user1.username(), user1.email(), user1.password());
            profile.registerUser(user2.username(), user2.email(), user2.password());
            profile.registerUser(user3.username(), user3.email(), user3.password());
        } catch (UserRegistrationException e) {
            fail("Profile should be created!");
        }
        try {
            profile.loginUser(user1.username(), user1.password());
        } catch (UserLoginException e) {
            fail("Should be able to login!");
        }

        queryEngine = HSQLDBFactory.get();
        productQueriable = queryEngine.products();

        product = new Product("1","test product");
        product2 = new Product("2","test product2");
        productQueriable.addProduct(product);
        productQueriable.addProduct(product2);

        auctionQueriable = queryEngine.auctions();

        CreateAuction createAuction = new CreateAuction(queryEngine, profile);

        try {
            createAuction.invoke(TestAuction.of(product).toAuctionInfo(), product);
        } catch (CreateAuctionException e) {
            fail("Setup of the auctions should not fail");
        }

        this.catalog = Catalog.of();
    }

    @Test
    public void testGetById(){
        AuctionCatalogDTO res = catalog.getById("1").get();
        Auction auction = auctionQueriable.findById("1");
        assertEquals(auction.id(),res.id);
    }

    @Test
    public void testGetAll() {
        List<AuctionCatalogDTO> result = catalog.getAll().get();
        assertNotNull(result);
        assertEquals(auctionQueriable.findAll().size(), result.size());
    }

    @Test
    public void testRecentAuctionsForSeller(){
        List<AuctionCatalogDTO> results = catalog.getRecentAuctionsForSeller("1",4).get();
        assertNotNull(results);
        assertEquals(results.size(),1);
    }

    @Test
    public void testGetAllByTags(){
        List<String> tags = new ArrayList<>();
        tags.addAll(Arrays.asList("Laptops","Pencils"));
        List<AuctionCatalogDTO> auctions = catalog.getByTags(tags).get();

        for (AuctionCatalogDTO a: auctions){
            assertTrue(Flux.of(auctions.get(0).tags).map(x -> x.tag()).get().contains("Laptops")
                    || Flux.of(auctions.get(0).tags).map(x -> x.tag()).get().contains("Pencils"));
        }
    }

    @Test
    public void testGettingInvalidTags(){
        List<String> tags = new ArrayList<>();
        tags.addAll(Arrays.asList(UUID.randomUUID().toString()));
        List<AuctionCatalogDTO> auctions = catalog.getByTags(tags).get();
        assertTrue(auctions.isEmpty());
    }
}

