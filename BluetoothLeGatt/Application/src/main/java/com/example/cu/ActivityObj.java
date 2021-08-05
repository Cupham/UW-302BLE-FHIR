package com.example.cu;

import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.StringType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityObj {
	private Date measureTime;
	private double temperature;
	private int step;
	private int calories;
	private int sleptHours;
	private String sleepStatus;
	private int sleptMinutes;
	private boolean isWearing;
	
	public ActivityObj() {
		
	}
	public ActivityObj(byte[] bytes) {
		byte[] timeBytes = new byte[] {bytes[0],bytes[1],bytes[2],bytes[3]};
		this.setMeasureTime(timeFromBytes(timeBytes));
		this.setWearing(wearStatusFromBytes(bytes[3]));
		this.setTemperature(bytes[4]*0.2 +60);
		this.setStep(bytes[5]);
		this.setCalories(bytes[8]*10);
		this.setSleepStatus(sleepStatusFromBytes(bytes[9]));
		this.setSleptHours(bytes[10]);
		this.setSleptMinutes(bytes[11]);
	}
	public Date getMeasureTime() {
		return measureTime;
	}
	public void setMeasureTime(Date measureTime) {
		this.measureTime = measureTime;
	}
	public double getTemperature() {
		return temperature;
	}
	public Observation.ObservationComponentComponent getTemperatureObservation() {
		Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
		component.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("8310-5")
				.setDisplay("Body temperature");
		component.setValue(
				new Quantity()
						.setValue(this.getTemperature())
						.setUnit("Celsius")
						.setSystem("http://unitsofmeasure.org")
						.setCode("Cel"));
		return component;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public int getStep() {
		return step;
	}
	public Observation.ObservationComponentComponent getStepObservation() {
		Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
		component.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("41950-7")
				.setDisplay("Number of steps in 24 hour Measured");
		component.setValue(
				new Quantity()
						.setValue(this.getTemperature())
						.setUnit("steps"));
		return component;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public int getCalories() {
		return calories;
	}
	public Observation.ObservationComponentComponent getCaloriesObservation() {
		Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
		component.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("41979-6")
				.setDisplay("Calories burned in 24 hour Calculated");
		component.setValue(
				new Quantity()
						.setValue(this.getCalories())
						.setUnit("kcal")
						.setSystem("http://unitsofmeasure.org")
						.setCode("kilocalorie"));
		return component;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}
	public int getSleptHours() {
		return sleptHours;
	}
	public Observation.ObservationComponentComponent getSleptHoursObservation() {
		Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
		component.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("93832-4")
				.setDisplay("Sleep duration");
		component.setValue(
				new Quantity()
						.setValue(this.getSleptHours())
						.setUnit("Hour")
						.setSystem("http://unitsofmeasure.org")
						.setCode("h"));
		return component;
	}
	public void setSleptHours(int sleptHours) {
		this.sleptHours = sleptHours;
	}
	public int getSleptMinutes() {
		return sleptMinutes;
	}
	public Observation.ObservationComponentComponent getSleptMinutesObservation() {
		Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
		component.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("93830-8")
				.setDisplay("Light sleep duration");
		component.setValue(
				new Quantity()
						.setValue(this.getSleptMinutes())
						.setUnit("Minutes")
						.setSystem("http://unitsofmeasure.org")
						.setCode("min"));
		return component;
	}
	public void setSleptMinutes(int sleptMinutes) {
		this.sleptMinutes = sleptMinutes;
	}
	public boolean isWearing() {
		return isWearing;
	}
	public Observation.ObservationComponentComponent getWearingObservation() {
		Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
		component.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("unknown")
				.setDisplay("SmartWatch wearing status");
		component.setValue(
				new StringType()
						.setValue(isWearing ? "Wearing" : "Not_Wear"));
		return component;
	}
	public void setWearing(boolean isWearing) {
		this.isWearing = isWearing;
	}
	public String getSleepStatus() {
		return sleepStatus;
	}
	public Observation.ObservationComponentComponent getSleepStatusObservation() {
		Observation.ObservationComponentComponent component = new Observation.ObservationComponentComponent();
		component.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("unknown")
				.setDisplay("Sleep status");
		component.setValue(
				new StringType()
						.setValue(this.getSleepStatus()));
		return component;
	}
	public void setSleepStatus(String sleepStatus) {
		this.sleepStatus = sleepStatus;
	}
	private String byteToString(byte b) {
	    byte[] masks = { -128, 64, 32, 16, 8, 4, 2, 1 };
	    StringBuilder builder = new StringBuilder();
	    for (byte m : masks) {
	        if ((b & m) == m) {
	            builder.append('1');
	        } else {
	            builder.append('0');
	        }
	    }
	    return builder.toString();
	} 
	private Date timeFromBytes(byte[] input) {
		String binaryYear = byteToString(input[3]).substring(5, 8).
					concat(byteToString(input[0]).substring(0,3));
		
		String binaryMonth = byteToString(input[0]).substring(3, 7);
				
		String binaryDay = byteToString(input[0]).substring(7, 8).
				concat(byteToString(input[1]).substring(0,4));
		
		String binaryHour = byteToString(input[1]).substring(4, 8).
				concat(byteToString(input[2]).substring(0,1));
		String binaryMinute = byteToString(input[2]).substring(1, 7);
		
		String binarySec = byteToString(input[2]).substring(7, 8).
				concat(byteToString(input[3]).substring(0,5));
		
		int year = Integer.parseInt(binaryYear, 2) + 2005;
		int month = Integer.parseInt(binaryMonth, 2) + 1;
		int day = Integer.parseInt(binaryDay, 2);
		int hour = Integer.parseInt(binaryHour, 2);
		int min = Integer.parseInt(binaryMinute, 2);
		int sec = Integer.parseInt(binarySec, 2);
		
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
	
	private boolean wearStatusFromBytes(byte input) {
		return byteToString(input).substring(4,5) == "0" ? true:false;
	}
	private String sleepStatusFromBytes(byte input) {
		String rs = "";
		if(input == 0) {
			rs = "Sleeping";
		} else if(input == 1) {
			rs = "NotSleeping";
		} else if (input == 2){
			rs = "Calculating";
		} else {
			rs = "N/A";
		}
		return rs;
	}

	@Override
	public String toString() {
		String rs = "Time: "  + measureTime + ", temperature=" + temperature + ", step=" + step
				+ ", calories=" + calories + ", sleptHours=" + sleptHours + ", sleepStatus=" + sleepStatus
				+ ", sleptMinutes=" + sleptMinutes + ", isWearing=" + isWearing;
		return rs;
	}
	public Observation toObservation(Patient patient){
		Observation obs = new Observation();
		obs.setStatus(Observation.ObservationStatus.FINAL);
		obs.getCode().addCoding().setSystem("http://loinc.org")
				.setCode("82611-5")
				.setDisplay("Wearable device external physiologic monitoring panel");
		obs.getCategoryFirstRep().addCoding()
				.setSystem("http://terminology.hl7.org/CodeSystem/observation-category")
				.setCode("vital-signs")
				.setDisplay("Vital Signs");
		obs.setSubject(new Reference(patient.getIdElement().getValue()));
		if(this.getMeasureTime() != null) {
			obs.getValueDateTimeType().setValue(this.getMeasureTime());
		}
		obs.addComponent(getTemperatureObservation());
		obs.addComponent(getStepObservation());
		obs.addComponent(getCaloriesObservation());
		obs.addComponent(getSleepStatusObservation());
		obs.addComponent(getSleptHoursObservation());
		obs.addComponent(getSleptMinutesObservation());
		return obs;
	}
	
}
