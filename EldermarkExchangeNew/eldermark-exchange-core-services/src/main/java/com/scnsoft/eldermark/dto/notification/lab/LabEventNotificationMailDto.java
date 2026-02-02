package com.scnsoft.eldermark.dto.notification.lab;

import com.scnsoft.eldermark.dto.notification.BaseNotificationMailDto;

public class LabEventNotificationMailDto extends BaseNotificationMailDto {

    private String labOrderUrl;

    public String getLabOrderUrl() {
        return labOrderUrl;
    }

    public void setLabOrderUrl(String labOrderUrl) {
        this.labOrderUrl = labOrderUrl;
    }
}
