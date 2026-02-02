package com.scnsoft.eldermark.dto.notification.lab;

import com.scnsoft.eldermark.dto.notification.BaseFaxNotificationDto;

import java.util.List;

public class LabEventFaxNotificationDto extends BaseFaxNotificationDto {

    private List<byte[]> labDocuments;

    public List<byte[]> getLabDocuments() {
        return labDocuments;
    }

    public void setLabDocuments(List<byte[]> labDocuments) {
        this.labDocuments = labDocuments;
    }
}
