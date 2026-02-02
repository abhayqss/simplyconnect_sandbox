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
@Table(name = "AllergyObservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergyObservation extends LongLegacyIdAwareEntity {

    @Column(name = "effective_time_low")
    private Instant timeLow;

    @ManyToOne
    @JoinColumn(name = "product_code_id")
    private CcdCode productCode;

    @Column(name = "product_text")
    private String productText;

    @ManyToOne
    @JoinColumn(name = "allergy_type_code_id")
    private CcdCode adverseEventTypeCode;

    @Column(name = "allergy_type_text")
    private String adverseEventTypeText;

    @ManyToOne
    @JoinColumn(name = "observation_status_code_id")
    private CcdCode observationStatusCode;

    @Column(name = "consana_id")
    private String consanaId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "allergy_id", nullable = false)
    private Allergy allergy;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "AllergyObservation_ReactionObservation",
            joinColumns = @JoinColumn(name = "allergy_observation_id"),
            inverseJoinColumns = @JoinColumn(name = "reaction_observation_id"))
    private List<ReactionObservation> reactionObservations;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "severity_observation_id")
    private SeverityObservation severityObservation;
}
