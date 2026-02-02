package com.scnsoft.eldermark.services.converters;

import com.scnsoft.eldermark.entity.ResidentAssessmentResult;
import com.scnsoft.eldermark.shared.carecoordination.assessments.AssessmentHistoryDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AssessmentHistoryConverter implements Converter<ResidentAssessmentResult, AssessmentHistoryDto> {
    @Override
    public AssessmentHistoryDto convert(ResidentAssessmentResult src) {
        if (src == null) {
            return null;
        }
        AssessmentHistoryDto destination = new AssessmentHistoryDto();
        destination.setId(src.getId());
        destination.setDate(src.getLastModifiedDate());
        destination.setStatus(src.getStatus() != null ? src.getStatus().getDisplayName() : null);
        destination.setAuthor(src.getEmployee().getFullName() + ", " + src.getEmployee().getCareTeamRole().getName());
        destination.setParentAssessment(src.getChainId());
        return destination;
    }
}
