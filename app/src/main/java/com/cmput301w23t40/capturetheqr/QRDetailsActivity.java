package com.cmput301w23t40.capturetheqr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

        // set View contents
        QRCode code = (QRCode) getIntent().getSerializableExtra("qrcode");

        TextView visText = findViewById(R.id.txtvw_qrDetVis);
        TextView nameText = findViewById(R.id.txtvw_qrDetName);
        TextView scoreText = findViewById(R.id.txtvw_qrDetScore);
        TextView locationText = findViewById(R.id.txtvw_qrDetLocation);
        TextView scanCountText = findViewById(R.id.txtvw_qrDetCount);
        TextView commentsText = findViewById(R.id.txtvw_qrDetComments);
        ImageView imageView = findViewById(R.id.imgvw_qrImage);
        String imageBitmap = DB.getImageFromDB("kevin", code.getHashValue(), new DB.CallbackGetImage() {
            @Override
            public String onCallBack(Object o) {
                Log.i("image link:", o.toString());
                return o.toString();
            }
        });

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
        String scannerString = "";
        ArrayList<QRCode.ScannerInfo> scannerInfo = code.getScannersInfo();
        //ImageView view2 = new ImageView(QRDetailsActivity.this);
        
        for (QRCode.ScannerInfo si: scannerInfo) {
            if (scannerString != null) {
                imageView.setImageBitmap(convertStringToBitmap(si.getImageLink()));
            }
            //imageView.setImageBitmap(convertStringToBitmap("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 "));
            //view2.setImageBitmap(convertStringToBitmap("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 YQS7CHQYG/M85qMrb2kZTyIxJNYkgf/Z"));
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
}
