package com.scnsoft.eldermark.beans;

public class InvitationDto {

    private String toEmail;

    private String targetFullname;
    private String targetUsername;
    private String targetCompanyId;
    private String creator;
    private String buttonUrl;

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getTargetFullname() {
        return targetFullname;
    }

    public void setTargetFullname(String targetFullname) {
        this.targetFullname = targetFullname;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getTargetCompanyId() {
        return targetCompanyId;
    }

    public void setTargetCompanyId(String targetCompanyId) {
        this.targetCompanyId = targetCompanyId;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getButtonUrl() {
        return buttonUrl;
    }

    public void setButtonUrl(String buttonUrl) {
        this.buttonUrl = buttonUrl;
    }
}
