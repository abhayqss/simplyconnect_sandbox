package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.IncidentNotificationDto;
import com.scnsoft.eldermark.dto.IncidentNotificationsDto;
import com.scnsoft.eldermark.entity.IncidentReportNotificationDestination;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportNotification;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class IncidentNotificationsEntityListConverter {

    public List<IncidentReportNotification> convertToList(IncidentNotificationsDto source, IncidentReport report) {
        if (source == null) {
            return Collections.emptyList();
        }
        var family = convert(source.getFamily(), IncidentReportNotificationDestination.FAMILY, report);
        var friend = convert(source.getFriend(), IncidentReportNotificationDestination.FRIEND, report);
        var physician = convert(source.getPhysician(), IncidentReportNotificationDestination.PHYSICIAN, report);
        var adultProtectiveServices = convert(source.getAdultProtectiveServices(), IncidentReportNotificationDestination.ADULT_PROTECTIVE_SERVICES, report);
        var careManager = convert(source.getCareManager(), IncidentReportNotificationDestination.CARE_MANAGER, report);
        var ohioHealthDepartment = convert(source.getOhioHealthDepartment(), IncidentReportNotificationDestination.OHIO_DEPARTMENT_OF_HEALTH, report);
        var emergency = convert(source.getEmergency(), IncidentReportNotificationDestination._9_1_1, report);
        var police = convert(source.getPolice(), IncidentReportNotificationDestination.POLICE, report);
        var other = convert(source.getOther(), IncidentReportNotificationDestination.OTHER, report);
        return Stream.of(family, friend, physician, adultProtectiveServices, careManager, ohioHealthDepartment, emergency, police, other)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private IncidentReportNotification convert(IncidentNotificationDto source, IncidentReportNotificationDestination destination, IncidentReport report) {
        if (source == null) {
            return null;
        }
        var target = new IncidentReportNotification();
        target.setId(source.getId());
        target.setDestination(destination);
        target.setDatetime(DateTimeUtils.toInstant(source.getDate()));
        target.setByWhom(source.getByWhom());
        target.setFullName(source.getFullName());
        target.setPhone(source.getPhone());
        target.setResponse(source.getResponse());
        target.setResponseDatetime(DateTimeUtils.toInstant(source.getResponseDate()));
        target.setComment(source.getComment());
        target.setNotified(source.getNotified());
        target.setIncidentReport(report);
        return target;
    }


}