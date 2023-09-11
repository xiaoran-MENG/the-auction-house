package Comp3350.group9.theauctionhouse.tests;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.fail;
import static Comp3350.group9.theauctionhouse.common.Setup.setupTestDB;
import static Comp3350.group9.theauctionhouse.common.espressoHelpers.TextInputMatcher.hasTextInputLayoutErrorText;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import Comp3350.group9.theauctionhouse.AllAcceptanceTests;
import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;
import comp3350.group9.theauctionhouse.presentation.components.account.LoginActivity;
import comp3350.group9.theauctionhouse.presentation.components.account.RegistrationActivity;
import comp3350.group9.theauctionhouse.presentation.utils.LocalApplicationSetup;

/**
 * Tests the account feature. Since LoginActivity is the launch activity that setups up real database
 * We need to manually set to use the test DB after creating LoginActivity Scenario
 **/
public class AccountTests {
    @RunWith(AndroidJUnit4ClassRunner.class)
    @LargeTest
    public static class LoginActivityTests {
        //This rule triggers before each @test/@before and creates the activity and activity sets up the Configs to use the real DB
        @Rule
        public ActivityScenarioRule<LoginActivity> activityScenarioRule
                = new ActivityScenarioRule<>(LoginActivity.class);

        //Overwrite the DB configs to use testDB instead of real DB. Works because @Before is called AFTER @Rule for each @Test
        @Before
        @Test
        public void useTestDb() {
            activityScenarioRule.getScenario().onActivity(a -> {
                try {
                    LocalApplicationSetup.copyDatabaseToDevice(a.getApplicationContext(), a.getAssets(), true);
                } catch (IOException e) {
                    AllAcceptanceTests.errorLOG("\"Could not setup test database: " + e.getMessage(), LoginActivityTests.class);
                    fail("Could not setup test DB");
                } catch (ConfigsAccessException e) {
                    AllAcceptanceTests.errorLOG("Could not check app version: " + e.getMessage(), LoginActivity.class);
                    fail("Could not check app version");
                }
            });
        }

        @Test
        public void testErrorInvalidUsername() {
            String uniqueName = UUID.randomUUID().toString().substring(0, 29).replace('-', '_');
            // Type text and then press the button.
            onView(withId(R.id.login_username_layout_edittext))
                    .perform(clearText(), typeText(uniqueName), closeSoftKeyboard());
            onView(withId(R.id.login_password_layout_edittext))
                    .perform(clearText(), typeText("PASSWORD"), closeSoftKeyboard());

            onView(withId(R.id.login_button)).perform(click());
            onView(withId(R.id.login_username_layout)).check(matches(hasTextInputLayoutErrorText("No such user exists.")));
        }

        @Test
        public void testErrorInvalidPassword() {
            String username = "admin_test_user1";
            String password = "?????";

            // Type text and then press the button.
            onView(withId(R.id.login_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.login_password_layout_edittext))
                    .perform(clearText(), typeText(password), closeSoftKeyboard());

            onView(withId(R.id.login_button)).perform(click());
            onView(withId(R.id.login_password_layout)).check(matches(hasTextInputLayoutErrorText("Incorrect password.")));
        }
    }

    @RunWith(AndroidJUnit4ClassRunner.class)
    @LargeTest
    public static class RegistrationActivityTests {
        private final String validUsername = UUID.randomUUID().toString().substring(0,21).replace('-','_');

        @Rule public ActivityScenarioRule<RegistrationActivity> activityScenarioRule
                = new ActivityScenarioRule<>(RegistrationActivity.class);

        @BeforeClass
        public static void setupDB() throws IOException, ConfigsAccessException {
            setupTestDB();
        }

