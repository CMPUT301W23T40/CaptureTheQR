package com.cmput301w23t40.capturetheqr;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * This class models the location of a player and the QR codes around them
 */
public class PlayerLocation {
    /**
     * The location of the player
     */
    private LatLng location;

    /**
     * Get the location of the player
     * @return
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * Update the player's location
     * @param location
     */
    public void setLocation(LatLng location) {
        this.location = location;
    }

    /**
     * Get all the QR codes within a radius of the player location
     * @param radius
     * The maximum distance a QRCode can be from the current location for
     * it to be included in the returned list
     * @return
     * A list of QR codes within radius of the current location
     */
    public ArrayList<QRCode> getNearbyCodes(double radius) {
        // TODO: retrieve QR codes and filter by radius
        return new ArrayList<QRCode>();
    }
}
