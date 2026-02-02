package com.scnsoft.eldermark.entity.serviceplan;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ServicePlanEducationTaskNeed")
public class ServicePlanEducationNeed extends ServicePlanNeed {

    @Column(name = "activation_or_education_task", nullable = false)
    private String activationOrEducationTask;

    @Column(name = "target_completion_date", nullable = false)
    private Instant targetCompletionDate;

    @Column(name = "completion_date")
    private Instant completionDate;

    public String getActivationOrEducationTask() {
        return activationOrEducationTask;
    }

    public void setActivationOrEducationTask(String activationOrEducationTask) {
        this.activationOrEducationTask = activationOrEducationTask;
    }

    public Instant getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Instant targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Instant getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Instant completionDate) {
        this.completionDate = completionDate;
    }
}
