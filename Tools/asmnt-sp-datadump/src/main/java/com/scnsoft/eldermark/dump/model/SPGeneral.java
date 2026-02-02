package com.scnsoft.eldermark.dump.model;

public class SPGeneral {

    long numberOfPatients;
    long numberOfSPopened;

    long numberOfSpClosed;

    long totalNumberOfNeeds;
    long totalNumberOfGoals;

    long numberOfAccomplishedGoals;

    public long getNumberOfPatients() {
        return numberOfPatients;
    }

    public void setNumberOfPatients(long numberOfPatients) {
        this.numberOfPatients = numberOfPatients;
    }

    public long getNumberOfSPopened() {
        return numberOfSPopened;
    }

    public void setNumberOfSpOpened(long numberOfSPopened) {
        this.numberOfSPopened = numberOfSPopened;
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
}
