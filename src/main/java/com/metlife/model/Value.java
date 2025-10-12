package com.metlife.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Value {

	private Integer intVal;
	private Double fpVal;
	public Integer getIntVal() {
		return intVal;
	}
	public void setIntVal(Integer intVal) {
		this.intVal = intVal;
	}
	public Double getFpVal() {
		return fpVal;
	}
	public void setFpVal(Double fpVal) {
		this.fpVal = fpVal;
	}
	public Value(Integer intVal, Double fpVal) {
		super();
		this.intVal = intVal;
		this.fpVal = fpVal;
	}
	public Value() {
		super();
	}
	
	
}
