package Comp3350.group9.theauctionhouse.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static Comp3350.group9.theauctionhouse.common.Setup.authTestUserAndLaunchBaseScenario;
import static Comp3350.group9.theauctionhouse.common.Setup.resetTestDB;

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

import Comp3350.group9.theauctionhouse.common.espressoHelpers.GetTextViewAction;
import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class CategorizingTests {
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

    private void navToFragmentAuctionList() {
        onView(withId(R.id.navigation_auctionlist_fragment)).perform(click());
    }

    @Test
    public void createAuctionWithTagsTest(){
        onView(withId(R.id.auctions_list_add_btn)).perform(click());

        onView(withId(R.id.popupcreateauction_auction_title_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("My Test Title"), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_auction_desc_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("My Test Desc"), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_auction_minbid_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("200"), closeSoftKeyboard());

        DateTime tmr = DateTime.now().addDays(1);
        onView(withId(R.id.popupcreateauction_expiry_picker_btn)).inRoot(isPlatformPopup()).perform(click());
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(tmr.getYear(), tmr.getMonth()+1, tmr.getDay()));
        onView(withId(android.R.id.button1)).inRoot(isPlatformPopup()).inRoot(RootMatchers.isDialog()).perform(click());

        onView(withId(R.id.popupcreateauction_product_title_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("Product Name"), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_product_desc_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("Product Desc"), closeSoftKeyboard());

        onView(withId(R.id.popupcreateauction_chip_group)).inRoot(isPlatformPopup())
                .perform(scrollTo());

        onView(allOf(withText(containsString("Laptops")), isAssignableFrom(Chip.class)))
                .inRoot(isPlatformPopup()).perform(click());
        onView(allOf(withText(containsString("Textbooks")), isAssignableFrom(Chip.class)))
                .inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.popupcreateauction_create_btn)).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Created New Auction")).inRoot(withDecorView(Matchers.not(decorView)))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        GetTextViewAction textViewGetter = new GetTextViewAction();
        onView(withId(R.id.bidding_auction_categories)).perform(textViewGetter.viewAction());
        assertTrue(textViewGetter.getTextView().getText().toString().contains("Textbooks"));
        assertTrue(textViewGetter.getTextView().getText().toString().contains("Laptops"));
    }
}

