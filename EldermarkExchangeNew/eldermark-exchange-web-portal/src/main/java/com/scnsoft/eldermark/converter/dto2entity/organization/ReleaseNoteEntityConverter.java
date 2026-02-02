package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.ReleaseNoteDto;
import com.scnsoft.eldermark.entity.ReleaseNote;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ReleaseNoteEntityConverter implements Converter<ReleaseNoteDto, ReleaseNote> {

    @Override
    public ReleaseNote convert(ReleaseNoteDto source) {
        var target = new ReleaseNote();
        var currentTime = Instant.now();
        if (source.getId() == null) {
            target.setCreatedDate(currentTime);
            target.setEmailNotificationEnabled(source.getIsEmailNotificationEnabled());
            target.setInAppNotificationEnabled(source.getIsInAppNotificationEnabled());
        }
        target.setModifiedDate(currentTime);
        target.setId(source.getId());
        target.setBugFixes(source.getFixes());
        target.setWhatsNew(source.getFeatures());
        target.setDescription(source.getDescription());
        return target;
    }
}
