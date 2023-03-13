package com.cmput301w23t40.capturetheqr;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * This class models the location of a player and the QR codes around them
 */
public class PlayerLocation {
    interface CallbackNearbyCodes {
        void onUpdateNearbyCodes();
    }
    /**
     * The location of the player
     */
    private QRCode.Geolocation location;
    private ArrayList<QRCode> nearbyCodes;

    public PlayerLocation() {
        // FIXME: For testing, this is set to somewhere in the middle of UofA quad
        this.location = new QRCode.Geolocation(53.52704,-113.52563);
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