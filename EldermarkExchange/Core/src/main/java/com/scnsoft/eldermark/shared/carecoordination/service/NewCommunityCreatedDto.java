package com.scnsoft.eldermark.shared.carecoordination.service;

public class NewCommunityCreatedDto {
    private String communityName;
    private String target;
    private String manageComLink;
    private String toEmail;

    public NewCommunityCreatedDto() {

    }

    public NewCommunityCreatedDto(String communityName, String target, String manageComLink, String toEmail) {
        this.communityName = communityName;
        this.target = target;
        this.manageComLink = manageComLink;
        this.toEmail = toEmail;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getManageComLink() {
        return manageComLink;
    }

    public void setManageComLink(String manageComLink) {
        this.manageComLink = manageComLink;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
