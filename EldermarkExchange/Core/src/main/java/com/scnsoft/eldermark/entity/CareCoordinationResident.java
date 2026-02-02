package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.marketplace.InsurancePlan;
import com.scnsoft.eldermark.entity.phr.InNetworkInsurance;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Indexed
@Table(name="Resident")
@NamedQueries({
        @NamedQuery(name = "ccResident.selectCreatedById", query = "Select r.createdById from CareCoordinationResident r WHERE r.id = :residentId"),
        @NamedQuery(name = "ccResident.selectFacilityId", query = "Select r.facility.id from CareCoordinationResident r WHERE r.id = :residentId "),
        @NamedQuery(name = "ccResident.selectId", query = "Select r.id from CareCoordinationResident r " +
                "WHERE r.createdById in (:employeeIds) and r.databaseId = :databaseId")
})
public class CareCoordinationResident extends StringLegacyTableAwareEntity implements Serializable {
    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Organization facility;

    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "age")
    private Integer age;

    @ManyToOne
    @JoinColumn
    private CcdCode gender;

    @ManyToOne
    @JoinColumn(name = "marital_status_id")
    private CcdCode maritalStatus;

    @Field
    @Column(name = "ssn", length = 11)
    private String socialSecurity;

    @Column(name = "ssn_last_four_digits", length = 4)
    private String ssnLastFourDigits;


    @Column(name = "opt_out")
    private Boolean isOptOut;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;

    @OneToOne
    @JoinColumn(name = "data_enterer_id")
    private DataEnterer dataEnterer;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "resident", fetch = FetchType.LAZY)
    private List<ResidentCareTeamMember> residentCareTeamMembers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<Event> events;

    @Field
    @Column(name = "first_name")
    private String firstName;

    @Field
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "preferred_name")
    private String preferredName;

    @Column(name = "created_by_id")
    private Long createdById;

    // (DE)ACTIVATED in Care Coordination
    @Column(name="active", nullable = false)
    private Boolean active;

    @Column(name = "last_updated")
    private Date lastUpdated;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "hash_key", updatable = false, insertable = false)
    private String hashKey;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "survivingResident")
    private Set<MpiMergedResidents> secondaryResidents;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "mergedResident")
    private Set<MpiMergedResidents> mainResidents;

    @ManyToOne
    @JoinColumn(name = "in_network_insurance_id")
    private InNetworkInsurance inNetworkInsurance;

    @ManyToOne
    @JoinColumn(name = "insurance_plan_id")
    private InsurancePlan insurancePlan;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "resident")
    private List<ResidentDevice> devices;

    @Column(name = "medicare_number")
    private String medicareNumber;

    @Column(name = "medicaid_number")
    private String medicaidNumber;

    @Column(name = "group_number")
    private String groupNumber;

    @Column(name = "member_number")
    private String memberNumber;

    @Column(name = "retained")
    private Boolean retained;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "intake_date")
    private Date intakeDate;

    @Column(name = "referral_source")
    private String referralSource;

    @Column(name = "current_pharmacy_name")
    private String currentPharmacyName;

    @Column(name = "discharge_date")
    private Date dischargeDate;

    @Column(name = "admit_date")
    private Date admitDate;

    @Column(name = "death_date")
    private Date deathDate;

    @Column(name = "death_indicator")
    private Boolean deathIndicator;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "insurance_plan")
    private String insurancePlanName;

    @Column(name = "dental_insurance", columnDefinition = "text")
    private String dentalInsurance;

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH }, mappedBy = "resident")
    private Set<MPI> mpi;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "resident")
    private List<ResidentHealthPlan> healthPlans;

    @OneToMany(fetch = FetchType.LAZY, targetEntity = ResidentComprehensiveAssessment.class, mappedBy = "resident")
    private List<ResidentComprehensiveAssessment> residentComprehensiveAssessments;

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public Organization getFacility() {
        return facility;
    }

    public void setFacility(Organization facility) {
        this.facility = facility;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public CcdCode getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(CcdCode maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSocialSecurity() {
        return socialSecurity;
    }

    public void setSocialSecurity(String socialSecurity) {
        this.socialSecurity = socialSecurity;
    }

    public String getSsnLastFourDigits() {
        return ssnLastFourDigits;
    }

    public void setSsnLastFourDigits(String ssnLastFourDigits) {
        this.ssnLastFourDigits = ssnLastFourDigits;
    }

    public Boolean getIsOptOut() {
        return isOptOut;
    }

    public void setIsOptOut(Boolean isOptOut) {
        this.isOptOut = isOptOut;
    }

    public List<ResidentCareTeamMember> getResidentCareTeamMembers() {
        return residentCareTeamMembers;
    }

    public void setResidentCareTeamMembers(List<ResidentCareTeamMember> residentCareTeamMembers) {
        this.residentCareTeamMembers = residentCareTeamMembers;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public DataEnterer getDataEnterer() {
        return dataEnterer;
    }

    public void setDataEnterer(DataEnterer dataEnterer) {
        this.dataEnterer = dataEnterer;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getFullName() {
        String result = StringUtils.isNotEmpty(getFirstName()) ? getFirstName() : "";
        if (StringUtils.isNotEmpty(getLastName())) {
            result = StringUtils.isNotEmpty(result) ? result + " " + getLastName() : getLastName();
        }
        return result;
    }

    public Set<MpiMergedResidents> getSecondaryResidents() {
        return secondaryResidents;
    }

    public void setSecondaryResidents(Set<MpiMergedResidents> secondaryResidents) {
        this.secondaryResidents = secondaryResidents;
    }

    public Set<MpiMergedResidents> getMainResidents() {
        return mainResidents;
    }

    public void setMainResidents(Set<MpiMergedResidents> mainResidents) {
        this.mainResidents = mainResidents;
    }

    public InNetworkInsurance getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(InNetworkInsurance inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public InsurancePlan getInsurancePlan() {
        return insurancePlan;
    }

    public void setInsurancePlan(InsurancePlan insurancePlan) {
        this.insurancePlan = insurancePlan;
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

    public Boolean getRetained() {
        return retained;
    }

    public void setRetained(Boolean retained) {
        this.retained = retained;
    }

    /*public String getPrimaryCarePhysician() {
        return primaryCarePhysician;
    }

    public void setPrimaryCarePhysician(String primaryCarePhysician) {
        this.primaryCarePhysician = primaryCarePhysician;
    }*/

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

    public void setCurrentPharmacyName(String currentPharmacyName) {
        this.currentPharmacyName = currentPharmacyName;
    }

    public List<ResidentDevice> getDevices() {
        return devices;
    }

    public void setDevices(List<ResidentDevice> devices) {
        this.devices = devices;
    }

    public Date getDischargeDate() {
        return dischargeDate;
    }

    public void setDischargeDate(Date dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    public Date getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(Date admitDate) {
        this.admitDate = admitDate;
    }

    public Date getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(Date deathDate) {
        this.deathDate = deathDate;
    }

    public Boolean getDeathIndicator() {
        return deathIndicator;
    }

    public void setDeathIndicator(Boolean deathIndicator) {
        this.deathIndicator = deathIndicator;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInsurancePlanName() {
        return insurancePlanName;
    }

    public void setInsurancePlanName(String insurancePlanName) {
        this.insurancePlanName = insurancePlanName;
    }

    public Set<MPI> getMpi() {
        return mpi;
    }

    public void setMpi(Set<MPI> mpi) {
        this.mpi = mpi;
    }

    public Boolean getOptOut() {
        return isOptOut;
    }

    public void setOptOut(Boolean optOut) {
        isOptOut = optOut;
    }

    public String getDentalInsurance() {
        return dentalInsurance;
    }

    public void setDentalInsurance(String dentalInsurance) {
        this.dentalInsurance = dentalInsurance;
    }

    public List<ResidentHealthPlan> getHealthPlans() {
        return healthPlans;
    }

    public void setHealthPlans(List<ResidentHealthPlan> healthPlans) {
        this.healthPlans = healthPlans;
    }

    public List<ResidentComprehensiveAssessment> getResidentComprehensiveAssessments() {
        return residentComprehensiveAssessments;
    }

    public void setResidentComprehensiveAssessments(List<ResidentComprehensiveAssessment> residentComprehensiveAssessments) {
        this.residentComprehensiveAssessments = residentComprehensiveAssessments;
    }
}
