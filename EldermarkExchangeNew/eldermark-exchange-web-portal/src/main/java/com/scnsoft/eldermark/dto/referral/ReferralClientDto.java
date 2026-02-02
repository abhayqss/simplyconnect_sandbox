package com.scnsoft.eldermark.dto.referral;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferralClientDto {

    @NotNull
    private Long id;

    private String fullName;
    private String gender;
    private String birthDate;
    private List<String> diagnoses;
    private boolean canView;

    @Size(max = 256)
    private String location;

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String locationPhone;

    @Valid
    private AddressDto address;

    @Size(max = 256)
    private String insuranceNetworkTitle;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public List<String> getDiagnoses() {
        return diagnoses;
    }

    public void setDiagnoses(List<String> diagnoses) {
        this.diagnoses = diagnoses;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocationPhone() {
        return locationPhone;
    }

    public void setLocationPhone(String locationPhone) {
        this.locationPhone = locationPhone;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public String getInsuranceNetworkTitle() {
        return insuranceNetworkTitle;
    }

    public void setInsuranceNetworkTitle(String insuranceNetworkTitle) {
        this.insuranceNetworkTitle = insuranceNetworkTitle;
    }

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }
}
