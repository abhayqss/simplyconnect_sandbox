package com.scnsoft.eldermark.dto.notification.MAP;

import com.scnsoft.eldermark.service.DirectAttachment;

import java.util.List;

public class MAPNotificationSecureMailDto extends MAPNotificationMailDto {
    private DirectAttachment mapPdf;

    public DirectAttachment getMapPdf() {
        return mapPdf;
    }

    public void setMapPdf(DirectAttachment mapPdf) {
        this.mapPdf = mapPdf;
    }
}
