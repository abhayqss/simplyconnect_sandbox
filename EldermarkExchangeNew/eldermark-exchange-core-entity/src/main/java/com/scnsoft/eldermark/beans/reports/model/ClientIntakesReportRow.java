package com.scnsoft.eldermark.beans.reports.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class ClientIntakesReportRow {

    private Long clientId;
    private String clientName;
    private String communityName;
    private Instant intakeDate;
    private Instant deactivatedDate;
    private Instant activatedDate;
    private String intakeComment;
    private String exitComment;
    private String deactivationReason;
    private Instant exitDate;
    private String status;
    private LocalDate birthDate;
    private Instant createdDate;
    private String gender;
    private String race;
    private String city;
    private String insuranceNetwork;
    private String insurancePlan;
    private List<String> healthPlans;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public List<String> getHealthPlans() {
        return healthPlans;
    }

    public void setHealthPlans(List<String> healthPlans) {
        this.healthPlans = healthPlans;
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

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public Instant getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Instant intakeDate) {
        this.intakeDate = intakeDate;
    }

    public Instant getDeactivatedDate() {
        return deactivatedDate;
    }

    public void setDeactivatedDate(Instant deactivatedDate) {
        this.deactivatedDate = deactivatedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIntakeComment() {
        return intakeComment;
    }

    public void setIntakeComment(final String intakeComment) {
        this.intakeComment = intakeComment;
    }

    public String getExitComment() {
        return exitComment;
    }

    public void setExitComment(final String exitComment) {
        this.exitComment = exitComment;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(final String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public Instant getExitDate() {
        return exitDate;
    }

    public void setExitDate(final Instant exitDate) {
        this.exitDate = exitDate;
    }

    public Instant getActivatedDate() {
        return activatedDate;
    }

    public void setActivatedDate(final Instant activatedDate) {
        this.activatedDate = activatedDate;
    }
}
