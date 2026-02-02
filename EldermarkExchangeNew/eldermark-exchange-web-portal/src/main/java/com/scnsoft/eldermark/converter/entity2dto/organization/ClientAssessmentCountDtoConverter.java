package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.StatusCountDto;
import org.springframework.stereotype.Component;

@Component
public class ClientAssessmentCountDtoConverter implements ListAndItemConverter<ClientAssessmentCount, StatusCountDto> {

    @Override
    public StatusCountDto convert(ClientAssessmentCount source) {
        StatusCountDto target = new StatusCountDto();
        target.setCount(source.getCount());
        target.setStatus(source.getStatus().getDisplayName());
        return target;
    }
}
