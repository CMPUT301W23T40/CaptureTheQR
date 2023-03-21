package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

        // TODO: use query (currently have fake data)
        String[] username = {"Rachel1", "User10", "Test Username", "Fake player", "HIII"};
        playerList = new ArrayList<Player>();
        DB.orderBasedOnScore(new DB.CallbackOrderQRCodes() {
            ArrayList<String> usernameList= new ArrayList<String>();
            ArrayList<Integer> scorelist= new ArrayList<Integer>();
            ArrayList<Integer> rankList = new ArrayList<Integer>();
            @Override
            public void onCallBack(ArrayList<QRCode> orderedQRCodes) {
                int rank = 1;
                for (QRCode qrCode: orderedQRCodes) {

                    ArrayList<QRCode.ScannerInfo> SCInfo = qrCode.getScannersInfo();
                    for(QRCode.ScannerInfo player : SCInfo){
                        if(!usernameList.contains(player.getUsername())) {
                            usernameList.add(player.getUsername());
                            Player currPlayer = new Player(player.getUsername(), "10000","FAKEDEVICE");
                            currPlayer.setRank(rank);
                            currPlayer.setHighScore(qrCode.getScore());
                            playerList.add(currPlayer);
                        }

                    }
                    rank++;
                }
                listView = findViewById(R.id.ltvw_ranks);
                playerAdapter = new ScoreboardList(getApplicationContext(), playerList);
                listView.setAdapter(playerAdapter);

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
