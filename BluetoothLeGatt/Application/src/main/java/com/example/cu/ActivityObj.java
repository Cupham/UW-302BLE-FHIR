package com.example.cu;

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
		this.setTemperature(bytes[5]);
		this.setCalories(bytes[8]/10);
		this.setSleepStatus(sleepStatusFromBytes(bytes[9]));
		this.setSleptHours(bytes[10]);
		this.setSleptHours(bytes[11]);
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
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}
	public int getStep() {
		return step;
	}
	public void setStep(int step) {
		this.step = step;
	}
	public int getCalories() {
		return calories;
	}
	public void setCalories(int calories) {
		this.calories = calories;
	}
	public int getSleptHours() {
		return sleptHours;
	}
	public void setSleptHours(int sleptHours) {
		this.sleptHours = sleptHours;
	}
	public int getSleptMinutes() {
		return sleptMinutes;
	}
	public void setSleptMinutes(int sleptMinutes) {
		this.sleptMinutes = sleptMinutes;
	}
	public boolean isWearing() {
		return isWearing;
	}
	public void setWearing(boolean isWearing) {
		this.isWearing = isWearing;
	}
	public String getSleepStatus() {
		return sleepStatus;
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
	
}
