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
        String rs = "N/A";
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
        byte flag = bytes[0];
        if(flag >> 7 %2 ==0) {
            this.setUnit("Celsius");
            ByteBuffer temperature = ByteBuffer.wrap(Arrays.copyOfRange(bytes,1,5)).order(ByteOrder.LITTLE_ENDIAN);
            this.setTemperature(floatFromSFLOAT32(temperature));
        } else {
            this.setUnit("Fahrenheit");
            ByteBuffer temperature = ByteBuffer.wrap(Arrays.copyOfRange(bytes,1,5)).order(ByteOrder.LITTLE_ENDIAN);
            this.setTemperature(floatFromSFLOAT32(temperature));
        }
        if(flag >>6 %2 ==0) {
            this.setTemperatureType(typeFromByte(bytes[5]));
            //No Time

        } else {
            //Have Time
            this.setMeasureTime(timeFromBytes(Arrays.copyOfRange(bytes,5,12)));
            this.setTemperatureType(typeFromByte(bytes[12]));
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
    public static float floatFromSFLOAT32(ByteBuffer data) {
        byte b0 = data.get();
        byte b1 = data.get();
        byte b2 = data.get();
        byte b3 = data.get();
        int mantissa = unsignedToSigned((b0 & 0xFF) + ((b1 & 0xFF) << 8)
                + ((b2 & 0xFF) << 16), 24);
        return (float) (mantissa * Math.pow(10, b3));
    }

    private static int unsignedToSigned(int unsigned, int size) {
        if ((unsigned & (1 << size - 1)) != 0)
            unsigned = -1
                    * ((1 << size - 1) - (unsigned & ((1 << size - 1) - 1)));
        return unsigned;
    }
    @Override
    public String toString() {
        return "Time:" + measureTime + ", temp=" +this.getTemperatureType()  +  " " +unit;
    }
}
