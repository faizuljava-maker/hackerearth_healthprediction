package com.hackerrank.util;

import java.util.Arrays;
import java.util.Date;

import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;

public class EHRUtil {

	private static final String fhirServerBase = "https://hapi.fhir.org/baseR4"; // Replace with actual endpoint
	private static final FhirContext fhirContext = FhirContext.forR4();
	private static final IGenericClient client = fhirContext.newRestfulGenericClient(fhirServerBase);

	public static void createHealthData(String patientId, Integer bpm, Boolean hasDiabetes, Boolean hasHypertention,
			Long oxygenSaturation, Long bpMinValue, Long bpMaxValue, Long glucose, Long cholesterol, Long temperature,
			Long weight, Long height) {
		System.out.println("" + patientId+""+bpm+""+ hasDiabetes+""+ hasHypertention+""+ oxygenSaturation+""+ bpMinValue+""+ bpMaxValue+""+
				glucose+""+ cholesterol+""+ temperature+""+ weight+""+ height);
		if (bpm != null && bpm > 0) {
			System.out.println(bpm);
			Observation observation = createHeartRateObservation(patientId, bpm);
			client.create().resource(observation).execute();
		}
		if (hasDiabetes) {
			System.out.println(hasDiabetes);

			Condition condition = createDiabetes(patientId);
			client.create().resource(condition).execute();
		}
		if (hasHypertention) {
			System.out.println(hasHypertention);

			Condition condition = createHypertention(patientId);
			client.create().resource(condition).execute();
		}
		if (oxygenSaturation != null && oxygenSaturation > 0) {
			System.out.println(oxygenSaturation);

			Observation observation = createBloodOxygenSaturation(patientId, oxygenSaturation);
			client.create().resource(observation).execute();
		}
		if (bpMinValue != null && bpMaxValue != null && bpMinValue > 0 && bpMaxValue > 0) {
			System.out.println(bpMinValue);

			Observation observation = createBloodPressure(patientId, bpMinValue, bpMaxValue);
			client.create().resource(observation).execute();
		}
		if (glucose != null && glucose > 0) {
			System.out.println(glucose);

			Observation observation = createGlucose(patientId, glucose);
			client.create().resource(observation).execute();
		}
		if (cholesterol != null && cholesterol > 0) {
			System.out.println(cholesterol);

			Observation observation = createCholesterol(patientId, cholesterol);
			client.create().resource(observation).execute();
		}
		if (temperature != null && temperature > 0) {
			System.out.println(temperature);

			Observation observation = createTemperature(patientId, temperature);
			client.create().resource(observation).execute();
		}
		if (weight != null && weight > 0) {
			System.out.println(weight);

			Observation observation = createWeight(patientId, weight);
			client.create().resource(observation).execute();
		}
		if (height != null && height > 0) {
			System.out.println(height);

			Observation observation = createHeight(patientId, height);
			client.create().resource(observation).execute();
		}
	}

	public static Observation createHeartRateObservation(String patientId, int bpm) {
		Observation obs = new Observation();
		obs.setStatus(Observation.ObservationStatus.FINAL);
		obs.setCode(new CodeableConcept().addCoding(new Coding("http://loinc.org", "8867-4", "Heart rate")));
		obs.setValue(new Quantity().setValue((double) bpm).setUnit("beats/minute"));
		obs.setSubject(new Reference("Patient/" + patientId));
		obs.setEffective(new DateTimeType(new Date()));
		return obs;
	}

	public static Condition createDiabetes(String patientId) {
		Condition condition = new Condition();
		condition.setSubject(new Reference("Patient/" + patientId));
		condition.setCode(new CodeableConcept()
				.addCoding(new Coding("http://snomed.info/sct", "44054006", "Diabetes mellitus type 2")));
		condition.setClinicalStatus(new CodeableConcept()
				.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/condition-clinical", "active", "Active")));
		return condition;
	}

	public static Condition createHypertention(String patientId) {

		Condition condition = new Condition();
		condition.setSubject(new Reference("Patient/" + patientId));
		condition.setCode(new CodeableConcept().addCoding(
				new Coding().setSystem("http://snomed.info/sct").setCode("38341003").setDisplay("Hypertension")));
		condition.setClinicalStatus(new CodeableConcept().addCoding(
				new Coding().setSystem("http://terminology.hl7.org/CodeSystem/condition-clinical").setCode("active").setDisplay("Active")));
		condition.setVerificationStatus(new CodeableConcept().addCoding(new Coding()
				.setSystem("http://terminology.hl7.org/CodeSystem/condition-ver-status").setCode("confirmed")));
		condition.setOnset(new DateTimeType("2023-05-01"));
		return condition;
	}

	public static Observation createBloodOxygenSaturation(String patientId, Long value) {
		Observation obs = new Observation();
		obs.setStatus(ObservationStatus.FINAL);
		obs.setCode(new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("59408-5")
				.setDisplay("Oxygen saturation in Arterial blood")));
		obs.setValue(new Quantity().setValue(value).setUnit("%").setSystem("http://unitsofmeasure.org").setCode("%"));
		obs.setEffective(new DateTimeType(new Date()));
		obs.setSubject(new Reference("Patient/" + patientId));
		return obs;
	}

