package com.scnsoft.eldermark.entity.basic;

public enum ProblemStatus {
    ACTIVE("active"), RESOLVED("resolved"), OTHER("other");

    private String statusType;

    ProblemStatus(String statusType) {
        this.statusType = statusType;
    }

    public String getStatusName() {
        return statusType;
    }
}
