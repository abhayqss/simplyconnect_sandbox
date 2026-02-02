package com.scnsoft.eldermark.dto.notification;

import java.time.Instant;

public class DemoRequestSubmittedNotificationDto {

    private String receiverEmail;

    private Instant date;

    private String authorOrganizationName;

    private String authorName;

    private String authorPhoneNumber;

    private String demoTitle;

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getAuthorOrganizationName() {
        return authorOrganizationName;
    }

    public void setAuthorOrganizationName(String authorOrganizationName) {
        this.authorOrganizationName = authorOrganizationName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorPhoneNumber() {
        return authorPhoneNumber;
    }

    public void setAuthorPhoneNumber(String authorPhoneNumber) {
        this.authorPhoneNumber = authorPhoneNumber;
    }

    public String getDemoTitle() {
        return demoTitle;
    }

    public void setDemoTitle(String demoTitle) {
        this.demoTitle = demoTitle;
    }
}
