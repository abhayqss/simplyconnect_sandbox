package com.scnsoft.eldermark.entity.lab;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.Document;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "LabResearchOrder")
public class LabResearchOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "requisition_number")
    private String requisitionNumber;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LabResearchOrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_for_testing")
    private LabResearchOrderReason reason;

    @Column(name = "clinic")
    private String clinic;

    @Column(name = "clinic_address")
    private String clinicAddress;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Client client;

    @Column(name = "resident_id", nullable = false, insertable = false, updatable = false)
    private Long clientId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Employee createdBy;

    @Column(name = "in_network_insurance")
    private String inNetworkInsurance;

    @Column(name = "policy_number")
    private String policyNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy_holder")
    private LabOrderPolicyHolder policyHolder;

    @Column(name = "policy_holder_name")
    private String policyHolderName;

    @Column(name = "policy_holder_date_of_birth", columnDefinition = "datetime2")
    private LocalDate policyHolderDOB;

    @Column(name = "is_covid19")
    private boolean isCovid19;

    @Column(name = "provider_first_name")
    private String providerFirstName;

    @Column(name = "provider_last_name")
    private String providerLastName;

    @ManyToOne
    @JoinColumn(name = "gender_id")
    private CcdCode gender;

    @ManyToOne
    @JoinColumn(name = "race_id")
    private CcdCode race;

    @Column(name = "birth_date", columnDefinition = "datetime2")
    private LocalDate birthDate;

    @ManyToMany
    @JoinTable(name = "LabResearchOrder_SpecimenType",
            joinColumns = @JoinColumn(name = "lab_research_order_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "specimen_type_id", nullable = false))
    private List<SpecimenType> specimenTypes;

    @Column(name = "collector_name")
    private String collectorsName;

    @Column(name = "site")
    private String site;

    @Column(name = "specimen_date")
    private Instant specimenDate;

    @Column(name = "order_date")
    private Instant orderDate;

    @ElementCollection
    @CollectionTable(name = "LabResearchOrder_Icd10Code",
            joinColumns = @JoinColumn(name = "lab_research_order_id"))
    @Column(name = "code")
    private List<String> icd10Codes;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "labResearchOrder")
    private List<Document> documents;

    @OneToOne(mappedBy = "labOrder")
    private LabResearchOrderORU orderORU;

    @OneToMany(mappedBy = "labOrder")
    private List<LabResearchOrderObservationResult> observationResults;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequisitionNumber() {
        return requisitionNumber;
    }

    public void setRequisitionNumber(String requisitionNumber) {
        this.requisitionNumber = requisitionNumber;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public LabResearchOrderStatus getStatus() {
        return status;
    }

    public void setStatus(LabResearchOrderStatus status) {
        this.status = status;
    }

    public LabResearchOrderReason getReason() {
        return reason;
    }

    public void setReason(LabResearchOrderReason reason) {
        this.reason = reason;
    }

    public String getClinic() {
        return clinic;
    }

    public void setClinic(String clinic) {
        this.clinic = clinic;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public String getInNetworkInsurance() {
        return inNetworkInsurance;
    }

    public void setInNetworkInsurance(String inNetworkInsurance) {
        this.inNetworkInsurance = inNetworkInsurance;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public LabOrderPolicyHolder getPolicyHolder() {
        return policyHolder;
    }

    public void setPolicyHolder(LabOrderPolicyHolder policyHolder) {
        this.policyHolder = policyHolder;
    }


    public String getCollectorsName() {
        return collectorsName;
    }

    public void setCollectorsName(String collectorsName) {
        this.collectorsName = collectorsName;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public void setPolicyHolderName(String policyHolderName) {
        this.policyHolderName = policyHolderName;
    }

    public LocalDate getPolicyHolderDOB() {
        return policyHolderDOB;
    }

    public void setPolicyHolderDOB(LocalDate policyHolderDOB) {
        this.policyHolderDOB = policyHolderDOB;
    }

    public boolean isCovid19() {
        return isCovid19;
    }

    public void setIsCovid19(boolean covid19) {
        isCovid19 = covid19;
    }

    public String getProviderFirstName() {
        return providerFirstName;
    }

    public void setProviderFirstName(String providerFirstName) {
        this.providerFirstName = providerFirstName;
    }

    public String getProviderLastName() {
        return providerLastName;
    }

    public void setProviderLastName(String providerLastName) {
        this.providerLastName = providerLastName;
    }

    public CcdCode getGender() {
        return gender;
    }

    public void setGender(CcdCode gender) {
        this.gender = gender;
    }

    public CcdCode getRace() {
        return race;
    }

    public void setRace(CcdCode race) {
        this.race = race;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public List<SpecimenType> getSpecimenTypes() {
        return specimenTypes;
    }

    public void setSpecimenTypes(List<SpecimenType> specimenTypes) {
        this.specimenTypes = specimenTypes;
    }

    public Instant getSpecimenDate() {
        return specimenDate;
    }

    public void setSpecimenDate(Instant specimenDate) {
        this.specimenDate = specimenDate;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
    }

    public List<String> getIcd10Codes() {
        return icd10Codes;
    }

    public void setIcd10Codes(List<String> icd10Codes) {
        this.icd10Codes = icd10Codes;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public LabResearchOrderORU getOrderORU() {
        return orderORU;
    }

    public void setOrderORU(LabResearchOrderORU orderORU) {
        this.orderORU = orderORU;
    }

    public List<LabResearchOrderObservationResult> getObservationResults() {
        return observationResults;
    }

    public void setObservationResults(List<LabResearchOrderObservationResult> observationResults) {
        this.observationResults = observationResults;
    }
}
