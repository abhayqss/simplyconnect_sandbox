package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.dto.assessment.ClientAssessmentResultDto;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.service.AssessmentScoringService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClientAssessmentResultDtoConverter implements Converter<ClientAssessmentResult, ClientAssessmentResultDto> {

    @Autowired
    private AssessmentScoringService assessmentScoringService;

    @Override
    public ClientAssessmentResultDto convert(ClientAssessmentResult clientAssessmentResult) {
        ClientAssessmentResultDto dto = new ClientAssessmentResultDto();
        dto.setId(clientAssessmentResult.getId());
        dto.setClientId(clientAssessmentResult.getEmployee().getId());
        dto.setTypeId(clientAssessmentResult.getAssessment().getId());
        dto.setDataJson(clientAssessmentResult.getResult());
        dto.setContactId(clientAssessmentResult.getEmployee().getId());
        dto.setContactName(clientAssessmentResult.getEmployee().getFullName());

        dto.setDateAssigned(DateTimeUtils.toEpochMilli(clientAssessmentResult.getDateStarted()));

        if (clientAssessmentResult.getDateCompleted() != null) {
            dto.setDateCompleted(
                    clientAssessmentResult.getDateCompleted().toEpochMilli());
        }

        dto.setComment(clientAssessmentResult.getComment());
        dto.setStatusName(clientAssessmentResult.getAssessmentStatus());
        dto.setStatusTitle(clientAssessmentResult.getAssessmentStatus().getDisplayName());
        if (StringUtils.isNotEmpty(clientAssessmentResult.getResult())) {
            Long score = clientAssessmentResult.getAssessment().getScoringEnabled()
                    ? assessmentScoringService.calculateScore(clientAssessmentResult.getAssessment().getId(),
                    clientAssessmentResult.getResult())
                    : 0;
            dto.setScore(score);
        }
        dto.setHasErrors(clientAssessmentResult.getHasErrors());
        dto.setServicePlanNeedIdentificationExcludedQuestions(clientAssessmentResult.getServicePlanNeedIdentificationExcludedQuestions());
        dto.setServicePlanNeedIdentificationExcludedSections(clientAssessmentResult.getServicePlanNeedIdentificationExcludedSections());
        return dto;
    }

}
