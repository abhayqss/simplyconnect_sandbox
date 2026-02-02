package com.scnsoft.eldermark.entity.inbound.marco;

public class MarcoDocumentEmailDto {
    private String toEmail;
    private boolean isAssigned;
    private String fileName;
    private String subject;
    private String recipientName;
    private String patientInitials;
    private String errorMessage;
    private String documentTitle;
    private String organzationName;

    private String mpiPatientId;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public boolean isAssigned() {
        return isAssigned;
    }

    public void setAssigned(boolean assigned) {
        isAssigned = assigned;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getPatientInitials() {
        return patientInitials;
    }

    public void setPatientInitials(String patientInitials) {
        this.patientInitials = patientInitials;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getOrganzationName() {
        return organzationName;
    }

    public void setOrganzationName(String organzationName) {
        this.organzationName = organzationName;
    }

    public String getMpiPatientId() {
        return mpiPatientId;
    }

    public void setMpiPatientId(String mpiPatientId) {
        this.mpiPatientId = mpiPatientId;
    }
}
