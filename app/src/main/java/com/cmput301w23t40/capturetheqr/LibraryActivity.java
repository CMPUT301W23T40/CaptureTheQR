package com.cmput301w23t40.capturetheqr;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This class defines the UI page for the QR Code Library
 */
public class LibraryActivity extends AppCompatActivity {
    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
