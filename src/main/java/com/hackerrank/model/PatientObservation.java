package com.hackerrank.model;

public class PatientObservation {

	private String value;
	private String type;
	private String unit;
	private String date;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public PatientObservation(String value, String type, String unit, String date) {
		super();
		this.value = value;
		this.type = type;
		this.unit = unit;
		this.date = date;
	}
	public PatientObservation() {
		super();
	}
	

	
					
					
}
