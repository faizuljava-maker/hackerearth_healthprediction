package com.hackerrank.model;

import java.util.List;

public class Point {

	private Long startTimeNanos;
	private Long endTimeNanos;
	private String dataTypeName;
	private String originDataSourceId;
	private List<Value> value;
	public Long getStartTimeNanos() {
		return startTimeNanos;
	}
	public void setStartTimeNanos(Long startTimeNanos) {
		this.startTimeNanos = startTimeNanos;
	}
	public Long getEndTimeNanos() {
		return endTimeNanos;
	}
	public void setEndTimeNanos(Long endTimeNanos) {
		this.endTimeNanos = endTimeNanos;
	}
	public String getDataTypeName() {
		return dataTypeName;
	}
	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}
	public String getOriginDataSourceId() {
		return originDataSourceId;
	}
	public void setOriginDataSourceId(String originDataSourceId) {
		this.originDataSourceId = originDataSourceId;
	}
	public List<Value> getValue() {
		return value;
	}
	public void setValue(List<Value> value) {
		this.value = value;
	}
	public Point(Long startTimeNanos, Long endTimeNanos, String dataTypeName, String originDataSourceId,
			List<Value> value) {
		super();
		this.startTimeNanos = startTimeNanos;
		this.endTimeNanos = endTimeNanos;
		this.dataTypeName = dataTypeName;
		this.originDataSourceId = originDataSourceId;
		this.value = value;
	}
	public Point() {
		super();
	}
	
	
	
}
