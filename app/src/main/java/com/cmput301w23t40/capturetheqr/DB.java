package com.cmput301w23t40.capturetheqr;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
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
     * Known bug: cannot be right after saveQRCodeInDB()
     * @param qrCode qrCode object to be commented on
     * @param comment comment, which includes the commenter's name, date, content
     */
    // tested
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
    // tested
    static protected boolean saveScannerInfoInDB(QRCode qrCode, QRCode.ScannerInfo scannerInfo){
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
                            } else {
                                Log.d("Saving scanner to code", "scanner does not exist");
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
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Adding scanner info", "failed", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d("Verifying if username is new", "failed to verify");
                        }
                    }
                });
        // FIXME return value
        return false;
    }

    /**
     * Method for deleting the scanner from the QRCode in DB
     * */
    // tested
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
    // tested
    static protected void savePlayerInDB(Player player){
        Map<String, Object> newPlayer = new HashMap<>();
        newPlayer.put("deviceID", player.getDeviceID());
        newPlayer.put("phoneNumber", player.getPhoneNumber());
        collectionReferencePlayer.document(player.getUsername())
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
            //delQRCodeInDB(qrCode.getHashValue());
            //saveQRCodeInDB(qrCode);
            //saveCommentInDB(qrCode, new QRCode.Comment("commenter", new Date(), "content"));
            //saveScannerInfoInDB(qrCode, new QRCode.ScannerInfo("scanner name", "fakeimageLink", new Date()));
            // comment tested
            // scanner info delete add(scanner exists)
            // add player tested

            // device ID new  tested
            // username new
            // get username
            //savePlayerInDB(new Player("fakeplayer", "123-456-789", "fake deviceID"));
            //Log.d("", String.valueOf(deviceIDIsNew("fake deviceID")));


        }
    }

    /**
     * Method for deleting QR codes from DB, should only be used for refreshing testing data
     * */
    // tested
    static private void delQRCodeInDB(String hashValue){
        collectionReferenceQR.document(hashValue)
                .collection("Comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            documentSnapshot.getReference().delete();
                        }
                    }
                });
        collectionReferenceQR.document(hashValue)
                .collection("ScannerInfo")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            documentSnapshot.getReference().delete();
                        }
                    }
                });
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
     * Prompt the user to register if the deviceID does not exists in the DB
     * @param deviceID deviceID to be searched for in the DB
     * @param context
     */
    static public void registerIfNewUser(String deviceID, Context context){
        collectionReferencePlayer.whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if(task.getResult().isEmpty()){
                                Log.d("Verifying if deviceID is new", "yes");
                                startActivity(context, new Intent(context, FirstTimeLogInActivity.class), null);
                            } else {
                                Log.d("Verifying if deviceID is new", "no");
                            }
                        } else {
                            Log.d("Verifying if deviceID is new", "failed to verify");
                        }
                    }
                });
    };

    /*
    NOT WORKING
    static public void usernameVerification(String username){
        collectionReferencePlayer.document(username)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()){
                                Log.d("Verifying if username is new", "no");
                            } else {
                                Log.d("Verifying if username is new", "yes");
                            }
                        } else {
                            Log.d("Verifying if username is new", "failed to verify");
                        }
                    }
                });
    }
    */


    /**
     * Given a unique deviceID, return the username
     * @param deviceID unique deviceID
     * @return username, could be null
     */
    static protected void getUserName(String deviceID, FirestoreCallBack firestoreCallBack){
        collectionReferencePlayer.whereEqualTo("deviceID", deviceID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d("Getting username from deviceID", "succeeded");
                            firestoreCallBack.onCallBack(task.getResult().toString());
                        } else {
                            Log.d("Getting username from deviceID", "failed");
                        }
                    }
                });
    }

    /**
     * Javadoc
     */
    public interface FirestoreCallBack{
        void onCallBack(String username);
    }
}