	public static Observation createBloodPressure(String patientId, Long minValue, Long maxValue) {
		Observation bp = new Observation();
		bp.setStatus(ObservationStatus.FINAL);
		bp.setCode(new CodeableConcept().addCoding(
				new Coding().setSystem("http://loinc.org").setCode("85354-9").setDisplay("Blood pressure panel")));

		bp.setComponent(Arrays.asList(
				new Observation.ObservationComponentComponent()
						.setCode(new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org")
								.setCode("8480-6").setDisplay("Systolic blood pressure")))
						.setValue(new Quantity().setValue(maxValue).setUnit("mmHg")
								.setSystem("http://unitsofmeasure.org").setCode("mm[Hg]")),
				new Observation.ObservationComponentComponent()
						.setCode(new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org")
								.setCode("8462-4").setDisplay("Diastolic blood pressure")))
						.setValue(new Quantity().setValue(minValue).setUnit("mmHg")
								.setSystem("http://unitsofmeasure.org").setCode("mm[Hg]"))));
		bp.setEffective(new DateTimeType(new Date()));
		bp.setSubject(new Reference("Patient/" + patientId));
		return bp;
	}

	public static Observation createGlucose(String patientId, Long value) {
		Observation glucose = new Observation();
		glucose.setStatus(ObservationStatus.FINAL);
		glucose.setCode(new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("1558-6")
				.setDisplay("Glucose [Mass/volume] in Blood")));
		glucose.setValue(new Quantity().setValue(value).setUnit("mg/dL").setSystem("http://unitsofmeasure.org")
				.setCode("mg/dL"));
		glucose.setEffective(new DateTimeType(new Date()));
		glucose.setSubject(new Reference("Patient/" + patientId));
		return glucose;
	}

	public static Observation createCholesterol(String patientId, Long value) {
		Observation cholesterol = new Observation();
		cholesterol.setStatus(ObservationStatus.FINAL);
		cholesterol.setCode(new CodeableConcept().addCoding(new Coding().setSystem("http://loinc.org").setCode("2093-3")
				.setDisplay("Cholesterol [Mass/volume] in Serum or Plasma")));
		cholesterol.setValue(new Quantity().setValue(value).setUnit("mg/dL").setSystem("http://unitsofmeasure.org")
				.setCode("mg/dL"));
		cholesterol.setEffective(new DateTimeType(new Date()));
		cholesterol.setSubject(new Reference("Patient/" + patientId));
		return cholesterol;
	}

	public static Observation createTemperature(String patientId, Long value) {
		Observation temp = new Observation();
		temp.setStatus(ObservationStatus.FINAL);
		temp.setCode(new CodeableConcept().addCoding(
				new Coding().setSystem("http://loinc.org").setCode("8310-5").setDisplay("Body temperature")));
		temp.setValue(
				new Quantity().setValue(value).setUnit("°F").setSystem("http://unitsofmeasure.org").setCode("degF"));
		temp.setEffective(new DateTimeType(new Date()));
		temp.setSubject(new Reference("Patient/" + patientId));
		return temp;
	}

	public static Observation createWeight(String patientId, Long value) {
		Observation weight = new Observation();
		weight.setStatus(ObservationStatus.FINAL);
		weight.setCode(new CodeableConcept()
				.addCoding(new Coding().setSystem("http://loinc.org").setCode("29463-7").setDisplay("Body weight")));
		weight.setValue(
				new Quantity().setValue(value).setUnit("kg").setSystem("http://unitsofmeasure.org").setCode("kg"));
		weight.setEffective(new DateTimeType(new Date()));
		weight.setSubject(new Reference("Patient/" + patientId));
		return weight;
	}

	public static Observation createHeight(String patientId, Long value) {
		Observation height = new Observation();
		height.setStatus(ObservationStatus.FINAL);
		height.setCode(new CodeableConcept()
				.addCoding(new Coding().setSystem("http://loinc.org").setCode("8302-2").setDisplay("Body height")));
		height.setValue(
				new Quantity().setValue(value).setUnit("cm").setSystem("http://unitsofmeasure.org").setCode("cm"));
		height.setEffective(new DateTimeType(new Date()));
		height.setSubject(new Reference("Patient/" + patientId));
		return height;
	}
}
