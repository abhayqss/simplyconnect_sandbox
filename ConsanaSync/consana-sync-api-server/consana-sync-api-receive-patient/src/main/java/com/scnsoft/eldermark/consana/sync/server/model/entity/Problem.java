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
@Table(name = "Problem")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Problem extends LongLegacyIdAwareEntity {

    @Column(name = "effective_time_low")
    private Instant timeLow;

    @Column(name = "effective_time_high")
    private Instant timeHigh;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProblemObservation> problemObservations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;
}
