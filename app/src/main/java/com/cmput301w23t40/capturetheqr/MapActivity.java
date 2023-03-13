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
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, PlayerLocation.CallbackNearbyCodes {

    /**
     * The model of the players location and surroundings
     */
    private PlayerLocation playerLocation;
    private GoogleMap googleMap;

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        playerLocation = new PlayerLocation();

        /* Copied the following code snippet for getting the map fragment
                author: Google Inc.
                url: https://developers.google.com/maps/documentation/android-sdk/map
                last updated: March 2, 2023
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frgmt_qrMap);
        mapFragment.getMapAsync(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    /**
     * Implement onMapReady callback for OnMapReadyCallback
     * and initialize the map UI
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // Move the camera to the current user location
        QRCode.Geolocation geolocation = playerLocation.getLocation();
        LatLng latLng = new LatLng(geolocation.getLatitude(), geolocation.getLongitude());
        CameraUpdate camPosition;
        camPosition = CameraUpdateFactory.newLatLng(latLng);
        googleMap.moveCamera(camPosition);

        // FIXME: set search radius based on visible part of map
        playerLocation.refreshNearbyQRs(1, this);
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
        /* FIXME: Same issue as LibraryActivity where we assume codes are unique by hash,
         * but location also must be considered */
        intent.putExtra("qrcode", code.getHashValue());
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

    /**
     * Implements PlayerLocation.CallbackNearbyCodes
     * Called after the refreshed data has been retrieved by playerLocation
     */
    @Override
    public void onUpdateNearbyCodes() {
        ArrayList<QRCode> nearbyQRs = playerLocation.getNearbyCodes();
        // Add all visible QR codes to map
        for (QRCode qr : nearbyQRs) {
            // The Maps API uses LatLng objects to the geolocation must be converted
            QRCode.Geolocation coords = qr.getGeolocation();
            LatLng position = new LatLng(coords.getLatitude(), coords.getLongitude());
            Marker marker = googleMap.addMarker(new MarkerOptions().position(position));
            marker.setTitle(Integer.toString(qr.getScore()));
            marker.setTag(qr);
        }
    }
}
