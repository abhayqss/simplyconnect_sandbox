package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.notifications.inapp.InAppNotificationDto;
import com.scnsoft.eldermark.dto.notifications.inapp.InAppNotificationType;
import com.scnsoft.eldermark.dto.notifications.inapp.ReleaseNotificationBody;
import com.scnsoft.eldermark.entity.ReleaseNote;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ReleaseNoteInAppNotificationDtoConverter implements Converter<ReleaseNote, InAppNotificationDto> {

    @Override
    public InAppNotificationDto convert(ReleaseNote source) {
        var target = new InAppNotificationDto();
        target.setId(source.getId());
        target.setTitle("New features have been released");
        target.setType(InAppNotificationType.RELEASE);
        target.setBody(new ReleaseNotificationBody(source.getWhatsNew(), source.getBugFixes()));
        return target;
    }
}
