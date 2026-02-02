package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.IncidentPictureDto;
import com.scnsoft.eldermark.entity.event.incident.IncidentPicture;
import org.springframework.stereotype.Component;

@Component
public class IncidentPictureDtoListConverter implements ListAndItemConverter<IncidentPicture, IncidentPictureDto> {

    @Override
    public IncidentPictureDto convert(IncidentPicture source) {
        var target = new IncidentPictureDto();
        target.setId(source.getId());
        target.setName(source.getOriginalFileName());
        target.setMimeType(source.getMimeType());
        return target;
    }
}
