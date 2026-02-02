package com.scnsoft.eldermark.entity.document.ccd;

public enum ClientProblemStatus {
    ACTIVE("Active"),
    RESOLVED("Resolved"),
    OTHER("Other");

    ClientProblemStatus(String title) {
        this.title = title;
    }

    private String title;

    public String getTitle() {
        return title;
    }
}
