package com.scnsoft.eldermark.converter.lab;

import com.scnsoft.eldermark.dto.notification.lab.LabResearchTestResultReceivedNotificationMailDto;
import com.scnsoft.eldermark.entity.lab.LabResearchResultsEmployeeNotification;
import com.scnsoft.eldermark.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class LabResearchTestResultReceivedNotificationMailDtoConverter implements Converter<LabResearchResultsEmployeeNotification, LabResearchTestResultReceivedNotificationMailDto> {

    @Autowired
    private UrlService urlService;

    @Override
    public LabResearchTestResultReceivedNotificationMailDto convert(LabResearchResultsEmployeeNotification source) {
        var notificationDto = new LabResearchTestResultReceivedNotificationMailDto();
        notificationDto.setReceiverFullName(source.getEmployee().getFullName());
        notificationDto.setReceiverEmail(source.getDestination());
        notificationDto.setSubject("New lab results");
        notificationDto.setUrl(urlService.labResearchOrderBulkReviewUrl(source.getEmployee().getOrganizationId()));
        notificationDto.setTemplateFile("lab/LabResearchTestResultReceiveNotification.vm");
        return notificationDto;
    }
}
