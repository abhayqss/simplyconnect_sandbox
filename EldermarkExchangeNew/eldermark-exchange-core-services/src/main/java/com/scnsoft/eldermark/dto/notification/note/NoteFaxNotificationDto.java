package com.scnsoft.eldermark.dto.notification.note;

import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;

public class NoteFaxNotificationDto extends BaseFaxNotificationDto {

    private NoteDetailsNotificationDto details;

    public NoteDetailsNotificationDto getDetails() {
        return details;
    }

    public void setDetails(NoteDetailsNotificationDto details) {
        this.details = details;
    }
}
