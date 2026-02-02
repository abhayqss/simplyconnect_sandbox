package com.scnsoft.eldermark.dto.filter;

import com.scnsoft.eldermark.beans.ClientAccessType;
import com.scnsoft.eldermark.beans.ClientStatus;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.util.List;

public class ClientFilterDto {

    private Long organizationId;

    private List<Long> communityIds;

    private String ssnLast4;

    private String ssn;

    private Long genderId;

    private String firstName;

    private String lastName;

    private String birthDate;

    private ClientStatus recordStatus;

    private List<Long> programStatusIds;

    private String primaryCarePhysician;

    private String insuranceNetworkAggregatedName;

    private List<String> pharmacyNames;

    private Boolean hasNoPharmacies;

    private Boolean isAdmitted;

    private String unit;

    private String medicaidNumber;

    private String medicareNumber;

    private Boolean withAccessibleAppointments;

    private HieConsentPolicyType hieConsentPolicyName;

    private ClientAccessType clientAccessType;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public List<Long> getCommunityIds() {
        return communityIds;
    }

    public void setCommunityIds(List<Long> communityIds) {
        this.communityIds = communityIds;
    }

    public String getSsnLast4() {
        return ssnLast4;
    }

    public void setSsnLast4(String ssnLast4) {
        this.ssnLast4 = ssnLast4;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public ClientStatus getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(ClientStatus recordStatus) {
        this.recordStatus = recordStatus;
    }

    public List<Long> getProgramStatusIds() {
        return programStatusIds;
    }

    public void setProgramStatusIds(List<Long> programStatusIds) {
        this.programStatusIds = programStatusIds;
    }

    public String getPrimaryCarePhysician() {
        return primaryCarePhysician;
    }

    public void setPrimaryCarePhysician(String primaryCarePhysician) {
        this.primaryCarePhysician = primaryCarePhysician;
    }

    public String getInsuranceNetworkAggregatedName() {
        return insuranceNetworkAggregatedName;
    }

    public void setInsuranceNetworkAggregatedName(String insuranceNetworkAggregatedName) {
        this.insuranceNetworkAggregatedName = insuranceNetworkAggregatedName;
    }

    public List<String> getPharmacyNames() {
        return pharmacyNames;
    }

    public void setPharmacyNames(List<String> pharmacyNames) {
        this.pharmacyNames = pharmacyNames;
    }

    public Boolean getHasNoPharmacies() {
        return hasNoPharmacies;
    }

    public void setHasNoPharmacies(Boolean hasNoPharmacies) {
        this.hasNoPharmacies = hasNoPharmacies;
    }

    public Boolean getIsAdmitted() {
        return isAdmitted;
    }

    public void setIsAdmitted(Boolean admitted) {
        isAdmitted = admitted;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public Boolean getWithAccessibleAppointments() {
        return withAccessibleAppointments;
    }

    public void setWithAccessibleAppointments(Boolean withAccessibleAppointments) {
        this.withAccessibleAppointments = withAccessibleAppointments;
    }

    public HieConsentPolicyType getHieConsentPolicyName() {
        return hieConsentPolicyName;
    }

    public void setHieConsentPolicyName(HieConsentPolicyType hieConsentPolicyName) {
        this.hieConsentPolicyName = hieConsentPolicyName;
    }

    public ClientAccessType getClientAccessType() {
        return clientAccessType;
    }

    public void setClientAccessType(ClientAccessType clientAccessType) {
        this.clientAccessType = clientAccessType;
    }
}
