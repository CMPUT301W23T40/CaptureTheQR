package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines the main UI page for the Map flow
 */
public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, PlayerLocation.CallbackNearbyCodes {

    /**
     * The model of the players location and surroundings
     */
    private PlayerLocation locationHelper;
    private GoogleMap googleMap;
    private SearchView locationSearch;

    /**
     * override Activity onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        locationHelper = new PlayerLocation(this);

        /* Copied the following code snippet for getting the map fragment
                author: Google Inc.
                url: https://developers.google.com/maps/documentation/android-sdk/map
                last updated: March 2, 2023
         */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frgmt_qrMap);
        mapFragment.getMapAsync(this);

        locationSearch = findViewById(R.id.srchvw_map);
        locationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                setMapLocation(s);
                /* return true to tell android that we handled the event
                and that no additional handling is necessary
                 */
                return true;
            }

            /* Necessary for OnQueryTextListener interface, return false
            to tell android that it needs to handle the event
             */
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Move the location helper to the specified location
     * @param location
     * The user supplied location query
     */
    private void setMapLocation(String location) {
        /* Match a comma separated latitude longitude pair, where each coordinate is a floating point
        value. There may be an unlimited amount of whitespace after the comma before the longitude
         */
        Pattern regex = Pattern.compile("(-?\\d*\\.?\\d*),\\s*(-?\\d*\\.?\\d*)");
        Matcher match = regex.matcher(location);
        if (match.matches()) {
            double latitude = Double.valueOf(match.group(1));
            double longitude = Double.valueOf(match.group(2));
            QRCode.Geolocation geolocation = new QRCode.Geolocation(latitude, longitude);
            locationHelper.setLocation(geolocation);
            moveMapToLocation(geolocation);
        }
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
        QRCode.Geolocation geolocation = locationHelper.getLocation();
        moveMapToLocation(geolocation);
        googleMap.setOnInfoWindowClickListener(this);
    }

    private void moveMapToLocation(QRCode.Geolocation location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate camPosition;
        camPosition = CameraUpdateFactory.newLatLng(latLng);
        googleMap.moveCamera(camPosition);

        // FIXME: set search radius based on visible part of map
        locationHelper.refreshNearbyQRs(1, this);
    }

    /**
     * Implement GoogleMap.OnInfoWindowClickListener interface
     * Open the QRDetails activity for the selected QR marker info window
     * @param marker
     */
    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        QRCode code = (QRCode) marker.getTag();
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

    /**
     * Implements PlayerLocation.CallbackNearbyCodes
     * Called after the refreshed data has been retrieved by playerLocation
     */
    @Override
    public void onUpdateNearbyCodes() {
        ArrayList<QRCode> nearbyQRs = locationHelper.getNearbyCodes();
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
