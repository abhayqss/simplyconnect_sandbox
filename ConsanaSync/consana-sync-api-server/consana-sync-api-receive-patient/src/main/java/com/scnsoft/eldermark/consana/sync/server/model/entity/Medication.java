package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "Medication")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medication extends LongLegacyIdAwareEntity {

    @Column(name = "dose_quantity")
    private Integer doseQuantity;

    @Column(length = 50, name = "dose_units")
    private String doseUnits;

    @Lob
    @Column(name = "free_text_sig")
    private String freeTextSig;

    @Column(name = "medication_started")
    private Instant medicationStarted;

    @Column(name = "medication_stopped")
    private Instant medicationStopped;

    @Column(name = "repeat_number")
    private Integer repeatNumber;

    @Column(length = 50, name = "status_code")
    private String statusCode;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="medication_information_id")
    private MedicationInformation medicationInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resident_id", nullable = false)
    private Resident resident;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="person_id")
    private Person person;

    @ManyToOne
    @JoinColumn(name = "route_code_id")
    private CcdCode route;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "Medication_MedicationDispense",
            joinColumns = @JoinColumn( name="medication_id"),
            inverseJoinColumns = @JoinColumn( name="medication_dispense_id") )
    private List<MedicationDispense> medicationDispenses;

    @Column(name = "consana_id")
    private String consanaId;

}
