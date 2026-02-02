package com.scnsoft.eldermark.consana.sync.server.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ConsanaMedicationActionPlanLog")
@Data
@NoArgsConstructor
public class ConsanaMedicationActionPlanLog extends ConsanaBaseLog {

    @Column(name = "consana_plan_id")
    private String consanaPlanId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_log_id", nullable = false)
    private ConsanaPatientLog patientLog;

    public ConsanaMedicationActionPlanLog(String consanaPlanId, ConsanaPatientLog patientLog) {
        this.consanaPlanId = consanaPlanId;
        this.patientLog = patientLog;
    }
}
