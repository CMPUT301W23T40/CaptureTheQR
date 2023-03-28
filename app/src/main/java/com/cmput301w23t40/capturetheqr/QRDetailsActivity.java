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
        String imageBitmap = DB.getImageFromDB("kevin1", code.getHashValue(), new DB.CallbackGetImage() {
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
        //imageView.setImageBitmap(convertStringToBitmap("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 YQS7CHQYG/M85qMrb2kZTyIxJNYkgf/Z"));
        ArrayList<QRCode.ScannerInfo> scannerInfos = code.getScannersInfo();
        //ImageView view2 = new ImageView(QRDetailsActivity.this);
        for (QRCode.ScannerInfo si: scannerInfos) {
            imageView.setImageBitmap(convertStringToBitmap("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 YQS7CHQYG/M85qMrb2kZTyIxJNYkgf/Z"));
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
