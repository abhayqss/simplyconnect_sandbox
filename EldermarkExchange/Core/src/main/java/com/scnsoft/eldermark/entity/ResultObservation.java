package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class ResultObservation extends BasicEntity {
    @ManyToOne
    @JoinColumn(name="result_type_code_id")
    private CcdCode resultTypeCode;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @Column(name = "effective_time")
    private Date effectiveTime;

    @Column(name = "result_text")
    private String text;

    @Column(name = "result_value")
    private Integer value;

    @Column(length = 50, name ="result_value_unit")
    private String valueUnit;

    @ManyToOne
    @JoinColumn(name="method_code_id")
    private CcdCode methodCode;

    @ManyToOne
    @JoinColumn(name="site_code_id")
    private CcdCode targetSiteCode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="author_id")
    private Author author;

    @ManyToMany
    @JoinTable(name = "ResultObservationInterpretationCode",
            joinColumns = @JoinColumn( name="result_observation_id"),
            inverseJoinColumns = @JoinColumn( name="interpretation_code_id"))
    private List<CcdCode> interpretationCodes;

    @ElementCollection
    @CollectionTable(name="ResultObservationRange",
            joinColumns=@JoinColumn(name="result_observation_id"))
    @Column(name="result_range")
    private List<String> referenceRanges;

    @ManyToOne
    @JoinTable(name = "Result_ResultObservation",
            joinColumns = @JoinColumn( name="result_observation_id"),
            inverseJoinColumns = @JoinColumn( name="result_id"))
    private Result result;

    public CcdCode getResultTypeCode() {
        return resultTypeCode;
    }

    public void setResultTypeCode(CcdCode resultTypeCode) {
        this.resultTypeCode = resultTypeCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getValueUnit() {
        return valueUnit;
    }

    public void setValueUnit(String valueUnit) {
        this.valueUnit = valueUnit;
    }

    public CcdCode getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(CcdCode methodCode) {
        this.methodCode = methodCode;
    }

    public CcdCode getTargetSiteCode() {
        return targetSiteCode;
    }

    public void setTargetSiteCode(CcdCode targetSiteCode) {
        this.targetSiteCode = targetSiteCode;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public List<CcdCode> getInterpretationCodes() {
        return interpretationCodes;
    }

    public void setInterpretationCodes(List<CcdCode> interpretationCodes) {
        this.interpretationCodes = interpretationCodes;
    }

    public List<String> getReferenceRanges() {
        return referenceRanges;
    }

    public void setReferenceRanges(List<String> referenceRanges) {
        this.referenceRanges = referenceRanges;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
