package com.cmput301w23t40.capturetheqr;

import com.google.firebase.Timestamp;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;

/**
 * Each QR code object maintains the actual code's hash value, the generated code name, visualization,
 * score. It also has a list of ScannerInfo objects, which maintains the scanners' names, the links to
 * the pictures, the dates of scan, and a list of Comment objects, which maintains the commenters' names,
 * the dates of the comments, and the comment contents.
 */
public class QRCode implements Serializable{
    protected static class ScannerInfo implements Serializable{
        private String username;
        private String imageLink;

        public ScannerInfo() {
        }

        public ScannerInfo(String username, String imageLink) {
            this.username = username;
            this.imageLink = imageLink;
        }

        public String getUsername() {
            return username;
        }

        public String getImageLink() {
            return imageLink;
        }

        // optional feature
        public void deleteImage() {
            this.imageLink = null;
        }
    }

    protected static class Comment implements Serializable{
        private String username;
        private String content;

        public Comment() {
        }

        public Comment(String username, String content) {
            this.username = username;
            this.content = content;
        }

        public String getUsername() {
            return username;
        }

        public String getContent() {
            return content;
        }
    }

    protected static class Geolocation implements Serializable{
        private double latitude, longitude;
        final static private double radius = 0.5;

        public Geolocation() {
        }

        public Geolocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        @Override
        public String toString(){
            return this.latitude + ", " + this.longitude;
        }

        static public boolean nearby(Geolocation g1, Geolocation g2){
            return Math.pow(g1.getLatitude() - g2.getLatitude(), 2) +
                    Math.pow(g1.getLongitude() - g2.getLongitude(), 2)
                    < radius;
        }
    }
    private String hashValue;
    private String codeName;
    private String visualization;
    private int score;
    private ArrayList<ScannerInfo> scannersInfo;
    private ArrayList<Comment> comments;
    private Geolocation geolocation;
    private int timesScanned;

    public QRCode() {
    }

    public QRCode(String hashValue, String codeName, String visualization, int score, Geolocation geolocation, int timesScanned) {
        this.hashValue = hashValue;
        this.codeName = codeName;
        this.visualization = visualization;
        this.score = score;
        this.geolocation = geolocation;
        this.timesScanned = timesScanned;
    }

    public String getHashValue() {
        return hashValue;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getVisualization() {
        return visualization;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<ScannerInfo> getScannersInfo() {
        return scannersInfo;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public int getTimesScanned(){
        return timesScanned;
    }

    @Override
    public String toString() {
        return "QRCode{" +
                "hashValue='" + hashValue + '\'' +
                ", codeName='" + codeName + '\'' +
                ", visualization='" + visualization + '\'' +
                ", score=" + score +
                ", scannersInfo=" + scannersInfo +
                ", comments=" + comments +
                ", geolocation=" + geolocation +
                '}';
    }

    public void setScannersInfo(ArrayList<ScannerInfo> scannersInfo) {
        this.scannersInfo = scannersInfo;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
}
