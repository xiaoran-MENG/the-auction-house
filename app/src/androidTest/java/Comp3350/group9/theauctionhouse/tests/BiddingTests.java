package Comp3350.group9.theauctionhouse.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static Comp3350.group9.theauctionhouse.common.Setup.authTestUserAndLaunchBaseScenario;
import static Comp3350.group9.theauctionhouse.common.Setup.resetTestDB;
import static Comp3350.group9.theauctionhouse.common.espressoHelpers.TextViewMatcher.hasTextViewText;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import Comp3350.group9.theauctionhouse.AllAcceptanceTests;
import Comp3350.group9.theauctionhouse.common.espressoHelpers.GetChildViewAction;
import Comp3350.group9.theauctionhouse.common.espressoHelpers.GetTextViewAction;
import Comp3350.group9.theauctionhouse.common.espressoHelpers.RecycleViewAssertion;
import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.presentation.adapters.BidListAdapter;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;

/**
 * Test the bidding feature
 */
@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class BiddingTests {
    protected ActivityScenario<BaseActivity> activityScenario;
    protected View decorView;

    @BeforeClass
    public static void setup() throws IOException, ConfigsAccessException {
        resetTestDB();
    }

    @Before
    public void startFragment() throws UserLoginException {
        activityScenario = authTestUserAndLaunchBaseScenario();
        navToFragmentAuctionList();
    }

    private void navToFragmentAuctionList() {
        onView(withId(R.id.navigation_auctionlist_fragment)).perform(click());
    }

    private void navToFragmentProfile() {
        onView(withId(R.id.navigation_profile_fragment)).perform(click());
    }

    // Match info on 1 auction list item to the corresponding bidding page info
    @Test
    public void testMatchingInfoOnBiddingFrag() {
        // ----------- get the info on the first RecycleList item -------------
        String child0_price = getChildTextViewText(R.id.auctions_recycler_view, R.id.auction_viewholder_price, 0).replace("BID:","").trim();
        String child0_expiry = getChildTextViewText(R.id.auctions_recycler_view, R.id.auction_viewholder_expiry, 0).replace("Exp:", "").trim();

        double auctionListPrice = Double.parseDouble(child0_price.replace("$", "").trim());
        String auctionListTitle = getChildTextViewText(R.id.auctions_recycler_view, R.id.auction_viewholder_title, 0);
        DateTime auctionListExpiry = DateTime.of(child0_expiry, "yyyy/MM/dd", null);
        assertNotNull(auctionListExpiry);

        // ----------- get the info on the Bidding after clicking on it -------------
        GetTextViewAction textViewGetter = new GetTextViewAction();
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.bidding_auction_highest_bid)).perform(textViewGetter.viewAction());
        String highestBid = textViewGetter.getTextView().getText().toString()
                .replace("Current highest bid:", "").replace("$", "").trim();
        double biddingPagePrice = Double.parseDouble(highestBid);
        assertEquals(auctionListPrice, biddingPagePrice, 0.00);

        onView(withId(R.id.bidding_auction_title)).check(matches(hasTextViewText(auctionListTitle)));

        onView(withId(R.id.bidding_auction_expiry)).perform(textViewGetter.viewAction());
        String biddingExpiryText = textViewGetter.getTextView().getText().toString().replace("Expiry:", "").trim();
        DateTime biddingPageExpiry = DateTime.of(biddingExpiryText, "yyyy/MM/dd", null);
        assertNotNull(biddingPageExpiry);
        assertEquals(0, auctionListExpiry.compare(biddingPageExpiry));
    }

    @Test
    public void testPlacingValidBid() {
        GetTextViewAction textViewGetter = new GetTextViewAction();
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.bidding_auction_highest_bid)).perform(textViewGetter.viewAction());
        String highestBidText = textViewGetter.getTextView().getText().toString()
                .replace("Current highest bid:", "").replace("$", "").trim();
        double currHighestBid = Double.parseDouble(highestBidText);
        double newBid = currHighestBid + 1;

        // Add a new bigger bid
        onView(withId(R.id.bidding_place_bid_amt)).perform(clearText(), typeText(String.valueOf(newBid)), closeSoftKeyboard());
        onView(withId(R.id.bidding_place_bid_btn)).perform(click());

        // Check for toast
        onView(withText("Bid placed")).inRoot(withDecorView(Matchers.not(decorView))).check(ViewAssertions.matches(isDisplayed()));

        // go back to auction list, check if new price is shown in the list item
        activityScenario.onActivity(a -> {
            NavController nav = Navigation.findNavController(a, R.id.nav_host_fragment_activity_base);
            nav.navigate(R.id.navigation_auctionlist_fragment);
        });

        GetChildViewAction childGetter = new GetChildViewAction();
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(
                0, childGetter.viewAction(R.id.auction_viewholder_price)));
        double auctionListPrice = Double.parseDouble((childGetter.getTextView().getText().toString().replace("$", "").trim()));
        assertEquals(auctionListPrice, newBid, 0.00);
    }

    @Test
    public void testPlacingInvalidBid() {
        GetTextViewAction textViewGetter = new GetTextViewAction();
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.bidding_auction_highest_bid)).perform(textViewGetter.viewAction());
        String highestBidText = textViewGetter.getTextView().getText().toString()
                .replace("Current highest bid:", "").replace("$", "").trim();
        double currHighestBid = Double.parseDouble(highestBidText);
        double newBid = currHighestBid - 1;

        // Add a new bigger bid
        onView(withId(R.id.bidding_place_bid_amt)).perform(clearText(), typeText(String.valueOf(newBid)), closeSoftKeyboard());
        onView(withId(R.id.bidding_place_bid_btn)).perform(click());

        // Check for toast
        onView(withText("Error: Bidding price cannot be less than current highest bid")).inRoot(withDecorView(Matchers.not(decorView))).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void testPlacingBidsCheckHistory() throws IOException, UserLoginException, ConfigsAccessException {
        // Setup. Restart with fresh DB
        AllAcceptanceTests.debugLOG("Resetting Test DB for testPlacingBidsCheckHistory()...",BiddingTests.class);
        resetTestDB();
        activityScenario = authTestUserAndLaunchBaseScenario();
        AllAcceptanceTests.debugLOG("Starting test testPlacingBidsCheckHistory()...",BiddingTests.class);

        // go to auctionList, click first item
        navToFragmentAuctionList();
        GetTextViewAction textViewGetter = new GetTextViewAction();
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // bid on first item
        onView(withId(R.id.bidding_auction_title)).perform(textViewGetter.viewAction());
        String bid1Title1 = textViewGetter.getTextView().getText().toString();
        onView(withId(R.id.bidding_auction_highest_bid)).perform(textViewGetter.viewAction());
        String highestBidText0 = textViewGetter.getTextView().getText().toString()
                .replace("Current highest bid:", "").replace("$", "").trim();
        double currHighestBid0 = Double.parseDouble(highestBidText0);
        double newBid_1 = currHighestBid0 + 1;
        double newBid_2 = currHighestBid0 + 2;

        // place 2 bids on this, should result in first bid being updated by second
        onView(withId(R.id.bidding_place_bid_amt)).perform(clearText(), typeText(String.valueOf(newBid_1)), closeSoftKeyboard());
        onView(withId(R.id.bidding_place_bid_btn)).perform(click());
        onView(withId(R.id.bidding_place_bid_amt)).perform(clearText(), typeText(String.valueOf(newBid_2)), closeSoftKeyboard());
        onView(withId(R.id.bidding_place_bid_btn)).perform(click());

        // go back to auction list, click second item
        navToFragmentAuctionList();
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // bid on second item
        onView(withId(R.id.bidding_auction_title)).perform(textViewGetter.viewAction());
        String bid1Title2 = textViewGetter.getTextView().getText().toString();
        onView(withId(R.id.bidding_auction_highest_bid)).perform(textViewGetter.viewAction());
        String highestBidText1 = textViewGetter.getTextView().getText().toString()
                .replace("Current highest bid:", "").replace("$", "").trim();
        double currHighestBid1 = Double.parseDouble(highestBidText1);
        double newBid_3 = currHighestBid1 + 1;
        onView(withId(R.id.bidding_place_bid_amt)).perform(clearText(), typeText(String.valueOf(newBid_3)), closeSoftKeyboard());
        onView(withId(R.id.bidding_place_bid_btn)).perform(click());

        // go to profile tab, click bid history
        navToFragmentProfile();
        onView(withId(R.id.profile_bid_history_btn)).perform(click());

        // check if bid history contains only these 2 bids
        onView(withId(R.id.recycler_view)).check(new RecycleViewAssertion((recyclerView -> {
            BidListAdapter adapter = (BidListAdapter) recyclerView.getAdapter();
            assertEquals(2, adapter.getItemCount());
        })));

        assertEquals(bid1Title1, getChildTextViewText(R.id.recycler_view, R.id.biditem_auction_title, 0));
        String child0_bid = getChildTextViewText(R.id.recycler_view, R.id.biditem_bid_amt, 0).replace("BID:","").trim();
        assertEquals(String.valueOf(newBid_2), child0_bid);

        assertEquals(bid1Title2, getChildTextViewText(R.id.recycler_view, R.id.biditem_auction_title, 1));
        String child1_bid = getChildTextViewText(R.id.recycler_view, R.id.biditem_bid_amt, 1).replace("BID:","").trim();
        assertEquals(String.valueOf(newBid_3), child1_bid);
    }

    private String getChildTextViewText(int recycleViewId, int childId, int pos){
        GetChildViewAction childGetter = new GetChildViewAction();
        onView(withId(recycleViewId)).perform(RecyclerViewActions.actionOnItemAtPosition(
                pos, childGetter.viewAction(childId)));
        return childGetter.getTextView().getText().toString();
    }
}