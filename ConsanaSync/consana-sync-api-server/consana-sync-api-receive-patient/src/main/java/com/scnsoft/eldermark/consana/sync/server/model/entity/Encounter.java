package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Encounter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Encounter extends LongLegacyIdAwareEntity {

    @Column(name = "encounter_type_text")
    private String encounterTypeText;

    @Column(name = "effective_time")
    private Instant effectiveTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "problem_observation_id")
    private ProblemObservation problemObservation;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Encounter_DeliveryLocation", joinColumns = @JoinColumn(name = "encounter_id"), inverseJoinColumns = @JoinColumn(name = "location_id"))
    private List<DeliveryLocation> deliveryLocations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Encounter_Indication", joinColumns = @JoinColumn(name = "encounter_id"), inverseJoinColumns = @JoinColumn(name = "indication_id"))
    private List<Indication> indications;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "encounter")
    private List<EncounterPerformer> encounterPerformers;

    @Column(name = "consana_id")
    private String consanaId;
}