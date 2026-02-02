package com.scnsoft.eldermark.consana.sync.server.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ConsanaProblemObservationLog")
@Data
@NoArgsConstructor
public class ConsanaProblemObservationLog extends ConsanaBaseLog {

    @Column(name = "consana_problem_id")
    private String consanaProblemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_log_id", nullable = false)
    private ConsanaPatientLog patientLog;

    public ConsanaProblemObservationLog(String consanaProblemId, ConsanaPatientLog patientLog) {
        this.consanaProblemId = consanaProblemId;
        this.patientLog = patientLog;
    }
}
