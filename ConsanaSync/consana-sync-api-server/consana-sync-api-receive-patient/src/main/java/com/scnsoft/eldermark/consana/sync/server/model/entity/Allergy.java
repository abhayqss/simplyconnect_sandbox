package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Allergy")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Allergy extends LongLegacyIdAwareEntity {

    @Column(name = "effective_time_low")
    private Instant timeLow;

    @OneToMany(mappedBy = "allergy")
    private Set<AllergyObservation> allergyObservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;
}
