package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * This class defines the main UI page for the Map flow
 */
public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    /**
     * The model of the players location and surroundings
     */
    private PlayerLocation playerLocation;

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        /* Copied the following code snippet for getting the map fragment
                author: Google Inc.
                url: https://developers.google.com/maps/documentation/android-sdk/map
                last updated: March 2, 2023
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frgmt_qrMap);
        mapFragment.getMapAsync(this);

        playerLocation = new PlayerLocation();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Implement onMapReady callback for OnMapReadyCallback
     * and initialize the map UI
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Move the camera to the current user location
        CameraUpdate camPosition = CameraUpdateFactory.newLatLng(playerLocation.getLocation());
        googleMap.moveCamera(camPosition);

        // FIXME: set search radius based on visible part of map
        ArrayList<QRCode> nearbyQRs = playerLocation.getNearbyCodes(1);
        // Add all visible QR codes to map
        for (QRCode qr : nearbyQRs) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(qr.getGeolocation().getLatitude(), qr.getGeolocation().getLongitude())));
            marker.setTitle(Integer.toString(qr.getScore()));
            marker.setTag(qr);
        }
        googleMap.setOnInfoWindowClickListener(this);
    }

    /**
     * Implement GoogleMap.OnInfoWindowClickListener interface
     * Open the QRDetails activity for the selected QR marker info window
     * @param marker
     */
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        QRCode code = (QRCode)marker.getTag();
        Intent intent = new Intent(getApplicationContext(), QRDetailsActivity.class);
        intent.putExtra("qrcode", code);
        startActivity(intent);
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
