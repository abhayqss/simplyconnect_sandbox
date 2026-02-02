package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;

public class GAD7PHQ9Scoring {

    private String community;
    private Long clientId;
    private String firstName;
    private String lastName;
    private String organization;
    private List<Long> gad7scores;
    private List<Long> phq9Scores;
    private boolean isActive;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
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

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public List<Long> getGad7scores() {
        return gad7scores;
    }

    public void setGad7scores(List<Long> gad7scores) {
        this.gad7scores = gad7scores;
    }

    public List<Long> getPhq9Scores() {
        return phq9Scores;
    }

    public void setPhq9Scores(List<Long> phq9Scores) {
        this.phq9Scores = phq9Scores;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
