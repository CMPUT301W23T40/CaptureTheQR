package com.cmput301w23t40.capturetheqr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class does all the database queries.
 */
public class DB {
    static private FirebaseFirestore database;
    static private CollectionReference collectionReferenceQR;
    static private CollectionReference collectionReferencePlayer;

    /**
     * Adding a setter to establish connection between firebase*/
    static public void setDB(FirebaseFirestore instance){
        database = instance;
        collectionReferenceQR = database.collection("qrcode");
        collectionReferencePlayer = database.collection("player");
    }

    /**Getting the Collection Reference for QR */

    static public CollectionReference getCollectionReferenceQR(){
        return collectionReferenceQR;
    }

    /**
     * This method only saves the hashValue, codeName, visualization and score.
     * Comments and scanner's info will be added by other methods.
     * @param qrCode QRCode object to save to DB
     */
    // tested
    static protected void saveQRCodeInDB(QRCode qrCode){
        Map<String, Object> newQRCode = new HashMap<>();
        newQRCode.put("qrName", qrCode.getCodeName());
        newQRCode.put("qrScore", qrCode.getScore());
        newQRCode.put("qrVisual", qrCode.getVisualization());
        collectionReferenceQR.document(qrCode.getHashValue())
                .set(newQRCode)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding QRCode", "succeeded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Adding QRCode", "failed", e);
                    }
                });
    }

    /**
     * Save a comment in DB, no verification is implemented since if the user has not
     * scanned the code yet, the user cannot even see the button for commenting
     * @param qrCode qrCode object to be commented on
     * @param comment comment, which includes the commenter's name, date, content
     */
    static protected void saveCommentInDB(QRCode qrCode, QRCode.Comment comment){
        Map<String, Object> newComment = new HashMap<>();
        newComment.put("content", comment.getContent());
        newComment.put("date", comment.getDate());
        collectionReferenceQR.document(qrCode.getHashValue())
                .collection("Comments")
                .document(comment.getUsername())
                .set(newComment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding comment", "succeeded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Adding comment", "failed", e);
                    }
                });
    }

    /**
     * Add the scanner's info to the qr code. If the scanner scanned this code before,
     * return false, else true.
     * @param qrCode
     * @param scannerInfo
     */
    static protected boolean saveScannerInfoInDB(QRCode qrCode, QRCode.ScannerInfo scannerInfo){
        final boolean[] successfullyAdded = {false};
        final boolean[] alreadyExists = {false};
        collectionReferenceQR.document(qrCode.getHashValue())
                .collection("ScannerInfo")
                .document(scannerInfo.getUsername())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()){
                                Log.d("Saving scanner to code", "scanner already exists");
                                alreadyExists[0] = true;
                            } else {
                                Log.d("Saving scanner to code", "scanner does not exist");
                            }
                        } else {
                            Log.d("Verifying if username is new", "failed to verify");
                        }
                    }
                });
        if (alreadyExists[0]){
            return false;
        } else {
            Map<String, Object> newScannerInfo = new HashMap<>();
            newScannerInfo.put("imageLink", scannerInfo.getImageLink());
            newScannerInfo.put("scannedDate", scannerInfo.getScannedDate());
            collectionReferenceQR.document(qrCode.getHashValue())
                    .collection("ScannerInfo")
                    .document(scannerInfo.getUsername())
                    .set(newScannerInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Adding scanner info", "succeed");
                            successfullyAdded[0] = true;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Adding scanner info", "failed", e);
                        }
                    });
        }
        return successfullyAdded[0];
    }

    /**
     * Method for deleting the scanner from the QRCode in DB
     * */
    static public void deleteScannerFromQRCode(String hashValue, String username){
        collectionReferenceQR.document(hashValue)
                .collection("ScannerInfo")
                .document(username)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Deleting scanner from qr code", "succeeded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Deleting scanner from qr code", "failed", e);
                    }
                });
    }

    /**
     * Create a new user record in the database, could only be called when the user is new,
     * so no verification is implement to query if the user already exists in the DB before
     * adding the user to the DB
     * @param player player object
     */
    static protected void savePlayerInDB(Player player){
        Map<String, Object> newPlayer = new HashMap<>();
        newPlayer.put("deviceID", player.getDeviceID());
        newPlayer.put("phoneNumber", player.getPhoneNumber());
        collectionReferenceQR.document(player.getUsername())
                .set(newPlayer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Adding player", "succeeded");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Adding player", "failed", e);
                    }
                });
    }

    /**
     * This method is for refreshing all the testing data in the DB, should be called at the start of
     * MainActivity.onCreate()
     */
    static public void refreshTestingDataInDB(){
        ArrayList<QRCode> qrCodes = new ArrayList<>();
        qrCodes.add(new QRCode("fakeHashValue", "fakeCodeName", "fakeVisual", 12562));
        for(QRCode qrCode : qrCodes){
            delQRCodeInDB(qrCode.getHashValue());
            saveQRCodeInDB(qrCode);
        }
    }

    /**
     * Method for deleting QR codes from DB, should only be used for refreshing testing data
     * */
    // tested
    static private void delQRCodeInDB(String hashValue){
        collectionReferenceQR.document(hashValue)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Deleting the whole code", "succeeded");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Deleting the whole code", "failed" + e);
                    }
                });
    }


    /**
     * Query if the device ID is new in the database
     * @param deviceID deviceID to be searched for in the database
     * @return true if the device is new in the database; false if it exists
     */
    static public boolean deviceIDIsNew(String deviceID){
        final boolean[] deviceIDIsNew = {false};
        collectionReferencePlayer.whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Log.d("Verifying if deviceID is new", "yes");
                            } else {
                                Log.d("Verifying if deviceID is new", "no");
                                deviceIDIsNew[0] = true;
                            }
                        } else {
                            Log.d("Verifying if deviceID is new", "failed to verify");
                        }
                    }
                });
        return deviceIDIsNew[0];
    }

    /**
     * Query if the username is new in the database
     * @param username the username to be searched for
     * @return true if the username is new in the DB; false if it exists
     */
    static public boolean UsernameIsNew(String username){
        final boolean[] usernameIsNew = {false};
        collectionReferencePlayer.document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()){
                                Log.d("Verifying if username is new", "no");
                            } else {
                                Log.d("Verifying if username is new", "yes");
                                usernameIsNew[0] = true;
                            }
                        } else {
                            Log.d("Verifying if username is new", "failed to verify");
                        }
                    }
                });
        return usernameIsNew[0];
    }

    /**
     * Given a unique deviceID, return the username
     * @param deviceID unique deviceID
     * @return username, could be null
     */
    static protected String getUserName(String deviceID){
        final String[] username = new String[1];
        username[0] = null;
        collectionReferencePlayer.whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Getting username from deviceID", "succeeded");
                                username[0] = document.getId();
                            }
                        } else {
                            Log.d("Getting username from deviceID", "failed");
                        }
                    }
                });
        return username[0];
    }
}
