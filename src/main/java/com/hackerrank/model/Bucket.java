package com.hackerrank.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class Bucket {

	private Long startTimeMillis;
	private LocalDate date;
	private long endTimeMillis;
	private List<DataSet> dataset;
	
	public Long getStartTimeMillis() {
		return startTimeMillis;
	}
	public void setStartTimeMillis(Long startTimeMillis) {
		this.setDate(Instant.ofEpochMilli(startTimeMillis).atZone(ZoneId.systemDefault())
				.toLocalDate());

		this.startTimeMillis = startTimeMillis;
	}
	public long getEndTimeMillis() {
		return endTimeMillis;
	}
	public void setEndTimeMillis(long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}
	public List<DataSet> getDataset() {
		return dataset;
	}
	public void setDataset(List<DataSet> dataset) {
		this.dataset = dataset;
	}
	public Bucket(Long startTimeMillis, long endTimeMillis, List<DataSet> dataset) {
		super();
		this.startTimeMillis = startTimeMillis;
		this.endTimeMillis = endTimeMillis;
		this.dataset = dataset;
	}
	public Bucket() {
		super();
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	
}
