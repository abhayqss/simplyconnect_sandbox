package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Language extends LongLegacyIdAwareEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @ManyToOne
    @JoinColumn(name = "ability_mode_id")
    private CcdCode abilityMode;

    @ManyToOne
    @JoinColumn(name = "ability_proficiency_id")
    private CcdCode abilityProficiency;

    @Column(name = "preference_ind")
    private Boolean preferenceInd;
}
