package com.scnsoft.eldermark.consana.sync.server.log;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ConsanaAllergyObservationLog")
@Data
@NoArgsConstructor
public class ConsanaAllergyObservationLog extends ConsanaBaseLog {

    @Column(name = "consana_allergy_id")
    private String consanaAllergyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_log_id", nullable = false)
    private ConsanaPatientLog patientLog;

    public ConsanaAllergyObservationLog(String consanaAllergyId, ConsanaPatientLog patientLog) {
        this.consanaAllergyId = consanaAllergyId;
        this.patientLog = patientLog;
    }
}
