package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQueries({
//        @NamedQuery(name = "vitalSignObservation.listResidentVitalSigns", )
//        @NamedQuery(name = "vitalSignObservation.latestResidentVitalSigns", query = "SELECT q FROM " +
//                "(SELECT ccd.code, vso.effectiveTime, vso.value, vso.unit, row_number() " +
//                "OVER (PARTITION BY ccd.code ORDER BY vso.effectiveTime DESC) AS rn " +
//                "FROM VitalSignObservation vso INNER JOIN vso.vitalSign vs " +
//                "INNER JOIN vso.resultTypeCode ccd " +
//                "WHERE vs.resident.id IN :residentIds) AS q WHERE rn = 1")
        @NamedQuery(name = "vitalSignObservation. ", query = "select ccd.code, " +
                "min(vso.effectiveTime) from VitalSignObservation vso INNER JOIN vso.vitalSign vs " +
                "INNER JOIN vso.resultTypeCode ccd WHERE vs.resident.id IN :residentIds GROUP BY ccd.code"),
        @NamedQuery(name = "vitalSignObservation.getEarliestResidentVitalSign", query = "SELECT vso " +
                "FROM VitalSignObservation vso INNER JOIN vso.vitalSign vs INNER JOIN vso.resultTypeCode ccd " +
                "WHERE vs.resident.id IN :residentIds AND ccd.code = :ccdCode ORDER BY vso.effectiveTime ASC")
})
public class VitalSignObservation extends StringLegacyIdAwareEntity {
    @ManyToOne
    @JoinColumn(name="result_type_code_id")
    private CcdCode resultTypeCode;

    @Column(name = "effective_time")
    private Date effectiveTime;

    private Double value;

    @Column(length = 50)
    private String unit;

    @ManyToOne
    @JoinColumn(name = "interpretation_code_id")
    private CcdCode interpretationCode;

    @ManyToOne
    @JoinColumn(name = "method_code_id")
    private CcdCode methodCode;

    @ManyToOne
    @JoinColumn(name = "target_site_code_id")
    private CcdCode targetSiteCode;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="author_id")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "vital_sign_id", nullable = false)
    private VitalSign vitalSign;

    public CcdCode getResultTypeCode() {
        return resultTypeCode;
    }

    public void setResultTypeCode(CcdCode resultTypeCode) {
        this.resultTypeCode = resultTypeCode;
    }

    /**
     * Represents the biologically relevant time (e.g. time the specimen was obtained from the patient)
     */
    public Date getEffectiveTime() {
        return effectiveTime;
    }

    public void setEffectiveTime(Date effectiveTime) {
        this.effectiveTime = effectiveTime;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * The interpretation code may be present to provide an interpretation of the vital signs measure (e.g., High, Normal, Low, et cetera)
     */
    public CcdCode getInterpretationCode() {
        return interpretationCode;
    }

    public void setInterpretationCode(CcdCode interpretationCode) {
        this.interpretationCode = interpretationCode;
    }

    /**
     * The method code element may be present to indicate the method used to obtain the measure.
     * Note that method used is distinct from, but possibly related to the target site
     */
    public CcdCode getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(CcdCode methodCode) {
        this.methodCode = methodCode;
    }

    /**
     * The target site of the measure may be identified in the targetSiteCode element (e.g., Left arm [blood pressure], oral [temperature], et cetera)
     */
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

    public VitalSign getVitalSign() {
        return vitalSign;
    }

    public void setVitalSign(VitalSign vitalSign) {
        this.vitalSign = vitalSign;
    }
}
