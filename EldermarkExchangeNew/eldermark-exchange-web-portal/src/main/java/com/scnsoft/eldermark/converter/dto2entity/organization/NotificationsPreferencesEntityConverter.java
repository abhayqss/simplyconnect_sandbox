package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dao.EventTypeDao;
import com.scnsoft.eldermark.dto.NotificationsPreferencesDto;
import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.Responsibility;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationsPreferencesEntityConverter  implements ListAndItemConverter<NotificationsPreferencesDto, List<CareTeamMemberNotificationPreferences>> {

    @Autowired
    private EventTypeDao eventTypeDao;

    @Override
    public List<CareTeamMemberNotificationPreferences> convert(NotificationsPreferencesDto source) {
        List<CareTeamMemberNotificationPreferences> result = new ArrayList<>();
        //in case channels is empty - create for all channels
        var channels = CollectionUtils.isNotEmpty(source.getChannels()) ? source.getChannels().stream().map(s -> NotificationType.valueOf(s)).collect(Collectors.toList()) : Arrays.asList(NotificationType.values());
        channels.forEach(notificationType -> {
            CareTeamMemberNotificationPreferences target = new CareTeamMemberNotificationPreferences();
            target.setNotificationType(notificationType);
            target.setEventType(eventTypeDao.findById(source.getEventTypeId()).orElseThrow());
            target.setResponsibility(Responsibility.valueOf(source.getResponsibilityName()));
            result.add(target);
        });
        return result;
    }
}
