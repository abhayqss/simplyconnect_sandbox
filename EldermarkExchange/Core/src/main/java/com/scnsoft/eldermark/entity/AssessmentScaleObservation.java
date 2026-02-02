package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class AssessmentScaleObservation extends BasicEntity {

    @ManyToOne
    @JoinColumn
    private CcdCode code;

//    @Column(name = "code_system", length = 50)
//    private String codeSystem;

    @Lob
    @Column(name = "derivation_expr")
    private String derivationExpr;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @Column(name = "value")
    private Integer value;

    @ManyToMany
    @JoinTable(name = "AssessmentScaleObservation_InterpretationCode",
            joinColumns = @JoinColumn( name="observation_id"),
            inverseJoinColumns = @JoinColumn( name="interpretation_code_id"))
    private List<CcdCode> interpretationCodes;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "AssessmentScaleObservation_Author",
            joinColumns = @JoinColumn(name = "observation_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    private List<Author> authors;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "assessmentScaleObservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssessmentScaleSupportingObservation> assessmentScaleSupportingObservations;

    @ElementCollection
    @CollectionTable(name = "AssessmentScaleObservationRange",
            joinColumns = @JoinColumn(name = "observation_id"))
    @Column(name = "observation_range")
    private List<String> observationRanges;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

//    public String getCodeSystem() {
//        return codeSystem;
//    }
//
//    public void setCodeSystem(String codeSystem) {
//        this.codeSystem = codeSystem;
//    }

    public String getDerivationExpr() {
        return derivationExpr;
    }

    public void setDerivationExpr(String derivationExpr) {
        this.derivationExpr = derivationExpr;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public List<CcdCode> getInterpretationCodes() {
        return interpretationCodes;
    }

    public void setInterpretationCodes(List<CcdCode> interpretationCodes) {
        this.interpretationCodes = interpretationCodes;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    public List<AssessmentScaleSupportingObservation> getAssessmentScaleSupportingObservations() {
        return assessmentScaleSupportingObservations;
    }

    public void setAssessmentScaleSupportingObservations(List<AssessmentScaleSupportingObservation> assessmentScaleSupportingObservations) {
        this.assessmentScaleSupportingObservations = assessmentScaleSupportingObservations;
    }

    public List<String> getObservationRanges() {
        return observationRanges;
    }

    public void setObservationRanges(List<String> observationRanges) {
        this.observationRanges = observationRanges;
    }
}
