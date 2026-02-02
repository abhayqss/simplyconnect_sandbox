package com.scnsoft.eldermark.shared.carecoordination.patients;

import com.scnsoft.eldermark.shared.Gender;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Null;
import java.util.Date;

/**
 * Created by pzhurba on 16-Oct-15.
 */
public class PatientsFilterDto {
    private String firstName;
    private String lastName;
    @Null
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    private Date  birthDate;
    private Gender gender;
    private String lastFourSsn;
    private Boolean showDeactivated;
    private String primaryCarePhysician;
    private String insuranceNetwork;

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

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getLastFourSsn() {
        return lastFourSsn;
    }

    public void setLastFourSsn(String lastFourSsn) {
        this.lastFourSsn = lastFourSsn;
    }

    public Boolean getShowDeactivated() {
        return showDeactivated;
    }

    public void setShowDeactivated(Boolean showDeactivated) {
        this.showDeactivated = showDeactivated;
    }

    public String getPrimaryCarePhysician() {
        return primaryCarePhysician;
    }

    public void setPrimaryCarePhysician(String primaryCarePhysician) {
        this.primaryCarePhysician = primaryCarePhysician;
    }

    public String getInsuranceNetwork() {
        return insuranceNetwork;
    }

    public void setInsuranceNetwork(String insuranceNetwork) {
        this.insuranceNetwork = insuranceNetwork;
    }
}
