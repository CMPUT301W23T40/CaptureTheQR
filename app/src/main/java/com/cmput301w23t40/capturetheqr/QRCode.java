package com.cmput301w23t40.capturetheqr;

import java.util.ArrayList;
import java.util.Date;

public class QRCode {
    private static class ScannerInfo {
        private int scannerID;
        private String imageLink;
        private Date scannedDate;

        public ScannerInfo(int scannerID, String imageLink, Date scannedDate) {
            this.scannerID = scannerID;
            this.imageLink = imageLink;
            this.scannedDate = scannedDate;
        }

        public int getScannerID() {
            return scannerID;
        }

        public String getImageLink() {
            return imageLink;
        }

        public Date getScannedDate() {
            return scannedDate;
        }

        // optional feature
        public void deleteImage() {
            this.imageLink = null;
        }
    }

    private static class Comment{
        private String player;
        private Date date;
        private String content;
        public Comment(String player, Date date, String content) {
            this.player = player;
            this.date = date;
            this.content = content;
        }

        public String getPlayer() {
            return player;
        }

        public Date getDate() {
            return date;
        }

        public String getContent() {
            return content;
        }
    }
    private final String hashValue;
    private final String codeName;
    private final ArrayList<String> visualization;
    private final int score;
    private ArrayList<ScannerInfo> scannersInfo;
    private ArrayList<Comment> comments;
    private int timesScanned;

    public QRCode(String hashValue, String codeName, ArrayList<String> visualization, int score) {
        this.hashValue = hashValue;
        this.codeName = codeName;
        this.visualization = visualization;
        this.score = score;
        saveInDB();
    }

    public void saveInDB(){

    }
    // FIXME also need to save to DB
    public void comment(String player, Date date, String content){
        comments.add(new Comment(player, date, content));
    }

    public String getHashValue() {
        return hashValue;
    }

    public String getCodeName() {
        return codeName;
    }

    public ArrayList<String> getVisualization() {
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

    public int getTimesScanned() {
        return timesScanned;
    }
}
