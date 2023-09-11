package Comp3350.group9.theauctionhouse.tests;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static Comp3350.group9.theauctionhouse.common.Setup.authTestUserAndLaunchBaseScenario;
import static Comp3350.group9.theauctionhouse.common.Setup.resetTestDB;

import android.view.View;
import android.widget.Button;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Matchers;
import org.json.JSONException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import Comp3350.group9.theauctionhouse.common.espressoHelpers.RecycleViewAssertion;
import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;

/**
 * Tests the reporting feature
 **/
@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class ReportingTests {
    private ActivityScenario<BaseActivity> activityScenario;
    private View decorView;

    @BeforeClass
    public static void setup() throws IOException, JSONException, ConfigsAccessException {
        resetTestDB();
    }

    @Before
    public void startPublicProfileFragment() throws UserLoginException {
        activityScenario = authTestUserAndLaunchBaseScenario().onActivity(a -> decorView = a.getWindow().getDecorView());

        onView(withId(R.id.navigation_auctionlist_fragment)).perform(click());

        onView(withId(R.id.auctions_recycler_view)).check(new RecycleViewAssertion((recyclerView -> {
            View v = recyclerView.getChildAt(0);
            Button b = v.findViewById(R.id.auction_viewholder_profile_btn);
            b.performClick();
        })));
    }

    @Test
    public void testMakingReport() {
        onView(withId(R.id.public_profile_report_btn)).perform(click());

        onView(withId(R.id.report_type_spinner)).inRoot(isPlatformPopup()).perform(click());
        onData(anything()).atPosition(2).inRoot(isPlatformPopup()).perform(click());

        onView(withId(R.id.report_description_edittext)).inRoot(isPlatformPopup())
                .perform(clearText(), typeText("Report test"),closeSoftKeyboard());

        onView(withId(R.id.report_button)).inRoot(isPlatformPopup()).perform(click());

        onView(withText("Report Submitted for review.")).inRoot(withDecorView(Matchers.not(decorView))).check(ViewAssertions.matches(isDisplayed()));
    }
}

