package com.hackerrank.model;

public class PatientCondition {

	private String name;
	private String status;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public PatientCondition(String name, String status) {
		super();
		this.name = name;
		this.status = status;
	}
	public PatientCondition() {
		super();
	}
	
	
}
