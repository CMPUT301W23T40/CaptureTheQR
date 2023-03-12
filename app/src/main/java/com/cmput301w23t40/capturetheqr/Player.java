package com.cmput301w23t40.capturetheqr;

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

    private int highScore;
    private String deviceID;

    public Player() {
    }

    public Player(String username, String phoneNumber, String deviceID) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.deviceID = deviceID;
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

    public String getDeviceID() {
        return deviceID;
    }

    public void updateHighScore(){
        // TODO
    }
}
