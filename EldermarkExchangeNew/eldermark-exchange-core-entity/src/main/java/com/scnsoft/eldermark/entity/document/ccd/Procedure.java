package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;

@Entity
@Table(name = "ResidentProcedure")
@NamedQueries({
        @NamedQuery(name = "procedure.listByResidentId", query = "select p from Procedure p " +
                "LEFT join fetch p.activities pp LEFT join fetch p.acts pa LEFT join fetch p.observations po " +
                "LEFT join fetch pp.procedureType  LEFT join fetch pa.procedureType  LEFT join fetch po.procedureType " +
                "LEFT join fetch pp.bodySiteCodes  LEFT join fetch pp.specimenIds  LEFT join fetch pp.performers " +
                "LEFT join fetch pp.productInstances LEFT join fetch pp.serviceDeliveryLocations LEFT join fetch pp.encounterIds " +
                "LEFT join fetch pp.instructions LEFT join fetch pp.indications where p.client.id = :residentId")
})
public class Procedure extends LegacyIdAwareEntity {
    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Procedure_ActivityProcedure",
            joinColumns = @JoinColumn( name="procedure_id"),
            inverseJoinColumns = @JoinColumn( name="procedure_activity_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"procedure_id", "procedure_activity_id"}))
    private Set<ProcedureActivity> activities;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Procedure_ActivityAct",
            joinColumns = @JoinColumn( name="procedure_id"),
            inverseJoinColumns = @JoinColumn( name="procedure_act_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"procedure_id", "procedure_act_id"}))
    private Set<ProcedureActivity> acts;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Procedure_ActivityObservation",
            joinColumns = @JoinColumn( name="procedure_id"),
            inverseJoinColumns = @JoinColumn( name="procedure_observation_id") ,
            uniqueConstraints = @UniqueConstraint(columnNames = {"procedure_id", "procedure_observation_id"}))
    private Set<ProcedureActivity> observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Client client;

    public Set<ProcedureActivity> getActivities() {
        return activities;
    }

    public void setActivities(Set<ProcedureActivity> activities) {
        this.activities = activities;
    }

    public Set<ProcedureActivity> getObservations() {
        return observations;
    }

    public void setObservations(Set<ProcedureActivity> observations) {
        this.observations = observations;
    }

    public Set<ProcedureActivity> getActs() {
        return acts;
    }

    public void setActs(Set<ProcedureActivity> acts) {
        this.acts = acts;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
