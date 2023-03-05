package com.cmput301w23t40.capturetheqr;

import static com.cmput301w23t40.capturetheqr.QRAnalyzer.generateName;
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
        Intent intent = getIntent();
        String hash = intent.getExtras().getString("qrcode");
        // TODO: remove temp use of generate methods and other fake values!!!
        // TODO: instead use DB.getQRCodeInDB(hash, location) once implemented to get info
        String visFake = generateVisualization(hash);
        String nameFake = generateName(hash);
        int countFake = 10;

        TextView vis = findViewById(R.id.txtvw_qrdetvis);
        TextView name = findViewById(R.id.txtvw_qrdetname);
        TextView score = findViewById(R.id.txtvw_qrdetscore);
        TextView location = findViewById(R.id.txtvw_qrdetlocation);
        TextView scanCount = findViewById(R.id.txtvw_qrdetcount);

        vis.setText(visFake);
        name.setText(nameFake);
        score.setText("1000");
        location.setText("LOCATION");
        scanCount.setText("This code has been scanned " + String.valueOf(countFake) + " time(s).");



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
