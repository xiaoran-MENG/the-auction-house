package Comp3350.group9.theauctionhouse.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static Comp3350.group9.theauctionhouse.common.Setup.authTestUserAndLaunchBaseScenario;
import static Comp3350.group9.theauctionhouse.common.Setup.resetTestDB;
import static Comp3350.group9.theauctionhouse.common.espressoHelpers.TextViewMatcher.hasTextViewText;

import android.view.View;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.google.android.material.chip.Chip;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

import Comp3350.group9.theauctionhouse.common.espressoHelpers.GetChildViewAction;
import Comp3350.group9.theauctionhouse.common.espressoHelpers.RecycleViewAssertion;
import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class AuctionTests {
    protected ActivityScenario<BaseActivity> activityScenario;
    protected View decorView;

    @BeforeClass
    public static void setup() throws IOException, ConfigsAccessException {
        resetTestDB();
    }

    @Before
    public void startFragment() throws UserLoginException {
        activityScenario = authTestUserAndLaunchBaseScenario().onActivity(a -> decorView = a.getWindow().getDecorView());
        navToFragmentAuctionList();
    }

    private void navToFragmentHome(){
        onView(withId(R.id.navigation_home_fragment)).perform(click());
    }

    private void navToFragmentAuctionList() {
        onView(withId(R.id.navigation_auctionlist_fragment)).perform(click());
    }

    private void navToFragmentProfile(){
        onView(withId(R.id.navigation_profile_fragment)).perform(click());
    }

    @Test
    public void createAuctionTest() {
        String auctionTestTitle = "Test Title";
        String auctionTestDescription = "Test description";
        String auctionTestMinBid = "22.00";
        int expYear = LocalDate.now().getYear() + 1;

        String productTestTitle = "Test Product Title";
        String productTestDescription = "Test Product description";

        //create auction and check if its shown on home page
        createAuction(auctionTestTitle, auctionTestDescription, auctionTestMinBid, expYear, productTestTitle, productTestDescription);
        navToFragmentHome();
        assertEquals(auctionTestTitle, getChildTextViewText(R.id.home_auctions_recycler_view, R.id.my_auction_viewholder_title, 0));
    }

    @Test
    public void seeBiddersAndPickWinningBidderTest() throws UserLoginException {
        // place bid on admin_test_user2's auction
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withId(R.id.bidding_place_bid_amt)).perform(clearText(), typeText("25"), closeSoftKeyboard());
        onView(withId(R.id.bidding_place_bid_btn)).perform(click());
        onView(withText("Bid placed")).inRoot(withDecorView(Matchers.not(decorView))).check(ViewAssertions.matches(isDisplayed()));

        // login as admin_test_user2
        activityScenario = authTestUserAndLaunchBaseScenario("admin_test_user2","password")
                .onActivity(a -> decorView = a.getWindow().getDecorView());

        // admin_test_user2 sells auction to admin_test_user1
        AtomicReference<View> auctionViewHolder = new AtomicReference<>();
        onView(withId(R.id.home_auctions_recycler_view)).check(new RecycleViewAssertion((recyclerView -> auctionViewHolder.set(recyclerView.getChildAt(0)))));
        onView(is((View)auctionViewHolder.get().findViewById(R.id.my_auction_viewholder_recent_bids_layout))).perform(click());
        View bidderRecyclerView = auctionViewHolder.get().findViewById(R.id.recycler_view);
        onView(is(bidderRecyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        onView(withText(containsString("Sell to admin_test_user1"))).inRoot(isDialog()).check(matches(isDisplayed()));
        onView(withId(android.R.id.button1)).perform(click());
        onView(withText("Auction sold!")).inRoot(withDecorView(Matchers.not(decorView))).check(ViewAssertions.matches(isDisplayed()));

        // check if auction history has LOCK icon for this auction (meaning it is closed/completed)
        navToFragmentProfile();
        onView(withId(R.id.profile_auction_history_btn)).perform(click());
        AtomicReference<View> auctionHistoryViewHolder = new AtomicReference<>();
        onView(withId(R.id.recycler_view)).check(new RecycleViewAssertion((recyclerView -> auctionHistoryViewHolder.set(recyclerView.getChildAt(0)))));
        onView(is(auctionHistoryViewHolder.get()))
                .check(matches(withTagValue(equalTo(android.R.drawable.ic_lock_lock))));

        // log back in as admin_test_user1
        activityScenario = authTestUserAndLaunchBaseScenario("admin_test_user1","password")
                .onActivity(a -> decorView = a.getWindow().getDecorView());

        // check if status is purchased
        navToFragmentProfile();
        onView(withId(R.id.profile_bid_history_btn)).perform(click());
        AtomicReference<View> bidViewHolder = new AtomicReference<>();
        onView(withId(R.id.recycler_view)).check(new RecycleViewAssertion((recyclerView -> bidViewHolder.set(recyclerView.getChildAt(0)))));
        onView(is((View)bidViewHolder.get().findViewById(R.id.biditem_bid_status)))
                .check(matches(hasTextViewText("Status: Purchased")));
    }

    private void createAuction(String aucTitle, String aucDesc, String minBid, int expYear, String prodName, String prodDesc){
        onView(withId(R.id.auctions_list_add_btn)).perform(click());

        onView(withId(R.id.popupcreateauction_auction_title_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText(aucTitle), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_auction_desc_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText(aucDesc), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_auction_minbid_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText(minBid), closeSoftKeyboard());

        onView(withId(R.id.popupcreateauction_expiry_picker_btn)).inRoot(isPlatformPopup()).perform(click());
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(expYear, 1, 1));
        onView(withId(android.R.id.button1)).inRoot(isPlatformPopup()).inRoot(RootMatchers.isDialog()).perform(click());
        onView(withId(R.id.popupcreateauction_expiry_text)).inRoot(isPlatformPopup())
                .check(matches(hasTextViewText(String.format("01/01/%d", expYear))));

        onView(withId(R.id.popupcreateauction_product_title_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText(prodName), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_product_desc_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText(prodDesc), closeSoftKeyboard());

        onView(withId(R.id.popupcreateauction_create_btn)).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Created New Auction")).inRoot(withDecorView(Matchers.not(decorView)))
                .check(ViewAssertions.matches(isDisplayed()));
    }

    private String getChildTextViewText(int recycleViewId, int childId, int pos) {
        GetChildViewAction childGetter = new GetChildViewAction();
        onView(withId(recycleViewId)).perform(RecyclerViewActions.actionOnItemAtPosition(
                pos, childGetter.viewAction(childId)));
        return childGetter.getTextView().getText().toString();
    }
}
