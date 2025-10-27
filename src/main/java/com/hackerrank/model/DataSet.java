package com.hackerrank.model;

import java.util.List;

public class DataSet {

	private String dataSourceId;
	private List<Point> point;
	public String getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public List<Point> getPoint() {
		return point;
	}
	public void setPoint(List<Point> point) {
		this.point = point;
	}
	public DataSet(String dataSourceId, List<Point> point) {
		super();
		this.dataSourceId = dataSourceId;
		this.point = point;
	}
	public DataSet() {
		super();
	}
	
}
