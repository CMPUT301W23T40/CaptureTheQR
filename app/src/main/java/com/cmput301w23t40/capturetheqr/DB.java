package com.cmput301w23t40.capturetheqr;


import android.graphics.Bitmap;
import static java.lang.Integer.MAX_VALUE;
import android.util.Log;

import androidx.annotation.NonNull;
import android.util.Base64;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.primitives.Bytes;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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
    static protected void saveScannerInfoInDB(QRCode qrCode, QRCode.ScannerInfo scannerInfo, Bitmap photo, Callback callback) {
        saveImageInDB(scannerInfo.getUsername(), qrCode.getHashValue(), photo, new Callback() {
            @Override
            public void onCallBack() {
                Log.d("Saving scanner info", "Hash value: "+ qrCode.getHashValue() + " Scanner: " + scannerInfo.getUsername());
                collectionReferenceQR.document(qrCode.getHashValue())
                        .update("timesScanned", FieldValue.increment(1));
                collectionReferencePlayer.document(scannerInfo.getUsername())
                        .update("numberOfCodes", FieldValue.increment(1));
                collectionReferencePlayer.document(scannerInfo.getUsername())
                        .update("scoreSum", FieldValue.increment(qrCode.getScore()));
                collectionReferencePlayer.document(scannerInfo.getUsername()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Player player = task.getResult().toObject(Player.class);
                                if (qrCode.getScore() > player.getHighScore()){
                                    collectionReferencePlayer.document(scannerInfo.getUsername())
                                            .update("highScore", qrCode.getScore());
                                }
                            }
                        });
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
                            } else {
                                for (Map<String, Object> existingScannerInfo : scannerInfoArrayList) {
                                    if (existingScannerInfo.get("username").equals(username)) {
                                        String image = null;
                                        if(existingScannerInfo.get("imageLink") != null) {
                                            image = existingScannerInfo.get("imageLink").toString();
                                        }
                                        QRCode.ScannerInfo newScannerInfo = new QRCode.ScannerInfo(existingScannerInfo.get("username").toString(), image);
                                        task.getResult().getReference().update("scannersInfo", FieldValue.arrayRemove(newScannerInfo));
                                        Log.d("Deleting scannerInfo", username + ' ' + "deleted");
                                        collectionReferenceQR.document(hashValue)
                                                .update("timesScanned", FieldValue.increment(-1));
                                    }
                                }
                            }
                        }
                        collectionReferencePlayer.document(username)
                                .update("numberOfCodes", FieldValue.increment(-1));
                        collectionReferencePlayer.document(username)
                                .update("scoreSum", FieldValue.increment(-QRAnalyzer.generateScore(hashValue)));
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
     * Given a username, returns the player object in the callback function
     * Right now, this will only do exact match
     * @param username username to search for
     * @param callbackGetPlayer actions to be performed after the query is executed
     */
    static protected void searchForPlayer(String username, CallbackGetPlayer callbackGetPlayer){
        collectionReferencePlayer.whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.getResult().isEmpty()){
                                                   Log.d("Search for player", "username: " + username + " does not exists");
                                                   callbackGetPlayer.onCallBack(null);
                                               } else {
                                                   Player player = task.getResult().getDocuments().get(0).toObject(Player.class);
                                                   Log.d("Search for player", "username: " + username + " found");
                                                   callbackGetPlayer.onCallBack(player);
                                               }
                                           }
                                       }
                );
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
                            callbackGetAllQRCodes.onCallBack(qrCodes);
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
                                        String image = null;
                                        if(scannerInfo.get("imageLink") != null) {
                                            image = scannerInfo.get("imageLink").toString();
                                        }
                                        scannerInfoArrayList.add(new QRCode.ScannerInfo(scannerInfo.get("username").toString(), image));
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
                            callbackGetUsersQRCodes.onCallBack(qrCodes);
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
                                                String image = null;
                                                if(scannerInfo.get("imageLink") != null) {
                                                    image = scannerInfo.get("imageLink").toString();
                                                }
                                                scannerInfoArrayList.add(new QRCode.ScannerInfo(scannerInfo.get("username").toString(), image));
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
     * This function will get get the user's image of the specified QR from the db
     * @param username  the name of the user
     * @param hash  the hash value of the qr (ie. the id of the document in the DB)
     * @param cbGetImage    a callback to get the image
     */
    static protected void getImageFromDB(String username, String hash, CallbackGetImage cbGetImage){

        //get the qrcode
        DocumentReference docRef = collectionReferenceQR.document(hash);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    //document exists, now need to find correct picture
                    if(doc.exists()){

                        //get the scanner info obj from the db
                        List<Map<String, Object>> sci = (List<Map<String, Object>>) doc.get("scannersInfo");

                        //iterate through the object
                        for (int i = 0; i < sci.size(); i++)
                            //find the correct player
                            if(sci.get(i).get("username").equals(username))
                                //return the image
                                cbGetImage.onCallBack(sci.get(i).get("imageLink"));

                    }
                    //problems!!! lets log it
                } else
                    Log.d("Error getting qr from db", task.getException().toString());
            }
        });
    }

    /**
     * This function will save an image (in the form of a bitmap) to a users account
     * @param username  the user to save the image to
     * @param hash  the hash value of the qr (ie. the id of the document in the DB)
     * @param bmap  the bitmap of the image
     * @param cb    a basic callback showing completion
     */
    static protected void saveImageInDB(String username, String hash, Bitmap bmap, Callback cb){

        Map<String, Object> dataToInsert = new HashMap<>();
        String compressedImage = null;
        if(bmap != null){
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();

            compressedImage = Base64.encodeToString(data,Base64.DEFAULT);
        }

        dataToInsert.put("imageLink",compressedImage);
        dataToInsert.put("username",username);


        DocumentReference docRef = collectionReferenceQR.document(hash);

        docRef.update("scannersInfo", FieldValue.arrayUnion(dataToInsert))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               Log.d("Saving new image", "Saved");
                                               cb.onCallBack();
                                           }
                                       }
                );
    }

    /** Return the times scanned of the this code, if the return value is null, then this
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

    /**
     * The method gets the highest and lowest score for the players QR Codes
     * @param player
     * */
    static protected void getScore(Player player,CallbackScore callbackScore){
        List<Integer> scoreList = new ArrayList<Integer>();
        getUsersQRCodes(player, new CallbackGetUsersQRCodes() {
            @Override
            public void onCallBack(ArrayList<QRCode> myQRCodes) {
                int highScore = 0;
                int lowestScore = MAX_VALUE;
                QRCode maxCode = null;
                QRCode minCode = null;
                for(QRCode qrCode:myQRCodes){
                    if(qrCode!=null) {

                        if(qrCode.getScore()>highScore){
                            highScore = qrCode.getScore();
                            maxCode = qrCode;
                        }
                        if(qrCode.getScore()<lowestScore){
                            lowestScore = qrCode.getScore();
                            minCode = qrCode;
                        }


                        callbackScore.onCallBack(maxCode, minCode);
                    }
                }
            }
        });

    }

    /**
     * The query gets all the players from the database
     * */
    static protected void getAllPlayers(CallbackAllPlayers callbackAllPlayers){
        ArrayList<Player> allPlayers = new ArrayList<>();
        collectionReferencePlayer.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()){
                            Log.d("Getting all players", "No players exist in the DB at at all");
                        } else {
                            for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                                allPlayers.add(documentSnapshot.toObject(Player.class));
                            }
                        }
                        callbackAllPlayers.onCallBack(allPlayers);
                    }
                });
    }

    /**
     * Delete all the codes in the DB, should only be called for testing
     * @param callbackDeleteAllCodes actions to perform after the query is done
     */
    private static void deleteAllCodes(CallbackDeleteAllCodes callbackDeleteAllCodes){
        collectionReferenceQR.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            documentSnapshot.getReference().delete();
                        }
                        callbackDeleteAllCodes.onCallBack();
                        Log.d("Deleting all codes", "deleted");
                    }
                });
    }

    /**
     * Update the geolocation of the code scanned if the new geolocation is not null
     * @param qrCode qrCode object to be updated
     */
    protected static void updateGeolocationOfCode(QRCode qrCode){
        if (qrCode.getGeolocation() != null){
            Map<String, Object> newLocation = new HashMap<>();
            newLocation.put("geolocation", qrCode.getGeolocation());
            collectionReferenceQR.document(qrCode.getHashValue())
                    .update(newLocation);
        }
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

    public interface CallbackGetImage {
        void onCallBack(Object o);
    }

    public interface CallbackScore {
        void onCallBack(QRCode max, QRCode min);
    }

    public interface CallbackGetTimesScanned {
        void onCallBack(Integer timesScanned);
    }

    public interface CallbackDeleteAllCodes {
        void onCallBack();
    }

    public interface CallbackOrderQRCodes {
        void onCallBack(ArrayList<QRCode> orderedQRCodes);
    }
    public interface CallbackAllPlayers {
        void onCallBack(ArrayList<Player> allPlayers);
    }
}