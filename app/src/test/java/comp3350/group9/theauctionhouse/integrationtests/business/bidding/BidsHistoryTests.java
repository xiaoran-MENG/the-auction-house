package comp3350.group9.theauctionhouse.integrationtests.business.bidding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.stream.Collectors;

import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.business.bidding.BidsHistory;
import comp3350.group9.theauctionhouse.utils.IntegrationTesting;

public class BidsHistoryTests {
    private BidsHistory history;

    @Before
    public void setup() {
        try {
            IntegrationTesting.setupDB();
        } catch (IOException e) {
            fail(e.getMessage());
        }

        UserAccountManager accManager = UserAccountManager.of();
        history = BidsHistory.get();
    }

    @Test
    public void testBidsHistory() {
        history.getAll().stream()
            .map(x -> x.user.id())
            .collect(Collectors.toList())
            .forEach(x -> assertEquals(x, "1"));
    }

}
