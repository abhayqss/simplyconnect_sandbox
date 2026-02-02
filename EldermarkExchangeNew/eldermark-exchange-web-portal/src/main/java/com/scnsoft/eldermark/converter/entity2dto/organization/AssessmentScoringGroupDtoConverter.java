package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.assessment.AssessmentScoringGroupDto;
import com.scnsoft.eldermark.entity.assessment.AssessmentScoringGroup;
import org.springframework.stereotype.Component;

@Component
public class AssessmentScoringGroupDtoConverter implements ListAndItemConverter<AssessmentScoringGroup, AssessmentScoringGroupDto> {
    @Override
    public AssessmentScoringGroupDto convert(AssessmentScoringGroup assessmentScoringGroup) {
        AssessmentScoringGroupDto result = new AssessmentScoringGroupDto();
        result.setComments(assessmentScoringGroup.getComments());
        result.setHighlighting(assessmentScoringGroup.getHighlighting());
        result.setSeverity(assessmentScoringGroup.getSeverity());
        result.setSeverityShort(assessmentScoringGroup.getSeverityShort());
        result.setScoreHigh(assessmentScoringGroup.getScoreHigh());
        result.setScoreLow(assessmentScoringGroup.getScoreLow());
        result.setIsRiskIdentified(assessmentScoringGroup.getIsRiskIdentified());
        return result;
    }
}
