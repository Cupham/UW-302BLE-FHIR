package com.example.cu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class WeightObj {
    private Date measureTime;
    private double weight;

    public WeightObj() {

    }
    public WeightObj(byte[] bytes) {
        measurementDataFromBytes(Arrays.copyOfRange(bytes, 112,122));
    }

    public Date getMeasureTime() {
        return measureTime;
    }
    public void setMeasureTime(Date measureTime) {
        this.measureTime = measureTime;
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
    private int intFromBytesLE(byte[] bytes) {
        return ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);
    }
    private double weightFromBytes(byte[] bytes) {
        double rs = intFromBytesLE(bytes)/200;
        return rs;
    }
    private void measurementDataFromBytes(byte[] bytes) {
        this.setMeasureTime(timeFromBytes(bytes));
        this.setWeight(weightFromBytes(new byte[] {bytes[1], bytes[2]}));
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    @Override
    public String toString() {
        return "Time:" + measureTime + ", weight=" + weight + " kg";
    }
}
