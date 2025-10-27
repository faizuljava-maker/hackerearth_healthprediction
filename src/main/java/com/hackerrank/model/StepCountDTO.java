package com.hackerrank.model;

import java.time.LocalDate;
import java.util.List;

public class StepCountDTO {

	private LocalDate date;
	private List<DeviceData> deviceData;

	public List<DeviceData> getDeviceData() {
		return deviceData;
	}
	public void setDeviceData(List<DeviceData> deviceData) {
		this.deviceData = deviceData;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public StepCountDTO(LocalDate date, List<DeviceData> deviceData) {
		super();
		this.date = date;
		this.deviceData = deviceData;
	}

}
