package com.cmput301w23t40.capturetheqr;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            public boolean onQueryTextSubmit(String searchText) {
                searchMap(searchText);
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
     * Handle the location search query from the user
     * @param location
     * The user supplied location query
     */
    private void searchMap(String location) {
        Geocoder geocoder = new Geocoder(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(location, 1, new Geocoder.GeocodeListener() {
                @Override
                public void onGeocode(@NonNull List<Address> locationResults) {
                    handleGeocodeResults(locationResults);
                }
                public void onError(String errorMessage) {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT);
                }
            });
        } else {
            List<Address> locationResults = null;
            try {
                locationResults = geocoder.getFromLocationName(location, 1);
                handleGeocodeResults(locationResults);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), R.string.search_location_failure, Toast.LENGTH_SHORT);
            }
        }
    }

    /**
     * Get the coordinates from the geocode results and update the map
     * @param locationResults
     */
    private void handleGeocodeResults(@NonNull List<Address> locationResults) {
        if (locationResults.size() > 0) {
            double latitude = locationResults.get(0).getLatitude();
            double longitude = locationResults.get(0).getLongitude();
            QRCode.Geolocation geolocation = new QRCode.Geolocation(latitude, longitude);
            locationHelper.setLocation(geolocation);
            moveMapToLocation(geolocation);
        } else {
            Toast.makeText(getApplicationContext(), R.string.search_location_failure, Toast.LENGTH_SHORT);
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
