package com.example.cu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class UA_651Obj {
    private Date measureTime;
    private int SYS;
    private int DIA;
    private int MAP;
    private int PUL;
    private String measurementStatus;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String unit;

    public UA_651Obj() {

    }
    public UA_651Obj(byte[] bytes) {
        measurementDataFromBytes(bytes);
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
        int year = intFromBytesLE(new byte[] {input[0], input[1]});
        int month = input[2];
        int day = input[3];
        int hour = input[4];
        int min = input[5];
        int sec = input[6];

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
        byte flag = bytes[0];
        if(flag >> 7 % 2 ==0) {
            this.setUnit("mmHg");
            this.setSYS(intFromBytesLE(new byte[] {bytes[1], bytes[2]}));
            this.setDIA(intFromBytesLE(new byte[] {bytes[3], bytes[4]}));
            this.setMAP(intFromBytesLE(new byte[] {bytes[5], bytes[6]}));
        } else {
            this.setUnit("kPa");
            this.setSYS(intFromBytesLE(new byte[] {bytes[1], bytes[2]}));
            this.setDIA(intFromBytesLE(new byte[] {bytes[3], bytes[4]}));
            this.setMAP(intFromBytesLE(new byte[] {bytes[5], bytes[6]}));
        }
        if(flag >> 6 % 2 ==0) {
            if(flag >> 5 %2 ==0){

            } else {
                this.setPUL(intFromBytesLE(new byte[] {bytes[7], bytes[9]}));
            }
        } else {
            this.setMeasureTime(timeFromBytes(Arrays.copyOfRange(bytes,7,14)));
            if(flag >> 5 %2 ==0){

            } else {
                this.setPUL(intFromBytesLE(new byte[] {bytes[15], bytes[17]}));
            }
        }


    }
    @Override
    public String toString() {
        return "Time:" + measureTime + ", SYS=" + SYS + ", DIA=" + DIA + ", MAP=" + MAP
                + ", PUL=" + PUL ;
    }

}

