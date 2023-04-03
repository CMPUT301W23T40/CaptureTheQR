package com.cmput301w23t40.capturetheqr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

/**
 * This class models the location of a player and the QR codes around them
 */
public class PlayerLocation {
    /**
     * Callback for when the nearby codes have been processed
     */
    public interface CallbackNearbyCodes {
        void onUpdateNearbyCodes();
    }

    /**
     * Callback for when the current geolocation has been obtained from location services
     */
    public interface CallbackLocation {
        void onUpdateLocation();
    }

    /**
     * The location of the player
     */
    private QRCode.Geolocation location;
    private FusedLocationProviderClient locationClient;
    private ArrayList<QRCode> nearbyCodes;

    public PlayerLocation(Context context) {
        // FIXME: For testing, this is set to somewhere in the middle of UofA quad
        this.location = new QRCode.Geolocation(53.5267106493, -113.527117074);
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
        nearbyCodes = new ArrayList<>();
    }

    /**
     * Get the location of the player
     * @return
     */
    public QRCode.Geolocation getLocation() {
        return location;
    }

    /**
     * Request an update to the current location from location services
     * Assumes the calling activity has location permissions
     * @param callback Callback to be called when new location information is available
     */
    @SuppressLint("MissingPermission")
    public void updateLocation(CallbackLocation callback) {
        QRCode.Geolocation geolocation;
        locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location currentLocation) {
                        if (currentLocation != null) {
                            location.setLatitude(currentLocation.getLatitude());
                            location.setLongitude(currentLocation.getLongitude());
                            callback.onUpdateLocation();
                        }
                    }
                });
    }

    /**
     * Update the player's location
     * @param location
     */
    public void setLocation(QRCode.Geolocation location) {
        this.location = location;
    }

    /**
     * Refresh the list of nearby QRs
     * @param radius The geographic radius to filter codes to
     * @param callback An object implementing onUpdateNearbyCodes,
     *                 which will retrieve the updated data
     */
    public void refreshNearbyQRs(double radius, CallbackNearbyCodes callback) {
        this.nearbyCodes.clear();
        DB.getAllQRCodes(new DB.CallbackGetAllQRCodes() {
            @Override
            public void onCallBack(ArrayList<QRCode> allQRCodes) {
                for (QRCode qr: allQRCodes) {
                    // Filter out QRCodes with no location
                    // TODO: filter QR codes outside the radius
                    if (qr.getGeolocation() != null) {
                        nearbyCodes.add(qr);
                    }
                }
                callback.onUpdateNearbyCodes();
            }
        });
    }

    /**
     * Get all the QR codes within a radius of the player location
     * @return
     * A list of QR codes near the players current location
     */
    public ArrayList<QRCode> getNearbyCodes() {
        return this.nearbyCodes;
    }
}