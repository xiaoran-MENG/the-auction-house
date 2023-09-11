package Comp3350.group9.theauctionhouse.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static Comp3350.group9.theauctionhouse.common.Setup.authTestUserAndLaunchBaseScenario;
import static Comp3350.group9.theauctionhouse.common.Setup.resetTestDB;
import static Comp3350.group9.theauctionhouse.common.espressoHelpers.TextViewMatcher.hasTextViewText;

import android.view.View;
import android.widget.Button;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Matchers;
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

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class TrustFactorTests {
    private ActivityScenario<BaseActivity> activityScenario;
    private View decorView;

    @BeforeClass
    public static void setup() throws IOException, ConfigsAccessException {
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
    public void testPostingRating() throws UserLoginException {
        onView(withId(R.id.publicProfileUserRating)).check(matches(hasTextViewText("No rating")));

        onView(withId(R.id.public_profile_rate_btn)).perform(click());
        onView(withId(R.id.rating5)).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Rating Submitted.")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));

        onView(withId(R.id.publicProfileUserRating)).check(matches(hasTextViewText("Rating: 5")));

        onView(withId(R.id.public_profile_rate_btn)).perform(click());
        onView(withId(R.id.rating2)).inRoot(isPlatformPopup()).perform(click());
        onView(withText("Rating Submitted.")).inRoot(withDecorView(Matchers.not(decorView))).check(matches(isDisplayed()));

        onView(withId(R.id.publicProfileUserRating)).check(matches(hasTextViewText("Rating: 3.5")));

        // my rating shouldn't have changed
        navToProfile();
        onView(withId(R.id.profileUserRating)).check(matches(hasTextViewText("No rating")));

        //login as the user who just got rated
        activityScenario = authTestUserAndLaunchBaseScenario("admin_test_user2","password");
        navToProfile();
        onView(withId(R.id.profileUserRating)).check(matches(hasTextViewText("Rating: 3.5")));
    }

    public void navToProfile(){
        activityScenario.onActivity(a -> {
            NavController nav = Navigation.findNavController(a, R.id.nav_host_fragment_activity_base);
            nav.navigate(R.id.navigation_profile_fragment);
            decorView = a.getWindow().getDecorView();
        });
    }
}
