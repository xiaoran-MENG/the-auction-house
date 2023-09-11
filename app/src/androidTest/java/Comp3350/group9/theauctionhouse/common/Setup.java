package Comp3350.group9.theauctionhouse.common;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Comp3350.group9.theauctionhouse.AllAcceptanceTests;
import comp3350.group9.theauctionhouse.business.auth.UserAccountManager;
import comp3350.group9.theauctionhouse.core.domain.User;
import comp3350.group9.theauctionhouse.exception.auth.UserLoginException;
import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;
import comp3350.group9.theauctionhouse.presentation.components.account.LoginActivity;
import comp3350.group9.theauctionhouse.presentation.utils.LocalApplicationSetup;

public class Setup {
    // Note: these methods throw the error, so tests that use them fail if exception occurs here.
    public static void setupTestDB() throws IOException, ConfigsAccessException {
        try {
            LocalApplicationSetup.copyDatabaseToDevice(getApplicationContext(), getApplicationContext().getAssets(),true);
        }catch (final IOException ioe) {
            AllAcceptanceTests.errorLOG("Could not setup test database: " + ioe.getMessage(), Setup.class);
            throw ioe;
        } catch (ConfigsAccessException e) {
            AllAcceptanceTests.errorLOG("Could not check app version: " + e.getMessage(), Setup.class);
            throw e;
        }
    }

    public static void destroyTestDB() throws IOException {
        try{
            AllAcceptanceTests.debugLOG("Destroying any existing test DB...", Setup.class);
            ActivityScenario<LoginActivity> scenario = ActivityScenario.launch(LoginActivity.class);
            scenario.onActivity(activity -> LocalApplicationSetup.destroyTestDB(activity.getApplicationContext()));
        }catch (Exception e){
            AllAcceptanceTests.errorLOG("Something went wrong while deleting the test db folder.\n" + e.getMessage(), Setup.class);
            throw new IOException(e);
        }
    }

    public static void resetTestDB() throws IOException, ConfigsAccessException {
        destroyTestDB();
        setupTestDB();
    }

    public static ActivityScenario<BaseActivity> authTestUserAndLaunchBaseScenario() throws UserLoginException {
        try {
            User user = UserAccountManager.of().loginUser("admin_test_user1", "password");
            Map<String,String> authToken = new HashMap<>();
            authToken.put("UserId",user.id());
            return launchBaseActivityWithAuthToken(authToken);
        } catch (UserLoginException e) {
            AllAcceptanceTests.errorLOG("Could not authenticate user.", Setup.class);
            throw e;
        }
    }

    public static ActivityScenario<BaseActivity> authTestUserAndLaunchBaseScenario(String username, String password) throws UserLoginException {
        try {
            User user = UserAccountManager.of().loginUser(username, password);
            Map<String,String> authToken = new HashMap<>();
            authToken.put("UserId",user.id());
            return launchBaseActivityWithAuthToken(authToken);
        } catch (UserLoginException e) {
            AllAcceptanceTests.errorLOG("Could not authenticate user.", Setup.class);
            throw e;
        }
    }

    private static ActivityScenario<BaseActivity> launchBaseActivityWithAuthToken(Map<String,String> authToken){
        if (!authToken.isEmpty()) {
            Intent intent = new Intent(ApplicationProvider.getApplicationContext(), BaseActivity.class);
            Map.Entry<String, String> userTokenPair = authToken.entrySet().iterator().next();
            intent.putExtra(userTokenPair.getKey(), userTokenPair.getValue());
            return ActivityScenario.launch(intent);
        }
        return  ActivityScenario.launch(BaseActivity.class);
    }
}
