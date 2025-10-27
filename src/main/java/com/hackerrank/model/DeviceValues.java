package com.hackerrank.model;

import java.util.List;

public class DeviceValues {

	private List<Bucket> bucket;

	public List<Bucket> getBucket() {
		return bucket;
	}

	public void setBucket(List<Bucket> bucket) {
		this.bucket = bucket;
	}

	public DeviceValues(List<Bucket> bucket) {
		super();
		this.bucket = bucket;
	}

	public DeviceValues() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
