package com.scnsoft.eldermark.entity.healthpartner;

import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "HealthPartnersMedClaim")
public class HealthPartnersMedClaim extends BaseHealthPartnersRecord {

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "is_duplicate")
    private Boolean isDuplicate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_observation_id", referencedColumnName = "id")
    private ProblemObservation problemObservation;

    @Column(name = "problem_observation_id", insertable = false, updatable = false)
    private Long problemObservationId;

    @Column(name = "claim_no")
    private String claimNo;

    @Column(name = "service_date")
    private Instant serviceDate;

    @Column(name = "diagnosis_code")
    private String diagnosisCode;

    @Column(name = "icd_version")
    private Integer icdVersion;

    @Column(name = "diagnosis_txt")
    private String diagnosisTxt;

    @Column(name = "physician_first_name")
    private String physicianFirstName;

    @Column(name = "physician_middle_name")
    private String physicianMiddleName;

    @Column(name = "physician_last_name")
    private String physicianLastName;

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Boolean getDuplicate() {
        return isDuplicate;
    }

    public void setDuplicate(Boolean duplicate) {
        isDuplicate = duplicate;
    }

    public ProblemObservation getProblemObservation() {
        return problemObservation;
    }

    public void setProblemObservation(ProblemObservation problemObservation) {
        this.problemObservation = problemObservation;
    }

    public Long getProblemObservationId() {
        return problemObservationId;
    }

    public void setProblemObservationId(Long problemObservationId) {
        this.problemObservationId = problemObservationId;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public Instant getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(Instant serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(String diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public Integer getIcdVersion() {
        return icdVersion;
    }

    public void setIcdVersion(Integer icdVersion) {
        this.icdVersion = icdVersion;
    }

    public String getDiagnosisTxt() {
        return diagnosisTxt;
    }

    public void setDiagnosisTxt(String diagnosisTxt) {
        this.diagnosisTxt = diagnosisTxt;
    }

    public String getPhysicianFirstName() {
        return physicianFirstName;
    }

    public void setPhysicianFirstName(String physicianFirstName) {
        this.physicianFirstName = physicianFirstName;
    }

    public String getPhysicianMiddleName() {
        return physicianMiddleName;
    }

    public void setPhysicianMiddleName(String physicianMiddleName) {
        this.physicianMiddleName = physicianMiddleName;
    }

    public String getPhysicianLastName() {
        return physicianLastName;
    }

    public void setPhysicianLastName(String physicianLastName) {
        this.physicianLastName = physicianLastName;
    }
}
