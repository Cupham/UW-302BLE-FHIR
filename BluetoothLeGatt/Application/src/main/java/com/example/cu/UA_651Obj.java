package com.example.cu;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class UA_651Obj {
    private Date measureTime;
    private double SYS;
    private double DIA;
    private double MAP;
    private double PUL;
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
    public double getSYS() {
        return SYS;
    }
    public Observation.ObservationComponentComponent getSYSObservation() {
        Observation.ObservationComponentComponent sys = new Observation.ObservationComponentComponent();
        sys.getCode().addCoding().setSystem("http://loinc.org")
                .setCode("8480-6")
                .setDisplay("Systolic blood pressure");
        sys.setValue(
                new Quantity()
                        .setValue(this.getSYS())
                        .setUnit("mmHg")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("mm[Hg]"));
        return sys;
    }
    public void setSYS(double sYS) {
        SYS = sYS;
    }
    public double getDIA() {
        return DIA;
    }
    public Observation.ObservationComponentComponent getDIAObservation() {
        Observation.ObservationComponentComponent sys = new Observation.ObservationComponentComponent();
        sys.getCode().addCoding().setSystem("http://loinc.org")
                .setCode("8480-4")
                .setDisplay("Diastolic blood pressure");
        sys.setValue(
                new Quantity()
                        .setValue(this.getDIA())
                        .setUnit("mmHg")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("mm[Hg]"));
        return sys;
    }

    public void setDIA(double dIA) {
        DIA = dIA;
    }
    public double getMAP() {
        return MAP;
    }
    public Observation.ObservationComponentComponent getMAPObservation() {
        Observation.ObservationComponentComponent sys = new Observation.ObservationComponentComponent();
        sys.getCode().addCoding().setSystem("http://loinc.org")
                .setCode("8478-0")
                .setDisplay("Mean blood pressure");
        sys.setValue(
                new Quantity()
                        .setValue(this.getMAP())
                        .setUnit("mmHg")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("mm[Hg]"));
        return sys;
    }
    public void setMAP(double mAP) {
        MAP = mAP;
    }
    public double getPUL() {
        return PUL;
    }
    public Observation.ObservationComponentComponent getPULObservation() {
        Observation.ObservationComponentComponent sys = new Observation.ObservationComponentComponent();
        sys.getCode().addCoding().setSystem("http://loinc.org")
                .setCode("8867-4")
                .setDisplay("Heart rate");
        sys.setValue(
                new Quantity()
                        .setValue(this.getPUL())
                        .setUnit("beats/minute")
                        .setSystem("http://unitsofmeasure.org")
                        .setCode("/min"));
        return sys;
    }
    public void setPUL(double pUL) {
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
            this.setSYS(doubleFromSFLOATBytes(new byte[] {bytes[1], bytes[2]}));
            this.setDIA(doubleFromSFLOATBytes(new byte[] {bytes[3], bytes[4]}));
            this.setMAP(doubleFromSFLOATBytes(new byte[] {bytes[5], bytes[6]}));
        } else {
            this.setUnit("kPa");
            this.setSYS(doubleFromSFLOATBytes(new byte[] {bytes[1], bytes[2]}));
            this.setDIA(doubleFromSFLOATBytes(new byte[] {bytes[3], bytes[4]}));
            this.setMAP(doubleFromSFLOATBytes(new byte[] {bytes[5], bytes[6]}));
        }
        if(flag >> 6 % 2 ==1 && bytes.length >=18) {
            // With timeStamp
            this.setMeasureTime(timeFromBytes(Arrays.copyOfRange(bytes,7,14)));
            this.setPUL(intFromBytesLE(new byte[] {bytes[14], bytes[15]}));
            this.setMeasurementStatus(measurementStatusFromBytes(new byte[] {bytes[16], bytes[17]}));

        } else {
            // No timeStamp
            this.setPUL(intFromBytesLE(new byte[] {bytes[7], bytes[8]}));
            this.setMeasurementStatus(measurementStatusFromBytes(new byte[] {bytes[9], bytes[10]}));
        }


    }
    private String measurementStatusFromBytes(byte[] status){

        return "No body movement";
    }
    @Override
    public String toString() {
        return "Time:" + measureTime + ", SYS=" + SYS + ", DIA=" + DIA + ", MAP=" + MAP
                + ", PUL=" + PUL ;
    }



    public static float floatFromSFLOATByte(ByteBuffer data) {
        byte b0 = data.get();
        byte b1 = data.get();
        int mantissa = unsignedToSigned((b0 & 0xFF) + ((b1 & 0x0F) << 8),
                12);
        int exponent = unsignedToSigned((b1 & 0xFF) >> 4, 4);
        return (float) (mantissa * Math.pow(10, exponent));
    }

    private static int unsignedToSigned(int unsigned, int size) {
        if ((unsigned & (1 << size - 1)) != 0)
            unsigned = -1
                    * ((1 << size - 1) - (unsigned & ((1 << size - 1) - 1)));
        return unsigned;
    }
    private double doubleFromSFLOATBytes(byte[] two_bytes)
    {
        short value = ByteBuffer.wrap(two_bytes).order(ByteOrder.LITTLE_ENDIAN).getShort();
        // NaN
        if (value == 0x07FF)
        {
            return Double.NaN;
        }
        // NRes (not at this resolution)
        else if (value == 0x0800)
        {
            return Double.NaN;
        }
        // +INF
        else if (value == 0x07FE)
        {
            return Double.POSITIVE_INFINITY;
        }
        // -INF
        else if (value == 0x0802)
        {
            return Double.NEGATIVE_INFINITY;
        }
        // Reserved
        else if (value == 0x0801)
        {
            return Double.NaN;
        }
        else
        {
            return ((double) getMantissa(value)) * Math.pow(10, getExponent(value));
        }
    }
    private short getExponent(short value)
    {
        if (value < 0)
        { // if exponent should be negative
            return (byte) (((value >> 12) & 0x0F) | 0xF0);
        }
        return (short) ((value >> 12) & 0x0F);
    }

    private short getMantissa(short value)
    {
        if ((value & 0x0800) != 0)
        { // if mantissa should be negative
            return (short) ((value & 0x0FFF) | 0xF000);
        }
        return (short) (value & 0x0FFF);
    }
    public Observation toObservation(Patient patient){
        Observation obs = new Observation();
        obs.setStatus(Observation.ObservationStatus.FINAL);
        obs.getCode().addCoding().setSystem("http://loinc.org")
                .setCode("85354-9")
                .setDisplay("Blood pressure panel with all children optional");
        obs.getCode().setText("pchaBloodPressure");
        obs.getCategoryFirstRep().addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs");
        obs.setSubject(new Reference(patient.getIdElement().getValue()));
        if(this.getMeasureTime() != null) {
            obs.getValueDateTimeType().setValue(this.getMeasureTime());
        }
        obs.addComponent(this.getSYSObservation());
        obs.addComponent(this.getDIAObservation());
        obs.addComponent(this.getMAPObservation());
        obs.addComponent(this.getPULObservation());
        return obs;
    }


}

