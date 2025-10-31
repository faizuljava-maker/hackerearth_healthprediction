/**
 * 
 */
package com.hackerrank.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.fitness.FitnessScopes;
import com.google.api.services.fitness.model.AggregateBy;
import com.google.api.services.fitness.model.DataPoint;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.hackerrank.model.DeviceValues;
import com.hackerrank.model.StepCountDTO;
import com.hackerrank.service.DeviceAccessService;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 */
@RestController
public class DeviceAccessController {

	@Autowired
	DeviceAccessService service;

	@GetMapping("/getDeviceData")
	public void authorize(HttpServletResponse response) throws IOException {
		String redirectUri = "http://localhost:8080/oauth2callback";
		String clientId = "1019297408529-dlpjingshj2bcbu08o5s9nl92hqjjee3.apps.googleusercontent.com";
		String scope = "https://www.googleapis.com/auth/fitness.activity.read https://www.googleapis.com/auth/fitness.heart_rate.read "+FitnessScopes.FITNESS_SLEEP_READ;
		
		String authUrl = "https://accounts.google.com/o/oauth2/v2/auth?" + "client_id=" + clientId + "&redirect_uri="
				+ redirectUri + "&response_type=code" + "&scope=" + URLEncoder.encode(scope, "UTF-8")
				+ "&access_type=offline";

		response.sendRedirect(authUrl);
	}

	//@GetMapping("/oauth2callback")
	public ResponseEntity<DeviceValues> oauthCallback(@RequestParam("code") String code) throws IOException {
		System.out.println("inside oauth2callback");
		String tokenUrl = "https://oauth2.googleapis.com/token";
		String clientId = "1019297408529-dlpjingshj2bcbu08o5s9nl92hqjjee3.apps.googleusercontent.com";
		String clientSecret = "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";
		String redirectUri = "http://localhost:8080/oauth2callback";

		HttpTransport transport = new NetHttpTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(transport, jsonFactory, tokenUrl,
				clientId, clientSecret, code, redirectUri).execute();

		String accessToken = tokenResponse.getAccessToken();
		String refreshToken = tokenResponse.getRefreshToken();
		System.out.println(accessToken + ",,," + refreshToken);
		DeviceValues device = fetchFitnessData(accessToken);
		return ResponseEntity.ok(device);
	}

	public DeviceValues fetchFitnessData(String accessToken) throws IOException {
		System.out.println("inside fetch fitness");
		HttpTransport transport = new NetHttpTransport();

		AccessToken token = new AccessToken(accessToken, null);
		GoogleCredentials credential = GoogleCredentials.create(token);
		System.out.println("Get Token : " + credential.getAccessToken());
		HttpRequestFactory requestFactory = transport.createRequestFactory(request -> credential.refreshIfExpired());
		// Step Count Data Source
		String stepsUrl = "https://www.googleapis.com/fitness/v1/users/me/dataset:aggregate";

		
		long endTimeMillis = System.currentTimeMillis();
		// long startTimeMillis = getStartOfDayMillis(); //only for Today
		long startTimeMillis = endTimeMillis - (7 * 24 * 60 * 60 * 1000); // For one week data( 7 days ago )

		String requestBody = "{\n" + "  \"aggregateBy\": [{\"dataTypeName\": \"com.google.step_count.delta\"},{\"dataTypeName\": \"com.google.heart_minutes\"}],\n"
				+ "  \"bucketByTime\": {\"durationMillis\": 86400000},\n" + "  \"startTimeMillis\": " + startTimeMillis
				+ ",\n" + "  \"endTimeMillis\": " + System.currentTimeMillis() + "\n" + "}";

		HttpRequest request = requestFactory.buildPostRequest(new GenericUrl(stepsUrl),
				ByteArrayContent.fromString("application/json", requestBody));
		request.getHeaders().setAuthorization("Bearer " + accessToken);
		HttpResponse response = request.execute();

		 ObjectMapper mapper = new ObjectMapper();
		 DeviceValues device = mapper.readValue(response.parseAsString(), DeviceValues.class);
		 return device;
	}

	private long getStartOfDayMillis() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		System.out.println("time:" + calendar.getTime());
		return calendar.getTimeInMillis();
	}

	// Approach 2
	@GetMapping("/auth")
	public ResponseEntity<String> auth() {
		return ResponseEntity.ok(service.getAuthUrl());
	}

	// @GetMapping("/oauth2callback")
	public ResponseEntity<String> callback(@RequestParam String code) throws IOException {
		GoogleCredentials credentials = service.exchangeCode(code);
		long now = System.currentTimeMillis();
		long oneDayAgo = now - 86400000;

		List<DataPoint> steps = service.getStepData(credentials, oneDayAgo, now);
		int totalSteps = steps.stream().mapToInt(dp -> dp.getValue().get(0).getIntVal()).sum();

		return ResponseEntity.ok("Steps in last 24h: " + totalSteps);
	}

	// Approach 3
	 @GetMapping("/oauth2callback")
	public ResponseEntity<List<StepCountDTO>> getLastWeekSteps(@RequestParam String code) throws IOException {
		GoogleCredentials credentials = service.exchangeCode(code);

		long endTimeMillis = System.currentTimeMillis();
	        long startTimeMillis = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000); // 7 days ago
		 //long startTimeMillis = getStartOfDayMillis(); //only for Today
	     //   long startTimeMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7);

//		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata"));
//		calendar.set(Calendar.HOUR_OF_DAY, 0);
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//		calendar.set(Calendar.MILLISECOND, 0);

		// Set to Monday of current week
//		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//		long startTimeMillis = calendar.getTimeInMillis();

		List<StepCountDTO> dailySteps = service.getDailyStepCounts(credentials, startTimeMillis, endTimeMillis);
		return ResponseEntity.ok(dailySteps);
	}
}
