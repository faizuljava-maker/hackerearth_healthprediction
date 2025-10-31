package com.hackerrank.model;

import java.util.List;

public class HealthPrediction {

    private List<HealthData> healthDataList;
    
    private PredictionResponse predictionResponse;

	public PredictionResponse getPredictionResponse() {
		return predictionResponse;
	}

	public void setPredictionResponse(PredictionResponse predictionResponse) {
		this.predictionResponse = predictionResponse;
	}

	public List<HealthData> getHealthDataList() {
		return healthDataList;
	}

	public void setHealthDataList(List<HealthData> healthDataList) {
		this.healthDataList = healthDataList;
	}

	public HealthPrediction() {

	}

	
}


