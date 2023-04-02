package com.cmput301w23t40.capturetheqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        // initialize Views
        QRCode code = (QRCode) getIntent().getSerializableExtra("qrcode");
        LinearLayout linearLayout = findViewById(R.id.linLayout);
        TextView visText = findViewById(R.id.txtvw_qrDetVis);
        TextView nameText = findViewById(R.id.txtvw_qrDetName);
        TextView scoreText = findViewById(R.id.txtvw_qrDetScore);
        TextView locationText = findViewById(R.id.txtvw_qrDetLocation);
        TextView scanCountText = findViewById(R.id.txtvw_qrDetCount);
        TextView commentsText = findViewById(R.id.txtvw_qrDetComments);

        // set View contents
        visText.setText(code.getVisualization());
        nameText.setText(code.getCodeName());
        scoreText.setText(String.valueOf(code.getScore()) + " points");

        if(code.getGeolocation() != null){
            locationText.setText(getAddress(code.getGeolocation().getLatitude(), code.getGeolocation().getLongitude()));
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
        }
        else {
            commentsText.setText("There are no comments for this code.");
            commentsText.setTypeface(null, Typeface.ITALIC);
        }

        ArrayList<QRCode.ScannerInfo> scannerInfo = code.getScannersInfo();
        for (QRCode.ScannerInfo si: scannerInfo) {
            if (si.getImageLink() != null) {
                ImageView imgView = new ImageView(this);
                imgView.setImageBitmap((convertStringToBitmap(si.getImageLink())));
                imgView.setPadding(0,0, 0, 15);
                linearLayout.addView(imgView);
            }
        }
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

    /* Adapted code from the following resource to turn the bitmap string from DB into java Bitmap
    author: yinpeng263@hotmail.com
    url: http://www.java2s.com/example/android/graphics/convert-string-to-bitmap.html
    last updated: 16 October, 2023
    */
    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0, byteArray1.length);
        bmp = Bitmap.createScaledBitmap(bmp, 800, 800, true);
        return bmp;
    }

    /* Adapted code from the following resource to turn longitude/latitude of the QRCode
    into a readable location.
    author: https://stackoverflow.com/users/588763/dynamicmind & https://stackoverflow.com/users/12892553/nimantha
    url: https://stackoverflow.com/questions/6172451/given-a-latitude-and-longitude-get-the-location-name
    last updated: 4 November, 2021
     */
    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getThoroughfare() + "\n" +
                    obj.getLocality() + ", " +
                    obj.getAdminArea();
            return add;
        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        }
    }
}
