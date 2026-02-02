package com.scnsoft.eldermark.dto.notification.lab;

import com.scnsoft.eldermark.service.DirectAttachment;

import java.util.List;

public class LabEventNotificationSecureMailDto extends LabEventNotificationMailDto {
    private List<DirectAttachment> attachments;

    public List<DirectAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DirectAttachment> attachments) {
        this.attachments = attachments;
    }
}
