package com.scnsoft.eldermark.beans.reports.model;

import java.time.Instant;

public class Covid19ReportRow extends Report {

    private Instant specimenDate;

    private String communityName;

    private String reason;

    private String clientName;

    private Instant resultDate;

    private String result;

    private Instant notifiedDate;

    private String comment;

    public Instant getSpecimenDate() {
        return specimenDate;
    }

    public void setSpecimenDate(Instant specimenDate) {
        this.specimenDate = specimenDate;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Instant getResultDate() {
        return resultDate;
    }

    public void setResultDate(Instant resultDate) {
        this.resultDate = resultDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Instant getNotifiedDate() {
        return notifiedDate;
    }

    public void setNotifiedDate(Instant notifiedDate) {
        this.notifiedDate = notifiedDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
