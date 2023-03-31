package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This class defines the UI home page and starts the app
 */
public class MainActivity extends AppCompatActivity {
    Player currPlayer;
    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String deviceId = FirstTimeLogInActivity.getDeviceID(this);

        // query the device ID in the database, if it does not exist, show the signin page; if it
        // exists already, show the homepage of the app
        DB.setDB(FirebaseFirestore.getInstance());
        DB.verifyIfDeviceIDIsNew(deviceId, new DB.CallbackVerifyIfDeviceIDIsNew() {
            @Override
            public void onCallBack(Boolean deviceIDIsNew) {
                if(deviceIDIsNew){
                    startActivity(new Intent(getApplicationContext(), FirstTimeLogInActivity.class));
                }
            }
        });
        setContentView(R.layout.activity_main);


        // Set TextViews
        DB.getPlayer(deviceId, new DB.CallbackGetPlayer() {
            @Override
            public void onCallBack(Player player) {

                if(player!=null){
                    TextView helloText = findViewById(R.id.txtvw_usernameHello);
                    TextView contactText = findViewById(R.id.txtvw_contactInfo);

                    helloText.setText("Hello " + player.getUsername());
                    contactText.setText(player.getPhoneNumber());
                } else {
                    Log.d("user is null", "");
                }
            }
        });

        /* Adapted code from the following resource for the nav bar functionality
        author: https://stackoverflow.com/users/13523077/abu-saeed
        url: https://stackoverflow.com/a/68412210
        last updated: 20 July, 2021
         */
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.navigation_map) {
                    startActivity(new Intent(getApplicationContext(), MapActivity.class));
                } else if (id == R.id.navigation_addQR) {
                    scanCode();
                } else if (id == R.id.navigation_qrLibrary){
                    startActivity(new Intent(getApplicationContext(), LibraryActivity.class));
                } else if (id == R.id.navigation_score){
                    startActivity(new Intent(getApplicationContext(), ScoreboardActivity.class));

                }
                return true;
            }
        });
    }

    /**
     * override Activity onResume method
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Set TextViews
        DB.getPlayer( FirstTimeLogInActivity.getDeviceID(this), new DB.CallbackGetPlayer() {
            @Override
            public void onCallBack(Player player) {
                if(player!=null){
                    TextView helloText = findViewById(R.id.txtvw_usernameHello);
                    TextView contactText = findViewById(R.id.txtvw_contactInfo);

                    helloText.setText("Hello " + player.getUsername());
                    contactText.setText(player.getPhoneNumber());

                    // Set stats
                    TextView scoreSumTxt = findViewById(R.id.txtvw_scoreSum);
                    TextView scoreLabel = findViewById(R.id.txtvw_scoreSumLabel);
                    TextView numberOfCodesTxt = findViewById(R.id.txtvw_numberOfCodes);
                    TextView numberLabel = findViewById(R.id.txtvw_numberOfCodesLabel);
                    TextView highScoreTxt = findViewById(R.id.txtvw_highestScore);
                    TextView highScoreCodeTxt = findViewById(R.id.txtvw_highestScoreCode);
                    TextView lowScoreCodeTxt = findViewById(R.id.txtvw_lowestScoreCode);
                    TextView lowScoreTxt = findViewById(R.id.txtvw_lowestScore);
                    LinearLayout statistics = findViewById(R.id.statistics);
                    DB.getScore(player, new DB.CallbackScore() {
                        @Override
                        public void onCallBack(QRCode maxQR, QRCode minQR) {
                            if(maxQR != null){
                                statistics.setVisibility(View.VISIBLE);
                                scoreLabel.setVisibility(View.VISIBLE);
                                numberLabel.setVisibility(View.VISIBLE);
                                scoreSumTxt.setText(String.valueOf(player.getScoreSum()));
                                numberOfCodesTxt.setText(String.valueOf(player.getNumberOfCodes()));
                                highScoreCodeTxt.setText(maxQR.getCodeName());
                                highScoreTxt.setText(String.valueOf(maxQR.getScore()));
                                lowScoreCodeTxt.setText(minQR.getCodeName());
                                lowScoreTxt.setText(String.valueOf(minQR.getScore()));
                            }
                        }
                    });



                } else {
                    Log.d("user is null", "");
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LinearLayout statistics = findViewById(R.id.statistics);
        TextView scoreSumTxt = findViewById(R.id.txtvw_scoreSum);
        TextView scoreLabel = findViewById(R.id.txtvw_scoreSumLabel);
        TextView numberOfCodesTxt = findViewById(R.id.txtvw_numberOfCodes);
        TextView numberLabel = findViewById(R.id.txtvw_numberOfCodesLabel);

        statistics.setVisibility(View.INVISIBLE);
        scoreSumTxt.setText("");
        scoreLabel.setVisibility(View.INVISIBLE);
        numberOfCodesTxt.setText("");
        numberLabel.setVisibility(View.INVISIBLE);
    }

    /**
     * Opens the QR code Scanner and initializes the scan options.
     */
    private void scanCode() {
        /* Adapted code from the following resource for the QR scanner, using JourneyApps dependencies
        author: https://www.youtube.com/@CamboTutorial
        url: https://www.youtube.com/watch?v=jtT60yFPelI
        dependencies: https://github.com/journeyapps/zxing-android-embedded
        last updated: 18 March, 2022
         */
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(ScanActivity.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result ->
    {
        if(result.getContents() != null)
        {
            QRAnalyzer analyzer = new QRAnalyzer();
            Intent intent = new Intent(getApplicationContext(), AddQRActivity.class);
            String hash = analyzer.generateHashValue(result.getContents()); // this is the hash of the QR code
            Bundle bundle = new Bundle();
            bundle.putString("hash", hash);
            intent.putExtras(bundle);
            startActivity(intent);
            //startActivity(new Intent(getApplicationContext(), AddQRActivity.class));
        }
    });

}