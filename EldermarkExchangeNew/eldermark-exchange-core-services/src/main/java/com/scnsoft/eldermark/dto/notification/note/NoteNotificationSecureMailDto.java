package com.scnsoft.eldermark.dto.notification.note;

public class NoteNotificationSecureMailDto extends NoteNotificationMailDto {

    private NoteDetailsNotificationDto details;

    public NoteDetailsNotificationDto getDetails() {
        return details;
    }

    public void setDetails(NoteDetailsNotificationDto details) {
        this.details = details;
    }
}
