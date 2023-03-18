package com.cmput301w23t40.capturetheqr;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.Date;

/**
 * This class defines the main UI page for the Add QR Code flow
 */
public class AddQRActivity extends AppCompatActivity {
    private Bundle bundle;
    private String hash;
    private Button buttonSubmit;
    private Button buttonPicture;
    private static final int CAMERA_REQUEST = 1888;

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addqr);
        bundle = getIntent().getExtras();
        hash = bundle.getString("hash"); // this is the QR code hash
        QRCode qrCode = QRAnalyzer.generateQRCodeObject(hash);
        TextView qrCodeName, score, visualization, scanCount;
        qrCodeName = findViewById(R.id.txtvw_codeName);
        score = findViewById(R.id.txtvw_codePoints);
        visualization = findViewById(R.id.txtvw_codeDrawing);
        scanCount = findViewById(R.id.txtvw_scanCount);
        qrCodeName.setText(qrCode.getCodeName());
        score.setText(String.valueOf(qrCode.getScore()));
        visualization.setText(qrCode.getVisualization());
        DB.getTimesScanned(qrCode, new DB.CallbackGetTimesScanned() {
            @Override
            public void onCallBack(Integer timesScanned) {
                if(timesScanned != null){
                    scanCount.setText("This code has been scanned " + timesScanned + " time(s)!");
                } else {
                    scanCount.setText(R.string.first_scanner);
                }
            }
        });

        /* Adapted code from the following resource for the camera API
        author: https://www.youtube.com/@allcodingtutorials1857
        url: https://www.youtube.com/watch?v=59taMJThsFU
        last updated: 16 October, 2023
         */
        buttonPicture = findViewById(R.id.btn_uploadPic);
        Switch geoSwitch = findViewById(R.id.btn_geoToggle);
        buttonPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE);
            }
        });

        buttonSubmit = findViewById(R.id.btn_Submit);
        buttonSubmit.setOnClickListener(v->
        {
            if(geoSwitch.isChecked()){
                //qrCode.setGeolocation(new QRCode.Geolocation(-1, -1));
            }
            DB.saveQRCodeInDB(qrCode, new DB.Callback() {
                @Override
                public void onCallBack() {
                    DB.getPlayer(FirstTimeLogInActivity.getDeviceID(AddQRActivity.this), new DB.CallbackGetPlayer() {
                        @Override
                        public void onCallBack(Player player) {
                            QRCode.ScannerInfo scannerInfo = new QRCode.ScannerInfo(player.getUsername(), "fake image link");
                            DB.verifyIfScannerInfoIsNew(qrCode, scannerInfo, new DB.CallbackVerifyIfScannerInfoIsNew() {
                                @Override
                                public void onCallBack(Boolean scannerInfoIsNew) {
                                    if (scannerInfoIsNew){
                                        DB.saveScannerInfoInDB(qrCode, scannerInfo, new DB.Callback() {
                                            @Override
                                            public void onCallBack() {
                                                // nothing
                                            }
                                        });
                                        Toast.makeText(getApplicationContext(), R.string.add_qr_success_toast, Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Log.d("saving scanner info", "the user scanned this code before");
                                        Toast.makeText(getApplicationContext(), R.string.add_qr_failure_toast, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * After taking additional photo, retrieve the Bitmap of the photo.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* Adapted code from the following resource for the camera API
        author: https://www.youtube.com/@allcodingtutorials1857
        url: https://www.youtube.com/watch?v=59taMJThsFU
        last updated: 16 October, 2023
         */
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data"); // this is the bitmap of optional photo
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * override Activity onOptionsItemSelection method for our actionBar back button
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Note, the following resource was used:
        author: https://auth.geeksforgeeks.org/user/namanjha10
        url: https://www.geeksforgeeks.org/how-to-add-and-customize-back-button-of-action-bar-in-android/
        last updated: 23 Feb, 2021
        */
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
