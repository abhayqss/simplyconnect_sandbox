package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ReactionObservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactionObservation extends StringLegacyTableAwareEntity {

    @Column(name = "effective_time_low")
    private Instant timeLow;

    @ManyToOne
    @JoinColumn(name = "reaction_code_id")
    private CcdCode reactionCode;

    @Column(name = "reaction_text")
    private String reactionText;
}
