package com.hackerrank.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

import org.springframework.stereotype.Service;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.hackerrank.enums.AlcoholStatus;
import com.hackerrank.enums.DietType;
import com.hackerrank.enums.ExerciseFrequency;
import com.hackerrank.enums.SmokingStatus;
import com.hackerrank.enums.StressLevel;
import com.hackerrank.model.User;

@Service
public class UserService {
//    private final UserRepository userRepository;
//
//    public UserService(UserRepository userRepository){
//        this.userRepository=userRepository;
//    }
//
//    public UserValidationResponse createUser(User user){
//        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
//        if (existingUser.isPresent()) {
//            return new UserValidationResponse(false, "User already exists", null);
//        }
//
//        if (user.getDateOfBirth() == null) {
//            return new UserValidationResponse(false, "Date of birth is required", null);
//        }
//
//        LocalDate today = LocalDate.now();
//        int age = Period.between(user.getDateOfBirth(), today).getYears();
//
//        if (age < 18) {
//            return new UserValidationResponse(false, "User must be 18 years or older to register", null);
//        }
//
//        User savedUser = userRepository.save(user);
//        return new UserValidationResponse(true, "User created successfully", savedUser);
//    }
//
//    public UserValidationResponse getUserById(Long id){
//        Optional<User> user = userRepository.findById(id);
//
//        if(user.isPresent()){
//            return new UserValidationResponse(true, "User found successfully", user.get());
//        }
//        else{
//            return new UserValidationResponse(false, "User does not exist", null);
//        }
//    }
//
//    public UserValidationResponse getUserByEmailId(String emailId){
//        Optional<User> user = userRepository.findByEmail(emailId);
//
//        if(user.isPresent()){
//            return new UserValidationResponse(true, "User found successfully", user.get());
//        }
//        else{
//            return new UserValidationResponse(false, "User does not exist", null);
//        }
//    }
//
//    public User updateUser(Long id, User updatedUser){
//        Optional<User> existingUser = userRepository.findById(id);
//
//        if(existingUser.isPresent()){
//            User user = existingUser.get();
//            user.setEmail(updatedUser.getEmail());
//            user.setFirstName(updatedUser.getFirstName());
//            user.setLastName(updatedUser.getLastName());
//            user.setDateOfBirth(updatedUser.getDateOfBirth());
//            user.setGender(updatedUser.getGender());
//            userRepository.save(user);
//            return user;
//        }
//        else{
//            System.out.println("User not found "+id);
//        }
//        return null;
//    }

	public String getLLMResponse(User user) {

		//Client client = Client.builder().apiKey("AIzaSyDnADA5aZpl0dnsw-ovuv8zNUG_ieo2I6M").build();
		
		Client client = Client.builder().apiKey("sk-TE5BPNfSh4IOCNpW3I5EDQ").build();
		
		String gender=user.getGender();
		    LocalDate today = LocalDate.now();
		    int age=Period.between(user.getDateOfBirth(), today).getYears();
		    BigDecimal height=user.getHeightCm();
		    BigDecimal weight=user.getWeightKg();
		    String medications = user.getMedications();
		    Double cholesterol=user.getCholesterol();
		    String chronicConditions=user.getChronicConditions();
		    String familyMedicalHistory=user.getFamilyMedicalHistory();
		    SmokingStatus smokingStatus=user.getSmokingStatus();
		    AlcoholStatus alcoholStatus=user.getAlcoholStatus();
		    ExerciseFrequency exerciseFrequency=user.getExerciseFrequency();
		    DietType dietType=user.getDietType();
		    Double sleepHours=user.getSleepHours();
		    StressLevel stressLevel=user.getStressLevel();
		    String occupation=user.getOccupation();
		    // Calculate BMI
		    double bmi = weight.doubleValue() / Math.pow(height.doubleValue() / 100, 2);
		    String prompt = String.format(
		        "Analyze the cardiovascular risk factors for a person with heart rate of 100 bpm. " +
		        "Patient Profile: %d-year-old %s, BMI: %.1f, Cholesterol: %.1f mg/dL. " +
		        "Lifestyle: %s smoking status, %s alcohol consumption, %s exercise frequency, %s diet. " +
		        "Health: Sleep %.1f hours/night, %s stress level, taking medications: %s. " +
		        "Medical History: Chronic conditions: %s, Family history: %s, Occupation: %s. " +
		        "Based on these factors and the elevated heart rate of 100 bpm, assess the cardiovascular risk level. " +
		        "Respond with only a single number: 0=Low risk, 1=Moderate risk, 2=High risk, 3=Very high risk.",
		        age, gender, bmi, cholesterol != null ? cholesterol : 0.0,
		        smokingStatus != null ? smokingStatus.toString().replace("_", " ").toLowerCase() : "unknown",
		        alcoholStatus != null ? alcoholStatus.toString().replace("_", " ").toLowerCase() : "unknown",
		        exerciseFrequency != null ? exerciseFrequency.toString().replace("_", " ").toLowerCase() : "unknown",
		        dietType != null ? dietType.toString().replace("_", " ").toLowerCase() : "unknown",
		        sleepHours != null ? sleepHours : 0.0,
		        stressLevel != null ? stressLevel.toString().toLowerCase() : "unknown",
		        medications != null ? medications : "none",
		        chronicConditions != null ? chronicConditions : "none",
		        familyMedicalHistory != null ? familyMedicalHistory : "none",
		        occupation != null ? occupation : "unknown"
		    );
		    GenerateContentResponse response = null;
		    try {
		    response = client.models.generateContent("gemini-2.5-flash", prompt, null);
		    System.out.println(response.text());
		    }catch(Exception ex) {
		    	response = null;
		    }
		    // Parse the response to extract the risk number
		    try {
		    	if(response.equals(null)) {
		    		return "0";
		    	}
		        String responseText = response.text().trim();
		       // String numberStr = responseText.replaceAll("[^0-9]", "").substring(0, 1);
		        return responseText;
		    } catch (Exception e) {
		        System.out.println("Error parsing response: " + e.getMessage());
		        return e.getMessage(); // Default to moderate risk if parsing fails
		    }
//		GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash",
//				"one line of response for a person heartrate is nearly 100 bpm. what will be the risk", null);
//		return 1;
	}
}

 