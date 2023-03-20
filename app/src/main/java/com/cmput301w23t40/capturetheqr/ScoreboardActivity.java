package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
        String[] username = {"username 0", "User10", "Test Username", "Fake player", "HIII"};
        playerList = new ArrayList<Player>();

        for (int i=0; i<username.length; i++) {
            playerList.add(new Player(username[i], "10000", "FAKE DEVICE"));
        }

        listView = findViewById(R.id.ltvw_ranks);
        playerAdapter = new ScoreboardList(this, playerList);
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
