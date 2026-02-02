package com.scnsoft.eldermark.entity.serviceplan;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ServicePlanEducationTaskNeed")
public class ServicePlanEducationNeed extends ServicePlanNeed {

    @Column(name = "activation_or_education_task", nullable = false)
    private String activationOrEducationTask;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "target_completion_date", nullable = false)
    private Date targetCompletionDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "completion_date")
    private Date completionDate;

    public String getActivationOrEducationTask() {
        return activationOrEducationTask;
    }

    public void setActivationOrEducationTask(String activationOrEducationTask) {
        this.activationOrEducationTask = activationOrEducationTask;
    }

    public Date getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Date targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }
}
