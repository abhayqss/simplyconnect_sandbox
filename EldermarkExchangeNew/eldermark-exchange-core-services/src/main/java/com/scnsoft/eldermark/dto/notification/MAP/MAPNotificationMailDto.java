package com.scnsoft.eldermark.dto.notification.MAP;

import com.scnsoft.eldermark.dto.notification.BaseNotificationMailDto;

public class MAPNotificationMailDto extends BaseNotificationMailDto {

    private String mapUrl;

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }
}
