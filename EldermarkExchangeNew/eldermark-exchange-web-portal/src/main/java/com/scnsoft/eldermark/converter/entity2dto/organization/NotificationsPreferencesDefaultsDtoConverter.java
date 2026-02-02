package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.NotificationsPreferencesDto;
import com.scnsoft.eldermark.entity.EventTypeCareTeamRoleXref;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.Responsibility;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationsPreferencesDefaultsDtoConverter implements ListAndItemConverter<EventTypeCareTeamRoleXref, NotificationsPreferencesDto> {

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Override
    public NotificationsPreferencesDto convert(EventTypeCareTeamRoleXref source) {
        var target = new NotificationsPreferencesDto();
        target.setEventTypeId(source.getEventType().getId());
        target.setResponsibilityName(source.getResponsibility().name());

        if (source.getResponsibility() != Responsibility.N && source.getResponsibility() != Responsibility.V) {
            target.setChannels(careTeamMemberService.defaultNotificationChannels()
                    .stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()));
        } else {
            target.setChannels(Collections.emptyList());
        }
        target.setChannels(source.getResponsibility() != Responsibility.N && source.getResponsibility() != Responsibility.V ? List.of(NotificationType.EMAIL.name(), NotificationType.PUSH_NOTIFICATION.name()) : Collections.emptyList());
        target.setCanEdit(source.getResponsibility().isChangeable());
        return target;
    }
}
