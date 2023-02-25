package com.cmput301w23t40.capturetheqr;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * The QRAnalyzer class takes in the contents of a QR code, then it generates a hash value for it.
 * Based on the hash value, it then generates a name, a visualization, and a score for this QR code.
 */
public class QRAnalyzer {
    /**
     * Analyze a QR code given its contents. If the hash value for this code does not exist in the DB,
     * then generate a name, a visualization, and a score for the code and save them in the DB; if
     * the QR code already exists, add the scanner to the code's scanners list.
     * // FIXME add code to the player's list of scanned codes
     * @param codeContents
     */
    public static void addQRCodeToScanner(String codeContents){
        String hashValue = generateHashValue(codeContents);
        /*
        //query if the QR code exists nearby
        if(){
            // if yes, add the scanner info to the code
        }else{
            // create a new QR code in the db, and add the scanner info
        }
        */
    }
    private static String generateHashValue(String codeContents){
        // FIXME citation
        // https://www.baeldung.com/sha-256-hashing-java
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 algo must exist, so this catch never happens
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(codeContents.getBytes(StandardCharsets.UTF_8));
        return hash.toString();
    }
    private static String generateName(String hashValue){
        return null;
    }

    private static ArrayList<String> generateVisualization(String hashValue){
        return null;
    }
    private static int generateScore(String hashValue){
        return 0;
    }
}
