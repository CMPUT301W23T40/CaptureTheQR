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

    public PlayerLocation() {
        // FIXME: For testing, this is set to somewhere in the middle of UofA quad
        this.location = new LatLng(53.52704,-113.52563);
    }

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
        LatLng location1 = new LatLng(53.52710,-113.52611);
        LatLng location2 = new LatLng(53.52726,-113.52520);
        QRCode test1 = new QRCode("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6",
                "Test name 1", "test vis 1", 100, location1);
        QRCode test2 = new QRCode("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6",
                "Test name 2", "test vis 2", 1000, location2);
        ArrayList<QRCode> codes = new ArrayList<QRCode>();
        codes.add(test1);
        codes.add(test2);
        return codes;
    }
}