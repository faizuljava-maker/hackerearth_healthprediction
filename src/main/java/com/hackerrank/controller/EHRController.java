package com.hackerrank.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationComponentComponent;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hackerrank.model.HealthData;
import com.hackerrank.model.HealthPrediction;
import com.hackerrank.model.MetricSuggestion;
import com.hackerrank.model.PatientBasicDetails;
import com.hackerrank.model.PatientCondition;
import com.hackerrank.model.PatientObservation;
import com.hackerrank.model.PredictionResponse;
import com.hackerrank.util.EHRUtil;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

@RestController
public class EHRController {

	private final String fhirServerBase = "https://hapi.fhir.org/baseR4"; // Replace with actual endpoint

//	@GetMapping("/getehr")
	public PatientBasicDetails getEHRData() {

		// Example: Fetch patient with ID "12345"
		String patientId = "50868771";
		String url = fhirServerBase + "/Patient/" + patientId;

		// Create RestTemplate
		RestTemplate restTemplate = new RestTemplate();

		// Set headers (e.g., Authorization, Accept)
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		// headers.setBearerAuth("your-access-token"); // OAuth2 token

		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Make GET request
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

		// Print response
		System.out.println("FHIR Patient Data:");
		System.out.println(response.getBody());
		FhirContext ctx = FhirContext.forR4();
		Patient patient = (Patient) ctx.newJsonParser().parseResource(response.getBody());

		System.out.println("Patient Name: " + patient.getNameFirstRep().getNameAsSingleString());
		String name = patient.getNameFirstRep().getGivenAsSingleString();
		String gender = patient.getGender().toCode();
		String birthDate = new SimpleDateFormat("yyyy-MM-dd").format(patient.getBirthDate());
		PatientBasicDetails basicDetails = new PatientBasicDetails(name,gender,birthDate,null,null,null,null);
	//	patient.getAddress().get(0).getCity();
		return basicDetails;
	}

	private final FhirContext fhirContext = FhirContext.forR4();
	private final IGenericClient client = fhirContext.newRestfulGenericClient(fhirServerBase);

	@PostMapping("/createPatient")
	public String createPatient(@RequestBody PatientBasicDetails patientDetails) throws Exception {

		Patient patient = new Patient();
		patient.addName().setFamily(patientDetails.getLastName()).addGiven(patientDetails.getFirstName());
		patient.setGender(Enumerations.AdministrativeGender.fromCode(patientDetails.getGender().toLowerCase()));
		patient.setBirthDate(new SimpleDateFormat("yyyy-MM-dd").parse(patientDetails.getDateOfBirth()));
        patient.addTelecom().setSystem(ContactPoint.ContactPointSystem.PHONE).setValue(patientDetails.getMobileNo()).setUse(ContactPoint.ContactPointUse.MOBILE);		
        // Send to FHIR server
        Patient created = (Patient) client.create().resource(patient).execute().getResource();	
        HealthData data =  patientDetails.getHealthDataList().get(0);
        //create Observation
		String patientID = created.getIdElement().getIdPart();
		EHRUtil.createHealthData(patientID, data.getBpm(), data.getHasDiabetes(),
				data.getHasHypertention(), data.getOxygenSaturation(), data.getBpMinValue(), data.getBpMaxValue(), 
				data.getGlucose(),  data.getCholesterol(), data.getTemperature(), data.getWeight(), data.getHeight());
		
		return "Created Patient with ID: " + created.getIdElement().getIdPart();
	}
	@GetMapping("/getEHRDetails")
	public  List<PatientBasicDetails> getEHRDetailsbyMobile(@RequestParam String firstname, @RequestParam String mobileNo) {
		 Bundle results = client
		            .search()
		            .forResource(Patient.class)
		            .where(new StringClientParam("telecom").matches().value(mobileNo))
		            .where(new StringClientParam("name").matches().value(firstname))
		            .returnBundle(Bundle.class)
		            .execute();
		 List<PatientBasicDetails> patientSummaries = new ArrayList<>();
	        for (Bundle.BundleEntryComponent entry : results.getEntry()) {
	            Patient patient = (Patient) entry.getResource();
	            //Getting Patient Details by ID
	            PatientBasicDetails details = getPatient(patient.getIdElement().getIdPart());
	            patientSummaries.add(details);
	        }
		return patientSummaries;
	}
	
