package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 32))
public class ReactionObservation extends StringLegacyTableAwareEntity {
    @Column(name = "effective_time_low")
    private Date timeLow;

    @Column(name = "effective_time_high")
    private Date timeHigh;

    @ManyToOne
    @JoinColumn(name="reaction_code_id")
    private CcdCode reactionCode;

    @Column(name = "reaction_text")
    private String reactionText;

    /**
     * The gravity of the reaction, in terms of its actual or potential impact on the patient.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable( name = "ReactionObservation_SeverityObservation",
            joinColumns = @JoinColumn( name="reaction_observation_id"),
            inverseJoinColumns = @JoinColumn( name="severity_observation_id") )
    private List<SeverityObservation> severityObservations;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable( name = "ReactionObservation_Medication",
            joinColumns = @JoinColumn( name="reaction_observation_id"),
            inverseJoinColumns = @JoinColumn( name="medication_id") )
    private List<Medication> medications;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable( name = "ReactionObservation_ProcedureActivity",
            joinColumns = @JoinColumn( name="reaction_observation_id"),
            inverseJoinColumns = @JoinColumn( name="procedure_activity_id") )
    private List<ProcedureActivity> procedureActivities;


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

    public String getReactionText() {
        return reactionText;
    }

    public void setReactionText(String reactionText) {
        this.reactionText = reactionText;
    }

    public List<SeverityObservation> getSeverityObservations() {
        return severityObservations;
    }

    public void setSeverityObservations(List<SeverityObservation> severityObservations) {
        this.severityObservations = severityObservations;
    }

    public List<Medication> getMedications() {
        return medications;
    }

    public void setMedications(List<Medication> medications) {
        this.medications = medications;
    }

    public List<ProcedureActivity> getProcedureActivities() {
        return procedureActivities;
    }

    public void setProcedureActivities(List<ProcedureActivity> procedureActivities) {
        this.procedureActivities = procedureActivities;
    }

    public CcdCode getReactionCode() {
        return reactionCode;
    }

    public void setReactionCode(CcdCode reactionCode) {
        this.reactionCode = reactionCode;
    }
}
