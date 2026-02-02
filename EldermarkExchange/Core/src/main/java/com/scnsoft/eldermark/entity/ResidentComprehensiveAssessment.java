package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="ResidentComprehensiveAssessment")
public class ResidentComprehensiveAssessment implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "resident_id")
    private Long residentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", insertable = false, updatable = false)
    private CareCoordinationResident resident;

    @Column(name = "primary_care_physician_first_name")
    private String primaryCarePhysicianFirstName;

    @Column(name = "primary_care_physician_last_name")
    private String primaryCarePhysicianLastName;

    @OneToOne
    @JoinColumn(name = "resident_assessment_result_id")
    private ResidentAssessmentResult residentAssessmentResult;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
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

    public ResidentAssessmentResult getResidentAssessmentResult() {
        return residentAssessmentResult;
    }

    public void setResidentAssessmentResult(ResidentAssessmentResult residentAssessmentResult) {
        this.residentAssessmentResult = residentAssessmentResult;
    }

    public CareCoordinationResident getResident() {
        return resident;
    }

    public void setResident(CareCoordinationResident resident) {
        this.resident = resident;
    }
}
