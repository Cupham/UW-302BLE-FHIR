package com.example.cu;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class UC_352Obj {


    private String unit;
    private Date measureTime;
    private double weight;

    public UC_352Obj() {

    }
    public UC_352Obj(byte[] bytes)
    {
        measurementDataFromBytes(bytes);
    }
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    public Date getMeasureTime() {
        return measureTime;
    }
    public void setMeasureTime(Date measureTime)
    {
        this.measureTime = measureTime;
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
    private int intFromBytesLE(byte[] bytes) {
        return ((bytes[1] & 0xff) << 8) + (bytes[0] & 0xff);
    }
    private double weightFromBytes(byte[] bytes) {
        double rs = intFromBytesLE(bytes)/200.0;
        return rs;
    }
    private void measurementDataFromBytes(byte[] bytes)
    {
        setUnitFromByte(bytes[0]);
        this.setMeasureTime( timeFromBytes( Arrays.copyOfRange(bytes, 3,10)));
        this.setWeight(weightFromBytes(new byte[] {bytes[1], bytes[2]}));
    }
    private void setUnitFromByte(byte by)
    {
        if(by>>7==0) unit="Kg";
        else unit="Pound";
    }
    public double getWeight() {
        return weight;
    }
    public void setWeight(double weight) {
        this.weight = weight;
    }
    @Override
    public String toString() {
        return "Time:" + measureTime + ", weight=" + weight + unit;
    }
}
