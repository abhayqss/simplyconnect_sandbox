package com.scnsoft.eldermark.beans.reports.model;

import java.time.Instant;
import java.util.Arrays;

public class HospitalizationEventRow {

    private String communityName;
    private String clientName;
    private Long clientId;
    private Instant dateOfInstitutionalization;
    private String location;
    private String situation;
    private String background;
    private String assessment;
    private boolean isInjury;
    private boolean isFollowup;
    private Source source;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Instant getDateOfInstitutionalization() {
        return dateOfInstitutionalization;
    }

    public void setDateOfInstitutionalization(Instant dateOfInstitutionalization) {
        this.dateOfInstitutionalization = dateOfInstitutionalization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public boolean isInjury() {
        return isInjury;
    }

    public void setInjury(boolean injury) {
        isInjury = injury;
    }

    public boolean isFollowup() {
        return isFollowup;
    }

    public void setFollowup(boolean followup) {
        isFollowup = followup;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public enum Source {
        EVENT("Event"),
        CA("Comprehensive Assessment"),
        NOR_CAL_CA("Nor Cal Comprehensive Assessment");

        private final String display;

        Source(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }

        public static Source of(String name) {
            return Arrays.stream(Source.values())
                    .filter(source -> source.getDisplay().equals(name))
                    .findFirst()
                    .orElse(null);
        }
    }
}
