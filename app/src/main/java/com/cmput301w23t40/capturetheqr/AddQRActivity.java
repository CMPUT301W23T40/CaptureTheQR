package com.cmput301w23t40.capturetheqr;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class defines the main UI page for the Add QR Code flow
 */
public class AddQRActivity extends AppCompatActivity {

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addqr);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
