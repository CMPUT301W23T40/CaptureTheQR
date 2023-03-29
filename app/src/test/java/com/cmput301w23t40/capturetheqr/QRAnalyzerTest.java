package com.cmput301w23t40.capturetheqr;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class QRAnalyzerTest {

    @Test
    void testHash(){
        // the hash value for each input is pre-calculated in https://emn178.github.io/online-tools/sha256.html
        assertEquals(QRAnalyzer.generateHashValue("BFG5DGW54"),"696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
        assertEquals(QRAnalyzer.generateHashValue("D5F6A8Q0Q"),"ead1aa82a1c9d03b067b5709567979b853d6cdd65c8038c8b7321c328f47a715");
        assertEquals(QRAnalyzer.generateHashValue("A5Q9QGD7F"),"e17468b47ec14908de37d283bab2b85e7e8411dc5e4eef4bf38807738124b4e2");
        assertEquals(QRAnalyzer.generateHashValue("A6D1FH515"),"1e576b95393641f2f589ce2ae70aa27ffa9ffe1612e7481a5c36b9b17fba78d6");
        assertEquals(QRAnalyzer.generateHashValue("~!@#$%^&*"),"7f554cc34f12f85197599077210e1bcbaee3367c182bb5c6646067b5fc4bfc90");

        assertEquals(QRAnalyzer.generateHashValue(""),"01ba4719c80b6fe911b091a7c05124b64eeece964e09c058ef8f9805daca546b");
        assertEquals(QRAnalyzer.generateHashValue(" "),"e16f1596201850fd4a63680b27f603cb64e67176159be3d8ed78a4403fdb1700");
        assertEquals(QRAnalyzer.generateHashValue("0"),"9a271f2a916b0b6ee6cecb2426f0b3206ef074578be55d9bc94f6f3fe3ab86aa");

        assertDoesNotThrow(() -> { QRAnalyzer.generateHashValue(null); });
    }

    @Test
    void testScore(){
        String s = QRAnalyzer.generateHashValue("BFG5DGW54");
        assertEquals(QRAnalyzer.generateScore(s),115);
    }

    @Test
    void testScoreZeros(){
        assertEquals(QRAnalyzer.generateScore("0000"),8000);
    }

    @Test
    void testNoIsolatedZeros(){
        assertEquals(QRAnalyzer.countIsolatedZeros("0000"),0);
    }

    @Test
    void testIsolatedZeros(){
        assertEquals(QRAnalyzer.countIsolatedZeros("01203400560"),3);
    }

    @Test
    void testSingleZero(){
        assertEquals(QRAnalyzer.countIsolatedZeros("0"),1);
        assertEquals(QRAnalyzer.generateScore("0"),1);
    }

    @Test
    void testName(){
        String s = QRAnalyzer.generateHashValue("BFG5DGW54");
        assertEquals(QRAnalyzer.generateName(s),"cool FroLoUltraSpectralShark");
    }

}
