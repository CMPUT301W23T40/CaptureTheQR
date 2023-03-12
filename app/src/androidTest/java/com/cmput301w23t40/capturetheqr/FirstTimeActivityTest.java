package com.cmput301w23t40.capturetheqr;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class for First Time Login Activity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class FirstTimeActivityTest {

    private Solo solo;
    private String username = "TEST_Username";
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference crPlayer = database.collection("player");

    @Rule
    public ActivityTestRule<FirstTimeLogInActivity> rule =
            new ActivityTestRule<>(FirstTimeLogInActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        DB.setDB(FirebaseFirestore.getInstance());
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
     * Should accept the entered data
     */
    @Test
    public void checkAddUser(){
        solo.assertCurrentActivity("Wrong Activity", FirstTimeLogInActivity.class);

        solo.enterText((EditText) solo.getView(R.id.edtxt_username),  username);
        solo.enterText((EditText) solo.getView(R.id.edtxt_contactInfo), "9999999999");
        solo.clickOnView(solo.getView(R.id.btn_submit));

        assertFalse(solo.waitForText("Username already exists", 1, 2000));

        // TODO: this version of testing will not show the nav back to MainActivity
    }

    /**
     * Check for error message if user enters and submits an existing username
     */
    @Test
    public void checkExistingUsername(){
        solo.assertCurrentActivity("Wrong Activity", FirstTimeLogInActivity.class);

        // Add data to DB
        Map<String, Object> newPlayer = new HashMap<>();
        newPlayer.put("deviceID", "fakeDeviceID");
        newPlayer.put("phoneNumber", "fakeNumber");
        DocumentReference docRef =  crPlayer.document( username);
        docRef.set(newPlayer);

        // Enter and submit existing username
        solo.enterText((EditText) solo.getView(R.id.edtxt_username), username);
        solo.enterText((EditText) solo.getView(R.id.edtxt_contactInfo), "9999999999");
        solo.clickOnView(solo.getView(R.id.btn_submit));

        assertTrue(solo.waitForText("Username already exists", 1, 2000));
    }

    /**
     * Should show error if nothing inputted
     */
    @Test
    public void checkErrorIfNoInput(){
        solo.assertCurrentActivity("Wrong Activity", FirstTimeLogInActivity.class);

        // click submit without entering text
        solo.clickOnView(solo.getView(R.id.btn_submit));
        assertTrue(solo.waitForText("Username is required", 1, 2000));
        assertTrue(solo.waitForText("Contact info is required", 1, 2000));

        // check still on activity
        solo.assertCurrentActivity("Wrong Activity", FirstTimeLogInActivity.class);
    }

    /**
     * Cleans the DB after all tests have run
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();

        // delete the data this test added to DB
        crPlayer.document("TEST_Username")
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Delete test data", "completed");
                    }
                });
    }

}
