package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.NotificationsPreferencesDto;
import com.scnsoft.eldermark.entity.Responsibility;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationsPreferencesDtoConverter implements ListAndItemConverter<List<CareTeamMemberNotificationPreferences>, NotificationsPreferencesDto> {
    @Override
    public NotificationsPreferencesDto convert(List<CareTeamMemberNotificationPreferences> sourceList) {
        var target = new NotificationsPreferencesDto();
        var source = sourceList.get(0);
        target.setChannels(source.getResponsibility() != Responsibility.N && source.getResponsibility() != Responsibility.V ?
                sourceList.stream().map(careTeamMemberNotificationPreferences -> careTeamMemberNotificationPreferences.getNotificationType().name()).collect(Collectors.toList())
                : Collections.emptyList());
        target.setEventTypeId(source.getEventType().getId());
        target.setResponsibilityName(source.getResponsibility().name());
        return target;
    }
}
