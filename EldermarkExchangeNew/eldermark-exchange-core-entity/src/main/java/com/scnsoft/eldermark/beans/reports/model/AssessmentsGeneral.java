package com.scnsoft.eldermark.beans.reports.model;

public class AssessmentsGeneral {

    private String communityName;
    private long gad7Completed;
    private long phq9Completed;
    private long comprehensiveCompleted;
    private long norCalComprehensiveCompleted;

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

    public long getNorCalComprehensiveCompleted() {
        return norCalComprehensiveCompleted;
    }

    public void setNorCalComprehensiveCompleted(long norCalComprehensiveCompleted) {
        this.norCalComprehensiveCompleted = norCalComprehensiveCompleted;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}
