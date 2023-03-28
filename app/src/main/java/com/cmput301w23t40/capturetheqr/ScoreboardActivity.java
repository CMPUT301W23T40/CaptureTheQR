package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class defines the UI page for the QR Code Library
 */
public class ScoreboardActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Player> playerList;
    private ArrayAdapter<Player> playerAdapter;
    private int my_rank;


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


        DB.getAllPlayers(new DB.CallbackAllPlayers() {
            @Override
            public void onCallBack(ArrayList<Player> allPlayers) {
                Collections.sort(allPlayers, new Comparator<Player>() {
                    @Override
                    public int compare(Player o1, Player o2) {
                        return Integer.compare(o2.getHighScore(), o1.getHighScore());
                    }

                });
                for (Player player:allPlayers){
                    int index = allPlayers.indexOf(player);

                    Player currPlayer = new Player(player.getUsername(), "10000","FAKEDEVICE");
                    currPlayer.setRank((index+1));
                    currPlayer.setHighScore(player.getHighScore());
                    playerList.add(currPlayer);
                }
                DB.getPlayer(FirstTimeLogInActivity.getDeviceID(ScoreboardActivity.this), new DB.CallbackGetPlayer() {

                    @Override
                    public void onCallBack(Player player) {


                        if(player.getHighScore()!=0) {
                            my_rank = allPlayers.indexOf(player) + 1;

                        }
//                        else{
//                            myRankScoreText.setText("No rank! Please scan QR Code.");
//                        }


                    }

                });

                listView = findViewById(R.id.ltvw_ranks);
                playerAdapter = new ScoreboardList(getApplicationContext(), playerList);
                listView.setAdapter(playerAdapter);
                TextView myRankScoreText = findViewById(R.id.txt_vwv_estRank);
                myRankScoreText.setText("My rank is: " + String.valueOf(my_rank));
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

    }

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
