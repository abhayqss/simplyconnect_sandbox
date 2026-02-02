package com.scnsoft.eldermark.dto.notifications.inapp;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;

public class InAppNotificationDto extends IdentifiedTitledEntityDto {

    private InAppNotificationType type;
    private InAppNotificationBody body;

    public InAppNotificationDto() {
    }

    public InAppNotificationDto(Long id, String title, InAppNotificationType type, InAppNotificationBody body) {
        super(id, title);
        this.type = type;
        this.body = body;
    }

    public InAppNotificationType getType() {
        return type;
    }

    public void setType(InAppNotificationType type) {
        this.type = type;
    }

    public InAppNotificationBody getBody() {
        return body;
    }

    public void setBody(InAppNotificationBody body) {
        this.body = body;
    }
}
