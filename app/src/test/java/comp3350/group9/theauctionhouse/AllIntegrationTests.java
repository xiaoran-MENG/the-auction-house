package comp3350.group9.theauctionhouse;

import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;

import comp3350.group9.theauctionhouse.integrationtests.TestUserQueriable;
import comp3350.group9.theauctionhouse.integrationtests.business.account.LoginTests;
import comp3350.group9.theauctionhouse.integrationtests.business.account.RegistrationTests;
import comp3350.group9.theauctionhouse.integrationtests.business.account.ReportingTests;
import comp3350.group9.theauctionhouse.integrationtests.business.account.TrustFactorManagerTests;
import comp3350.group9.theauctionhouse.integrationtests.business.auctioning.AuctionCompletionServiceTests;
import comp3350.group9.theauctionhouse.integrationtests.business.auctioning.CreateAuctionTests;
import comp3350.group9.theauctionhouse.integrationtests.business.catalog.CatalogTests;
import comp3350.group9.theauctionhouse.integrationtests.persistance.account.TestUserPersistence;
import comp3350.group9.theauctionhouse.integrationtests.business.account.TrustFactorTests;
import comp3350.group9.theauctionhouse.integrationtests.business.account.UserAccountManagerTests;
import comp3350.group9.theauctionhouse.integrationtests.business.bidding.BidsHistoryTests;
import comp3350.group9.theauctionhouse.integrationtests.business.bidding.PlaceBidTests;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuctionCompletionServiceTests.class,
        CreateAuctionTests.class,
        CatalogTests.class,
        LoginTests.class,
        RegistrationTests.class,
        TestUserQueriable.class,
        TestUserPersistence.class,
        TrustFactorManagerTests.class,
        TrustFactorTests.class,
        ReportingTests.class,
        UserAccountManagerTests.class,
        BidsHistoryTests.class,
        PlaceBidTests.class,
})

public class AllIntegrationTests {
    @BeforeClass
    @Test
    public static void setupTemporaryIntegrationDB(){
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    @Test
    public static void destroyTemporaryIntegrationDB(){
        try {
            IntegrationTesting.tearDown();
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }
}
