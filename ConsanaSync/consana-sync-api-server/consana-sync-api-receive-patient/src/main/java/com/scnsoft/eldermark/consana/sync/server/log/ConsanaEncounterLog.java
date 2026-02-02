package com.scnsoft.eldermark.consana.sync.server.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ConsanaEncounterLog")
@Data
@NoArgsConstructor
public class ConsanaEncounterLog extends ConsanaBaseLog {

    @Column(name = "consana_encounter_id")
    private String consanaEncounterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_log_id", nullable = false)
    private ConsanaPatientLog patientLog;

    public ConsanaEncounterLog(String consanaEncounterId, ConsanaPatientLog patientLog) {
        this.consanaEncounterId = consanaEncounterId;
        this.patientLog = patientLog;
    }
}
