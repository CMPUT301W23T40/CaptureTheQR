package com.cmput301w23t40.capturetheqr;

import com.google.firebase.Timestamp;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Each QR code object maintains the actual code's hash value, the generated code name, visualization,
 * score. It also has a list of ScannerInfo objects, which maintains the scanners' names, the links to
 * the pictures, the dates of scan, and a list of Comment objects, which maintains the commenters' names,
 * the dates of the comments, and the comment contents.
 */
public class QRCode implements Serializable {
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

    protected static class Comment implements Serializable {
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

    protected static class Geolocation implements Serializable {
        private double latitude, longitude;

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
    }
    private String hashValue;
    private String codeName;
    private String visualization;
    private int score;
    private ArrayList<ScannerInfo> scannersInfo;
    private ArrayList<Comment> comments;
    private Geolocation geolocation;


    public QRCode(String hashValue, String codeName, String visualization, int score, Geolocation geolocation) {
        this.hashValue = hashValue;
        this.codeName = codeName;
        this.visualization = visualization;
        this.score = score;
        this.geolocation = new Geolocation(geolocation.latitude, geolocation.longitude);
    }

    public void comment(String username, String content){
        Comment comment = new Comment(username, content);
        comments.add(comment);
        DB.saveCommentInDB(this, comment, new DB.Callback() {
            @Override
            public void onCallBack() {
                // nothing on purpose
            }
        });
    }

    public void addScanner(String username, String imageLink, Timestamp scannedDate){
        ScannerInfo newScannerInfo = new ScannerInfo(username, imageLink);
        scannersInfo.add(newScannerInfo);
        DB.verifyIfScannerInfoIsNew(QRCode.this, newScannerInfo, new DB.CallbackVerifyIfScannerInfoIsNew() {
            @Override
            public void onCallBack(Boolean scannerIsNew) {
                if(scannerIsNew){
                    Log.d("Verifying if scanner info is new", "YES");
                    DB.saveScannerInfoInDB(QRCode.this, newScannerInfo, new DB.Callback() {
                        @Override
                        public void onCallBack() {
                            // nothing on purpose
                        }
                    });
                }else{
                    Log.d("Verifying if scanner info is new", "NO");
                    // prompts the user something
                }
            }
        });
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

    public int getTimesScanned(){
        if (scannersInfo != null) {
            return scannersInfo.size();
        }
        return -1;
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
