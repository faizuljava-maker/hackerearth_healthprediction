package com.hackerrank.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import com.hackerrank.model.PatientBasicDetails;
import com.hackerrank.model.PatientCondition;
import com.hackerrank.model.PatientObservation;
import com.hackerrank.util.EHRUtil;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.StringClientParam;

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
		PatientBasicDetails basicDetails = new PatientBasicDetails(name,gender,birthDate,null,null,null);
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
		PatientBasicDetails basicDetails = new PatientBasicDetails(name,gender,birthDate,mobileNo,observations,conditions);
		return basicDetails;
	}
}
