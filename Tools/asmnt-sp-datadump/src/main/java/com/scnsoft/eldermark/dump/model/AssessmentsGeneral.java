package com.scnsoft.eldermark.dump.model;

public class AssessmentsGeneral {

    private long gad7Completed;
    private long phq9Completed;
    private long comprehensiveCompleted;

    public long getGad7Completed() {
        return gad7Completed;
    }

    public void setGad7Completed(long gad7Completed) {
        this.gad7Completed = gad7Completed;
    }

    public long getPhq9Completed() {
        return phq9Completed;
    }

    public void setPhq9Completed(long phq9Completed) {
        this.phq9Completed = phq9Completed;
    }

    public long getComprehensiveCompleted() {
        return comprehensiveCompleted;
    }

    public void setComprehensiveCompleted(long comprehensiveCompleted) {
        this.comprehensiveCompleted = comprehensiveCompleted;
    }

    @Override
    public String toString() {
        return "AssessmentsGeneral{" +
                "gad7Completed=" + gad7Completed +
                ", phq9Completed=" + phq9Completed +
                ", comprehensiveCompleted=" + comprehensiveCompleted +
                '}';
    }
}
