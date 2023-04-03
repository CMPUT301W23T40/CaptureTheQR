package com.cmput301w23t40.capturetheqr;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.ext.junit.runners.AndroidJUnit4;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for Library Activity, QRCodeList and QRDetailsActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)
public class LibraryActivityTest {
    // ASSUME THIS IS DONE ON EXISTING PLAYER
    private Solo solo;
    private String otherUsername = "TEST_Username";
    private String deviceId = FirstTimeLogInActivity.getDeviceID(getApplicationContext());
    private String otherCode = "TEST_Hash";
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference crPlayer = database.collection("player");
    private CollectionReference crCode = database.collection("qrcode");

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(),rule.getActivity());
        DB.setDB(FirebaseFirestore.getInstance());
//        deviceId = FirstTimeLogInActivity.getDeviceID(getApplicationContext());
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
     * Should show the player's codes by default
     */
    @Test
    public void checkPlayerCodes() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_qrLibrary));
        solo.assertCurrentActivity("Wrong Activity", LibraryActivity.class);

        DB.getPlayer(deviceId, new DB.CallbackGetPlayer() {
            @Override
            public void onCallBack(Player player) {
                if (player != null){
                    DB.getUsersQRCodes(player, new DB.CallbackGetUsersQRCodes() {
                        @Override
                        public void onCallBack(ArrayList<QRCode> myQRCodes) {
                            // verify amount of items
                            RecyclerView list = (RecyclerView) solo.getView(R.id.qrcode_list);
                            assertEquals(list.getAdapter().getItemCount(), myQRCodes.size());

                            // verify that code names are displayed (first)
                            if (myQRCodes.size() != 0) {
                                solo.waitForText(myQRCodes.get(0).getCodeName(), 1, 2000);
                            }
                        }
                    });
                }
            }
        });
    }

    private void addFakeData(String username){
        // Add fake username data to DB
//        Map<String, Object> newPlayer = new HashMap<>();
//        newPlayer.put("username", otherUsername);
//        newPlayer.put("deviceID", "fakeDeviceID");
//        newPlayer.put("phoneNumber", "fakeNumber");
//        DocumentReference docRef =  crPlayer.document( otherUsername);
//        docRef.set(newPlayer);

        // Add QR for fake username
        Map<String, Object> newCode = new HashMap<>();
        List<QRCode.Comment> comments = new ArrayList<>();
        comments.add(new QRCode.Comment(username, "Fake comment"));

        List<QRCode.ScannerInfo> info = new ArrayList<>();
        info.add(new QRCode.ScannerInfo(username, null));

        newCode.put("hashValue", otherCode);
        newCode.put("codeName", "fakeName");
        newCode.put("comments", comments);
        newCode.put("geolocation", null);
        newCode.put("score", 500);
        newCode.put("timesScanned", 1);
        newCode.put("visualization", "fakeVis");
        newCode.put("scannersInfo", info);
        DocumentReference codeDocRef =  crCode.document(otherCode);
        codeDocRef.set(newCode);
    }

    /**
     * Should show all codes after switching tabs
     */
    @Test
    public void checkAllCodes() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_qrLibrary));
        solo.assertCurrentActivity("Wrong Activity", LibraryActivity.class);

        // select other tab
        solo.clickOnMenuItem("All QRs");
        solo.sleep(1000);

        DB.getAllQRCodes(allQRCodes -> {
            // verify amount of items
            RecyclerView list = (RecyclerView) solo.getView(R.id.qrcode_list);
            assertEquals(list.getAdapter().getItemCount(), allQRCodes.size());
        });
    }

    /**
     * Show details of selected code
     */
    @Test
    public void checkCodeDetails() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_qrLibrary));
        solo.assertCurrentActivity("Wrong Activity", LibraryActivity.class);

        DB.getPlayer(deviceId, player -> {
            if (player != null) {
                Log.d("TEST", "HERE!");
                addFakeData(player.getUsername());

                // test click on QR to open detail activity
                solo.waitForText("fakeName",1, 2000);
                solo.clickOnText("fakeName");
                solo.assertCurrentActivity("Wrong Activity", QRDetailsActivity.class);

                // verify some of the text on this page
                // test add comment

            } else {
                Log.d("TEST", "HERE 2!");
            }
        });
       }

       // TODO: test deletion

    /**
     * Cleans the DB after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();

        // delete the data this test added to DB
//        crPlayer.document("TEST_Username")
//                .delete()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.d("Delete test data", "completed");
//                    }
//                });
        crCode.document(otherCode)
                .delete()
                .addOnCompleteListener(task -> Log.d("Delete test data", "completed"));
    }

}
