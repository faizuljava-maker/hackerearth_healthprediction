package com.hackerrank.model;

import java.util.List;

public class PatientBasicDetails {

	private String firstName;
	private String lastName;
	private String name;
	private String gender;
	private String dateOfBirth;
	private String mobileNo;
	private List<HealthData> healthDataList;
	private List<PatientObservation> observationList;
	private List<PatientCondition> conditionList;

	public PatientBasicDetails(String name, String gender, String dateOfBirth, String mobileNo,
			List<PatientObservation> observationList, List<PatientCondition> conditionList) {
		super();
		this.name = name;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.mobileNo = mobileNo;
		this.observationList = observationList;
		this.conditionList = conditionList;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public PatientBasicDetails() {
		super();
	}
	public List<PatientObservation> getObservationList() {
		return observationList;
	}
	public void setObservationList(List<PatientObservation> observationList) {
		this.observationList = observationList;
	}
	public List<PatientCondition> getConditionList() {
		return conditionList;
	}
	public void setConditionList(List<PatientCondition> conditionList) {
		this.conditionList = conditionList;
	}
	public String getMobileNo() {
		return mobileNo;
	}
	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}
	public List<HealthData> getHealthDataList() {
		return healthDataList;
	}
	public void setHealthDataList(List<HealthData> healthDataList) {
		this.healthDataList = healthDataList;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
}
