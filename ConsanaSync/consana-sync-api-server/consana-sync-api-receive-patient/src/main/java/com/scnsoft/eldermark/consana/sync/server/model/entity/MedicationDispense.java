package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "MedicationDispense")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDispense extends LongLegacyIdAwareEntity{

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    @Column(name = "effective_time_low")
    private Instant effectiveTimeLow;

    @Column(name = "effective_time_high")
    private Instant effectiveTimeHigh;

    @Column(name = "repeat_number")
    private Integer repeatNumber;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="organization_id")
    private Organization organization;

}
