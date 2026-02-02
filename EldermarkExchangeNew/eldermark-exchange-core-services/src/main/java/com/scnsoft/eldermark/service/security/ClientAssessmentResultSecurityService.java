package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.projection.dto.ClientAssessmentResultSecurityFieldsAware;
import com.scnsoft.eldermark.entity.assessment.Assessment;

import java.util.List;

public interface ClientAssessmentResultSecurityService {

    Long ANY_TARGET_TYPE = -1L;

    boolean canAdd(ClientAssessmentResultSecurityFieldsAware dto);

    boolean canAdd(Long clientId, Long assessmentId);

    List<Assessment> findAccessibleTypesForAdd(Long clientId, List<String> filterByCode);

    List<Assessment> findAccessibleTypesForView(Long clientId, List<String> filterByCode);

    boolean canEdit(Long clientAssessmentResultId);

    boolean canViewList();

    boolean canView(Long clientAssessmentResultId);

    boolean canViewTypeOfClient(Long assessmentId, Long clientId);

    boolean canModifyTypeOfClient(Long assessmentId, Long clientId);

    boolean canHide(Long clientAssessmentResultId);

    boolean canRestore(Long clientAssessmentResultId);

    boolean canDownloadInTuneReport(Long clientId);
}
