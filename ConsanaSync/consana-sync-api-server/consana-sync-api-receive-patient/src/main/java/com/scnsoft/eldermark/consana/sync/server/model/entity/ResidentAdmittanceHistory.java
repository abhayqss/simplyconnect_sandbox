package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "ResidentAdmittanceHistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResidentAdmittanceHistory extends BasicEntity {

    @Column(name = "admit_date")
    private Instant admitDate;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Resident resident;

    @Column(name = "organization_id")
    private Long organizationId;

    @Column(name = "legacy_id", nullable = false)
    private long legacyId;

}
