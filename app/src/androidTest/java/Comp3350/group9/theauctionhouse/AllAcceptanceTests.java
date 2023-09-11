package Comp3350.group9.theauctionhouse;

import android.util.Log;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.IOException;

import Comp3350.group9.theauctionhouse.common.Setup;
import Comp3350.group9.theauctionhouse.tests.AccountTests;
import Comp3350.group9.theauctionhouse.tests.AuctionTests;
import Comp3350.group9.theauctionhouse.tests.BiddingTests;
import Comp3350.group9.theauctionhouse.tests.CategorizingTests;
import Comp3350.group9.theauctionhouse.tests.PicturesTest;
import Comp3350.group9.theauctionhouse.tests.TrustFactorTests;
import Comp3350.group9.theauctionhouse.tests.ReportingTests;

// Logcat filter: package:mine  tag:System.out tag:TestRunner tag:AllAcceptanceTests

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AccountTests.LoginActivityTests.class,
        AccountTests.RegistrationActivityTests.class,
        AccountTests.LoginRegistrationInteractionTests.class,
        BiddingTests.class,
        AuctionTests.class,
        CategorizingTests.class,
        PicturesTest.class,
        ReportingTests.class,
        TrustFactorTests.class
})
public class AllAcceptanceTests {
    public static final String TAG = "AllAcceptanceTests";
    @BeforeClass
    public static void reset() throws IOException {
        Setup.destroyTestDB();
    }

    @AfterClass
    public static void cleanup() throws IOException {
        Setup.destroyTestDB();
    }

    public static void debugLOG(String msg, Class c){
        Log.d(TAG, String.format("In (%s):\n", c.getName()) + msg);
    }

    public static void errorLOG(String msg, Class c){
        Log.e(TAG,String.format("In (%s):\n", c.getName()) + msg);
    }
}
