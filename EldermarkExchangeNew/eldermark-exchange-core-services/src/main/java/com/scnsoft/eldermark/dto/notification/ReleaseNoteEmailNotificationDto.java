package com.scnsoft.eldermark.dto.notification;

import java.util.List;

public class ReleaseNoteEmailNotificationDto {

    private String receiverFullName;
    private String receiverEmail;
    private String url;
    private List<String> whatsNews;
    private List<String> bugFixes;

    public String getReceiverFullName() {
        return receiverFullName;
    }

    public void setReceiverFullName(String receiverFullName) {
        this.receiverFullName = receiverFullName;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public List<String> getWhatsNews() {
        return whatsNews;
    }

    public void setWhatsNews(List<String> whatsNews) {
        this.whatsNews = whatsNews;
    }

    public List<String> getBugFixes() {
        return bugFixes;
    }

    public void setBugFixes(List<String> bugFixes) {
        this.bugFixes = bugFixes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
