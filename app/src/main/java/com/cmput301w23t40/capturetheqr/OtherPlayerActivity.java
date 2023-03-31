package com.cmput301w23t40.capturetheqr;

import android.os.Bundle;
import android.util.Log;
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

        // set textviews
        Player player = (Player) getIntent().getSerializableExtra("player");
        TextView usernameText = findViewById(R.id.txtvw_usernameHello);
        usernameText.setText(player.getUsername());

        // set stats
        TextView scoreSumTxt = findViewById(R.id.txtvw_scoreSum);
        TextView numberOfCodesTxt = findViewById(R.id.txtvw_numberOfCodes);
        TextView highScoreTxt = findViewById(R.id.txtvw_highestScore);
        TextView highScoreCodeTxt = findViewById(R.id.txtvw_highestScoreCode);
        TextView lowScoreCodeTxt = findViewById(R.id.txtvw_lowestScoreCode);
        TextView lowScoreTxt = findViewById(R.id.txtvw_lowestScore);

        DB.getScore(player, new DB.CallbackScore() {
            @Override
            public void onCallBack(QRCode maxQR, QRCode minQR) {
                if(maxQR != null) {
                    scoreSumTxt.setText(String.valueOf(player.getScoreSum()));
                    numberOfCodesTxt.setText(String.valueOf(player.getNumberOfCodes()));
                    highScoreCodeTxt.setText(maxQR.getCodeName());
                    highScoreTxt.setText(String.valueOf(maxQR.getScore()));
                    lowScoreCodeTxt.setText(minQR.getCodeName());
                    lowScoreTxt.setText(String.valueOf(minQR.getScore()));
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
