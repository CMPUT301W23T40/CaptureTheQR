package com.cmput301w23t40.capturetheqr;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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
     * Create a new user record in the database, could only be called when the user is new
     * @param player player object
     * @param callback actions to perform after the query is executed
     */
    static protected void savePlayerInDB(Player player, Callback callback){
        Map<String, Object> newPlayer = new HashMap<>();
        newPlayer.put("deviceID", player.getDeviceID());
        newPlayer.put("phoneNumber", player.getPhoneNumber());
        collectionReferencePlayer.document(player.getUsername())
                .set(newPlayer)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("Adding new player", "completed");
                        callback.onCallBack();
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
            DB.savePlayerInDB(players.get(i), new Callback() {
                @Override
                public void onCallBack() {
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
     * Verify is the username is new
     * @param username username to be verified
     * @param callbackVerifyIfUsernameIsNew actions to perform after the query is executed
     */
    static public void verifyIfUsernameIsNew(String username, CallbackVerifyIfUsernameIsNew callbackVerifyIfUsernameIsNew){
        collectionReferencePlayer.document(username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        Log.d("Verifying if username is new", "completed");
                        callbackVerifyIfUsernameIsNew.onCallBack(!(task.getResult().exists()));
                    }
                });
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
     * This method will get all the QRCodes scanned by the user.
     * @param username username
     * @param callback actions to be performed after the query is executed
     * */
    static protected void getQRCodeInDBPlayer( String username,Callback callback){

        collectionReferenceQR
                .whereEqualTo("username",username)
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
     * This function shows all the QR codes scanned on the QR Library page.
     * @param callback actions to be performed after the query is executed*/
    static protected void getQRCodeAllCodes( Callback callback){
        collectionReferenceQR
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
        //name
        //return entire QR code object

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
    public interface CallbackVerifyIfUsernameIsNew {
        void onCallBack(Boolean scannerIsNew);
    }
    public interface CallbackVerifyIfDeviceIDIsNew {
        void onCallBack(Boolean deviceIDIsNew);
    }
}
