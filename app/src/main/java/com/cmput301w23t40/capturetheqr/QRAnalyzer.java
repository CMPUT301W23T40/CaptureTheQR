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
    private static HashMap<Character, String> hexToBinMap;
    private static Map<Integer, ArrayList<String>> nameDict;
    /**
     * Generate a QRCode object from a real code's codeContent
     * @param codeContent codeContent of the real code
     * @return qrcode object
     */
    public static QRCode generateQRCodeObject(String codeContent){
        String hashValue = generateHashValue(codeContent);
        return new QRCode(hashValue,
                generateName(hashValue),
                generateVisualization(hashValue),
                generateScore(hashValue),
                null,
                0);
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

        //get the consecutive repeats in the input string
        Map<Character, Integer> map = getConsecutiveRepeats(inputString);

        // init the sum to the number of isolated 0's
        int sum = countIsolatedZeros(inputString);

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
     * This function will count isolated 0's in a string
     * @param inputString the input string to parse throught
     * @return the number of isolated 0's in the string
     */
    public static int countIsolatedZeros(String inputString) {
        int count = 0;

        for (int i = 0; i < inputString.length(); i++)
            if (inputString.charAt(i) == '0' && (i == 0 || inputString.charAt(i-1) != '0') && (i == inputString.length()-1 || inputString.charAt(i+1) != '0'))
                count++;

        return count;
    }

    /**
     * Initialize the maps for getting the first six bits of hash values, and getting the name components
     */
    private static void mapsInit(){
        if (hexToBinMap == null){
            hexToBinMap = new HashMap<>();
            hexToBinMap.put('0', "0000");
            hexToBinMap.put('1', "0001");
            hexToBinMap.put('2', "0010");
            hexToBinMap.put('3', "0011");
            hexToBinMap.put('4', "0100");
            hexToBinMap.put('5', "0101");
            hexToBinMap.put('6', "0110");
            hexToBinMap.put('7', "0111");
            hexToBinMap.put('8', "1000");
            hexToBinMap.put('9', "1001");
            hexToBinMap.put('A', "1010");
            hexToBinMap.put('B', "1011");
            hexToBinMap.put('C', "1100");
            hexToBinMap.put('D', "1101");
            hexToBinMap.put('E', "1110");
            hexToBinMap.put('F', "1111");
            hexToBinMap.put('a', "1010");
            hexToBinMap.put('b', "1011");
            hexToBinMap.put('c', "1100");
            hexToBinMap.put('d', "1101");
            hexToBinMap.put('e', "1110");
            hexToBinMap.put('f', "1111");
        }
        if (nameDict == null){
            nameDict = new HashMap<>();
            nameDict.put(0, new ArrayList<>(Arrays.asList("cool", "hot")));
            nameDict.put(1, new ArrayList<>(Arrays.asList("Fro", "Glo")));
            nameDict.put(2, new ArrayList<>(Arrays.asList("Mo", "Lo")));
            nameDict.put(3, new ArrayList<>(Arrays.asList("Mega", "Ultra")));
            nameDict.put(4, new ArrayList<>(Arrays.asList("Spectral", "Sonic")));
            nameDict.put(5, new ArrayList<>(Arrays.asList("Crab", "Shark")));
        }
    }

    /**
     * This function generates a name based on the first byte of the hashed value of a string
     * @param hashString - the string to generate a name from
     * @return a string representing the new name
     */
    public static String generateName(String hashString) {
        mapsInit();
        String firstTwoChar = hexToBinMap.get(hashString.charAt(0)) + hexToBinMap.get(hashString.charAt(1));
        String name = "";
        for (int i = 0; i < 6; i++) {
            name += nameDict.get(i).get(Integer.parseInt(String.valueOf(firstTwoChar.charAt(i))));
            if (i == 0) {
                name += " ";
            }
        }
        return name;
    }


    /**
     * This function generates a visualization based on the first byte of the hashed value of a string
     * @param hashString - the string to generate a visualization from
     * @return a string representing the visualization
     */
    public static String generateVisualization(String hashString) {
        mapsInit();
        // actually the variable bits contains the first eight bits
        String bits = hexToBinMap.get(hashString.charAt(0)) + hexToBinMap.get(hashString.charAt(1));
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
