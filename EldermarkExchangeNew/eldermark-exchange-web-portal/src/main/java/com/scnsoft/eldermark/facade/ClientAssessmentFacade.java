package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.assessment.*;
import com.scnsoft.eldermark.dto.report.InTuneReportCanGenerateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ClientAssessmentFacade {

    Long count(Long clientId);

    Page<ClientAssessmentListItemDto> find(Long clientId, String name, Pageable pageable);

    Page<ClientDashboardAssessmentListItemDto> findForDashboard(Long clientId, Pageable pageable);

    Page<ClientAssessmentResultHistoryItemDto> findHistoryById(Long assessmentResultId, Pageable pageable);

    ClientAssessmentResultDto findClientAssessmentById(Long assessmentResultId);

    void export(Long assessmentResultId, HttpServletResponse response, Integer timezoneOffset);

    String findSurveyJson(Long clientId, Long assessmentId);

    List<AssessmentTypeGroupDto> findGroupedAssessmentTypes(Long clientId, List<String> filterBy);

    AssessmentManagementDto getManagement(Long clientId, Long assessmentId);

    Long add(ClientAssessmentResultDto dto);

    Long edit(ClientAssessmentResultDto dto);

    Long calculateScore(Long clientId, Long assessmentId, String resultJson);


    List<ClientAssessmentStatusCountDto> getCountByStatus(Long clientId);

    boolean canAdd(Long clientId);

    boolean updateServicePlanNeedIdentification(ClientAssessmentResultServicePlanNeedIdentificationDto clientAssessmentResultServicePlanNeedIdentificationDto);

    Long hide(Long id, String comment);

    Long restore(Long id, String comment);

    boolean canView();

    void downloadInTuneReport(Long clientId, Integer timezoneOffset, HttpServletResponse httpServletResponse);

    boolean canDownloadInTuneReport(Long clientId);

    InTuneReportCanGenerateDto canGenerateInTuneReport(Long clientId);

    boolean existsInProcess(Long clientId, Long typeId);
}
