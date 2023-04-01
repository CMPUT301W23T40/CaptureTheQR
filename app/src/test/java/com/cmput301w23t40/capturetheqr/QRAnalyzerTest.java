package com.cmput301w23t40.capturetheqr;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class QRAnalyzerTest {

    @Test
    void testHash(){
        // the hash value for each input is pre-calculated by https://emn178.github.io/online-tools/sha256.html
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
    void testName(){
        String s = QRAnalyzer.generateHashValue("BFG5DGW54");
        // pre-calculated by hand
        assertEquals(QRAnalyzer.generateName(s),"cool GloLoMegaSonicCrab");

        // the first six bits of the following hash values are all "000000"
        assertEquals(QRAnalyzer.generateName("00"),"cool FroMoMegaSpectralCrab");
        assertEquals(QRAnalyzer.generateName("01"),"cool FroMoMegaSpectralCrab");
        assertEquals(QRAnalyzer.generateName("02"),"cool FroMoMegaSpectralCrab");
        assertEquals(QRAnalyzer.generateName("03"),"cool FroMoMegaSpectralCrab");

        // the first six bits of the following hash values are all "101011"
        assertEquals(QRAnalyzer.generateName("ac"),"hot FroLoMegaSonicShark");
        assertEquals(QRAnalyzer.generateName("ad"),"hot FroLoMegaSonicShark");
        assertEquals(QRAnalyzer.generateName("AE"),"hot FroLoMegaSonicShark");
        assertEquals(QRAnalyzer.generateName("AF"),"hot FroLoMegaSonicShark");
    }

    @Test
    void testScore(){
        // the score for each hash value is given by the project specifications
        String s = QRAnalyzer.generateHashValue("BFG5DGW54");
        assertEquals(QRAnalyzer.generateScore(s),115);

        assertEquals(QRAnalyzer.generateScore("00"),20);
        assertEquals(QRAnalyzer.generateScore("000"),400);
        assertEquals(QRAnalyzer.generateScore("0000"),8000);
        assertEquals(QRAnalyzer.generateScore("11"),1);
        assertEquals(QRAnalyzer.generateScore("111"),1);
        assertEquals(QRAnalyzer.generateScore("1111"),1);
        assertEquals(QRAnalyzer.generateScore("22"),2);
        assertEquals(QRAnalyzer.generateScore("222"),4);
        assertEquals(QRAnalyzer.generateScore("2222"),8);
        assertEquals(QRAnalyzer.generateScore("99"),9);
        assertEquals(QRAnalyzer.generateScore("999"),81);
        assertEquals(QRAnalyzer.generateScore("aa"),10);
        assertEquals(QRAnalyzer.generateScore("aaa"),100);
        assertEquals(QRAnalyzer.generateScore("ff"),15);
        assertEquals(QRAnalyzer.generateScore("fff"),225);

        assertEquals(QRAnalyzer.generateScore("b00b"),20);
        assertEquals(QRAnalyzer.generateScore("b000b"),400);
        assertEquals(QRAnalyzer.generateScore("b0000b"),8000);
        assertEquals(QRAnalyzer.generateScore("b11b"),1);
        assertEquals(QRAnalyzer.generateScore("b111b"),1);
        assertEquals(QRAnalyzer.generateScore("b1111b"),1);
        assertEquals(QRAnalyzer.generateScore("b22b"),2);
        assertEquals(QRAnalyzer.generateScore("b222b"),4);
        assertEquals(QRAnalyzer.generateScore("b2222b"),8);
        assertEquals(QRAnalyzer.generateScore("b99b"),9);
        assertEquals(QRAnalyzer.generateScore("b999b"),81);
        assertEquals(QRAnalyzer.generateScore("baab"),10);
        assertEquals(QRAnalyzer.generateScore("baaab"),100);
        assertEquals(QRAnalyzer.generateScore("bffb"),15);
        assertEquals(QRAnalyzer.generateScore("bfffb"),225);

        assertEquals(QRAnalyzer.generateScore("00112299aaff"),20+1+2+9+10+15);
        assertEquals(QRAnalyzer.generateScore("000111222999aaafff"),400+1+4+81+100+225);
        assertEquals(QRAnalyzer.generateScore("000011112222"),8000+1+8);
    }

    @Test
    void testVisualization(){
        assertEquals(QRAnalyzer.generateVisualization("00"),
                "   ____   \n" +
                "  /    \\  \n" +
                " |      | \n" +
                " |      | \n" +
                " | _   _| \n" +
                " |      | \n" +
                " | .--. | \n" +
                "  \\____/  ");
    }

    @Test
    void testGenerateQRCode(){
        QRCode qrCode = QRAnalyzer.generateQRCodeObject("696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
        assertEquals(qrCode.getHashValue(),"696ce4dbd7bb57cbfe58b64f530f428b74999cb37e2ee60980490cd9552de3a6");
        assertEquals(qrCode.getCodeName(), "cool GloLoMegaSonicCrab");
        assertEquals(qrCode.getScore(), 115);
        assertNull(qrCode.getComments());
        assertNull(qrCode.getScannersInfo());
        assertNull(qrCode.getGeolocation());
        assertEquals(qrCode.getTimesScanned(), 0);

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



}
