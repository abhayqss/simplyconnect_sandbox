package com.scnsoft.eldermark.converter.entity2dto.organization;


import com.scnsoft.eldermark.dto.TypeDto;
import com.scnsoft.eldermark.dto.assessment.BaseAssessmentListItemDto;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.util.DateTimeUtils;


public abstract class BaseAssessmentListItemDtoConverter<T extends BaseAssessmentListItemDto> {

    protected <T extends BaseAssessmentListItemDto> void fill(ClientAssessmentResult source, T target) {
        TypeDto casDto = new TypeDto();
        target.setId(source.getId());
        target.setTypeName(source.getAssessment().getCode());
        target.setTypeTitle(source.getAssessment().getName());
        target.setTypeShortTitle(source.getAssessment().getShortName());
        casDto.setName(source.getAssessmentStatus().getDisplayName().toUpperCase().replaceAll(" ", "_"));
        casDto.setTitle(source.getAssessmentStatus().getDisplayName());
        target.setStatus(casDto);
        target.setDateStarted(DateTimeUtils.toEpochMilli(source.getDateStarted()));
        target.setDateCompleted(DateTimeUtils.toEpochMilli(source.getDateCompleted()));
        target.setTypeId(source.getAssessment().getId());
    }
}
