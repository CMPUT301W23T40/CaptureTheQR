package com.cmput301w23t40.capturetheqr;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The QRAnalyzer class takes in the contents of a QR code, then it generates a hash value for it.
 * Based on the hash value, it then generates a name, a visualization, and a score for this QR code.
 */
public class QRAnalyzer {
    /**
     * Generate a QRCode object from a real code's hashvalue
     * @param hashValue hashvalue of the real code
     * @return qrcode object
     */
    public static QRCode generateQRCodeObject(String hashValue){
        return new QRCode(hashValue,
                generateName(hashValue),
                generateVisualization(hashValue),
                generateScore(hashValue),
                new QRCode.Geolocation(-1, -1));
    }

    /**
     *  Adapted/Copied code from the following resource for the SHA-256 hash in java functionality:
     *  author: https://auth.geeksforgeeks.org/user/bilal-hungund
     *  url: https://www.geeksforgeeks.org/sha-256-hash-in-java/
     *  last updated: Apr 29, 2022
     *
     * @param input - the input string for hashing
     * @return a byte array to be passed to toHexString to get the string value of the hash
     * @throws NoSuchAlgorithmException
     */
    private static byte[] getSHA(String input) throws NoSuchAlgorithmException {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        // to calculate message digest of an input
        // and return array of byte
        return md.digest(input.getBytes(StandardCharsets.UTF_8));
    }


