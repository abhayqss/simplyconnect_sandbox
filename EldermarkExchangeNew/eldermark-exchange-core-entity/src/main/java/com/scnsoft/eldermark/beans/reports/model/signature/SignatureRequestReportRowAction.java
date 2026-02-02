package com.scnsoft.eldermark.beans.reports.model.signature;

import com.scnsoft.eldermark.beans.projection.DocumentSignatureHistoryCommentsAware;

import java.time.Instant;

public class SignatureRequestReportRowAction {

    private String signatureStatusName;
    private String actorName;
    private String actorRoleName;
    private Instant actionDateTime;
    private DocumentSignatureHistoryCommentsAware comments;

    public String getSignatureStatusName() {
        return signatureStatusName;
    }

    public void setSignatureStatusName(String signatureStatusName) {
        this.signatureStatusName = signatureStatusName;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getActorRoleName() {
        return actorRoleName;
    }

    public void setActorRoleName(String actorRoleName) {
        this.actorRoleName = actorRoleName;
    }

    public Instant getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(Instant actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public DocumentSignatureHistoryCommentsAware getComments() {
        return comments;
    }

    public void setComments(DocumentSignatureHistoryCommentsAware comments) {
        this.comments = comments;
    }
}
