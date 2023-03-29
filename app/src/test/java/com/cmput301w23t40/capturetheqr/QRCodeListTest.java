package com.cmput301w23t40.capturetheqr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class QRCodeListTest {
    static private ArrayList<QRCode> qrCodeArrayList;
    static private QRCodeList qrCodeList;

    @BeforeEach
    public void clear(){
        qrCodeArrayList = null;
        qrCodeList = null;
    }

    @Test
    public void getCode(){
        assertThrows(NullPointerException.class, () -> qrCodeList.getCode(0));

        qrCodeList = new QRCodeList(null, qrCodeArrayList);
        assertThrows(NullPointerException.class, () -> qrCodeList.getCode(0));

        qrCodeArrayList = new ArrayList<>();
        qrCodeList = new QRCodeList(null, qrCodeArrayList);
        assertThrows(IndexOutOfBoundsException.class, () -> qrCodeList.getCode(0));


        qrCodeArrayList.add(QRAnalyzer.generateQRCodeObject("1"));
        qrCodeArrayList.add(QRAnalyzer.generateQRCodeObject("2"));
        qrCodeArrayList.add(QRAnalyzer.generateQRCodeObject("3"));
        qrCodeList = new QRCodeList(null, qrCodeArrayList);

        assertEquals(qrCodeList.getCode(0), qrCodeArrayList.get(0));
        assertEquals(qrCodeList.getCode(1), qrCodeArrayList.get(1));
        assertEquals(qrCodeList.getCode(2), qrCodeArrayList.get(2));

        assertNotEquals(qrCodeList.getCode(0), qrCodeArrayList.get(1));
        assertNotEquals(qrCodeList.getCode(1), qrCodeArrayList.get(2));
        assertNotEquals(qrCodeList.getCode(2), qrCodeArrayList.get(0));

        assertNotEquals(qrCodeList.getCode(0), qrCodeArrayList.get(2));
        assertNotEquals(qrCodeList.getCode(1), qrCodeArrayList.get(0));
        assertNotEquals(qrCodeList.getCode(2), qrCodeArrayList.get(1));

        assertThrows(IndexOutOfBoundsException.class, () -> qrCodeList.getCode(5));
    }

    @Test
    public void getItemCount(){
        assertThrows(NullPointerException.class, () -> qrCodeList.getItemCount());

        qrCodeList = new QRCodeList(null, qrCodeArrayList);
        assertThrows(NullPointerException.class, () -> qrCodeList.getItemCount());

        qrCodeArrayList = new ArrayList<>();
        qrCodeList = new QRCodeList(null, qrCodeArrayList);

        assertEquals(qrCodeList.getItemCount(), 0);

        qrCodeArrayList.add(QRAnalyzer.generateQRCodeObject("1"));
        assertEquals(qrCodeList.getItemCount(), 1);
        qrCodeArrayList.add(QRAnalyzer.generateQRCodeObject("2"));
        assertEquals(qrCodeList.getItemCount(), 2);
        qrCodeArrayList.add(QRAnalyzer.generateQRCodeObject("3"));
        assertEquals(qrCodeList.getItemCount(), 3);
    }
}
