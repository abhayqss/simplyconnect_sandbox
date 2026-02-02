package com.scnsoft.eldermark.dto.notification.lab;

import com.scnsoft.eldermark.dto.notification.BaseNotificationMailDto;

public class LabResearchTestResultReceivedNotificationMailDto extends BaseNotificationMailDto {
    private String subject;
    private String url;
    private String templateFile;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }
}
