package com.cmput301w23t40.capturetheqr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class QRAnalyzerTest {

    @Test
    void testHash(){
        QRAnalyzer qra = new QRAnalyzer();
        String s = qra.generateHash("BFG5DGW54");
        assertEquals(s,"696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
    }

    @Test
    void testScore(){
        QRAnalyzer qra = new QRAnalyzer();
        String s = qra.generateHash("BFG5DGW54");
        assertEquals(qra.generateScore(s),111);
    }

    @Test
    void testScoreZeros(){
        QRAnalyzer qra = new QRAnalyzer();
        String s = qra.generateHash("0000");
        assertEquals(qra.generateScore(s),8000);
    }

    @Test
    void testName(){
        QRAnalyzer qra = new QRAnalyzer();
        String s = qra.generateHash("BFG5DGW54");
        assertEquals(qra.generateName(s),"coolFroLoUltraSpectralShark");
    }

}
