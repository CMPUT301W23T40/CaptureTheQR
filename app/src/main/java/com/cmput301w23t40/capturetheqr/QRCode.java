package com.cmput301w23t40.capturetheqr;

import java.util.ArrayList;
import java.util.Date;

/**
 * Each QR code object maintains the actual code's hash value, the generated code name, visualization,
 * score. It also has a list of ScannerInfo objects, which maintains the scanners' names, the links to
 * the pictures, the dates of scan, and a list of Comment objects, which maintains the commenters' names,
 * the dates of the comments, and the comment contents.
 */
public class QRCode {
    private static class ScannerInfo {
        private String username;
        private String imageLink;
        private Date scannedDate;

        public ScannerInfo(String username, String imageLink, Date scannedDate) {
            this.username = username;
            this.imageLink = imageLink;
            this.scannedDate = scannedDate;
        }

        public String getUsername() {
            return username;
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
        private String username;
        private Date date;
        private String content;

        public Comment(String username, Date date, String content) {
            this.username = username;
            this.date = date;
            this.content = content;
        }

        public String getUsername() {
            return username;
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
        saveQRCodeInDB();
    }

    private void saveQRCodeInDB(){
    // FIXME DB
    }

    public void comment(String username, Date date, String content){
        Comment comment = new Comment(username, date, content);
        comments.add(comment);
        saveCommentInDB(comment);
    }

    private void saveCommentInDB(Comment comment){
        // FIXME need to save the comment to DB
    }

    public void addScanner(String username, String imageLink, Date scannedDate){
        ScannerInfo newScannerInfo = new ScannerInfo(username, imageLink, scannedDate);
        scannersInfo.add(newScannerInfo);
        saveScannerInfoInDB(newScannerInfo);
    }

    private void saveScannerInfoInDB(ScannerInfo scannerInfo){
        // FIXME need to save the comment to DB
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
