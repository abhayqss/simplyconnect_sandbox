package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class Result extends LegacyIdAwareEntity {
    @Column(length = 50, name = "class_code")
    private String classCode;

    @ManyToOne
    @JoinColumn(name="code_id")
    private CcdCode code;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Result_ResultObservation",
            joinColumns = @JoinColumn( name="result_id"),
            inverseJoinColumns = @JoinColumn( name="result_observation_id") )
    private List<ResultObservation> observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<ResultObservation> getObservations() {
        return observations;
    }

    public void setObservations(List<ResultObservation> observations) {
        this.observations = observations;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }
}
