package com.cmput301w23t40.capturetheqr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class QRAnalyzerTest {

    @Test
    void testHash(){
        String s = QRAnalyzer.generateHashValue("BFG5DGW54");
        assertEquals(s,"696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
    }

    @Test
    void testScore(){
        String s = QRAnalyzer.generateHashValue("BFG5DGW54");
        assertEquals(QRAnalyzer.generateScore(s),111);
    }

    @Test
    void testScoreZeros(){
        assertEquals(QRAnalyzer.generateScore("0000"),8000);
    }

    @Test
    void testName(){
        String s = QRAnalyzer.generateHashValue("BFG5DGW54");
        assertEquals(QRAnalyzer.generateName(s),"cool FroLoUltraSpectralShark");
    }

}
