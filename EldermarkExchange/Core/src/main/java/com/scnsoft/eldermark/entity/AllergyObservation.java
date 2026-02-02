package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * This entity is intended to represent a clinical statement that represents that an allergy or adverse reaction exists or does not exist.
 * The agent that is the cause of the allergy or adverse reaction is represented as a manufactured material participant playing entity
 * (see {@code productCode} and {@code productText}) in the allergy observation.
 */
@Entity
@Table(name = "AllergyObservation")
public class AllergyObservation extends LegacyIdAwareEntity {

    /**
     * This is the time stamp for the biological onset of the allergy.
     * It can be of any precision (for example, just a year if a specific month and date was not reported)
     */
    @Column(name = "effective_time_low")
    private Date timeLow;

    @Column(name = "effective_time_high")
    private Date timeHigh;

    /**
     * This specifies the allergy type:
     *  - allergy to a substance (cat hair) aka environmental allergy
     *  - allergy to a drug (penicillin)    aka drug allergy
     *  - allergy to a food (chocolate)     aka food allergy
     */
    @ManyToOne
    @JoinColumn(name="allergy_type_code_id")
    private CcdCode adverseEventTypeCode;

    @Column(name = "allergy_type_text")
    private String adverseEventTypeText;

    /**
     * product = playing entity = agent <br/>
     * The agent indicates the entity that is the cause of the allergy or adverse reaction.
     * While the agent is often implicit in the alert observation (e.g. “allergy to penicillin”),
     * it should also be asserted explicitly as an entity.
     */
    @ManyToOne
    @JoinColumn(name="product_code_id")
    private CcdCode productCode;

    @Column(name = "product_text")
    private String productText;

    /**
     * The status of the allergy indicating whether it is active, no longer active (inactive), or is an historic allergy (resolved).
     * Value should be selected from ValueSet 2.16.840.1.113883.3.88.12.80.68 (HITSP Problem Status) (CONF:7322)
     */
    @ManyToOne
    @JoinColumn(name="observation_status_code_id")
    private CcdCode observationStatusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allergy_id", nullable = false)
    private Allergy allergy;

    /**
     * An undesired symptom, finding, etc., due to an administered or exposed substance.
     * A reaction can be defined with respect to its severity, and can have been treated by one or more interventions.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable( name = "AllergyObservation_ReactionObservation",
            joinColumns = @JoinColumn( name="allergy_observation_id"),
            inverseJoinColumns = @JoinColumn( name="reaction_observation_id") )
    private Set<ReactionObservation> reactionObservations;

    /**
     * The gravity of the allergy, in terms of its actual or potential impact on the patient.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="severity_observation_id")
    private SeverityObservation severityObservation;

    @Column(name = "consana_id")
    private String consanaId;

    public Date getTimeLow() {
        return timeLow;
    }

    public void setTimeLow(Date timeLow) {
        this.timeLow = timeLow;
    }

    public Date getTimeHigh() {
        return timeHigh;
    }

    public void setTimeHigh(Date timeHigh) {
        this.timeHigh = timeHigh;
    }

    public CcdCode getAdverseEventTypeCode() {
        return adverseEventTypeCode;
    }

    public void setAdverseEventTypeCode(CcdCode adverseEventTypeCode) {
        this.adverseEventTypeCode = adverseEventTypeCode;
    }

    public CcdCode getProductCode() {
        return productCode;
    }

    public void setProductCode(CcdCode productCode) {
        this.productCode = productCode;
    }

    public CcdCode getObservationStatusCode() {
        return observationStatusCode;
    }

    public void setObservationStatusCode(CcdCode observationStatusCode) {
        this.observationStatusCode = observationStatusCode;
    }

    public String getAdverseEventTypeText() {
        return adverseEventTypeText;
    }

    public void setAdverseEventTypeText(String adverseEventTypeText) {
        this.adverseEventTypeText = adverseEventTypeText;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public Allergy getAllergy() {
        return allergy;
    }

    public void setAllergy(Allergy allergy) {
        this.allergy = allergy;
    }

    public Set<ReactionObservation> getReactionObservations() {
        return reactionObservations;
    }

    public void setReactionObservations(Set<ReactionObservation> reactionObservations) {
        this.reactionObservations = reactionObservations;
    }

    public SeverityObservation getSeverityObservation() {
        return severityObservation;
    }

    public void setSeverityObservation(SeverityObservation severityObservation) {
        this.severityObservation = severityObservation;
    }

    public String getConsanaId() {
        return consanaId;
    }

    public void setConsanaId(String consanaId) {
        this.consanaId = consanaId;
    }
}
