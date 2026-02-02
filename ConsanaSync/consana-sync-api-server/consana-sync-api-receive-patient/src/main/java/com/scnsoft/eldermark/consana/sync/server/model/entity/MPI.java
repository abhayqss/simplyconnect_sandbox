package com.scnsoft.eldermark.consana.sync.server.model.entity;


import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "MPI")
@Data
public class MPI {

    @Id
    @Column(name = "registry_patient_id")
    @GeneratedValue(generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = "uuid")
    private String registryPatientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "assigning_authority_universal")
    private String assigningAuthorityUniversal;

    @Column(name = "assigning_authority_universal_type")
    private String assigningAuthorityUniversalType;
}