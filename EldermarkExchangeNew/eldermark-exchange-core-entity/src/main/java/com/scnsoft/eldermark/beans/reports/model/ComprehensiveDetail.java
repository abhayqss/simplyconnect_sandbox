package com.scnsoft.eldermark.beans.reports.model;

public class ComprehensiveDetail {

    private String patientName;
    private Long patientId;
    private String community;
    private Long timeToCompleteInMinutes;
    private String income;
    private String genderFormClient;
    private String genderFromAssessment;
    private String raceFromClient;
    private String raceFromAssessment;
    private Long age;
    private String insuranceNetwork;
    private String insurancePlan;

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public Long getTimeToCompleteInMinutes() {
        return timeToCompleteInMinutes;
    }

    public void setTimeToCompleteInMinutes(Long timeToCompleteInMinutes) {
        this.timeToCompleteInMinutes = timeToCompleteInMinutes;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getGenderFormClient() {
        return genderFormClient;
    }

    public void setGenderFormClient(String genderFormClient) {
        this.genderFormClient = genderFormClient;
    }

    public String getGenderFromAssessment() {
        return genderFromAssessment;
    }

    public void setGenderFromAssessment(String genderFromAssessment) {
        this.genderFromAssessment = genderFromAssessment;
    }

    public String getRaceFromClient() {
        return raceFromClient;
    }

    public void setRaceFromClient(String raceFromClient) {
        this.raceFromClient = raceFromClient;
    }

    public String getRaceFromAssessment() {
        return raceFromAssessment;
    }

    public void setRaceFromAssessment(String raceFromAssessment) {
        this.raceFromAssessment = raceFromAssessment;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public String getInsuranceNetwork() {
        return insuranceNetwork;
    }

    public void setInsuranceNetwork(String insuranceNetwork) {
        this.insuranceNetwork = insuranceNetwork;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }
}
