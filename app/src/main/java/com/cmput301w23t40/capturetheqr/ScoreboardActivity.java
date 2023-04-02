package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class defines the UI page for the QR Code Library
 */
public class ScoreboardActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<Player> playerAdapter;
    private Player my_player;
    private String deviceID;
    private PlayerLocation locationHelper;

    private ArrayList<QRCode> nearbyQRCodes;
    private ActivityResultLauncher<String[]> locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), result -> {
        /* result is a map with the permission name as the key and the value being a
        boolean indicating whether the permission was granted */
                // User granted location permissions
                if (result.get("android.permission.ACCESS_COARSE_LOCATION") &&
                        result.get("android.permission.ACCESS_COARSE_LOCATION")) {
                    locationHelper = new PlayerLocation(ScoreboardActivity.this);
                }
            });

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        deviceID = FirstTimeLogInActivity.getDeviceID(ScoreboardActivity.this);
        /* The idea of how to implement a spinner was learnt from the tutorial below
         * Author: Code in Flow
         * url: https://www.youtube.com/watch?v=on_OrrX7Nw4
         * edited: Nov 13,2017
         * license: CC BY-SA 3.0
         */
        //spinner related declarations
        Spinner spinner = findViewById(R.id.search_spinner);
        ArrayAdapter<CharSequence> adapter= ArrayAdapter.createFromResource(getApplicationContext(), R.array.scorearray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        /* The idea of how to implement scrolling for a list is learnt from Aashay Pawar
         * Author: Aashay Pawar
         * url: https://www.geeksforgeeks.org/how-to-detect-scroll-up-and-down-gestures-in-a-listview-in-android/
         * edited: Feb 26, 2023
         * license: CC BY-SA 3.0
         */
        listView = findViewById(R.id.ltvw_ranks);
        listView.setOnScrollListener(
            new AbsListView.OnScrollListener() {
                // Storing first element from the previous scroll
                private int lastItem = 0;
                @Override
                public void onScrollStateChanged(AbsListView view, int scroll) {
                }
                @Override
                public void onScroll(AbsListView view, int firstItem, int itemsVisible, int total) {
                    // Updating the first visible item
                    lastItem = firstItem;
                }
            }
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (listView.getItemAtPosition(i) instanceof Player) {
                    Intent intent = new Intent(getApplicationContext(), OtherPlayerActivity.class);
                    intent.putExtra("player", (Player) listView.getItemAtPosition(i));
                    startActivity(intent);
                }
            }
        });
        DB.getAllPlayers(new DB.CallbackAllPlayers() {
            @Override
            public void onCallBack(ArrayList<Player> allPlayers) {
                TextView myRankScoreText = findViewById(R.id.txtvwv_estRank);
                spinner.setAdapter(adapter);
                /*
                 * The method below uses the spinner to present the user a choice to sort by and implements the sorting
                 * algorithm accordingly
                 * */
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String itemSelected = parent.getItemAtPosition(position).toString();
                        if(!itemSelected.equals("Nearby Highest QR codes")){
                            if (itemSelected.equals("Highest QR score")) {
                                Collections.sort(allPlayers, (o1, o2) -> Integer.compare(o2.getHighScore(), o1.getHighScore()));
                            } else if (itemSelected.equals("Highest number of QRs scanned")) {
                                Collections.sort(allPlayers, (o1, o2) -> Integer.compare(o2.getNumberOfCodes(), o1.getNumberOfCodes()));
                            } else if (itemSelected.equals("Highest sum of QR scores")) {
                                Collections.sort(allPlayers, (o1, o2) -> Integer.compare(o2.getScoreSum(), o1.getScoreSum()));
                            }
                            for(int i = 1; i <= allPlayers.size(); ++i){
                                Player tempPlayer;
                                tempPlayer = allPlayers.get(i-1);
                                tempPlayer.setRank(i);
                                if (tempPlayer.getDeviceID().equals(deviceID)){
                                    my_player = tempPlayer;
                                }
                            }
                            if (itemSelected.equals("Highest QR score")) {
                                playerAdapter = new ScoreboardList(getApplicationContext(), allPlayers, SortBy.HIGHEST_SCORE);
                            } else if (itemSelected.equals("Highest number of QRs scanned")) {
                                playerAdapter = new ScoreboardList(getApplicationContext(), allPlayers, SortBy.NUMBER_OF_CODES);
                            } else if (itemSelected.equals("Highest sum of QR scores")) {
                                playerAdapter = new ScoreboardList(getApplicationContext(), allPlayers, SortBy.SCORE_SUM);
                            }
                            listView.setAdapter(playerAdapter);

                            String rank = String.valueOf(my_player.getRank());
                            if (rank.endsWith("1") && !rank.endsWith("11"))
                                rank += "st";
                            else if (rank.endsWith("2") && !rank.endsWith("12"))
                                rank += "nd";
                            else
                                rank += "th";

                            myRankScoreText.setText("You have the " + rank + " " + itemSelected);
                        } else {
                            updateLocation();
                            locationHelper.updateLocation(new PlayerLocation.CallbackLocation() {
                                @Override
                                public void onUpdateLocation() {
                                    DB.getAllQRCodes(new DB.CallbackGetAllQRCodes() {
                                        @Override
                                        public void onCallBack(ArrayList<QRCode> allQRCodes) {
                                            QRCode.Geolocation playerLocation = locationHelper.getLocation();
                                            for (QRCode qrCode : allQRCodes){
                                                if(qrCode.getGeolocation() != null){
                                                    if(QRCode.Geolocation.nearby(playerLocation, qrCode.getGeolocation())){
                                                        nearbyQRCodes.add(qrCode);
                                                    }
                                                }
                                            }


                                        }
                                    });
                                }
                            });
                        }


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // this method never gets called, but need to have it
                    }
                });
            }
        });


        // get the search param
        EditText userSearch = findViewById(R.id.edtxt_searchUsername);

        userSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                //changes the enter icon on keyboard to search, also changed in activity_score.xml
                if (actionId == EditorInfo.IME_ACTION_SEARCH){

                    //search for player
                    DB.searchForPlayer(userSearch.getText().toString(), new DB.CallbackGetPlayer() {
                        @Override
                        public void onCallBack(Player player) {
                            if (player != null) {
                                //if player exists, show the new page
                                Intent intent = new Intent(getApplicationContext(), OtherPlayerActivity.class);
                                intent.putExtra("player", player);

                                startActivity(intent);
                            }
                            else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Sorry! That username does not exist.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
                }
                return true;
            }
        });
    }

    private void updateLocation() {
        // Check for location permissions
        int accessCoarseLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        // We already have location permissions, so continue with the action
        if (accessCoarseLocation == PackageManager.PERMISSION_GRANTED &&
                accessFineLocation == PackageManager.PERMISSION_GRANTED) {
            locationHelper = new PlayerLocation(this);
        } else {
            // Request location permissions
            locationPermissionLauncher.launch(new String[] {
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            });
        }
    }
}
