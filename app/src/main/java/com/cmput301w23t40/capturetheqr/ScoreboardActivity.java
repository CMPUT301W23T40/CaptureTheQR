package com.cmput301w23t40.capturetheqr;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * This class defines the UI page for the QR Code Library
 */
public class ScoreboardActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Player> playerList;
    private ArrayAdapter<Player> playerAdapter;
    private Player playerSelected = null;


    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] username = {"Rachel1", "User10", "Test Username", "Fake player", "HIII"};
        playerList = new ArrayList<Player>();

        for (int i=0; i<username.length; i++) {
            playerList.add(new Player(username[i], "10000", "FAKE DEVICE"));
        }

        listView = findViewById(R.id.ltvw_ranks);
        playerAdapter = new ScoreboardList(this, playerList);
        listView.setAdapter(playerAdapter);
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