    /**
     *  Adapted/Copied code from the following resource for the SHA-256 hash in java functionality:
     *  author: https://auth.geeksforgeeks.org/user/bilal-hungund
     *  url: https://www.geeksforgeeks.org/sha-256-hash-in-java/
     *  last updated: Apr 29, 2022
     *
     * @param hash the byte array to be turned into a string
     * @return a string representing the SHA-256 hash of the input string
     */
    private static String toHexString(byte[] hash) {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64) {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    /**
     * This function combines the getSHA and toHexString functions into one for ease of use.
     * Use this function any time a hash of an input string needs to be generated
     *
     * NOTE: This function appends a newline character to the input string when hashing
     * 	this is done since without it, the hash did match the assignment spec
     *
     * @param input - the input string to hash
     * @return a string representing the SHA-256 hash of the input string
     */
    public static String generateHashValue(String input) {
        try {
            return toHexString(getSHA(input + "\n"));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This function generates the score of a QR code given a string
     * In most cases, the hashed value of the string should be passed into this function
     * @param inputString - the string to generate the score from
     * @return an integer representing the score of the string
     */
    public static int generateScore(String inputString) {

        Map<Character, Integer> map = getConsecutiveRepeats(inputString);

        // init the sum
        int sum = 0;

        // loop through the map
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            // check for entry being 0, scoring system changes
            if (entry.getKey() == '0')
                sum += (int) Math.pow(20, entry.getValue() - 1);

            else {
                // convert the char into a decimal number
                // System.out.println(Integer.parseInt(String.valueOf(entry.getKey()), 16));
                int num = Integer.parseInt(String.valueOf(entry.getKey()), 16);
                sum += Math.pow(num, entry.getValue() - 1);
            }
        }

        return sum;
    }


    /**
     * This function finds all of the consecutive repeated characters in a string
     * @param inputString - string to find repeated characters from
     * @return a map<char,int> representing each character and the amount of repeats
     */
    private static Map<Character, Integer> getConsecutiveRepeats(String inputString) {

        // init an empty map to load the occurrence in
        Map<Character, Integer> occurrences = new HashMap<Character, Integer>();

        // init the current char
        char currentChar = inputString.charAt(0);
        // count of times the char has appeared. init to 1 for first char in string
        int count = 1;

        // start at 1 instead of 0 since we init currentChar to the first element in the
        // string
        for (int i = 1; i < inputString.length(); i++) {
            char nextChar = inputString.charAt(i);

            // if the current char and next char are the same, increase the count
            if (currentChar == nextChar)
                count++;
                // not the same char update map and values
            else {
                // only add the value to the map if it has consecutive chars
                if (count > 1)
                    occurrences.put(currentChar, count);
                // always update the count and current char if non consecutive was found
                count = 1;
                currentChar = nextChar;
            }
        }

        // the whole string consisted of the same character
        if (count > 1)
            occurrences.put(currentChar, count);

        return occurrences;
    }

    /**
     * This function generates a name based on the first byte of the hashed value of a string
     * @param hashString - the string to generate a name from
     * @return a string representing the new name
     */
    public static String generateName(String hashString) {
        // create a dictionary for the names
        Map<Integer, ArrayList<String>> nameDict = new HashMap<Integer, ArrayList<String>>();

        // the key is the index of the bit in the array, the value is the possible
        // options for the name in that slot
        nameDict.put(0, new ArrayList<String>(Arrays.asList("cool", "hot")));
        nameDict.put(1, new ArrayList<String>(Arrays.asList("Fro", "Glo")));
        nameDict.put(2, new ArrayList<String>(Arrays.asList("Mo", "Lo")));
        nameDict.put(3, new ArrayList<String>(Arrays.asList("Mega", "Ultra")));
        nameDict.put(4, new ArrayList<String>(Arrays.asList("Spectral", "Sonic")));
        nameDict.put(5, new ArrayList<String>(Arrays.asList("Crab", "Shark")));

        // get the first byte of the hashed string
        byte arrHash = hashString.getBytes()[0];
        // byte arrHash = 1;

        // turn the first byte into bits
        String bits = Integer.toBinaryString(arrHash);

        // if the bits of the string is less than 8, prefix 0's
        while (bits.length() < 8)
            bits = "0" + bits;

        // build the name, add a space between the first word and the rest
        String name = "";

        for (int i = 0; i < 6; i++) {
            if (i==0)
                name += nameDict.get(i).get(Integer.parseInt(String.valueOf(bits.charAt(i)))) + " ";
            else
                name += nameDict.get(i).get(Integer.parseInt(String.valueOf(bits.charAt(i))));
        }

        return name;
    }


    /**
     * This function generates a visualization based on the first byte of the hashed value of a string
     * @param hashString - the string to generate a visualization from
     * @return a string representing the visualization
     */
    public static String generateVisualization(String hashString) {

        // get the first byte of the hashed string
        byte arrHash = hashString.getBytes()[0];
        // byte arrHash = 1;

        // turn the first byte into bits
        String bits = Integer.toBinaryString(arrHash);

        // if the bits of the string is less than 8, prefix 0's
        while (bits.length() < 8)
            bits = "0" + bits;

        String face = "";

        // this picture will be build from the top down

        // first and second row, controlled by bit 2: determine if round or square head
        if (bits.charAt(2) == '1') {
            // square
            face += ("  ______  \n");
            face += (" |      | \n");
        } else if (bits.charAt(2) == '0') {
            // round
            face += ("   ____   \n");
            face += ("  /    \\  \n");
        }

        // third and fourth row, controlled by bit 1 & 5: determine if eyebrows and/or
        // ears

        if (bits.charAt(1) == '0' && bits.charAt(5) == '0') {
            // no eyebrows, no ears
            face += (" |      | \n");
            face += (" |      | \n");
        } else if (bits.charAt(1) == '0' && bits.charAt(5) == '1') {
            // no eyebrows, yes ears
            face += ("\\|      |/\n");
            face += ("@|      |@\n");

        } else if (bits.charAt(1) == '1' && bits.charAt(5) == '0') {
            // yes eyebrows, no ears
            face += (" | _  _ | \n");
            face += (" |  ||  | \n");
        } else if (bits.charAt(1) == '1' && bits.charAt(5) == '1') {
            // yes eyebrows, yes ears
            face += ("\\| _  _ |/\n");
            face += ("@|  ||  |@\n");
        }

        // fifth row, controlled by bit 0 & 5: determine if open/closed eyes and/or ears
        if (bits.charAt(0) == '0' && bits.charAt(5) == '0')
            // closed eyes, no ears
            face += (" | _   _| \n");
        else if (bits.charAt(0) == '0' && bits.charAt(5) == '1')
            // closed eyes, yes ears
            face += ("/| _   _|\\\n");
        else if (bits.charAt(0) == '1' && bits.charAt(5) == '0')
            // open eyes, no ears
            face += (" | ,   ,| \n");
        else if (bits.charAt(0) == '1' && bits.charAt(5) == '1')
            // open eyes, yes ears
            face += ("/| ,   ,|\\\n");

        // sixth row, controlled by bit 3: determine if nose or no nose
        if (bits.charAt(3) == '0')
            // no nose
            face += (" |      | \n");
        else if (bits.charAt(3) == '1')
            // nose
            face += (" |   o  | \n");

        // seventh row, controlled by bit 4: determine if smile or frown
        if (bits.charAt(4) == '0')
            // frown
            face += (" | .--. | \n");
        else if (bits.charAt(4) == '1')
            // smile
            face += (" | `--` | \n");

        // eighth row, controlled by bit 2: determine if round or square head
        if (bits.charAt(2) == '1')
            // square
            face += (" |______| ");
        else if (bits.charAt(2) == '0')
            // round
            face += ("  \\____/  ");

        return face;
    }

}
