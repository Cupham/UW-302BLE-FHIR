package com.example.cu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneMinuteSummary {
	private String type;
	private int numberOfData;
	private List<ActivityObj> activities;
	private WeightObj weight;
	private BloodPressureObj bloodPressure;

	public OneMinuteSummary() {
		// TODO Auto-generated constructor stub
	}
	public OneMinuteSummary(byte[] bytes) {
		if(bytes[0] == 0x50) {
			if(bytes[1] == 0x27) {
				activitiesFromBytes(bytes);
			} else if(bytes[1] == 0x28) {
				bloodPressureFromBytes(bytes);
			} else {
				unknown();
			}
		} else if(bytes[0] == 0x30) {
			if(bytes[1] == 0x29) {
				weightsFromBytes(bytes);
			} else {
				unknown();
			}
		} else {
			unknown();
		}
	}
	private void unknown() {
		this.setType("UNKNOWN");
	}
	private void activitiesFromBytes(byte[] bytes) {
		this.setType("Activity");
		this.setNumberOfData(bytes[2]);
		List<ActivityObj> activities = new ArrayList<ActivityObj>();
		for(int i = 3; i < bytes.length; i =i+12) {
			ActivityObj activity = new ActivityObj(Arrays.copyOfRange(bytes, i,i+12));
			activities.add(activity);
			if(activities.size() == this.getNumberOfData()) {
				break;
			}
		}
		this.setActivities(activities);
	}
	private void bloodPressureFromBytes(byte[] bytes) {
		this.setType("BloodPressure");
		this.setNumberOfData(bytes[2]);
		this.setBloodPressure(new BloodPressureObj(bytes));
	}
	private void weightsFromBytes(byte[] bytes) {
		this.setType("Weight");
		this.setNumberOfData(bytes[2]);
		this.setWeight(new WeightObj(bytes));
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getNumberOfData() {
		return numberOfData;
	}
	public void setNumberOfData(int numberOfData) {
		this.numberOfData = numberOfData;
	}
	public List<ActivityObj> getActivities() {
		return activities;
	}
	public void setActivities(List<ActivityObj> activities) {
		this.activities = activities;
	}
	@Override
	public String toString() {
		String rs = "";
		if(this.getActivities() != null && this.getActivities().size() >0) {
			for(ActivityObj obj : this.getActivities()) {
				rs += obj.toString() +"\n";
			}
		}
		if(this.getBloodPressure() != null)
			rs+= this.getBloodPressure().toString()+ "\n";;
		if(this.getWeight()!= null)
			rs+=this.getWeight() + "\n";
		return rs;
	}
	public WeightObj getWeight() {
		return weight;
	}
	public void setWeight(WeightObj weight) {
		this.weight = weight;
	}
	public BloodPressureObj getBloodPressure() {
		return bloodPressure;
	}
	public void setBloodPressure(BloodPressureObj bloodPressure) {
		this.bloodPressure = bloodPressure;
	}


}



