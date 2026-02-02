package com.scnsoft.eldermark.shared.carecoordination.serviceplan;

import java.util.Date;

public class GoalDto {
    private Long id;
    private String goal;
    private String barriers;
    private String interventionAction;
    private String resourceName;
    private Integer progress;
    private Date targetCompletionDate;
    private Date completionDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public String getBarriers() {
        return barriers;
    }

    public void setBarriers(String barriers) {
        this.barriers = barriers;
    }

    public String getInterventionAction() {
        return interventionAction;
    }

    public void setInterventionAction(String interventionAction) {
        this.interventionAction = interventionAction;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
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
