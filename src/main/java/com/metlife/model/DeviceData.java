/**
 * 
 */
package com.metlife.model;

/**
 * 
 */
public class DeviceData {

	private String device;
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	private Integer stepsCount;
	private Double heartRate;
	private Integer sleepHours;
	
	public Integer getStepsCount() {
		return stepsCount;
	}
	public void setStepsCount(Integer stepsCount) {
		this.stepsCount = stepsCount;
	}
	public Double getHeartRate() {
		return heartRate;
	}
	public void setHeartRate(Double heartRate) {
		this.heartRate = heartRate;
	}
	public Integer getSleepHours() {
		return sleepHours;
	}
	public void setSleepHours(Integer sleepHours) {
		this.sleepHours = sleepHours;
	}
	public DeviceData() {
		super();
		// TODO Auto-generated constructor stub
	}
	public DeviceData(String device, Integer stepsCount, Double heartRate, Integer sleepHours) {
		super();
		this.device = device;
		this.stepsCount = stepsCount;
		this.heartRate = heartRate;
		this.sleepHours = sleepHours;
	}
	
}
