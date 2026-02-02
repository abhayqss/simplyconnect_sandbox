package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.ReleaseNoteDto;
import com.scnsoft.eldermark.entity.ReleaseNote;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ReleaseNoteDtoConverter implements Converter<ReleaseNote, ReleaseNoteDto> {

    @Override
    public ReleaseNoteDto convert(ReleaseNote source) {
        var target = new ReleaseNoteDto();
        target.setId(source.getId());
        target.setFileName(source.getTitle());
        target.setDescription(source.getDescription());
        target.setCreatedDate(source.getCreatedDate().toEpochMilli());
        target.setFileMimeType(source.getMimeType());
        target.setFixes(source.getBugFixes());
        target.setFeatures(source.getWhatsNew());
        target.setIsEmailNotificationEnabled(source.getEmailNotificationEnabled());
        target.setIsInAppNotificationEnabled(source.getInAppNotificationEnabled());
        return target;
    }
}
