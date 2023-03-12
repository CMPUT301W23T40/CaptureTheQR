package com.cmput301w23t40.capturetheqr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
        Map<String, Object> newQRCode = new HashMap<>();
        newQRCode.put("qrName", qrCode.getCodeName());
        newQRCode.put("qrScore", qrCode.getScore());
        newQRCode.put("qrVisual", qrCode.getVisualization());

        collectionReferenceQR.document(qrCode.getHashValue())
                .set(newQRCode)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Saving QRCode", "completed");
                        callback.onCallBack();
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
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("content", comment.getContent());
        newComment.put("date", comment.getDate());
        collectionReferenceQR.document(qrCode.getHashValue())
                .collection("Comments")
                .document(comment.getUsername())
                .set(newComment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Saving comment", "completed");
                        callback.onCallBack();
                    }
                });
    }

    /**
     * Verify if the scanner is new to this code
     * @param qrCode qr code object
     * @param scannerInfo scanner info object
     * @param verifyIfScannerInfoIsNew actions to perform after the query is executed
     */
    static protected void verifyIfScannerInfoIsNew(QRCode qrCode, QRCode.ScannerInfo scannerInfo, VerifyIfScannerInfoIsNew verifyIfScannerInfoIsNew){
        collectionReferenceQR.document(qrCode.getHashValue())
                .collection("ScannerInfo")
                .document(scannerInfo.getUsername())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("Verifying if scanner info is new", "completed");
                        verifyIfScannerInfoIsNew.onCallBack(!(task.getResult().exists()));
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
        Map<String, Object> newScannerInfo = new HashMap<>();
        newScannerInfo.put("imageLink", scannerInfo.getImageLink());
        newScannerInfo.put("scannedDate", scannerInfo.getScannedDate());
        collectionReferenceQR.document(qrCode.getHashValue())
                .collection("ScannerInfo")
                .document(scannerInfo.getUsername())
                .set(newScannerInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Saving scanner info", "completed");
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
                .collection("ScannerInfo")
                .document(username)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Deleting scanner info", "completed");
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
        Map<String, Object> newPlayer = new HashMap<>();
        newPlayer.put("deviceID", player.getDeviceID());
        newPlayer.put("phoneNumber", player.getPhoneNumber());

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
                        docRef.set(newPlayer).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        ArrayList<QRCode> qrCodes = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < 5; ++i){
            i+=1;
            qrCodes.add(new QRCode("hashValue " + i, "codeName " + i, "visualization " + i, i*10000, null));
            players.add(new Player("username " + i, String.valueOf(i*111) + "-" + String.valueOf(i*111) + "-" +String.valueOf(i*1111), "deviceID " + i));
            i-=1;
        }
        for (int i = 0; i < players.size(); ++i){

            DB.addNewPlayer(players.get(i), new CallbackAddNewPlayer() {
                @Override
                public void onCallBack(Boolean playerExists) {
                    // nothing on purpose
                }
            });
        }
        for (int i = 0; i < qrCodes.size(); ++i){
            int finalI = i;
            delAllCommentInDB(qrCodes.get(i).getHashValue(), new Callback() {
                @Override
                public void onCallBack() {
                    delAllScannerInfoInDB(qrCodes.get(finalI).getHashValue(), new Callback() {
                        @Override
                        public void onCallBack() {
                            // comments and scanner info need to be deleted before deleting the qrCode
                            // otherwise they still exist in the db console
                            delQRCodeInDB(qrCodes.get(finalI).getHashValue(), new Callback() {
                                @Override
                                public void onCallBack() {
                                    saveQRCodeInDB(qrCodes.get(finalI), new Callback() {
                                        @Override
                                        public void onCallBack() {
                                            for (int n = 0; n < qrCodes.size(); n++){
                                                saveCommentInDB(qrCodes.get(finalI), new QRCode.Comment(players.get(n).getUsername(), new Date(), String.valueOf("comment: " + (n + 1))), new Callback() {
                                                    @Override
                                                    public void onCallBack() {
                                                        // nothing on purpose
                                                    }
                                                });
                                                saveScannerInfoInDB(qrCodes.get(finalI), new QRCode.ScannerInfo(players.get(n).getUsername(), String.valueOf("ImageLink " + (n + 1)), new Date()), new Callback() {
                                                    @Override
                                                    public void onCallBack() {
                                                        // nothing on purpose
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }

    /**
     * Method for deleting QR codes from DB, should only be used for refreshing testing data
     * @param hashValue hashvalue of the qrCode to be deleted
     * @param callback actions to perform after the query is executed
     */
    static private void delQRCodeInDB(String hashValue, Callback callback) {
         collectionReferenceQR.document(hashValue)
                .delete()
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         Log.d("Deleting the whole code", "completed");
                         callback.onCallBack();
                     }
                 });
    }

    /**
     * Delete all the comments on the qr code, need to be called before deleting the qr code itself
     * @param hashValue hash value of the qr code
     * @param callback actions to perform after the query is executed
     */
    static private void delAllCommentInDB(String hashValue, Callback callback){
        collectionReferenceQR.document(hashValue)
                .collection("Comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            documentSnapshot.getReference().delete();
                        }
                        Log.d("Deleting Comments", "completed");
                        callback.onCallBack();
                    }});
    }

    /**
     * Delete all the scanner info on the qr code, need to be called before deleting the qr code itself
     * @param hashValue hash value of the qr code
     * @param callback actions to perform after the query is executed
     */
    static private void delAllScannerInfoInDB(String hashValue, Callback callback){
        collectionReferenceQR.document(hashValue)
                .collection("ScannerInfo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            documentSnapshot.getReference().delete();
                        }
                        Log.d("Deleting ScannerInfo", "completed");
                        callback.onCallBack();
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
                        Log.d("Verifying if deviceID is new", "completed");
                        callbackVerifyIfDeviceIDIsNew.onCallBack(task.getResult().isEmpty());
                    }});
    }

    /**
     * Given a deviceID, returns the username in the callback function
     * @param deviceID deviceID
     * @param callbackGetUsername actions to be performed after the query is executed
     */
    static protected void getUserName(String deviceID, CallbackGetUsername callbackGetUsername){
        collectionReferencePlayer.whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("Getting username from deviceID", "completed");
                        callbackGetUsername.onCallBack(task.getResult().getDocuments().get(0).getId());
                    }
                });
    }

    /**
     * This function returns the visualization of the given QR code via callback
     * If there is no matching QR code, nothing comes through via callback
     * @param hashValue the unique value of the QR to get the visualization from
     * @param CallbackGetVisualization the callback for the method (pass in DB.CallbackGet...
     */
    static protected void getVisualization(String hashValue, CallbackGetVisualization CallbackGetVisualization){
        DocumentReference docRef = collectionReferenceQR.document(hashValue);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    //a "snapshot" of the data in the firestore db
                    DocumentSnapshot document = task.getResult();

                    //document exists, send the visual on call back
                    if (document.exists()) {
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Log.d("visual data", document.getData().get("qrVisual").toString());
                                CallbackGetVisualization.onCallBack(document.getData().get("qrVisual").toString());
                            }
                        });
                    }
                    //document DNE, log it, do nothing
                    else {
                        Log.d("visual data", "does not exist");
                    }
                }
                else {
                    Log.d("error getting visual from db", task.getException().toString());
                }
            }
        });
    }


    static protected void getQRCodeInDB(String hash, String location){
        // FIXME DB
        //  logic: loop through all QRCodes and find object matching the hash + location
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
    public interface CallbackGetUsername {
        void onCallBack(String username);
    }
    public interface VerifyIfScannerInfoIsNew {
        void onCallBack(Boolean scannerIsNew);
    }
    public interface CallbackAddNewPlayer {
        void onCallBack(Boolean playerExists);
    }
    public interface CallbackVerifyIfDeviceIDIsNew {
        void onCallBack(Boolean deviceIDIsNew);
    }
    public interface CallbackGetVisualization {
        void onCallBack(String visualization);
    }
}
