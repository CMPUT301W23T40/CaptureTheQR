package com.cmput301w23t40.capturetheqr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * This class defines the UI page for the QR Code Library
 */
public class ScoreboardActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Player> playerList;
    private ArrayAdapter<Player> playerAdapter;


    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playerList = new ArrayList<Player>();
        DB.orderBasedOnScore(new DB.CallbackOrderQRCodes() {
            ArrayList<String> usernameList= new ArrayList<String>();
            @Override
            public void onCallBack(ArrayList<QRCode> orderedQRCodes) {
                int rank = 1;
                for (QRCode qrCode: orderedQRCodes) {
                    Boolean addedPlayer = Boolean.FALSE;
                    ArrayList<QRCode.ScannerInfo> SCInfo = qrCode.getScannersInfo();
                    for(QRCode.ScannerInfo player : SCInfo){
                        if(!usernameList.contains(player.getUsername())) {
                            addedPlayer = Boolean.TRUE;

                            usernameList.add(player.getUsername());
                            Player currPlayer = new Player(player.getUsername(), "10000","FAKEDEVICE");
                            currPlayer.setRank(rank);
                            currPlayer.setHighScore(qrCode.getScore());
                            playerList.add(currPlayer);
                        }

                    }
                    if (addedPlayer)
                        rank++;
                }
                listView = findViewById(R.id.ltvw_ranks);
                playerAdapter = new ScoreboardList(getApplicationContext(), playerList);
                listView.setAdapter(playerAdapter);
                Player myplayer = (Player) getIntent().getSerializableExtra("player");
                TextView myRankScoreText = findViewById(R.id.txt_vwv_estRank);
                if(myplayer.getRank()!=0) {
                    myRankScoreText.setText("My Rank is : " + String.valueOf(myplayer.getRank()));
                }
                else{
                    myRankScoreText.setText("No rank. Please scan QR Code!");
                }
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
                /** The idea of how to implement scrolling for a list is learnt from Aashay Pawar
                 * Author: Aashay Pawar
                 * url: https://www.geeksforgeeks.org/how-to-detect-scroll-up-and-down-gestures-in-a-listview-in-android/
                 * edited: Feb 26, 2023
                 * license: CC BY-SA 3.0
                 */
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

            }
        });

        //get the search param
        EditText userSearch = findViewById(R.id.editTextSearchUsername);

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

//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View view = convertView;
//        if (view == null) {
//            view = LayoutInflater.from(context).inflate(R.layout.scoreboard_content, parent, false);
//
//        }
//        TextView scoreText = view.findViewById(R.id.txtvw_score);
//        rankText.setText(String.valueOf(player.getRank()));
//    }
    /**
     * override Activity onOptionsItemSelection method for our actionBar back button
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
