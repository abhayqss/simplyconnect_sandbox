package com.scnsoft.eldermark.beans.reports.model;

public class SPGeneral {

    private String communityName;

    private long numberOfPatients;

    private double averageNumberOfServicePlansPerClient;
    private double averageNumberOfNeedsPerClient;
    private double averageNumberOfGoalsPerClient;
    private double percentOfAccomplishedGoals;

    private long numberOfSpOpened;
    private long numberOfSpClosed;

    private long totalNumberOfNeeds;
    private long totalNumberOfGoals;

    private long numberOfAccomplishedGoals;

    public long getNumberOfPatients() {
        return numberOfPatients;
    }

    public void setNumberOfPatients(long numberOfPatients) {
        this.numberOfPatients = numberOfPatients;
    }

    public long getNumberOfSPopened() {
        return numberOfSpOpened;
    }

    public void setNumberOfSpOpened(long numberOfSpOpened) {
        this.numberOfSpOpened = numberOfSpOpened;
    }

    public long getNumberOfSpClosed() {
        return numberOfSpClosed;
    }

    public void setNumberOfSpClosed(long numberOfSpClosed) {
        this.numberOfSpClosed = numberOfSpClosed;
    }

    public long getTotalNumberOfNeeds() {
        return totalNumberOfNeeds;
    }

    public void setTotalNumberOfNeeds(long totalNumberOfNeeds) {
        this.totalNumberOfNeeds = totalNumberOfNeeds;
    }

    public long getTotalNumberOfGoals() {
        return totalNumberOfGoals;
    }

    public void setTotalNumberOfGoals(long totalNumberOfGoals) {
        this.totalNumberOfGoals = totalNumberOfGoals;
    }

    public long getNumberOfAccomplishedGoals() {
        return numberOfAccomplishedGoals;
    }

    public void setNumberOfAccomplishedGoals(long numberOfAccomplishedGoals) {
        this.numberOfAccomplishedGoals = numberOfAccomplishedGoals;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public double getAverageNumberOfServicePlansPerClient() {
        return averageNumberOfServicePlansPerClient;
    }

    public void setAverageNumberOfServicePlansPerClient(double averageNumberOfServicePlansPerClient) {
        this.averageNumberOfServicePlansPerClient = averageNumberOfServicePlansPerClient;
    }

    public double getAverageNumberOfNeedsPerClient() {
        return averageNumberOfNeedsPerClient;
    }

    public void setAverageNumberOfNeedsPerClient(double averageNumberOfNeedsPerClient) {
        this.averageNumberOfNeedsPerClient = averageNumberOfNeedsPerClient;
    }

    public double getAverageNumberOfGoalsPerClient() {
        return averageNumberOfGoalsPerClient;
    }

    public void setAverageNumberOfGoalsPerClient(double averageNumberOfGoalsPerClient) {
        this.averageNumberOfGoalsPerClient = averageNumberOfGoalsPerClient;
    }

    public long getNumberOfSpOpened() {
        return numberOfSpOpened;
    }

    public double getPercentOfAccomplishedGoals() {
        return percentOfAccomplishedGoals;
    }

    public void setPercentOfAccomplishedGoals(double percentOfAccomplishedGoals) {
        this.percentOfAccomplishedGoals = percentOfAccomplishedGoals;
    }
}
