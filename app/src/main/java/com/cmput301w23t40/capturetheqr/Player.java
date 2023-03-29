package com.cmput301w23t40.capturetheqr;

import java.io.Serializable;

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
    private int lowScore;
    private int rank;
    private String deviceID;
    private int numberOfCodes;
    private int scoreSum;

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

    public void setHighScore(int highScore){
        this.highScore = highScore;
    }
    public void setRank(int rank){
        this.rank= rank;
    }

    public void setLowScore(int lowScore){
        this.lowScore = lowScore;
    }
    public int getLowScore() {
        return lowScore;
    }

    public int getHighScore() {
        return highScore;
    }
    public int getRank() {
        return rank;
    }

    public int getNumberOfCodes() {
        return numberOfCodes;
    }
    public int getScoreSum() {
        return scoreSum;
    }
}
