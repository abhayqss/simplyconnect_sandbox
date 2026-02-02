package com.scnsoft.eldermark.shared.carecoordination;

import com.scnsoft.eldermark.shared.SpecialtyPhysicianDto;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This DTO is intended to represent patient info.
 * Created by pzhurba on 05-Oct-15.
 */
public class PatientDto extends NameDto {
    Long id;
    @DateTimeFormat(pattern="MM/dd/yyyy")
    Date birthDate;
    String ssn;
    String gender;
    Long genderId;
    String maritalStatus;
    private String status;
    AddressDto address;
    String organization;
    Long organizationId;
    String community;
    Long communityId;
    Boolean editable = false;
    private String phone;

    private String email;
    private Long insuranceId;
    private String insurancePlan;
    private String groupNumber;
    private String memberNumber;
    private String deviceID;
    private String deviceIDSecondary;
    private Boolean retained;
    @DateTimeFormat(pattern = "MM/dd/yyyy hh:mm a (Z)")
    private Date intakeDate;
    private String referralSource;
    private Boolean active;
    private String hashKey;

    private Set<Date> admitDates;
    private Set<Date> dischargeDates;
    private Date deathDate;

    private String cellPhone;
    private String homePhone;
    private String workPhone;

    private String primaryCarePhysician;
    private List<PrimaryCarePhysicianDto> primaryCarePhysicians;
    private List<SpecialtyPhysicianDto> specialtyPhysicians;
    private List<PharmacyDto> pharmacyDtos;

    private String currentPharmacyName;
    private String pharmacyPhone;
    private String pharmacyAddress;

    private String medicareNumber;
    private String medicaidNumber;
    private String healthPlans;
    private String dentalPlan;

    public PatientDto() {
    }

    public PatientDto(PatientDto other) {
        this.id = other.getId();
        this.birthDate = other.getBirthDate();
        this.ssn = other.getSsn();
        this.gender = other.getGender();
        this.genderId = other.getGenderId();
        this.maritalStatus = other.getMaritalStatus();
        this.status = other.getStatus();
        this.address = other.getAddress();
        this.organization = other.getOrganization();
        this.community = other.getCommunity();
        this.editable = other.getEditable();
        this.phone = other.getPhone();
        this.cellPhone = other.getCellPhone();
        this.insuranceId = other.getInsuranceId();
        this.groupNumber = other.getGroupNumber();
        this.memberNumber = other.getMemberNumber();
        this.medicareNumber = other.getMedicareNumber();
        this.medicaidNumber = other.getMedicaidNumber();
        this.retained = other.getRetained();

        this.intakeDate = other.getIntakeDate();
        this.referralSource = other.getReferralSource();
        this.currentPharmacyName = other.getCurrentPharmacyName();
        this.email = other.getEmail();
        this.insurancePlan = other.getInsurancePlan();
        this.setLastName(other.getLastName());
        this.setFirstName(other.getFirstName());
        this.setDeviceID(other.getDeviceID());
        this.setDeviceIDSecondary(other.getDeviceIDSecondary());

        this.setCellPhone(other.getCellPhone());
        this.setWorkPhone(other.getWorkPhone());
        this.setHomePhone(other.getHomePhone());

        this.primaryCarePhysician = other.getPrimaryCarePhysician();
        this.primaryCarePhysicians = other.getPrimaryCarePhysicians();
        this.specialtyPhysicians = other.specialtyPhysicians;
        this.pharmacyDtos = other.pharmacyDtos;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getDeviceIDSecondary() {
        return deviceIDSecondary;
    }

    public void setDeviceIDSecondary(String deviceIDSecondary) {
        this.deviceIDSecondary = deviceIDSecondary;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Long getGenderId() {
        return genderId;
    }

    public void setGenderId(Long genderId) {
        this.genderId = genderId;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getEditable() {
        return editable;
    }

    public void setEditable(Boolean editable) {
        this.editable = editable;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(Long insuranceId) {
        this.insuranceId = insuranceId;
    }

    public String getGroupNumber() {
        return groupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        this.groupNumber = groupNumber;
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) {
        this.memberNumber = memberNumber;
    }

    public String getMedicareNumber() {
        return medicareNumber;
    }

    public void setMedicareNumber(String medicareNumber) {
        this.medicareNumber = medicareNumber;
    }

    public String getMedicaidNumber() {
        return medicaidNumber;
    }

    public void setMedicaidNumber(String medicaidNumber) {
        this.medicaidNumber = medicaidNumber;
    }

    public Boolean getRetained() {
        return retained;
    }

    public void setRetained(Boolean retained) {
        this.retained = retained;
    }

    public Date getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }

    public String getReferralSource() {
        return referralSource;
    }

    public void setReferralSource(String referralSource) {
        this.referralSource = referralSource;
    }

    public String getCurrentPharmacyName() {
        return currentPharmacyName;
    }

    public void setCurrentPharmacyName(String curentPharmacyName) {
        this.currentPharmacyName = curentPharmacyName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(String insurancePlan) {
        this.insurancePlan = insurancePlan;
    }

    public Set<Date> getAdmitDates() {
        return admitDates;
    }

    public void setAdmitDates(Set<Date> admitDates) {
        this.admitDates = admitDates;
    }

    public Set<Date> getDischargeDates() {
        return dischargeDates;
    }

    public void setDischargeDates(Set<Date> dischargeDates) {
        this.dischargeDates = dischargeDates;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public List<SpecialtyPhysicianDto> getSpecialtyPhysicians() {
        return specialtyPhysicians;
    }

    public void setSpecialtyPhysicians(List<SpecialtyPhysicianDto> specialtyPhysicians) {
        this.specialtyPhysicians = specialtyPhysicians;
    }

    public String getPharmacyPhone() {
        return pharmacyPhone;
    }

    public void setPharmacyPhone(String pharmacyPhone) {
        this.pharmacyPhone = pharmacyPhone;
    }

    public String getPharmacyAddress() {
        return pharmacyAddress;
    }

    public void setPharmacyAddress(String pharmacyAddress) {
        this.pharmacyAddress = pharmacyAddress;
    }

    public String getHealthPlans() {
        return healthPlans;
    }

    public void setHealthPlans(String healthPlans) {
        this.healthPlans = healthPlans;
    }

    public String getDentalPlan() {
        return dentalPlan;
    }

    public void setDentalPlan(String dentalPlan) {
        this.dentalPlan = dentalPlan;
    }

    public List<PrimaryCarePhysicianDto> getPrimaryCarePhysicians() {
        return primaryCarePhysicians;
    }

    public void setPrimaryCarePhysicians(List<PrimaryCarePhysicianDto> primaryCarePhysicians) {
        this.primaryCarePhysicians = primaryCarePhysicians;
    }

    public String getPrimaryCarePhysician() {
        return primaryCarePhysician;
    }

    public void setPrimaryCarePhysician(String primaryCarePhysician) {
        this.primaryCarePhysician = primaryCarePhysician;
    }

    public List<PharmacyDto> getPharmacyDtos() {
        return pharmacyDtos;
    }

    public void setPharmacyDtos(List<PharmacyDto> pharmacyDtos) {
        this.pharmacyDtos = pharmacyDtos;
    }
}
