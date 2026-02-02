package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.assessment.AssessmentManagementDto;
import com.scnsoft.eldermark.dto.assessment.AssessmentScoringGroupDto;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.AssessmentScoringGroup;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AssessmentManagementDtoConverter implements Converter<Assessment, AssessmentManagementDto> {

    @Autowired
    private ListAndItemConverter<AssessmentScoringGroup, AssessmentScoringGroupDto> assessmentScoringGroupDtoConverter;

    @Override
    public AssessmentManagementDto convert(Assessment assessment) {
        if (BooleanUtils.isNotTrue(assessment.getScoringEnabled()) ||
                (StringUtils.isBlank(assessment.getManagementComment()) && CollectionUtils.isEmpty(assessment.getScoringGroups()))) {
            return null;
        }
        AssessmentManagementDto result = new AssessmentManagementDto();
        result.setMessage(assessment.getManagementComment());
        result.setScale(assessmentScoringGroupDtoConverter.convertList(assessment.getScoringGroups()));
        return result;
    }
}
