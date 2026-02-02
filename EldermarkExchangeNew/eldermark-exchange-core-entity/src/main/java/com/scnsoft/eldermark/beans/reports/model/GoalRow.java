package com.scnsoft.eldermark.beans.reports.model;

public class GoalRow {

    private String goalName;

    private String resourceName;

    private Integer goalStatus;

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

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
}
