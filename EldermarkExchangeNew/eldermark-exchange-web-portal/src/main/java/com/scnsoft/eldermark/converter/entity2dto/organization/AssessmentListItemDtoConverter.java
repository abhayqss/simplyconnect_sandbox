package com.scnsoft.eldermark.converter.entity2dto.organization;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.assessment.ClientAssessmentListItemDto;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.service.AssessmentScoringService;
import com.scnsoft.eldermark.service.security.ClientAssessmentResultSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AssessmentListItemDtoConverter
        extends BaseAssessmentListItemDtoConverter<ClientAssessmentListItemDto>
        implements ListAndItemConverter<ClientAssessmentResult, ClientAssessmentListItemDto> {

    private final static Set<String> ASSESSMENT_TYPES_WITH_VISIBLE_SCORE = Set.of(
        Assessment.GAD7,
        Assessment.PHQ9,
        Assessment.ARIZONA_SSM
    );

    @Autowired
    private ClientAssessmentResultSecurityService clientAssessmentResultSecurityService;

    @Autowired
    private AssessmentScoringService assessmentScoringService;


    @Override
    public ClientAssessmentListItemDto convert(ClientAssessmentResult source) {
        var target = new ClientAssessmentListItemDto();
        fill(source, target);
        if (ASSESSMENT_TYPES_WITH_VISIBLE_SCORE.contains(source.getAssessment().getShortName())
                && source.getAssessmentStatus() == AssessmentStatus.COMPLETED) {
            target.setScore(assessmentScoringService.calculateScore(source).toString());
        }
        target.setExportable(Assessment.COMPREHENSIVE.equals(source.getAssessment().getShortName()));
        target.setCanEdit(clientAssessmentResultSecurityService.canEdit(source.getId()));
        if (Assessment.COMPREHENSIVE.equals(source.getAssessment().getShortName())) {
            if (source.getAssessmentStatus().equals(AssessmentStatus.HIDDEN)) {
                target.setCanRestore(clientAssessmentResultSecurityService.canRestore(source.getId()));
            } else {
                target.setCanHide(clientAssessmentResultSecurityService.canHide(source.getId()));
            }
        }
        if (source.getEmployee() != null) {
            target.setAuthor(source.getEmployee().getFullName());
        }
        return target;
    }
}
