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
@Table(name = "ProblemObservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemObservation extends LongLegacyIdAwareEntity {

    @Column(name = "age_observation_unit")
    private String ageObservationUnit;

    @Column(name = "age_observation_value")
    private Integer ageObservationValue;

    @Column(name = "effective_time_high")
    private Instant problemDateTimeHigh;

    @Column(name = "effective_time_low")
    private Instant problemDateTimeLow;

    @Lob
    @Column(name = "problem_name")
    private String problemName;

    @Lob
    @Column(name = "problem_status_text")
    private String problemStatusText;

    @Column(name = "negation_ind")
    private Boolean negationInd;

    @ManyToOne
    @JoinColumn(name = "problem_value_code_id")
    private CcdCode problemCode;

    @ManyToOne
    @JoinColumn(name = "problem_status_code_id")
    private CcdCode problemStatusCode;

    @Column(name = "problem_value_code")
    private String problemIcdCode;

    @Column(name = "problem_value_code_set")
    private String problemIcdCodeSet;

    @Column(name = "is_manual", nullable = false)
    private Boolean manual;

    @Column(name = "recorded_date")
    private Instant recordedDate;

    @Column(name = "onset_date")
    private Instant onsetDate;

    @Lob
    @Column(name = "comments")
    private String comments;

    @Column(name = "consana_id")
    private String consanaId;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "ProblemObservationTranslation", joinColumns = @JoinColumn(name = "problem_observation_id"), inverseJoinColumns = @JoinColumn(name = "translation_code_id"))
    private Set<CcdCode> translations;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;
}
