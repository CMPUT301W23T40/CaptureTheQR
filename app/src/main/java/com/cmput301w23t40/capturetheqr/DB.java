package com.cmput301w23t40.capturetheqr;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

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
     * Method for deleting QR codes from DB
     * */

    static public void delQRCodeInDB(String hashValue){
        database.collection("qrcode").document(hashValue)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
// These are a method which gets executed when the task is succeeded

                        Log.d(TAG, "Data has been deleted successfully!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Data could not be deleted!" + e.toString());
                    }
                });
    }
    /**
     * Query if the username is new in the database
     * @param username the username to be searched for
     * @return true if the username is new in the DB; false if it exists
     */
    static public boolean UsernameIsNew(String username){
        // FIXME DB queries
        return false;
    }

    /**
     * Create a new user record in the database
     * @param username username, which is unique for each user
     * @param contactInfo phone number for now
     * @param deviceID Android_ID
     */
    static protected void saveUser(String username, String contactInfo, String deviceID){
        // FIXME DB queries
    }

    /**
     * Query if the device ID is new in the database
     * @param deviceID deviceID to be searched for in the database
     * @return true if the device is new in the database; false if it exists
     */
    static public boolean deviceIDIsNew(String deviceID){
        // TODO DB queries
        return false;
    }

    /**
     * This method only saves the hashValue, codeName, visualization and score.
     * Comments and scanner's info will be added by other methods.
     * @param qrCode QRCode object to save to DB
     */
    static protected void saveQRCodeInDB(QRCode qrCode){
        // FIXME DB
    }

    static protected void saveCommentInDB(QRCode qrCode, QRCode.Comment comment){
        // FIXME need to save the comment to DB
    }

    static protected void saveScannerInfoInDB(QRCode qrCode, QRCode.ScannerInfo scannerInfo){
        // FIXME need to save the comment to DB
    }

}
