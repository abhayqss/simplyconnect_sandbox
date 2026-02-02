package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.entity.event.incident.IncidentReportNotification;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class IncidentNotificationDtoConverter implements Converter<List<IncidentReportNotification>, IncidentNotificationsDto> {

    @Override
    public IncidentNotificationsDto convert(List<IncidentReportNotification> source) {
        if (CollectionUtils.isEmpty(source)) {
            return null;
        }
        var target = new IncidentNotificationsDto();
        for (IncidentReportNotification notification : source) {
            switch (notification.getDestination()) {
                case FAMILY:
                    target.setFamily(convert(notification, new IncidentPersonalNotificationDto()));
                    break;
                case FRIEND:
                    target.setFriend(convert(notification, new IncidentPersonalNotificationDto()));
                    break;
                case PHYSICIAN:
                    target.setPhysician(convert(notification, new IncidentRespondedNotificationDto()));
                    break;
                case ADULT_PROTECTIVE_SERVICES:
                    target.setAdultProtectiveServices(convert(notification, new IncidentNotificationDto()));
                    break;
                case CARE_MANAGER:
                    target.setCareManager(convert(notification, new IncidentPersonalNotificationDto()));
                    break;
                case OHIO_DEPARTMENT_OF_HEALTH:
                    target.setOhioHealthDepartment(convert(notification, new IncidentNotificationDto()));
                    break;
                case _9_1_1:
                    target.setEmergency(convert(notification, new IncidentNotificationDto()));
                    break;
                case POLICE:
                    target.setPolice(convert(notification, new IncidentNotificationDto()));
                    break;
                case OTHER:
                    target.setOther(convert(notification, new IncidentCommentedNotificationDto()));
                    break;
            }
        }
        return target;
    }

    private <T extends IncidentNotificationDto> T convert(IncidentReportNotification source, T target) {
        target.setId(source.getId());
        target.setDate(DateTimeUtils.toEpochMilli(source.getDatetime()));
        target.setByWhom(source.getByWhom());
        target.setFullName(source.getFullName());
        target.setPhone(source.getPhone());
        target.setResponse(source.getResponse());
        target.setResponseDate(DateTimeUtils.toEpochMilli(source.getResponseDatetime()));
        target.setComment(source.getComment());
        target.setNotified(source.getNotified());
        return target;
    }
}