        @Test
        public void testErrorInvalidCharUsername() {
            String username = "***;;'/=-";

            onView(withId(R.id.register_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.register_email_layout_edittext))
                    .perform(clearText(), typeText(username + "@myumanitoba.ca"), closeSoftKeyboard());
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText("PASSWORD"), closeSoftKeyboard());

            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.register_username_layout)).check(matches(
                    hasTextInputLayoutErrorText("Username cannot contain any special characters (ex. User_Name#)")));
        }

        public void testErrorUsernameLength() {
            String username = "1";

            onView(withId(R.id.register_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.register_email_layout_edittext))
                    .perform(clearText(), typeText(username + "@myumanitoba.ca"), closeSoftKeyboard());
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText("PASSWORD"), closeSoftKeyboard());
            onView(withId(R.id.register_button)).perform(click());

            onView(withId(R.id.register_username_layout)).check(matches(
                    hasTextInputLayoutErrorText("Username needs to be at least " + User.MIN_USERNAME_LENGTH + " characters long!")));

            username = UUID.randomUUID().toString().replace('-','_') + "...."; // > 36 chars
            onView(withId(R.id.register_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.register_username_layout)).check(matches(
                    hasTextInputLayoutErrorText("Username needs to be at least " + User.MAX_USERNAME_LENGTH + " characters long!")));
        }

        @Test
        public void testErrorPasswordLength() {
            String username = validUsername;
            String password = "p";

            onView(withId(R.id.register_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.register_email_layout_edittext))
                    .perform(clearText(), typeText(username + "@myumanitoba.ca"), closeSoftKeyboard());
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText(password), closeSoftKeyboard());
            onView(withId(R.id.register_button)).perform(click());

            onView(withId(R.id.register_password_layout)).check(matches(
                    hasTextInputLayoutErrorText("Password needs to be at least " + User.MIN_PASSWORD_LENGTH + " characters long!")));

            //
            password = UUID.randomUUID().toString();
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText(password), closeSoftKeyboard());
            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.register_password_layout)).check(matches(
                    hasTextInputLayoutErrorText("Password needs to be shorter than "+User.MAX_PASSWORD_LENGTH+" characters long!")));
        }

        @Test
        public void testErrorEmailDomain() {
            String username = validUsername;

            onView(withId(R.id.register_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.register_email_layout_edittext))
                    .perform(clearText(), typeText(username + "@manitoba.ca"), closeSoftKeyboard());
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText("PASSWORD"), closeSoftKeyboard());

            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.register_email_layout)).check(matches(
                    hasTextInputLayoutErrorText("Required University of Manitoba email (ex. XXX@myumanitoba.ca)")));
        }

        @Test
        public void testErrorEmailLength() {
            String username = UUID.randomUUID().toString().substring(0,30).replace('-','_');

            onView(withId(R.id.register_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.register_email_layout_edittext))
                    .perform(clearText(), typeText(username + "@myumanitoba.ca"), closeSoftKeyboard());
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText("PASSWORD"), closeSoftKeyboard());

            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.register_email_layout)).check(matches(
                    hasTextInputLayoutErrorText("Email must not be greater than 36 characters.")));


            onView(withId(R.id.register_email_layout_edittext))
                    .perform(clearText(), typeText("" + "@myumanitoba.ca"), closeSoftKeyboard());
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText("PASSWORD"), closeSoftKeyboard());

            onView(withId(R.id.register_button)).perform(click());
            onView(withId(R.id.register_email_layout)).check(matches(
                    hasTextInputLayoutErrorText("Require more than 0 characters before '@'")));
        }
    }

    @RunWith(AndroidJUnit4ClassRunner.class)
    @LargeTest
    public static class LoginRegistrationInteractionTests {
        private final String validUsername = UUID.randomUUID().toString().substring(0,21).replace('-','_');
        private ActivityScenario<LoginActivity> activityScenario;

        public void setup() {
            activityScenario = ActivityScenario.launch(LoginActivity.class);
            activityScenario.onActivity(a -> {
                try {
                    //Overwrite the DB configs to use testDB instead of real DB.
                    LocalApplicationSetup.copyDatabaseToDevice(a.getApplicationContext(), a.getAssets(), true);
                    AllAcceptanceTests.debugLOG("Setup test database: ", LoginRegistrationInteractionTests.class);
                } catch (IOException e) {
                    AllAcceptanceTests.errorLOG("Could not setup test database: " + e.getMessage(), LoginRegistrationInteractionTests.class);
                    fail("Could not setup test DB");
                } catch (ConfigsAccessException e) {
                    AllAcceptanceTests.errorLOG("Could not check app version: " + e.getMessage(), LoginRegistrationInteractionTests.class);
                    fail("Could not check app version");
                }
            });
        }

        @Test
        public void testRegistrationLoginLogout() {
            setup();

            String username = validUsername;
            String password = "PASSWORD";

            Intents.init();
            onView(withId(R.id.login_registration_button)).perform(click());
            intended(hasComponent(RegistrationActivity.class.getName()));
            Intents.release();

            onView(withId(R.id.register_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.register_email_layout_edittext))
                    .perform(clearText(), typeText(username + "@myumanitoba.ca"), closeSoftKeyboard());
            onView(withId(R.id.register_password_layout_edittext))
                    .perform(clearText(), typeText(password), closeSoftKeyboard());

            Intents.init();
            onView(withId(R.id.register_button)).perform(click());
            intended(hasComponent(LoginActivity.class.getName()));
            Intents.release();

            onView(withId(R.id.login_username_layout_edittext))
                    .perform(clearText(), typeText(username), closeSoftKeyboard());
            onView(withId(R.id.login_password_layout_edittext))
                    .perform(clearText(), typeText(password), closeSoftKeyboard());

            Intents.init();
            onView(withId(R.id.login_button)).perform(click());
            intended(hasComponent(BaseActivity.class.getName()));
            Intents.release();

            navigateToProfileFragment();

            onView(withId(R.id.logout_button)).perform(click());
            onView(withText("Are you sure you want to logout?")).inRoot(isDialog())
                .check(matches(isDisplayed()));

            Intents.init();
            onView(withId(android.R.id.button1)).perform(click());
            intended(hasComponent(LoginActivity.class.getName()));
            Intents.release();
        }

        public void navigateToProfileFragment(){
            getInstrumentation().runOnMainSync(() -> {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    BaseActivity a = (BaseActivity) resumedActivities.iterator().next();
                    NavController nav = Navigation.findNavController(a, R.id.nav_host_fragment_activity_base);
                    nav.navigate(R.id.navigation_profile_fragment);
                }
            });
        }
    }
}