package com.cmput301w23t40.capturetheqr;

import java.util.ArrayList;

/**
 * This class represents a player of the game
 */
public class Player {
    /**
     * The username of the player, used to uniquely identify them.
     */
    private String username;
    /**
     * The phone number of the player, used for contact information
     */
    private String phoneNumber;
    /**
     * A list of QR codes the user has scanned
     */
    private ArrayList<QRCode> codes;


    public Player(String username, String phoneNumber) {
        this.username = username;
        this.phoneNumber = phoneNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Get the list of QR codes the player has scanned.
     * @return
     */
    public ArrayList<QRCode> getScannedCodes() {
        return this.codes;
    }
}