package com.hackerrank.model;

public class HealthData {

	private Integer bpm;
	private Boolean hasDiabetes;
	private Boolean hasHypertention;
	private Long oxygenSaturation;
	private Long bpMinValue;
	private Long bpMaxValue;
	private Long glucose;
	private Long cholesterol;
	private Long temperature;
	private Long weight;
	private Long height;
	public Integer getBpm() {
		return bpm;
	}
	public void setBpm(Integer bpm) {
		this.bpm = bpm;
	}
	public Boolean getHasDiabetes() {
		return hasDiabetes;
	}
	public void setHasDiabetes(Boolean hasDiabetes) {
		this.hasDiabetes = hasDiabetes;
	}
	public Boolean getHasHypertention() {
		return hasHypertention;
	}
	public void setHasHypertention(Boolean hasHypertention) {
		this.hasHypertention = hasHypertention;
	}
	public Long getOxygenSaturation() {
		return oxygenSaturation;
	}
	public void setOxygenSaturation(Long oxygenSaturation) {
		this.oxygenSaturation = oxygenSaturation;
	}
	public Long getBpMinValue() {
		return bpMinValue;
	}
	public void setBpMinValue(Long bpMinValue) {
		this.bpMinValue = bpMinValue;
	}
	public Long getBpMaxValue() {
		return bpMaxValue;
	}
	public void setBpMaxValue(Long bpMaxValue) {
		this.bpMaxValue = bpMaxValue;
	}
	public Long getGlucose() {
		return glucose;
	}
	public void setGlucose(Long glucose) {
		this.glucose = glucose;
	}
	public Long getCholesterol() {
		return cholesterol;
	}
	public void setCholesterol(Long cholesterol) {
		this.cholesterol = cholesterol;
	}
	public Long getTemperature() {
		return temperature;
	}
	public void setTemperature(Long temperature) {
		this.temperature = temperature;
	}
	public Long getWeight() {
		return weight;
	}
	public void setWeight(Long weight) {
		this.weight = weight;
	}
	public Long getHeight() {
		return height;
	}
	public void setHeight(Long height) {
		this.height = height;
	}
	public HealthData(Integer bpm, Boolean hasDiabetes, Boolean hasHypertention, Long oxygenSaturation, Long bpMinValue,
			Long bpMaxValue, Long glucose, Long cholesterol, Long temperature, Long weight, Long height) {
		super();
		this.bpm = bpm;
		this.hasDiabetes = hasDiabetes;
		this.hasHypertention = hasHypertention;
		this.oxygenSaturation = oxygenSaturation;
		this.bpMinValue = bpMinValue;
		this.bpMaxValue = bpMaxValue;
		this.glucose = glucose;
		this.cholesterol = cholesterol;
		this.temperature = temperature;
		this.weight = weight;
		this.height = height;
	}
	public HealthData() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
