package com.example.cu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class UT_201Obj {


    private String unit;
    private Date measureTime;



    private float temperature;
    private String temperatureType;

    public UT_201Obj() {

    }
    public UT_201Obj(byte[] temperatureBytes, byte type)
    {
        measurementDataFromBytes(temperatureBytes, type);
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
    private float temperatureFromBytes(byte[] bytes) {
        float rs = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getFloat();
        return rs;
    }
    private String typeFromByte(byte type){
        String rs = "";
        switch (type){
            case 1:
                rs = "Armpit";
                break;
            case 2:
                rs = "Body";
                break;
            case 3:
                rs = "Ear";
                break;
            case 4:
                rs = "Finger";
                break;
            case 5:
                rs = "Gastro-intestinal Track";
                break;
            case 6:
                rs = "Mouth";
                break;
            case 7:
                rs = "Rectum";
                break;
            case 8:
                rs = "Toe";
                break;
            case 9:
                rs = "Tympanum";
                break;
            default:
                rs="Unknown";
                break;
        }
        return  rs;

    }
    private void measurementDataFromBytes(byte[] bytes, byte type)
    {
        String flag = String.format("%8s", Integer.toBinaryString(bytes[0] & 0xFF)).replace(' ', '0');

        if(flag.substring(0,1).equals("0")) {
            this.setUnit("Celsius");
            this.setTemperature(temperatureFromBytes(Arrays.copyOfRange(bytes,1,5)));
        } else {
            this.setUnit("Fahrenheit");
            this.setTemperature(temperatureFromBytes(Arrays.copyOfRange(bytes,1,5)));
        }

        if(flag.substring(1,2).equals("0")) {
            this.setMeasureTime(null);
        } else {
            this.setMeasureTime(timeFromBytes(Arrays.copyOfRange(bytes,5,12)));
        }
        if(flag.substring(2,3).equals("0")) {
            this.setTemperatureType("");
        } else {
            this.setTemperatureType(typeFromByte(bytes[5]));
        }
    }
    private void setUnitFromByte(byte by)
    {
        if(by>>7==0) unit="Kg";
        else unit="Pound";
    }
    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public String getTemperatureType() {
        return temperatureType;
    }

    public void setTemperatureType(String temperatureType) {
        this.temperatureType = temperatureType;
    }
    @Override
    public String toString() {
        return "Time:" + measureTime + ", temp=" +this.getTemperatureType()  +  " " +unit;
    }
}
