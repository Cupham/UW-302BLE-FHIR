package com.example.cu;

import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

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
        measurementDataFromBytes(Arrays.copyOfRange(bytes, 95,113));
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
        double rs = intFromBytesLE(bytes)/200.0;
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
    public Observation toObservation(Patient patient){
        Observation obs = new Observation();
        obs.setStatus(Observation.ObservationStatus.FINAL);
        obs.getCode().addCoding().setSystem("http://loinc.org")
                .setCode("29463-7")
                .setDisplay("Body Weight");
        obs.getCategoryFirstRep().addCoding()
                .setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
                .setCode("vital-signs")
                .setDisplay("Vital Signs");
        obs.setSubject(new Reference(patient.getIdElement().getValue()));
        if(this.getMeasureTime() != null) {
            obs.getValueDateTimeType().setValue(this.getMeasureTime());
        }
        obs.setValue(
          new Quantity()
                  .setValue(this.getWeight())
                  .setUnit("kg")
                  .setSystem("http://unitsofmeasure.org")
                  .setCode("kg")
        );
        return obs;
    }
}
