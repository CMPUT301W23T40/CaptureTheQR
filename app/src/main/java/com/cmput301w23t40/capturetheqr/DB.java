package com.cmput301w23t40.capturetheqr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class does all the database queries. Note that many of the query methods have callback parameters.
 * If your function must be executed strictly after the queries, then you must put your function inside
 * the callback functions created when you call the query methods. If the queries can be executed asynchronously
 * then you can leave the callback blank, but be sure to write down a comment saying this blank is on purpose.
 * Also, no matter whether you use the callback functions or not, you must create them. In other words, they
 * cannot be null.
 */
public class DB {
    static private FirebaseFirestore database;
    static private CollectionReference collectionReferenceQR;
    static private CollectionReference collectionReferencePlayer;

    /**
     * Adding a setter to establish connection between firebase
     */
    static public void setDB(FirebaseFirestore instance){
        database = instance;
        collectionReferenceQR = database.collection("qrcode");
        collectionReferencePlayer = database.collection("player");
    }

    /**
     * Getting the Collection Reference for QR
     */
    static public CollectionReference getCollectionReferenceQR(){
        return collectionReferenceQR;
    }

    /**
     * This method only saves the hashValue, codeName, visualization and score.
     * Comments and scanner's info will be added by other methods.
     * @param qrCode qr code object to be added
     * @param callback actions to perform after the query is executed
     */
    static protected void saveQRCodeInDB(QRCode qrCode, Callback callback){
        DocumentReference documentReference = collectionReferenceQR.document(qrCode.getHashValue());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            Log.d("Saving a qrCode", "Hash value: " + qrCode.getHashValue() + "already exists");
                            callback.onCallBack();
                        } else {
                            documentReference
                                    .set(qrCode)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Log.d("Saving QRCode", "Hash value: " + qrCode.getHashValue() + "added");
                                            callback.onCallBack();
                                        }
                                    });
                        }
                    }
                });
    }

    /**
     * Save a comment in DB, no verification is implemented since if the user has not
     * scanned the code yet, the user cannot even see the button for commenting
     * @param qrCode qrCode object to be commented on
     * @param comment comment object
     * @param callback actions to perform after the query is executed
     */
    static protected void saveCommentInDB(QRCode qrCode, QRCode.Comment comment, Callback callback){
        collectionReferenceQR.document(qrCode.getHashValue())
                .update("comments", FieldValue.arrayUnion(comment))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Saving comment", "Hash value: "+ qrCode.getHashValue() + " Commenter: " + comment.getUsername());
                        callback.onCallBack();
                    }
                });
    }

    /**
     * Verify if the scanner is new to this code
     * @param qrCode qr code object
     * @param scannerInfo scanner info object
     * @param callbackVerifyIfScannerInfoIsNew actions to perform after the query is executed
     */
    static protected void verifyIfScannerInfoIsNew(QRCode qrCode, QRCode.ScannerInfo scannerInfo, CallbackVerifyIfScannerInfoIsNew callbackVerifyIfScannerInfoIsNew){
        collectionReferenceQR.document(qrCode.getHashValue())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        List<Map<String, Object>> scannerInfoArrayList = (List<Map<String, Object>>) documentSnapshot.get("scannersInfo");
                        if (scannerInfoArrayList != null) {
                            for (Map<String, Object> existingScannerInfo : scannerInfoArrayList) {
                                if (existingScannerInfo.get("username").equals(scannerInfo.getUsername())) {
                                    Log.d("Verifying if scanner info is new", "hash value: " + qrCode.getHashValue() + " scanner: " + scannerInfo.getUsername() + ' ' + false);
                                    callbackVerifyIfScannerInfoIsNew.onCallBack(false);
                                    return;
                                }
                            }
                        }
                        Log.d("Verifying if scanner info is new", scannerInfo.getUsername() + ' ' + true);
                        callbackVerifyIfScannerInfoIsNew.onCallBack(true);
                    }
                    });
    }

    /**
     * Save the scanner into to the qr code
     * @param qrCode qr code object
     * @param scannerInfo scanner info to be saved
     * @param callback actions to perform after the query is executed
     */
    static protected void saveScannerInfoInDB(QRCode qrCode, QRCode.ScannerInfo scannerInfo, Callback callback) {
        collectionReferenceQR.document(qrCode.getHashValue())
                .update("scannersInfo", FieldValue.arrayUnion(scannerInfo))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Saving scanner info", "Hash value: "+ qrCode.getHashValue() + " Scanner: " + scannerInfo.getUsername());
                        collectionReferenceQR.document(qrCode.getHashValue())
                                .update("timesScanned", FieldValue.increment(1));
                        callback.onCallBack();
                    }
                });
    }

    /**
     * Delete scanner info from the qr code
     * @param hashValue hash value of the qr code
     * @param username username of the scanner
     * @param callback actions to perform after the query is executed
     */
    static public void deleteScannerFromQRCode(String hashValue, String username, Callback callback){
        collectionReferenceQR.document(hashValue)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        List<Map<String, Object>> scannerInfoArrayList = (List<Map<String, Object>>) documentSnapshot.get("scannersInfo");
                        if (scannerInfoArrayList != null) {
                            if(scannerInfoArrayList.size() == 1){
                                Log.d("Deleting scannerInfo", "the whole code got deleted");
                                documentSnapshot.getReference().delete();
                                callback.onCallBack();
                                return;
                            } else {
                                for (Map<String, Object> existingScannerInfo : scannerInfoArrayList) {
                                    if (existingScannerInfo.get("username").equals(username)) {
                                        QRCode.ScannerInfo newScannerInfo = new QRCode.ScannerInfo(existingScannerInfo.get("username").toString(),
                                                existingScannerInfo.get("imageLink").toString());
                                        task.getResult().getReference().update("scannersInfo", FieldValue.arrayRemove(newScannerInfo));
                                        Log.d("Deleting scannerInfo", username + ' ' + "deleted");
                                        collectionReferenceQR.document(hashValue)
                                                .update("timesScanned", FieldValue.increment(-1));
                                        callback.onCallBack();
                                        return;
                                    }
                                }
                            }
                        }
                        Log.d("Deleting scannerInfo", username + ' ' + "not even exists");
                        callback.onCallBack();
                    }
                });
    }

    /**
     * This method adds a new player to the DB. Since it is only called when an account is being
     * generated, error checking is also done to make sure the user does not already exist
     * @param player the new player to add
     * @param cbplayerExists a call back to pass back to main describing if the player exists
     */
    static protected void addNewPlayer(Player player, CallbackAddNewPlayer cbplayerExists){
        //get the reference for the usernames
        DocumentReference docRef =  collectionReferencePlayer.document(player.getUsername());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //a "snapshot" of the data in the firestore db
                    DocumentSnapshot document = task.getResult();

                    //document exists, send a true back on the callback
                    if (document.exists()) {
                        Log.d("user exists?", "true");
                        cbplayerExists.onCallBack(true);
                    }
                    //document DNE, add the player, send a false on the callback
                    else {
                        Log.d("user exits?", "false");
                        docRef.set(player).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Adding new player", "completed");
                                cbplayerExists.onCallBack(false);
                            }
                        });
                    }
                //problems!!! lets log it
                } else {
                    Log.d("error getting user from db", task.getException().toString());
                }
            }
        });
    }

    /**
     * This method is for refreshing all the testing data in the DB, should be called at the start of
     * MainActivity.onCreate()
     */
    static public void refreshTestingDataInDB() {
        // CSC
        final double latCSC = 53.52683477962721;
        final double lonCSC = -113.5269479646002;
        // U of A hospital
        final double latHospital = 53.520888854154364;
        final double lonHospital = -113.52287265600516;
        // distance
        final double distance = Math.sqrt(Math.pow(latCSC-latHospital, 2) + Math.pow(lonCSC-lonHospital, 2));
        ArrayList<QRCode> qrCodes = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; ++i){
            qrCodes.add(new QRCode("hashValue " + i, "codeName " + i, "visualization " + i, i*10000, new QRCode.Geolocation(latCSC + distance * Math.cos(i+1) * i, lonCSC + distance * Math.sin(i+1) * i), 0));
            players.add(new Player("username " + i, String.valueOf(i+1111111111-1), "deviceID " + i));
        }
        for (int i = 0; i < players.size(); ++i){
            DB.addNewPlayer(players.get(i), new CallbackAddNewPlayer() {
                @Override
                public void onCallBack(Boolean playerExists) {
                    // nothing on purpose
                }
            });
        }
        for (int i = 0; i < qrCodes.size(); ++i) {
            int finalI = i;
            saveQRCodeInDB(qrCodes.get(finalI), new Callback() {
                @Override
                public void onCallBack() {
                        for (int n = 0; n < 2; n++) {
                            saveCommentInDB(qrCodes.get(finalI), new QRCode.Comment(players.get(n).getUsername(), String.valueOf("comment: " + n )), new Callback() {
                                @Override
                                public void onCallBack() {
                                    // nothing on purpose
                                }
                            });
                            saveScannerInfoInDB(qrCodes.get(finalI), new QRCode.ScannerInfo(players.get(n).getUsername(), String.valueOf("ImageLink " + n )), new Callback() {
                                @Override
                                public void onCallBack() {
                                    // nothing on purpose
                                }
                            });
                        }
                }
            });
        }
    }

    /**
     * Verify if deviceID is new
     * @param deviceID deviceID of the device that is being used
     * @param callbackVerifyIfDeviceIDIsNew actions to perform after the query is executed
     */
    static public void verifyIfDeviceIDIsNew(String deviceID, CallbackVerifyIfDeviceIDIsNew callbackVerifyIfDeviceIDIsNew){
        collectionReferencePlayer.whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.d("Verifying if deviceID is new", deviceID + " is new");
                        } else {
                            Log.d("Verifying if deviceID is new", deviceID + " is not new");
                        }
                        callbackVerifyIfDeviceIDIsNew.onCallBack(task.getResult().isEmpty());
                    }});
    }

    /**
     * Given a deviceID, returns the player object in the callback function
     * @param deviceID deviceID
     * @param callbackGetPlayer actions to be performed after the query is executed
     */
    static protected void getPlayer(String deviceID, CallbackGetPlayer callbackGetPlayer){
        collectionReferencePlayer.whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.d("Getting player from deviceID", "deviceID " + deviceID + " does not exists");
                            callbackGetPlayer.onCallBack(null);
                        } else {
                            Player player = task.getResult().getDocuments().get(0).toObject(Player.class);
                            Log.d("Getting player from deviceID", "deviceID: " + deviceID + " username: " + player.getUsername());
                            callbackGetPlayer.onCallBack(player);
                        }
                    }
                });
    }

    /**
     * Get all the qrcodes that have been scanned by all users
     * @param callbackGetAllQRCodes actions to perform after the query is done
     */
    static protected void getAllQRCodes(CallbackGetAllQRCodes callbackGetAllQRCodes){
        ArrayList<QRCode> qrCodes = new ArrayList<>();
        collectionReferenceQR.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.d("Getting all QR Codes", "No codes exist in the DB at at all");
                            callbackGetAllQRCodes.onCallBack(null);
                        } else {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                List<Map<String, Object>> scannerInfoArrayListInDB = (List<Map<String, Object>>) documentSnapshot.get("scannersInfo");
                                List<Map<String, Object>> commentsArrayListInDB = (List<Map<String, Object>>) documentSnapshot.get("comments");
//                            Log.d("scannerinfo", String.valueOf(scannerInfoArrayListInDB.size()));
//                            Log.d("comments", String.valueOf(commentsArrayListInDB.size()));
//
//                            Log.d("hashvalue", documentSnapshot.getString("hashValue"));
//                            Log.d("codename", documentSnapshot.getString("codeName"));
//                            Log.d("visualization", documentSnapshot.getString("visualization"));
//                            Log.d("score", String.valueOf(documentSnapshot.getLong("score").intValue()));
                                QRCode.Geolocation geolocation = null;
                                if(documentSnapshot.get("geolocation", QRCode.Geolocation.class) != null){
                                    geolocation = documentSnapshot.get("geolocation", QRCode.Geolocation.class);
                                }
                                QRCode newQRCode = new QRCode(
                                        documentSnapshot.getString("hashValue"),
                                        documentSnapshot.getString("codeName"),
                                        documentSnapshot.getString("visualization"),
                                        documentSnapshot.getLong("score").intValue(),
                                        geolocation,
                                        documentSnapshot.getLong("timesScanned").intValue());
                                ArrayList<QRCode.ScannerInfo> scannerInfoArrayList = new ArrayList<>();
                                if (scannerInfoArrayListInDB != null) {
                                    for (Map<String, Object> scannerInfo : scannerInfoArrayListInDB) {
                                        scannerInfoArrayList.add(new QRCode.ScannerInfo(scannerInfo.get("username").toString(),
                                                scannerInfo.get("imageLink").toString()));
                                    }
                                }
                                ArrayList<QRCode.Comment> commentsArrayList = new ArrayList<>();
                                if (commentsArrayListInDB != null) {
                                    for (Map<String, Object> comment : commentsArrayListInDB) {
                                        commentsArrayList.add(new QRCode.Comment(comment.get("username").toString(),
                                                comment.get("content").toString()));
                                    }
                                }
                                newQRCode.setScannersInfo(scannerInfoArrayList);
                                newQRCode.setComments(commentsArrayList);
                                qrCodes.add(newQRCode);
                            }
                            callbackGetAllQRCodes.onCallBack(qrCodes);
                        }
                    }
                });
    }
    /**
     * Get all the qrcodes that have been scanned by this user
     * @param callbackGetUsersQRCodes actions to perform after the query is done
     */
    static protected void getUsersQRCodes(Player player, CallbackGetUsersQRCodes callbackGetUsersQRCodes){
        ArrayList<QRCode> qrCodes = new ArrayList<>();
        collectionReferenceQR.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){

                            callbackGetUsersQRCodes.onCallBack(null);
                        } else {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                List<Map<String, Object>> scannerInfoArrayListInDB = (List<Map<String, Object>>) documentSnapshot.get("scannersInfo");
                                if (scannerInfoArrayListInDB == null){
                                    Log.d("Getting user's QR Codes", "Weird, this code does not have any scanner");
                                } else {
                                    for (Map<String, Object> scannerInfoForFindingUser : scannerInfoArrayListInDB){
                                        if(scannerInfoForFindingUser.get("username").equals(player.getUsername())){
                                            QRCode.Geolocation geolocation = null;
                                            if(documentSnapshot.get("geolocation", QRCode.Geolocation.class) != null){
                                                geolocation = documentSnapshot.get("geolocation", QRCode.Geolocation.class);
                                            }
                                            QRCode newQRCode = new QRCode(
                                                    documentSnapshot.getString("hashValue"),
                                                    documentSnapshot.getString("codeName"),
                                                    documentSnapshot.getString("visualization"),
                                                    documentSnapshot.getLong("score").intValue(),
                                                    geolocation,
                                                    documentSnapshot.getLong("timesScanned").intValue());
                                            ArrayList<QRCode.ScannerInfo> scannerInfoArrayList = new ArrayList<>();
                                            for (Map<String, Object> scannerInfo : scannerInfoArrayListInDB){
                                                scannerInfoArrayList.add(new QRCode.ScannerInfo(scannerInfo.get("username").toString(),
                                                        scannerInfo.get("imageLink").toString()));
                                            }
                                            List<Map<String, Object>> commentsArrayListInDB = (List<Map<String, Object>>) documentSnapshot.get("comments");
                                            ArrayList<QRCode.Comment> commentsArrayList = new ArrayList<>();
                                            if(commentsArrayListInDB != null){
                                                for (Map<String, Object> comment : commentsArrayListInDB){
                                                    commentsArrayList.add(new QRCode.Comment(comment.get("username").toString(),
                                                            comment.get("content").toString()));
                                                }
                                            }
                                            newQRCode.setScannersInfo(scannerInfoArrayList);
                                            newQRCode.setComments(commentsArrayList);
                                            qrCodes.add(newQRCode);
                                            break;
                                        }
                                    }
                                }
                            }
                            callbackGetUsersQRCodes.onCallBack(qrCodes);
                        }
                    }
                });
    }

    /**
     * Return the times scanned of the this code, if the return value is null, then this
     * code has never been scanned
     * @param qrCode qrCode object to be queried
     * @param callbackGetTimesScanned actions to perform after the query is done
     */
    static protected void getTimesScanned(QRCode qrCode, CallbackGetTimesScanned callbackGetTimesScanned){
        collectionReferenceQR.document(qrCode.getHashValue())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.getResult().exists()){
                            Log.d("Getting timesScanned of this code", String.valueOf(task.getResult().getLong("timesScanned").intValue()));
                            callbackGetTimesScanned.onCallBack(task.getResult().getLong("timesScanned").intValue());
                        } else {
                            Log.d("Getting timesScanned of this code", "Code does not exist");
                            callbackGetTimesScanned.onCallBack(null);
                        }
                    }
                });
    }

    static protected void getHighestScoringCodesOfUsers(CallbackGetHighestScoringCodes callbackGetHighestScoringCodes){
        HashMap<String, QRCode> highestScoringCodeOfUser = new HashMap<>();
        getAllQRCodes(new CallbackGetAllQRCodes() {
            @Override
            public void onCallBack(ArrayList<QRCode> allQRCodes) {
                for (QRCode qrCode : allQRCodes){
                    if(qrCode.getScannersInfo() != null){
                        for (QRCode.ScannerInfo scannerInfo : qrCode.getScannersInfo()){
                            if(!highestScoringCodeOfUser.containsKey(scannerInfo.getUsername())){
                                highestScoringCodeOfUser.put(scannerInfo.getUsername(), qrCode);
                            } else {
                                if(qrCode.getScore() > highestScoringCodeOfUser.get(scannerInfo.getUsername()).getScore()){
                                    highestScoringCodeOfUser.replace(scannerInfo.getUsername(), qrCode);
                                }
                            }
                        }
                    }
                }
                callbackGetHighestScoringCodes.onCallBack(highestScoringCodeOfUser);
            }
        });
    }
    static protected void getNumbersOfCodesOfUsers(CallbackGetNumbersOfCodesOfUsers callbackGetNumbersOfCodesOfUsers){
        HashMap<String, Integer> numbersOfCodesOfUsers = new HashMap<>();
        getAllQRCodes(new CallbackGetAllQRCodes() {
            @Override
            public void onCallBack(ArrayList<QRCode> allQRCodes) {
                for (QRCode qrCode : allQRCodes){
                    if (qrCode.getScannersInfo() != null){
                        for (QRCode.ScannerInfo scannerInfo : qrCode.getScannersInfo()){
                            if (!numbersOfCodesOfUsers.containsKey(scannerInfo.getUsername())){
                                numbersOfCodesOfUsers.put(scannerInfo.getUsername(), 1);
                            } else {
                                numbersOfCodesOfUsers.replace(scannerInfo.getUsername(), 1 + numbersOfCodesOfUsers.get(scannerInfo.getUsername()));
                            }
                        }
                    }
                }
                callbackGetNumbersOfCodesOfUsers.onCallBack(numbersOfCodesOfUsers);
            }
        });
    }
    static protected void getScoreSumsOfUsers(CallbackGetScoreSumsOfUsers callbackGetScoreSumsOfUsers){
        HashMap<String, Integer> scoreSumsOfUsers = new HashMap<>();
        getAllQRCodes(new CallbackGetAllQRCodes() {
            @Override
            public void onCallBack(ArrayList<QRCode> allQRCodes) {
                for (QRCode qrCode : allQRCodes){
                    if (qrCode.getScannersInfo() != null){
                        for (QRCode.ScannerInfo scannerInfo : qrCode.getScannersInfo()){
                            if (!scoreSumsOfUsers.containsKey(scannerInfo.getUsername())){
                                scoreSumsOfUsers.put(scannerInfo.getUsername(), qrCode.getScore());
                            } else {
                                scoreSumsOfUsers.replace(scannerInfo.getUsername(), qrCode.getScore() + scoreSumsOfUsers.get(scannerInfo.getUsername()));
                            }
                        }
                    }
                }
                callbackGetScoreSumsOfUsers.onCallBack(scoreSumsOfUsers);
            }
        });
    }
    static protected void getHighestScoringCodesNearby(){

    }

    /** The idea of using callbacks is learnt from Alex Mamo
     * Author: Alex Mamo
     * url: https://stackoverflow.com/questions/48499310/how-to-return-a-documentsnapshot-as-a-result-of-a-method/48500679#48500679
     * edited: Mar 3, 2018 at 15:48
     * license: CC BY-SA 3.0
     */

    public interface Callback {
        void onCallBack();
    }
    public interface CallbackGetPlayer {
        void onCallBack(Player player);
    }
    public interface CallbackVerifyIfScannerInfoIsNew {
        void onCallBack(Boolean scannerInfoIsNew);
    }
    public interface CallbackAddNewPlayer {
        void onCallBack(Boolean playerExists);
    }
    public interface CallbackVerifyIfDeviceIDIsNew {
        void onCallBack(Boolean deviceIDIsNew);
    }
    public interface CallbackGetAllQRCodes {
        void onCallBack(ArrayList<QRCode> allQRCodes);
    }
    public interface CallbackGetUsersQRCodes {
        void onCallBack(ArrayList<QRCode> myQRCodes);
    }
    public interface CallbackGetTimesScanned {
        void onCallBack(Integer timesScanned);
    }
    public interface CallbackGetHighestScoringCodes {
        void onCallBack(HashMap<String, QRCode> codeOfUser);
    }
    public interface CallbackGetNumbersOfCodesOfUsers {
        void onCallBack(HashMap<String, Integer> numberOfCodesOfUser);
    }
    public interface CallbackGetScoreSumsOfUsers {
        void onCallBack(HashMap<String, Integer> scoreSumOfUser);
    }
}
