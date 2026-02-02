package com.scnsoft.eldermark.dto.notification;

public class AddedToCareTeamNotificationDto {

    private String fullName;
    private String toEmail;

    public AddedToCareTeamNotificationDto(String fullName, String toEmail) {
        this.fullName = fullName;
        this.toEmail = toEmail;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
