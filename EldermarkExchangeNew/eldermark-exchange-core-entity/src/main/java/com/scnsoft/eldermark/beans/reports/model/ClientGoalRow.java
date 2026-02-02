package com.scnsoft.eldermark.beans.reports.model;

import java.time.Instant;

public class ClientGoalRow {

    private String resourceName;

    private Instant completionDate;

    private Instant targetCompletionDate;

    private Integer goalStatus;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Integer getGoalStatus() {
        return goalStatus;
    }

    public void setGoalStatus(Integer goalStatus) {
        this.goalStatus = goalStatus;
    }

    public Instant getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Instant completionDate) {
        this.completionDate = completionDate;
    }

    public Instant getTargetCompletionDate() {
        return targetCompletionDate;
    }

    public void setTargetCompletionDate(Instant targetCompletionDate) {
        this.targetCompletionDate = targetCompletionDate;
    }
}
