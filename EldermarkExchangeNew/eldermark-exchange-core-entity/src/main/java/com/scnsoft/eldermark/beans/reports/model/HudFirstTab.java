package com.scnsoft.eldermark.beans.reports.model;

import java.time.Instant;

public class HudFirstTab {

    private String agency = "HUD";

    private String reportName = "HUD-PRL Report";

    private String program;

    private Instant reportingPeriodStartDate;

    private Instant reportingPeriodEndDate;

    private String dunsNumber;

    private String grantNumber = "FAJG84FU";

    private String comments;

    private String approverName;

    private String approverPhone;

    private String approverEmail;

    private Instant submitDate;

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public Instant getReportingPeriodStartDate() {
        return reportingPeriodStartDate;
    }

    public void setReportingPeriodStartDate(Instant reportingPeriodStartDate) {
        this.reportingPeriodStartDate = reportingPeriodStartDate;
    }

    public Instant getReportingPeriodEndDate() {
        return reportingPeriodEndDate;
    }

    public void setReportingPeriodEndDate(Instant reportingPeriodEndDate) {
        this.reportingPeriodEndDate = reportingPeriodEndDate;
    }

    public String getDunsNumber() {
        return dunsNumber;
    }

    public void setDunsNumber(String dunsNumber) {
        this.dunsNumber = dunsNumber;
    }

    public String getGrantNumber() {
        return grantNumber;
    }

    public void setGrantNumber(String grantNumber) {
        this.grantNumber = grantNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getApproverPhone() {
        return approverPhone;
    }

    public void setApproverPhone(String approverPhone) {
        this.approverPhone = approverPhone;
    }

    public String getApproverEmail() {
        return approverEmail;
    }

    public void setApproverEmail(String approverEmail) {
        this.approverEmail = approverEmail;
    }

    public Instant getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Instant submitDate) {
        this.submitDate = submitDate;
    }
}
