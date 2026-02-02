package com.scnsoft.eldermark.converter.entity2dto.organization;


import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.assessment.ClientDashboardAssessmentListItemDto;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.service.AssessmentScoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDashboardAssessmentListItemDtoConverter
        extends BaseAssessmentListItemDtoConverter<ClientDashboardAssessmentListItemDto>
        implements ListAndItemConverter<ClientAssessmentResult, ClientDashboardAssessmentListItemDto> {

    @Autowired
    private AssessmentScoringService assessmentScoringService;

    @Override
    public ClientDashboardAssessmentListItemDto convert(ClientAssessmentResult source) {
        var target = new ClientDashboardAssessmentListItemDto();
        fill(source,target);
        if (source.getResult() != null) {
            Long score = source.getAssessmentStatus() == AssessmentStatus.COMPLETED && source.getAssessment().getScoringEnabled()
                    ? assessmentScoringService.calculateScore(source.getAssessment().getId(), source.getResult())
                    : 0;
            target.setPoints(score);
        }
        return target;
    }
}
