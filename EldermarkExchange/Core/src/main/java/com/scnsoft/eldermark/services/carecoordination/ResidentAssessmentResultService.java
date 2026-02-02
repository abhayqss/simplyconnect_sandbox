package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.entity.ResidentAssessmentResult;
import com.scnsoft.eldermark.facades.beans.ComprehensiveAssessmentBean;
import com.scnsoft.eldermark.shared.carecoordination.assessments.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ResidentAssessmentResultService extends AuditableEntityService<ResidentAssessmentResult, ResidentAssessmentResultDto> {

    //Long createAssessmentResult(ResidentAssessmentResultDto residentAssessmentResultDto);

    Long count(Long patientId);

    Page<ResidentAssessmentResultListDto> list(Long patientId, AssessmentsFilterDto assessmentsFilterDto, Pageable pageRequest);

    ResidentAssessmentScoringDto calculateAssessmentScoringResults(Long assessmentId, String resultJson);

    ResidentAssessmentScoringDto calculateAssessmentScoringResults(Long residentAssessmentResultId);

    ResidentAssessmentResultDto find(Long residentAssessmentResultId);

    ResidentAssessmentPriorityCheckDto showPriorityCheckResult(ResidentAssessmentResultDto residentAssessmentResultDto);

    void downloadResidentAssessmentResult(Long assessmentResultId, HttpServletResponse response, int timeZoneOffset);

    List<ComprehensiveAssessmentBean> parseComprehensiveAssessments(Long residentId);

}
