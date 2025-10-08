/**
 * 
 */
package com.metlife.service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fitness.Fitness;
import com.google.api.services.fitness.model.AggregateBucket;
import com.google.api.services.fitness.model.AggregateBy;
import com.google.api.services.fitness.model.AggregateRequest;
import com.google.api.services.fitness.model.AggregateResponse;
import com.google.api.services.fitness.model.BucketByTime;
import com.google.api.services.fitness.model.DataPoint;
import com.google.api.services.fitness.model.Dataset;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.metlife.model.DeviceData;
import com.metlife.model.StepCountDTO;

/**
 * 
 */
@Service
public class DeviceAccessService {

	private static final String CLIENT_ID = "1019297408529-dlpjingshj2bcbu08o5s9nl92hqjjee3.apps.googleusercontent.com";
	private static final String CLIENT_SECRET = "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";
	private static final String REDIRECT_URI = "http://localhost:8080/oauth2callback";

	public String getAuthUrl() {
		GoogleAuthorizationCodeRequestUrl url = new GoogleAuthorizationCodeRequestUrl(CLIENT_ID, REDIRECT_URI,
				List.of("https://www.googleapis.com/auth/fitness.activity.read","https://www.googleapis.com/auth/fitness.heart_rate.read"));
		return url.build();
	}

	public GoogleCredentials exchangeCode(String code) throws IOException {

		GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(new NetHttpTransport(),
				new JacksonFactory(), CLIENT_ID, CLIENT_SECRET, code, REDIRECT_URI).execute();

		AccessToken accessToken = new AccessToken(tokenResponse.getAccessToken(),
				new Date(System.currentTimeMillis() + tokenResponse.getExpiresInSeconds() * 1000));

System.out.println("token:"+tokenResponse.getAccessToken());
		return GoogleCredentials.create(accessToken);
	}

	public List<DataPoint> getStepData(GoogleCredentials credentials, long startMillis, long endMillis)
			throws IOException {
		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

		Fitness fitness = new Fitness.Builder(new NetHttpTransport(), new JacksonFactory(), requestInitializer)
				.setApplicationName("SpringFitApp").build();

		String dataSourceId = "derived:com.google.step_count.delta:com.google.android.gms:estimated_steps";
		String datasetId = startMillis * 1000000 + "-" + endMillis * 1000000;

		com.google.api.services.fitness.model.Dataset dataset = fitness.users().dataSources().datasets()
				.get("faizul211", dataSourceId, datasetId).execute();

		return dataset.getPoint();
	}
	// approach 3

	public List<StepCountDTO> getDailyStepCounts(GoogleCredentials credentials, long startMillis, long endMillis)
			throws IOException {
		HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

		Fitness fitness = new Fitness.Builder(new NetHttpTransport(), new JacksonFactory(), requestInitializer)
				.setApplicationName("SpringFitApp").build();

		AggregateRequest request = new AggregateRequest()
				.setAggregateBy(List.of(new AggregateBy().setDataTypeName("com.google.step_count.delta")
						.setDataSourceId("derived:com.google.step_count.delta:com.google.android.gms:estimated_steps"),
						new AggregateBy().setDataTypeName("com.google.heart_rate.bpm")
						.setDataSourceId("raw:com.google.heart_rate.bpm:com.fitbit.android:fitbit")))
				.setBucketByTime(new BucketByTime().setDurationMillis((long) 86400000)) // daily
				.setStartTimeMillis(startMillis).setEndTimeMillis(endMillis);
//		AggregateRequest request = new AggregateRequest()
//				.setAggregateBy(List.of(new AggregateBy().setDataTypeName("com.google.step_count.delta"),
//						new AggregateBy().setDataTypeName("com.google.heart_rate.bpm")))
//				.setBucketByTime(new BucketByTime().setDurationMillis(86400000L)) // daily
//				.setStartTimeMillis(startMillis).setEndTimeMillis(endMillis);

		AggregateResponse response = fitness.users().dataset().aggregate("me", request).execute();

		List<StepCountDTO> result = new ArrayList<>();
		System.out.println("rsult:" + response);
		for (AggregateBucket bucket : response.getBucket()) {
			LocalDate date = Instant.ofEpochMilli(bucket.getStartTimeMillis()).atZone(ZoneId.systemDefault())
					.toLocalDate();

//	        int steps = bucket.getDataset().stream()
//	            .flatMap(ds -> ds.getPoint().stream())
//	            .mapToInt(dp -> dp.getValue().get(0).getIntVal())
//	            .sum();
			Map<String, Integer> deviceMap = new HashMap<>();
			List<DeviceData> devicedata = new ArrayList<>();
			 for (Dataset dataset : bucket.getDataset()) {
			        String type = dataset.getDataSourceId();

			        for (DataPoint point : dataset.getPoint()) {
						String deviceId = point.getOriginDataSourceId();
						String device = deviceId.split(":").length >= 4 ? deviceId.split(":")[3] : "unknown";
						DeviceData deviceData = new DeviceData();
						deviceData.setDevice(device);
			            if (type.contains("step_count")) {
			                int steps = point.getValue().get(0).getIntVal();
			                System.out.println(date + " - Steps: " + steps);
			                deviceData.setStepsCount(steps);
			            } else if (type.contains("heart_rate")) {
			                Double bpm = point.getValue().get(0).getFpVal();
			                System.out.println(date + " - Heart Rate: " + bpm);
			                deviceData.setHeartRate(bpm);
			            }
			            devicedata.add(deviceData);
			        }
			    }
			 
//			bucket.getDataset().stream().flatMap(ds -> ds.getPoint().stream()).map(dpp -> {
//				String deviceId = dpp.getOriginDataSourceId();
//				int stepscount = dpp.getValue().get(0).getIntVal();
//				String device = deviceId.split(":").length >= 4 ? deviceId.split(":")[3] : "unknown";
//				deviceMap.put(device, stepscount);
//				return deviceMap;
//			}).collect(Collectors.toList());

			result.add(new StepCountDTO(date, devicedata));

		}

		return result;
	}

}
