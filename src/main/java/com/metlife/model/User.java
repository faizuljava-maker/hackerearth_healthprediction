package com.metlife.model;

import com.metlife.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class User {
    private Long id;
    private String email;

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String gender;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private String medications;
    private Double cholesterol;
    private String chronicConditions;
    private String familyMedicalHistory;

    private SmokingStatus smokingStatus;

    private AlcoholStatus alcoholStatus;

    private ExerciseFrequency exerciseFrequency;

    private DietType dietType;

    private Double sleepHours;

    private StressLevel stressLevel;

    private String occupation;

    public User(){}

    public User(String email, String firstName, String lastName, LocalDate dateOfBirth, String gender, BigDecimal heightCm, BigDecimal weightKg, String medications, Double cholesterol, String chronicConditions, String familyMedicalHistory, SmokingStatus smokingStatus, AlcoholStatus alcoholStatus, ExerciseFrequency exerciseFrequency, DietType dietType, Double sleepHours, StressLevel stressLevel, String occupation){
        this.email=email;
        this.firstName=firstName;
        this.lastName=lastName;
        this.dateOfBirth=dateOfBirth;
        this.gender=gender;
        this.heightCm=heightCm;
        this.weightKg=weightKg;
        this.medications=medications;
        this.cholesterol=cholesterol;
        this.chronicConditions=chronicConditions;
        this.familyMedicalHistory=familyMedicalHistory;
        this.smokingStatus=smokingStatus;
        this.alcoholStatus=alcoholStatus;
        this.exerciseFrequency=exerciseFrequency;
        this.dietType=dietType;
        this.sleepHours=sleepHours;
        this.stressLevel=stressLevel;
        this.occupation=occupation;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public Double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(Double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public String getChronicConditions() {
        return chronicConditions;
    }

    public void setChronicConditions(String chronicConditions) {
        this.chronicConditions = chronicConditions;
    }

    public String getFamilyMedicalHistory() {
        return familyMedicalHistory;
    }

    public void setFamilyMedicalHistory(String familyMedicalHistory) {
        this.familyMedicalHistory = familyMedicalHistory;
    }

    public SmokingStatus getSmokingStatus() {
        return smokingStatus;
    }

    public void setSmokingStatus(SmokingStatus smokingStatus) {
        this.smokingStatus = smokingStatus;
    }

    public AlcoholStatus getAlcoholStatus() {
        return alcoholStatus;
    }

    public void setAlcoholStatus(AlcoholStatus alcoholStatus) {
        this.alcoholStatus = alcoholStatus;
    }

    public ExerciseFrequency getExerciseFrequency() {
        return exerciseFrequency;
    }

    public void setExerciseFrequency(ExerciseFrequency exerciseFrequency) {
        this.exerciseFrequency = exerciseFrequency;
    }

    public DietType getDietType() {
        return dietType;
    }

    public void setDietType(DietType dietType) {
        this.dietType = dietType;
    }

    public StressLevel getStressLevel() {
        return stressLevel;
    }

    public void setStressLevel(StressLevel stressLevel) {
        this.stressLevel = stressLevel;
    }

    public Double getSleepHours() {
        return sleepHours;
    }

    public void setSleepHours(Double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
}
