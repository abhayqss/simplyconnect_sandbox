package com.scnsoft.eldermark.converter.entity2dto.organization;

import org.springframework.stereotype.Component;

import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.assessment.ClientAssessmentStatusCountDto;

@Component
public class AssessmentStatusDtoConverter implements ListAndItemConverter<ClientAssessmentCount, ClientAssessmentStatusCountDto> {

    @Override
    public ClientAssessmentStatusCountDto convert(ClientAssessmentCount source) {
        ClientAssessmentStatusCountDto target = new ClientAssessmentStatusCountDto();
        target.setCount(source.getCount());
        target.setStatus(source.getStatus().getDisplayName());
        return target;
    }

}
