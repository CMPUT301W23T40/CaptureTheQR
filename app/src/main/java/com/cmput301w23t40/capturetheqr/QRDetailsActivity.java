package com.cmput301w23t40.capturetheqr;

import static com.cmput301w23t40.capturetheqr.QRAnalyzer.generateName;
import static com.cmput301w23t40.capturetheqr.QRAnalyzer.generateScore;
import static com.cmput301w23t40.capturetheqr.QRAnalyzer.generateVisualization;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class QRDetailsActivity extends AppCompatActivity {

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdetails);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // set View contents
        QRCode code = (QRCode) getIntent().getSerializableExtra("qrcode");
//        Intent intent = getIntent();
//        String hash = intent.getExtras().getString("qrcode");
//        String vis = generateVisualization(hash);
//        String name = generateName(hash);
//        String score = String.valueOf(generateScore(hash));

//        String location = intent.getExtras().getString("location");

        TextView visText = findViewById(R.id.txtvw_qrdetvis);
        TextView nameText = findViewById(R.id.txtvw_qrdetname);
        TextView scoreText = findViewById(R.id.txtvw_qrdetscore);
        TextView locationText = findViewById(R.id.txtvw_qrdetlocation);
        TextView scanCountText = findViewById(R.id.txtvw_qrdetcount);

        visText.setText(code.getVisualization());
        nameText.setText(code.getCodeName());
        scoreText.setText(String.valueOf(code.getScore()));
        locationText.setText(code.getGeolocation().toString());
        scanCountText.setText("This code has been scanned " + String.valueOf(code.getTimesScanned()) + " time(s).");


        // TODO: handle comments for final checkpoint!!!
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
