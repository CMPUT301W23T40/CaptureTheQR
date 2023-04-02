package com.cmput301w23t40.capturetheqr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.utilities.Score;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for Scoreboard Activity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class ScoreboardActivityTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }

    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }



    /**
     * Checking if clicking on player in the scoreboard will open their player profile
     */
    @Test
    public void checkClickingPlayerProfile(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_score));
        solo.assertCurrentActivity("Wrong Activity", ScoreboardActivity.class);
        ScoreboardActivity activity = (ScoreboardActivity) solo.getCurrentActivity();
        final ListView listView = activity.listView; // Get the listview
        //click on the listview
        solo.clickOnView(solo.getView(R.id.ltvw_ranks));
        solo.assertCurrentActivity("Wrong Activity", OtherPlayerActivity.class);

    }


    /**
     * Want to search by highest score of the QR codes
     */
    @Test
    public void checkSpinnerHighestScore(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_score));
        solo.assertCurrentActivity("Wrong Activity", ScoreboardActivity.class);

        View view1 = solo.getView(Spinner.class, 0);
        solo.clickOnView(view1);
        solo.scrollToTop();
        // choose highest metric
        solo.clickOnText("Highest QR score");
        ScoreboardActivity activity = (ScoreboardActivity) solo.getCurrentActivity();
        final ListView listView = activity.listView; // Get the listview

        String score = "Highest QR score";
        TestCase.assertTrue(solo.waitForText(score, 1, 2000));
    }

    /**
     * Want to search by most number of QR Codes
     */
    @Test
    public void checkSpinnerMostQRCodesScore(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_score));
        solo.assertCurrentActivity("Wrong Activity", ScoreboardActivity.class);

        View view1 = solo.getView(Spinner.class, 0);
        solo.clickOnView(view1);
        solo.scrollToTop();
        solo.clickOnText("Highest number of QRs scanned");
        ScoreboardActivity activity = (ScoreboardActivity) solo.getCurrentActivity();
        final ListView listView = activity.listView; // Get the listview

        String score = "Highest number of QRs scanned" ;
        TestCase.assertTrue(solo.waitForText(score, 1, 200));
    }

    /**
     * Want to search by highest sum of QR Codes
     */
    @Test
    public void checkSpinnerHighestSumScore(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_score));
        solo.assertCurrentActivity("Wrong Activity", ScoreboardActivity.class);

        View view1 = solo.getView(Spinner.class, 0);
        solo.clickOnView(view1);
        solo.scrollToTop();
        // select sum metric
        solo.clickOnText("Highest sum of QR scores");
        ScoreboardActivity activity = (ScoreboardActivity) solo.getCurrentActivity();
        final ListView listView = activity.listView; // Get the listview


        String score = "Highest sum of QR scores" ;
        TestCase.assertTrue(solo.waitForText(score, 1, 2000));
    }

    /**
     * Should show the main player profile page once the player clicks back
     */
    @Test
    public void checkBackButton(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_score));
        solo.assertCurrentActivity("Wrong Activity", ScoreboardActivity.class);

        solo.goBack();
        // check if we go back to Main Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
    }
    /**
     * Should show the players that have been searched and then their player profile is opened
     */
    @Test
    public void checkSearchFunction(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_score));
        solo.assertCurrentActivity("Wrong Activity", ScoreboardActivity.class);
        solo.enterText((EditText) solo.getView(R.id.edtxt_searchUsername), "anushka");
        solo.pressSoftKeyboardSearchButton();
        solo.assertCurrentActivity("Wrong Activity", OtherPlayerActivity.class);
    }
    /**
     * Cleans the DB after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();

    }

}
