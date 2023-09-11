package Comp3350.group9.theauctionhouse.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertTrue;
import static Comp3350.group9.theauctionhouse.common.Setup.authTestUserAndLaunchBaseScenario;
import static Comp3350.group9.theauctionhouse.common.Setup.resetTestDB;
import static Comp3350.group9.theauctionhouse.common.espressoHelpers.TextViewMatcher.hasTextViewText;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.View;
import android.widget.DatePicker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import Comp3350.group9.theauctionhouse.common.espressoHelpers.GetChildViewAction;
import Comp3350.group9.theauctionhouse.common.espressoHelpers.RecycleViewAssertion;
import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;
import comp3350.group9.theauctionhouse.presentation.adapters.AuctionListAdapter;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class PicturesTest {
    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
    public void addImageTest() {
        AtomicInteger listCount = new AtomicInteger();
        onView(withId(R.id.auctions_recycler_view)).check(new RecycleViewAssertion((recyclerView -> {
            AuctionListAdapter adapter = (AuctionListAdapter) recyclerView.getAdapter();
            listCount.set(adapter.getItemCount());
        })));

        GetChildViewAction childGetter = new GetChildViewAction();
        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(
                listCount.get() - 1, childGetter.viewAction(R.id.auction_viewholder_expiry)));
        String expiry_text = childGetter.getTextView().getText().toString().replace("Exp:", "").trim();
        int endOfListYear = DateTime.of(expiry_text, "MM/dd/yyyy", null).getYear();

        onView(withId(R.id.auctions_list_add_btn)).perform(click());

        onView(withId(R.id.popupcreateauction_auction_title_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("TITLE"), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_auction_desc_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("DESC"), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_auction_minbid_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("20.00"), closeSoftKeyboard());

        onView(withId(R.id.popupcreateauction_expiry_picker_btn)).inRoot(isPlatformPopup()).perform(click());
        onView(withClassName(equalTo(DatePicker.class.getName())))
                .perform(PickerActions.setDate(endOfListYear + 1, 1, 1));
        onView(withId(android.R.id.button1)).inRoot(isPlatformPopup()).inRoot(RootMatchers.isDialog()).perform(click());
        onView(withId(R.id.popupcreateauction_expiry_text)).inRoot(isPlatformPopup())
                .check(matches(hasTextViewText(String.format("01/01/%d", endOfListYear + 1))));

        onView(withId(R.id.popupcreateauction_product_title_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("NAME"), closeSoftKeyboard());
        onView(withId(R.id.popupcreateauction_product_desc_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("DESC"), closeSoftKeyboard());

        onView(withId(R.id.popupcreateauction_chip_group)).inRoot(isPlatformPopup())
                .perform(scrollTo());

        // mock the camera image click
        Intents.init();
        Intent intent = new Intent();
        intent.putExtra("data", BitmapFactory.decodeResource(getInstrumentation().getTargetContext().getResources(), android.R.mipmap.sym_def_app_icon));
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
        onView(withId(R.id.popupcreateauction_image_button)).inRoot(isPlatformPopup()).perform(click());
        Intents.release();

        onView(withId(R.id.popupcreateauction_create_btn)).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Created New Auction")).inRoot(withDecorView(Matchers.not(decorView)))
                .check(ViewAssertions.matches(isDisplayed()));

        onView(withId(R.id.auctions_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition(listCount.get(), click()));

        assertTrue(checkToastDoesNotExist("ERROR: image could not be loaded!"));
    }

    private boolean checkToastDoesNotExist(String msg) {
        try {
            onView(withText(msg)).inRoot(withDecorView(Matchers.not(decorView)))
                    .check(ViewAssertions.matches(isDisplayed()));
        } catch (NoMatchingRootException | NoMatchingViewException e) {
            return true;
        }
        return false;
    }
}
