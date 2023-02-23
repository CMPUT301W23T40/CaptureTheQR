package com.cmput301w23t40.capturetheqr;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class QRAnalyzer {
    public static void analyzeQRCode(String codeContents){
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
    public static String generateName(String hashValue){
        return null;
    }

    public static ArrayList<String> generateVisualization(String hashValue){
        return null;
    }
    public static int generateScore(String hashValue){
        return 0;
    }
}
