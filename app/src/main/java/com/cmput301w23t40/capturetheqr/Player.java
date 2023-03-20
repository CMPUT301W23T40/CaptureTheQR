package com.cmput301w23t40.capturetheqr;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a player of the game
 */
public class Player implements Serializable {
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

    // code adapted from CORE JAVA Volume I-Fundamentals, 11th Edition section 5.2.3
    // Author: Cay Horstmann
    @Override
    public boolean equals(@Nullable Object obj) {
        // if the references refer to the same object
        if(this == obj) return true;
        // if the parameter is a null reference
        if(obj == null) return false;
        // if the parameter is not a Player
        if(this.getClass() != obj.getClass()) return false;
        // cast the parameter to a Player object
        Player other = (Player) obj;
        // compare the equality of the strings
        return Objects.equals(this.getUsername(), other.getUsername());
    }
}
