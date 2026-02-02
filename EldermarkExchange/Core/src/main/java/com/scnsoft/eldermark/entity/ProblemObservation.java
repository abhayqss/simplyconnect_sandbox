package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * A problem is a clinical statement that a clinician has noted. In health care it is a condition that requires monitoring or
 * diagnostic, therapeutic, or educational action. It also refers to any unmet or partially met basic human need.
 */
@Entity
@DynamicInsert
@DynamicUpdate
public class ProblemObservation extends LegacyIdAwareEntity {

    /**
     * The {@code negationInd} attribute, if true, specifies that the problem indicated was observed to not have occurred
     * (which is subtly but importantly different from having not been observed). {@code negationInd='true'} is an acceptable
     * way to make a clinical assertion that something did not occur, for example, "no diabetes".
     */
    @Column(name = "negation_ind")
    private Boolean negationInd;

    @ManyToOne
    @JoinColumn(name="problem_type_code_id")
    private CcdCode problemType;

    @Lob
    @Column(name = "problem_name")
    private String problemName;

    @Column(name = "effective_time_low")
    private Date problemDateTimeLow;

    /**
     * Problem resolution date.<br/>
     * In CCD resolution date SHALL be recorded in the high element of the effectiveTime element when known (CONF:9052)<br/>
     * The existence of an high element within a problem does indicate that the problem has been resolved (CONF:9053)
     */
    @Column(name = "effective_time_high")
    private Date problemDateTimeHigh;

    @ManyToOne
    @JoinColumn(name="problem_value_code_id")
    private CcdCode problemCode;

    @Column(name = "age_observation_value")
    private Integer ageObservationValue;

    @Column(length = 50, name = "age_observation_unit")
    private String ageObservationUnit;

    @Lob
    @Column(name = "problem_status_text")
    private String problemStatusText;

    @ManyToOne
    @JoinColumn(name="problem_status_code_id")
    private CcdCode problemStatusCode;

    @Lob
    @Column(name = "health_status_observation_text")
    private String healthStatusObservationText;

    /**
     * {@code Observation value} may contain one or more {@code Observation value translation}, to represent equivalent values from other code systems.
     */
    @ManyToMany
    @JoinTable(name = "ProblemObservationTranslation",
            joinColumns = @JoinColumn( name="problem_observation_id"),
            inverseJoinColumns = @JoinColumn( name="translation_code_id"))
    private Set<CcdCode> translations;

    @Column(name = "problem_value_code")
    private String problemIcdCode;

    @Column(name = "problem_value_code_set")
    private String problemIcdCodeSet;

    @ManyToOne
    @JoinColumn(name="health_status_code_id")
    private CcdCode healthStatusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "is_manual", nullable = false)
    private Boolean manual;

    @Column(name = "is_primary")
    private Boolean primary;

    @Column(name = "recorded_date")
    private Date recordedDate;

    @Column(name = "onset_date")
    private Date onsetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by")
    private Employee recordedBy;

    @Lob
    @Column(name = "comments")
    private String comments;

    @Column(name = "consana_id")
    private String consanaId;

    public Boolean getNegationInd() {
        return negationInd;
    }

    public void setNegationInd(Boolean negationInd) {
        this.negationInd = negationInd;
    }

    public CcdCode getProblemType() {
        return problemType;
    }

    public void setProblemType(CcdCode problemType) {
        this.problemType = problemType;
    }

    public String getProblemName() {
        return problemName;
    }

    public void setProblemName(String problemName) {
        this.problemName = problemName;
    }

    public Date getProblemDateTimeLow() {
        return problemDateTimeLow;
    }

    public void setProblemDateTimeLow(Date problemDateTimeLow) {
        this.problemDateTimeLow = problemDateTimeLow;
    }

    public Date getProblemDateTimeHigh() {
        return problemDateTimeHigh;
    }

    public void setProblemDateTimeHigh(Date problemDateTimeHigh) {
        this.problemDateTimeHigh = problemDateTimeHigh;
    }

    public CcdCode getProblemCode() {
        return problemCode;
    }

    public void setProblemCode(CcdCode problemCode) {
        this.problemCode = problemCode;
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

    public String getProblemStatusText() {
        return problemStatusText;
    }

    public void setProblemStatusText(String problemStatusText) {
        this.problemStatusText = problemStatusText;
    }

    public CcdCode getProblemStatusCode() {
        return problemStatusCode;
    }

    public void setProblemStatusCode(CcdCode problemStatusCode) {
        this.problemStatusCode = problemStatusCode;
    }

    public String getHealthStatusObservationText() {
        return healthStatusObservationText;
    }

    public void setHealthStatusObservationText(String healthStatusObservationText) {
        this.healthStatusObservationText = healthStatusObservationText;
    }

    public CcdCode getHealthStatusCode() {
        return healthStatusCode;
    }

    public void setHealthStatusCode(CcdCode healthStatusCode) {
        this.healthStatusCode = healthStatusCode;
    }

    public Problem getProblem() {
        return problem;
    }

    public void setProblem(Problem problem) {
        this.problem = problem;
    }

    public Set<CcdCode> getTranslations() {
        return translations;
    }

    public void setTranslations(Set<CcdCode> translations) {
        this.translations = translations;
    }

    public String getProblemIcdCode() {
        return problemIcdCode;
    }

    public void setProblemIcdCode(String problemIcdCode) {
        this.problemIcdCode = problemIcdCode;
    }

    public String getProblemIcdCodeSet() {
        return problemIcdCodeSet;
    }

    public void setProblemIcdCodeSet(String problemIcdCodeSet) {
        this.problemIcdCodeSet = problemIcdCodeSet;
    }

    public Boolean getManual() {
        return manual;
    }

    public void setManual(Boolean manual) {
        this.manual = manual;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public Date getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Date recordedDate) {
        this.recordedDate = recordedDate;
    }

    public Date getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(Date onsetDate) {
        this.onsetDate = onsetDate;
    }

    public Employee getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(Employee recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getConsanaId() {
        return consanaId;
    }

    public void setConsanaId(String consanaId) {
        this.consanaId = consanaId;
    }
}
