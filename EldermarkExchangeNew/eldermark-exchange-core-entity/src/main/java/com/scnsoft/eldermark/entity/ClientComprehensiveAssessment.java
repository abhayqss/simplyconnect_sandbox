package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;

import javax.persistence.*;

@Entity
@Table(name="ResidentComprehensiveAssessment")
public class ClientComprehensiveAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "resident_id")
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", insertable = false, updatable = false)
    private Client client;

    @Column(name = "primary_care_physician_first_name")
    private String primaryCarePhysicianFirstName;

    @Column(name = "primary_care_physician_last_name")
    private String primaryCarePhysicianLastName;

    @Column(name = "pharmacy_name")
    private String pharmacyName;

    @OneToOne
    @JoinColumn(name = "resident_assessment_result_id")
    private ClientAssessmentResult clientAssessmentResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientAssessmentResult getClientAssessmentResult() {
        return clientAssessmentResult;
    }

    public void setClientAssessmentResult(ClientAssessmentResult clientAssessmentResult) {
        this.clientAssessmentResult = clientAssessmentResult;
    }
}
