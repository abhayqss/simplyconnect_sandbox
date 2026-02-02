package com.scnsoft.eldermark.converter.assessment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ComprehensiveAssessment {

    @JsonProperty("Gender")
    private String gender;

    @JsonProperty("Marital status")
    private String maritalStatus;

    @JsonProperty("First name2")
    private String primaryCarePhysicianFirstName;

    @JsonProperty("Last Name2")
    private String primaryCarePhysicianLastName;

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getPrimaryCarePhysicianFirstName() {
        return primaryCarePhysicianFirstName;
    }

    public void setPrimaryCarePhysicianFirstName(String primaryCarePhysicianFirstName) {
        this.primaryCarePhysicianFirstName = primaryCarePhysicianFirstName;
    }

    public String getPrimaryCarePhysicianLastName() {
        return primaryCarePhysicianLastName;
    }

    public void setPrimaryCarePhysicianLastName(String primaryCarePhysicianLastName) {
        this.primaryCarePhysicianLastName = primaryCarePhysicianLastName;
    }
}