	@GetMapping("/getehr/{id}")
	public PatientBasicDetails getPatient(@PathVariable String id) {
		Patient patient =  client.read().resource(Patient.class).withId(id).execute();
		String name = patient.getNameFirstRep().getNameAsSingleString();
		String gender = patient.getGender().toCode();
		String birthDate = new SimpleDateFormat("yyyy-MM-dd").format(patient.getBirthDate());
		String mobileNo = !patient.getTelecom().isEmpty() ? patient.getTelecom().get(0).getValue() : "";
		   // Fetch Observations
        Bundle obsBundle = client.search()
            .forResource(Observation.class)
            .where(Observation.SUBJECT.hasId("Patient/" + id))
            .returnBundle(Bundle.class)
            .execute();
        List<PatientObservation> observations = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : obsBundle.getEntry()) {
            Observation obs = (Observation) entry.getResource();
            String code = obs.getCode().getCoding().get(0).getCode();
            String value = "";
            if("85354-9".equals(code)) {
            	String maxValue = "120";
            	String minValue = "80";
            	
            	for (Observation.ObservationComponentComponent component : obs.getComponent()) {
                    String bpcode = component.getCode().getCodingFirstRep().getCode();
                    Quantity quantity = (Quantity) component.getValue();

                    if ("8480-6".equals(bpcode)) {
                    	maxValue = quantity.getValue().toString();
                    } else if ("8462-4".equals(bpcode)) {
                    	minValue = quantity.getValue().toString();
                    }
                }
            	value = maxValue+"/"+minValue;
            }
            else {
            	value =  ((Quantity) obs.getValue()).getValue().toString();
            }
            observations.add(new PatientObservation(obs.getCode().getCoding().get(0).getDisplay(),
            		value, obs.getValue() instanceof Quantity ? ((Quantity) obs.getValue()).getUnit() : ""
            		, 	obs.getEffectiveDateTimeType().getValueAsString()
            ));
        }
        // Fetch Conditions
        Bundle condBundle = client.search()
            .forResource(Condition.class)
            .where(Condition.SUBJECT.hasId("Patient/" + id))
            .returnBundle(Bundle.class)
            .execute();
        List<PatientCondition> conditions = new ArrayList<>();
        for (Bundle.BundleEntryComponent entry : condBundle.getEntry()) {
            Condition condition = (Condition) entry.getResource();
            conditions.add(new PatientCondition(condition.getCode().getCoding().get(0).getDisplay(),
            		condition.getClinicalStatus().getCoding().get(0).getDisplay()));
        }
		PatientBasicDetails basicDetails = new PatientBasicDetails(name,gender,birthDate,mobileNo,observations,conditions,null);
		return basicDetails;
	}

	@PostMapping("/healthPrediction/v2")
    public HealthPrediction healthPredictionV2(@RequestBody PatientBasicDetails patientDetails) {
        Map<String, String> fieldPrompts = new LinkedHashMap<>() {{
            put("bpm",
                "A user has the following historical heart rate (BPM) values: %s. " + 
                "Based on these values, predict the user's overall heart rate risk category. " + 
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("diabetes",
                "A user has the following historical diabetes values: %s. " + 
                "Based on these values, predict the user's overall heart rate risk category. " + 
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("hypertension",
                "A user has the following historical hypertension values: %s. " + 
                "Based on these values, predict the user's overall heart rate risk category. " + 
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("oxygenSaturation",
                "A user has the following historical oxygen saturation values: %s. " +
                "Based on these values, predict the user's overall oxygen saturation risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("bpMinValue",
                "A user has the following historical blood pressure minimum values (diastolic): %s. " +
                "Based on these values, predict the user's diastolic blood pressure risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("bpMaxValue",
                "A user has the following historical blood pressure maximum values (systolic): %s. " +
                "Based on these values, predict the user's systolic blood pressure risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("glucose",
                "A user has the following historical blood glucose values: %s. " +
                "Based on these values, predict the user's blood glucose risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("cholesterol",
                "A user has the following historical cholesterol values: %s. " +
                "Based on these values, predict the user's cholesterol risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("temperature",
                "A user has the following historical body temperature values: %s. " +
                "Based on these values, predict the user's temperature risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("weight",
                "A user has the following historical weight values: %s. " +
                "Based on these values, predict the user's weight risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
            put("height",
                "A user has the following historical height values: %s. " +
                "Based on these values, predict the user's height risk category. " +
                "Respond with only one word: Low, Moderate, High, or Critical.");
        }};
        System.out.println("Generated prompt :" + fieldPrompts);

        Map<String, List<String>> fieldValues = new HashMap<>();
        for (String key : fieldPrompts.keySet())
            fieldValues.put(key, new ArrayList<>());
        System.out.println("Prompt feilds:" + fieldValues);

        for (HealthPrediction prediction : patientDetails.getPredictionList()) {
            if (prediction.getHealthDataList() != null) {
                for (HealthData data : prediction.getHealthDataList()) {
                    if (data.getBpm() != null) fieldValues.get("bpm").add(data.getBpm().toString());
                    if (data.getHasDiabetes() != null) fieldValues.get("diabetes").add(data.getHasDiabetes().toString());
                    if (data.getHasHypertention() != null) fieldValues.get("hypertension").add(data.getHasHypertention().toString());
                    if (data.getOxygenSaturation() != null) fieldValues.get("oxygenSaturation").add(data.getOxygenSaturation().toString());
                    if (data.getBpMinValue() != null) fieldValues.get("bpMinValue").add(data.getBpMinValue().toString());
                    if (data.getBpMaxValue() != null) fieldValues.get("bpMaxValue").add(data.getBpMaxValue().toString());
                    if (data.getGlucose() != null) fieldValues.get("glucose").add(data.getGlucose().toString());
                    if (data.getCholesterol() != null) fieldValues.get("cholesterol").add(data.getCholesterol().toString());
                    if (data.getTemperature() != null) fieldValues.get("temperature").add(data.getTemperature().toString());
                    if (data.getWeight() != null) fieldValues.get("weight").add(data.getWeight().toString());
                    if (data.getHeight() != null) fieldValues.get("height").add(data.getHeight().toString());
                } 
            }
        }
        
        Client client = Client.builder().apiKey("AIzaSyDnADA5aZpl0dnsw-ovuv8zNUG_ieo2I6M").build();

        PredictionResponse predictionResponse = new PredictionResponse();

        for (Map.Entry<String, String> entry : fieldPrompts.entrySet()) {
            String field = entry.getKey();
            List<String> values = fieldValues.get(field);
            String status = null;
            if (values != null && !values.isEmpty()) {
                String splitedValues = String.join(", ", values);
                String prompt = String.format(entry.getValue(), splitedValues);
                status = getAIPrediction(client, prompt);
            }
            System.out.println("AI generated variable status:" + status);

            switch (field) {
                case "bpm": predictionResponse.setBpm(status); break;
                case "diabetes": predictionResponse.setDiabetes(status); break;
                case "hypertension": predictionResponse.setHypertention(status); break;
                case "oxygenSaturation": predictionResponse.setOxygenSaturation(status); break;
                case "bpMinValue": predictionResponse.setBpMinValue(status); break;
                case "bpMaxValue": predictionResponse.setBpMaxValue(status); break;
                case "glucose": predictionResponse.setGlucose(status); break;
                case "cholesterol": predictionResponse.setCholesterol(status); break;
                case "temperature": predictionResponse.setTemperature(status); break;
                case "weight": predictionResponse.setWeight(status); break;
                case "height": predictionResponse.setHeight(status); break;
            }
        }
        HealthPrediction result = new HealthPrediction();
        result.setPredictionResponse(predictionResponse);
        result.setHealthDataList(null);
        return result;
    }


    private String getAIPrediction(Client client, String prompt) {
        String defaultStatus = "Low";
        try {
            GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);
            if (response != null && response.text() != null) {
                String text = response.text().trim().toLowerCase();
                if (text.contains("low")) return "Low";
                else if (text.contains("moderate")) return "Moderate";
                else if (text.contains("high") && !text.contains("very")) return "High";
                else if (text.contains("critical") || text.contains("very high")) return "Critical";
            }
            System.out.println("Final AI generated response:" + response);

        } catch (Exception e) {
        	e.printStackTrace();
        }
        return defaultStatus;
    }
    
    
    @GetMapping("/healthSuggestions/v1")
    public List<MetricSuggestion> getHealthSuggestionsV1() {

        Map<String, String> statusMap = new LinkedHashMap<>();
        statusMap.put("BPM", "Low");
        statusMap.put("Diabetes", "Yes");
        statusMap.put("Hypertension", "Yes");
        statusMap.put("Oxygen Saturation", "Low");
        statusMap.put("BP", "High");
        statusMap.put("Glucose", "High");
        statusMap.put("Cholesterol", "High");
        statusMap.put("Temperature", "High");
        statusMap.put("Weight", "High");
        statusMap.put("Height", "Normal");

        Map<String, String> staticSuggestions = Map.ofEntries(
        	    Map.entry("BPM", "Keep your heart rate in a healthy range with regular activity and hydration."),
        	    Map.entry("Diabetes", "Follow your diabetes management plan and monitor blood sugar consistently."),
        	    Map.entry("Hypertension", "Maintain a low-sodium diet, manage stress, and check blood pressure regularly."),
        	    Map.entry("Oxygen Saturation", "Practice breathing exercises and seek care if you notice sudden changes."),
        	    Map.entry("BP", "Support healthy blood pressure with exercise, diet, and hydration."),
        	    Map.entry("Glucose", "Balance your meals with fiber and monitor your blood sugar as recommended."),
        	    Map.entry("Cholesterol", "Eat a heart-healthy diet, emphasizing fruits, vegetables, and limiting unhealthy fats."),
        	    Map.entry("Temperature", "Monitor for abnormal temperatures and rest, stay hydrated, and consult a provider if concerned."),
        	    Map.entry("Weight", "Maintain a healthy weight with balanced nutrition and regular physical activity."),
        	    Map.entry("Height", "Continue good lifestyle habits appropriate for your age and growth stage.")
        	);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Below are a user's health metric statuses (Low/High/Yes/No/Normal). For each metric, provide only a one-line actionable precaution or improvement suggestion (not a diagnosis or reference):\n\n");
        int order = 1;
        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            promptBuilder.append(order++).append(". ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        promptBuilder.append("\nReturn your answer as a numbered list, matching the order above. Each line should be a short, actionable suggestion only.");

        String prompt = promptBuilder.toString();

        String aiRawResponse = "";
        try {
            Client client = Client.builder().apiKey("AIzaSyDnADA5aZpl0dnsw-ovuv8zNUG_ieo2I6M").build();
            GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", prompt, null);
            if (response != null && response.text() != null) {
                aiRawResponse = response.text().trim();
            }
        } catch (Exception ex) {
            List<MetricSuggestion> errorFallback = new ArrayList<>();
            for (Map.Entry<String, String> entry : statusMap.entrySet()) {
                String key = entry.getKey() + ":" + entry.getValue();
                String staticAdvice = staticSuggestions.getOrDefault(key, "Please consult your physician.");
                errorFallback.add(new MetricSuggestion(entry.getKey(), entry.getValue(), staticAdvice));
            }
            return errorFallback;
        }

        List<String> cleanLines = new ArrayList<>();
        String[] aiLines = aiRawResponse.split("\n");
        for (String line : aiLines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.toLowerCase().contains("actionable")) continue;
            trimmed = trimmed.replaceAll("^\\s*[\\d\\-\\.]+\\s*", "");
            cleanLines.add(trimmed);
        }

        List<MetricSuggestion> results = new ArrayList<>();
        int i = 0;
        for (Map.Entry<String, String> entry : statusMap.entrySet()) {
            String key = entry.getKey() + ":" + entry.getValue();
            String aiSuggest = (i < cleanLines.size() && !cleanLines.get(i).isBlank()) ? cleanLines.get(i) : null;
            if (aiSuggest == null || aiSuggest.isBlank()) {
                aiSuggest = staticSuggestions.getOrDefault(key, "Please consult your physician.");
            }
            results.add(new MetricSuggestion(entry.getKey(), entry.getValue(), aiSuggest));
            i++;
        }
        return results;
    }
}
