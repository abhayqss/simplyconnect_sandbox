package com.scnsoft.eldermark.entity.document.ccd;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.document.CcdCode;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ClientProblem")
public class ClientProblem {

    @Id
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", insertable = false, updatable = false)
    private Client client;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", insertable = false, updatable = false)
    private Employee recordedBy;

    @Column(name = "problem")
    private String problem;

    @Column(name = "code")
    private String problemCode;

    @Column(name = "code_set")
    private String problemCodeSet;

    @Column(name = "problem_type")
    private String type;

    @Column(name = "identified_date")
    private Instant identifiedDate;

    @Column(name = "stopped_date")
    private Instant stoppedDate;

    @Column(name = "onset_date")
    private Instant onsetDate;

    @Column(name = "recorded_date")
    private Instant recordedDate;

    @Column(name = "is_primary")
    private Boolean primary;

    @Column(name = "comments")
    private String comments;

    @Column(name = "age_observation_value")
    private Integer ageObservationValue;

    @Column(name = "age_observation_unit")
    private String ageObservationUnit;

    @ManyToOne
    @JoinColumn(name = "problem_status_code_id")
    private CcdCode problemStatusCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ClientProblemStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id", insertable = false, updatable = false)
    private ProblemObservation problemObservation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Employee getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Employee recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getProblem() {
        return problem;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }

    public String getProblemCode() {
        return problemCode;
    }

    public void setProblemCode(String problemCode) {
        this.problemCode = problemCode;
    }

    public String getProblemCodeSet() {
        return problemCodeSet;
    }

    public void setProblemCodeSet(String problemCodeSet) {
        this.problemCodeSet = problemCodeSet;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getIdentifiedDate() {
        return identifiedDate;
    }

    public void setIdentifiedDate(Instant identifiedDate) {
        this.identifiedDate = identifiedDate;
    }

    public Instant getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Instant stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

    public Instant getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(Instant onsetDate) {
        this.onsetDate = onsetDate;
    }

    public Instant getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Instant recordedDate) {
        this.recordedDate = recordedDate;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getAgeObservationValue() {
        return ageObservationValue;
    }

    public void setAgeObservationValue(Integer ageObservationValue) {
        this.ageObservationValue = ageObservationValue;
    }

    public String getAgeObservationUnit() {
        return ageObservationUnit;
    }

    public void setAgeObservationUnit(String ageObservationUnit) {
        this.ageObservationUnit = ageObservationUnit;
    }

    public CcdCode getProblemStatusCode() {
        return problemStatusCode;
    }

    public void setProblemStatusCode(CcdCode problemStatusCode) {
        this.problemStatusCode = problemStatusCode;
    }

    public ClientProblemStatus getStatus() {
        return status;
    }

    public void setStatus(ClientProblemStatus status) {
        this.status = status;
    }

    public ProblemObservation getProblemObservation() {
        return problemObservation;
    }

    public void setProblemObservation(ProblemObservation problemObservation) {
        this.problemObservation = problemObservation;
    }
}
