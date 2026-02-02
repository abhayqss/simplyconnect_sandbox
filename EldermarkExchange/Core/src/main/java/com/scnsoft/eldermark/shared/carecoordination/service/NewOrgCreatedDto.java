package com.scnsoft.eldermark.shared.carecoordination.service;

public class NewOrgCreatedDto {
    private String orgName;
    private String target;
    private String manageOrgLink;
    private String toEmail;


    public NewOrgCreatedDto() {

    }

    public NewOrgCreatedDto(String orgName, String target, String manageOrgLink, String toEmail) {
        this.orgName = orgName;
        this.target = target;
        this.manageOrgLink = manageOrgLink;
        this.toEmail = toEmail;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getManageOrgLink() {
        return manageOrgLink;
    }

    public void setManageOrgLink(String manageOrgLink) {
        this.manageOrgLink = manageOrgLink;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
