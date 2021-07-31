package com.example.cu;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class BloodPressureObj {
    private Date measureTime;
    private int SYS;
    private int DIA;
    private int MAP;
    private int PUL;
    private String measurementStatus;

    public BloodPressureObj() {

    }
    public BloodPressureObj(byte[] bytes) {
        measurementDataFromBytes(Arrays.copyOfRange(bytes, 93,111));
    }

    public Date getMeasureTime() {
        return measureTime;
    }
    public void setMeasureTime(Date measureTime) {
        this.measureTime = measureTime;
    }
    public int getSYS() {
        return SYS;
    }
    public void setSYS(int sYS) {
        SYS = sYS;
    }
    public int getDIA() {
        return DIA;
    }
    public void setDIA(int dIA) {
        DIA = dIA;
    }
    public int getMAP() {
        return MAP;
    }
    public void setMAP(int mAP) {
        MAP = mAP;
    }
    public int getPUL() {
        return PUL;
    }
    public void setPUL(int pUL) {
        PUL = pUL;
    }
    public String isMeasurementStatus() {
        return measurementStatus;
    }
    public void setMeasurementStatus(String measurementStatus) {
        this.measurementStatus = measurementStatus;
    }
    private Date timeFromBytes(byte[] input) {
        int year = intFromBytesLE(new byte[] {input[7], input[8]});
        int month = input[9];
        int day = input[10];
        int hour = input[11];
        int min = input[12];
        int sec = input[13];

        String dateStr = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" +sec;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date  = null;
        try {
            date = formatter.parse(dateStr);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }
    private String statusFromBytes(byte[] bytes) {
        if(bytes[0]==0x07 && bytes[1]==0xFF) {
            return "Failed";
        } else {
            return "Success";
        }
    }
    private int intFromBytesLE(byte[] bytes) {
        return ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);
    }

    private void measurementDataFromBytes(byte[] bytes) {
        this.setMeasureTime(timeFromBytes(bytes));
        this.setSYS(intFromBytesLE(new byte[] {bytes[1], bytes[2]}));
        this.setDIA(intFromBytesLE(new byte[] {bytes[3], bytes[4]}));
        this.setMAP(intFromBytesLE(new byte[] {bytes[5], bytes[6]}));
        this.setPUL(intFromBytesLE(new byte[] {bytes[14], bytes[15]}));
        this.setMeasurementStatus(statusFromBytes(new byte[] {bytes[16], bytes[17]}));

    }
    @Override
    public String toString() {
        return "Time:" + measureTime + ", SYS=" + SYS + ", DIA=" + DIA + ", MAP=" + MAP
                + ", PUL=" + PUL + ", Status=" + measurementStatus;
    }

}

