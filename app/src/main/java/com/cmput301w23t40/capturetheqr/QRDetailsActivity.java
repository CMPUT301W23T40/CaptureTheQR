package com.cmput301w23t40.capturetheqr;

import static com.cmput301w23t40.capturetheqr.QRAnalyzer.generateName;
import static com.cmput301w23t40.capturetheqr.QRAnalyzer.generateScore;
import static com.cmput301w23t40.capturetheqr.QRAnalyzer.generateVisualization;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * This class displays the details of a code clicked
 */
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

        TextView visText = findViewById(R.id.txtvw_qrDetVis);
        TextView nameText = findViewById(R.id.txtvw_qrDetName);
        TextView scoreText = findViewById(R.id.txtvw_qrDetScore);
        TextView locationText = findViewById(R.id.txtvw_qrDetLocation);
        TextView scanCountText = findViewById(R.id.txtvw_qrDetCount);
        TextView commentsText = findViewById(R.id.txtvw_qrDetComments);

        visText.setText(code.getVisualization());
        nameText.setText(code.getCodeName());
        scoreText.setText(String.valueOf(code.getScore()));
        if(code.getGeolocation() != null){
            locationText.setText(code.getGeolocation().toString());
        } else {
            locationText.setText("No geolocation recorded for this code");
        }
        scanCountText.setText("This code has been scanned " + String.valueOf(code.getTimesScanned()) + " time(s).");

        String commentString = "";
        ArrayList<QRCode.Comment> comments = code.getComments();

        for (QRCode.Comment com: comments) {
            if (!commentString.equals("")) {
                commentString += "<br>";
            }
            commentString += "<b>" + com.getUsername() + ": " + "</b> " + com.getContent();
        }

        if (!commentString.equals("")) {
            System.out.println(commentString);
            commentsText.setText(Html.fromHtml(commentString));
            commentsText.setVisibility(TextView.VISIBLE);
        }


        // TODO: display images for final checkpoint!!!
        // What about if 2 users can the same QR Code at same location and both
        // take an image - do we only keep the first one or display all???
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
