package comp3350.group9.theauctionhouse;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.group9.theauctionhouse.unittests.business.account.LoginTests;
import comp3350.group9.theauctionhouse.unittests.business.account.RegistrationTests;
import comp3350.group9.theauctionhouse.unittests.business.account.ReportingTests;
import comp3350.group9.theauctionhouse.unittests.business.account.TrustFactorManagerTests;
import comp3350.group9.theauctionhouse.unittests.business.account.TrustFactorTests;
import comp3350.group9.theauctionhouse.unittests.business.account.UserAccountManagerTests;
import comp3350.group9.theauctionhouse.unittests.business.auctioning.AuctionCompletionServiceTests;
import comp3350.group9.theauctionhouse.unittests.business.auctioning.CreateAuctionTests;
import comp3350.group9.theauctionhouse.unittests.business.bidding.BidsHistoryTests;
import comp3350.group9.theauctionhouse.unittests.business.bidding.PlaceBidTests;
import comp3350.group9.theauctionhouse.unittests.business.catalog.UpdateAuctionTests;
import comp3350.group9.theauctionhouse.unittests.business.catalog.CatalogTests;
import comp3350.group9.theauctionhouse.unittests.business.catalog.SellerCatalogTests;
import comp3350.group9.theauctionhouse.unittests.business.common.EmailValidatorTest;
import comp3350.group9.theauctionhouse.unittests.domain.account.UserTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UserAccountManagerTests.class,
        RegistrationTests.class,
        LoginTests.class,
        CatalogTests.class,
        SellerCatalogTests.class,
        CreateAuctionTests.class,
        UpdateAuctionTests.class,
        AuctionCompletionServiceTests.class,
        PlaceBidTests.class,
        BidsHistoryTests.class,
        UserTests.class,
        TrustFactorTests.class,
        ReportingTests.class,
        TrustFactorManagerTests.class,
        EmailValidatorTest.class,
})
public class AllUnitTests {
}