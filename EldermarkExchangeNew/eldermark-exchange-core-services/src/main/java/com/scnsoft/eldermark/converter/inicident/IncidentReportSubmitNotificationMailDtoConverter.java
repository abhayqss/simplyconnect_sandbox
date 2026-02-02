package com.scnsoft.eldermark.converter.inicident;

import com.scnsoft.eldermark.dto.notification.lab.IncidentReportSubmitNotificationMailDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportSubmitNotification;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.util.ClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class IncidentReportSubmitNotificationMailDtoConverter implements Converter<IncidentReportSubmitNotification, IncidentReportSubmitNotificationMailDto> {

    @Autowired
    private UrlService urlService;

    @Override
    public IncidentReportSubmitNotificationMailDto convert(IncidentReportSubmitNotification source) {
        var target = new IncidentReportSubmitNotificationMailDto();
        target.setReceiverFullName(source.getEmployee().getFullName());
        target.setReceiverEmail(source.getDestination());
        target.setInitials(ClientUtils.getInitials(source.getIncidentReport().getFirstName(), source.getIncidentReport().getLastName(), ""));
        target.setSubject("New incident report for " + target.getInitials());
        target.setUrl(urlService.incidentReportDetailsUrl(source.getIncidentReport()));
        target.setTemplateFile("incident/IncidentReportSubmitNotification.vm");
        return target;
    }
}
