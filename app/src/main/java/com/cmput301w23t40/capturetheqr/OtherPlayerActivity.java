package com.cmput301w23t40.capturetheqr;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OtherPlayerActivity extends AppCompatActivity {

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: add more stuff once Main Activity has more stats as well

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_player);

        // set textview
        Player player = (Player) getIntent().getSerializableExtra("player");
        TextView usernameText = findViewById(R.id.txtvw_playerUsername);
        TextView contactText = findViewById(R.id.txtvw_playerContactInfo);

        usernameText.setText(player.getUsername());
        // TODO: allow players to hide their contact info as an extra feature
        contactText.setText(player.getPhoneNumber());
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
