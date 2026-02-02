package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SeverityObservation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeverityObservation extends LongLegacyTableAwareEntity {

    @ManyToOne
    @JoinColumn(name = "severity_code_id")
    private CcdCode severityCode;

    @Column(name = "severity_text")
    private String severityText;
}