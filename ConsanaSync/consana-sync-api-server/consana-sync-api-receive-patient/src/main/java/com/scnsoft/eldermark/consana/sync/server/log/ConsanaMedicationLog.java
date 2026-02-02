package com.scnsoft.eldermark.consana.sync.server.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ConsanaMedicationLog")
@Data
@NoArgsConstructor
public class ConsanaMedicationLog extends ConsanaBaseLog {

    @Column(name = "consana_medication_id")
    private String consanaMedicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_log_id", nullable = false)
    private ConsanaPatientLog patientLog;

    public ConsanaMedicationLog(String consanaMedicationId, ConsanaPatientLog patientLog) {
        this.consanaMedicationId = consanaMedicationId;
        this.patientLog = patientLog;
    }
}
