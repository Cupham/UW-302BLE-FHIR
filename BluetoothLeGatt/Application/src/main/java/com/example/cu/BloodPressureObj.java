package com.example.cu;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    public void setSYS(int sYS) {
        SYS = sYS;
    }
    public int getDIA() {
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
    public void setDIA(int dIA) {
        DIA = dIA;
    }
    public int getMAP() {
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
    public void setMAP(int mAP) {
        MAP = mAP;
    }
    public int getPUL() {
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
    public Observation toObservation(Patient patient){
        Observation obs = new Observation();
        obs.setStatus(Observation.ObservationStatus.FINAL);
        obs.getCode().addCoding().setSystem("http://loinc.org")
                .setCode("85354-9")
                .setDisplay("Blood pressure panel with all children optional");
        obs.getCategoryFirstRep().addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs");
        obs.setSubject(new Reference(patient.getIdElement().getValue()));
        if(this.getMeasureTime() != null) {
            Date date = this.getMeasureTime();
            obs.getEffectiveDateTimeType()
                    .setYear(date.getYear())
                    .setMonth(date.getMonth())
                    .setDay(date.getDay())
                    .setHour(date.getHours())
                    .setMinute(date.getMinutes())
                    .setSecond(date.getSeconds());
        }
        obs.addComponent(this.getSYSObservation());
        obs.addComponent(this.getDIAObservation());
        obs.addComponent(this.getMAPObservation());
        obs.addComponent(this.getPULObservation());
        return obs;
    }

}

