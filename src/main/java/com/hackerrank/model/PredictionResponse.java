package com.hackerrank.model;

public class PredictionResponse {
	private String bpm;
    private String hasDiabetes;
    private String hasHypertention;
    private String oxygenSaturation;
    private String bpMinValue;
    private String bpMaxValue;
    private String glucose;
    private String cholesterol;
    private String temperature;
    private String weight;
    private String height;
	public String getBpm() {
		return bpm;
	}
	public void setBpm(String bpm) {
		this.bpm = bpm;
	}
	public String getHasDiabetes() {
		return hasDiabetes;
	}
	public void setHasDiabetes(String hasDiabetes) {
		this.hasDiabetes = hasDiabetes;
	}
	public String getHasHypertention() {
		return hasHypertention;
	}
	public void setHasHypertention(String hasHypertention) {
		this.hasHypertention = hasHypertention;
	}
	public String getOxygenSaturation() {
		return oxygenSaturation;
	}
	public void setOxygenSaturation(String oxygenSaturation) {
		this.oxygenSaturation = oxygenSaturation;
	}
	public String getBpMinValue() {
		return bpMinValue;
	}
	public void setBpMinValue(String bpMinValue) {
		this.bpMinValue = bpMinValue;
	}
	public String getBpMaxValue() {
		return bpMaxValue;
	}
	public void setBpMaxValue(String bpMaxValue) {
		this.bpMaxValue = bpMaxValue;
	}
	public String getGlucose() {
		return glucose;
	}
	public void setGlucose(String glucose) {
		this.glucose = glucose;
	}
	public String getCholesterol() {
		return cholesterol;
	}
	public void setCholesterol(String cholesterol) {
		this.cholesterol = cholesterol;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public PredictionResponse() {
	
	}
}
