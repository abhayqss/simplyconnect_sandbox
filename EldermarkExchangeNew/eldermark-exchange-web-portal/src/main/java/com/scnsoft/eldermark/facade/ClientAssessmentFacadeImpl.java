package com.scnsoft.eldermark.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAssessmentResultSecurityFieldsAware;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.assessment.*;
import com.scnsoft.eldermark.dto.report.InTuneReportCanGenerateDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.assessment.*;
import com.scnsoft.eldermark.exception.*;
import com.scnsoft.eldermark.service.AssessmentScoringService;
import com.scnsoft.eldermark.service.AssessmentService;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.report.generator.InTuneReportGenerator;
import com.scnsoft.eldermark.service.report.workbook.InTuneWorkbookGenerator;
import com.scnsoft.eldermark.service.security.ClientAssessmentResultSecurityService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientAssessmentFacadeImpl implements ClientAssessmentFacade {

    private static final Collection<String> COMPREHENSIVE_TYPE_LIST = Set.of(
            Assessment.COMPREHENSIVE,
            Assessment.NOR_CAL_COMPREHENSIVE
    );
    private static final Sort.Order DATE_STARTED_DESC = Sort.Order.desc(ClientAssessmentResult_.DATE_STARTED);
    private static DateTimeFormatter exportFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ClientAssessmentResultService clientAssessmentService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private AssessmentScoringService assessmentScoringService;

    @Autowired
    private ListAndItemConverter<ClientAssessmentCount, ClientAssessmentStatusCountDto> assessmentStatusDtoConverter;

    @Autowired
    private ListAndItemConverter<ClientAssessmentResult, ClientAssessmentListItemDto> assessmentListItemDtoConverter;

    @Autowired
    private ListAndItemConverter<ClientAssessmentResult, ClientDashboardAssessmentListItemDto> сlientDashboardAssessmentListItemDtoConverter;

    @Autowired
    private ListAndItemConverter<ClientAssessmentResult, ClientAssessmentChartItemDto> assessmentChartItemDtoConverter;

    @Autowired
    private Converter<ClientAssessmentResult, ClientAssessmentResultDto> clientAssessmentResultDtoConverter;

    @Autowired
    private Converter<ClientAssessmentResultDto, ClientAssessmentResult> clientAssessmentResultEntityConverter;

    @Autowired
    private ListAndItemConverter<Assessment, AssessmentTypeDto> assessmentDtoConverter;

    @Autowired
    private Converter<Assessment, AssessmentManagementDto> assessmentManagementDtoConverter;

    @Autowired
    private Converter<ClientAssessmentResult, ClientAssessmentResultHistoryItemDto> historyDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientAssessmentResultSecurityService clientAssessmentResultSecurityService;

    @Autowired
    private InTuneReportGenerator inTuneReportGenerator;

    @Autowired
    private InTuneWorkbookGenerator inTuneWorkbookGenerator;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    public Long count(Long clientId) {
        return clientAssessmentService.count(permissionFilterService.createPermissionFilterForCurrentUser(), clientId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    public Page<ClientAssessmentListItemDto> find(Long clientId, String searchString, Pageable pageable) {
        return findAssessmentResults(clientId, searchString, pageable)
                .map(clientAssessmentResult -> assessmentListItemDtoConverter.convert(clientAssessmentResult));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    public Page<ClientDashboardAssessmentListItemDto> findForDashboard(Long clientId, Pageable pageable) {
        return findAssessmentResults(clientId, null, pageable)
                .map(clientAssessmentResult -> сlientDashboardAssessmentListItemDtoConverter.convert(clientAssessmentResult));
    }

    private Page<ClientAssessmentResult> findAssessmentResults(Long clientId, String searchString, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAssessmentService.find(clientId, searchString, permissionFilter,
                PaginationUtils.sortByDefault(PaginationUtils.applyEntitySort(pageable, ClientAssessmentListItemDto.class),
                        Sort.by(Sort.Order.desc(ClientAssessmentResult_.DATE_STARTED))));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canView(#assessmentResultId)")
    public Page<ClientAssessmentResultHistoryItemDto> findHistoryById(@P("assessmentResultId") Long assessmentResultId,
                                                                      Pageable pageable) {
        return clientAssessmentService.findHistoryById(assessmentResultId, PaginationUtils.setHistorySort(pageable))
                .map(historyDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canView(#assessmentResultId)")
    public ClientAssessmentResultDto findClientAssessmentById(@P("assessmentResultId") Long assessmentResultId) {
        ClientAssessmentResult residentAssessmentResult = clientAssessmentService.findById(assessmentResultId);
        return clientAssessmentResultDtoConverter.convert(residentAssessmentResult);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canView(#assessmentResultId)")
    public void export(Long assessmentResultId, HttpServletResponse response, Integer timezoneOffset) {
        ClientAssessmentResult assessmentResult = clientAssessmentService.findById(assessmentResultId);
        var fileName = buildExportFileName(assessmentResult, timezoneOffset == null ? 0 : timezoneOffset);
        var exportDto = new AssessmentExportDto(assessmentResult.getClient().getId(), assessmentResult.getResult());

        copyDocumentContentToResponse(exportDto, fileName, response);
    }

    private String buildExportFileName(ClientAssessmentResult assessmentResult, int timezoneOffset) {
        Client client = assessmentResult.getClient();

        String initials = StringUtils.join(StringUtils.left(client.getFirstName(), 1), StringUtils.left(client.getLastName(), 1));
        String assessmentType = assessmentResult.getAssessment().getShortName().toLowerCase();

        var shiftedAssignedDate = DateTimeUtils.toLocalDate(assessmentResult.getDateStarted(), timezoneOffset);
        var dateStr = exportFormatter.format(shiftedAssignedDate);

        return initials + " " + assessmentType + " " + dateStr + ".txt";
    }

    private void copyDocumentContentToResponse(AssessmentExportDto exportDto, String fileName, HttpServletResponse response) {
        String contentType = "text/plain";
        String openType = "attachment";

        response.setContentType(contentType);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");
        response.setHeader("Content-Disposition", openType + ";filename=\"" + fileName + "\"");

        try {
            objectMapper.writerFor(AssessmentExportDto.class).writeValue(response.getOutputStream(), exportDto);
        } catch (IOException e) {
            throw new InternalServerException(InternalServerExceptionType.ASSESSMENT_EXPORT_FAILURE);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewTypeOfClient(#assessmentId, #clientId)")
    public String findSurveyJson(@P("clientId") Long clientId, @P("assessmentId") Long assessmentId) {
        return assessmentService.findSurveyJson(assessmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentTypeGroupDto> findGroupedAssessmentTypes(Long clientId, List<String> filterBy) {
        var availableTypesForView = clientAssessmentResultSecurityService.findAccessibleTypesForView(clientId, filterBy);
        if (CollectionUtils.isEmpty(availableTypesForView)) {
            return Collections.emptyList();
        }
        var availableTypesForAdd = CareCoordinationUtils.toIdsSet(clientAssessmentResultSecurityService.findAccessibleTypesForAdd(clientId, filterBy));

        var grouped = availableTypesForView.stream().collect(Collectors.groupingBy(Assessment::getAssessmentGroup));

        var result = grouped.entrySet()
                .stream()
                .sorted(Comparator.comparing(Map.Entry::getKey, Comparator.comparing(AssessmentGroup::getName)))
                .map(assessmentGroupListEntry ->
                        new AssessmentTypeGroupDto(assessmentGroupListEntry.getKey().getId(),
                                assessmentGroupListEntry.getKey().getName(),
                                assessmentDtoConverter.convertList(assessmentGroupListEntry.getValue()))
                )
                .peek(assessmentTypeGroupDto -> assessmentTypeGroupDto.getTypes().forEach(type -> type.setCanAdd(availableTypesForAdd.contains(type.getId()))))
                .peek(assessmentTypeGroupDto -> assessmentTypeGroupDto.getTypes().sort(Comparator.comparing(AssessmentTypeDto::getTitle)))
                .collect(Collectors.toList());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewTypeOfClient(#assessmentId, #clientId)")
    public AssessmentManagementDto getManagement(@P("clientId") Long clientId, @P("assessmentId") Long assessmentId) {
        var assessment = assessmentService.findById(assessmentId).orElseThrow();
        return assessmentManagementDtoConverter.convert(assessment);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canModifyTypeOfClient(#assessmentId, #clientId)")
    public Long calculateScore(@P("clientId") Long clientId, @P("assessmentId") Long assessmentId, String resultJson) {
        return assessmentScoringService.calculateScore(assessmentId, resultJson);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    public List<ClientAssessmentStatusCountDto> getCountByStatus(Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var assessmentStatus = clientAssessmentService.countGroupedByStatus(clientId, permissionFilter);
        return assessmentStatusDtoConverter.convertList(assessmentStatus);
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canAdd(#dto)")
    public Long add(@P("dto") ClientAssessmentResultDto dto) {
        clientService.validateActive(dto.getClientId());
        validateHousingInProcess(dto.getTypeId(), dto.getClientId());
        var savedId = save(dto);
        clientAssessmentService.createEventForAssessmentWithRiskIdentified(savedId, null);
        return savedId;
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canEdit(#dto.id)")
    public Long edit(@P("dto") ClientAssessmentResultDto dto) {
        clientService.validateActive(dto.getClientId());
        Long previousId = dto.getId();
        Long updatedId = save(dto);
        clientAssessmentService.createEventNoteForAssessmentWithRiskIdentified(previousId, updatedId);
        clientAssessmentService.createEventForAssessmentWithRiskIdentified(updatedId, previousId);
        return updatedId;
    }

    private Long save(ClientAssessmentResultDto dto) {
        var result = clientAssessmentResultEntityConverter.convert(dto);
        validateAssessmentStatus(result);
        Long clientAssessmentId;
        if (dto.getId() == null) {
            clientAssessmentId = clientAssessmentService.createAuditableEntity(result);
        } else {
            clientAssessmentId = clientAssessmentService.updateAuditableEntity(result);
        }
        clientService.updateClientAccordingToComprehensiveAssessment(dto.getClientId(), clientAssessmentId);
        return clientAssessmentId;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long clientId) {
        return clientAssessmentResultSecurityService.canAdd(new ClientAssessmentResultSecurityFieldsAware() {
            @Override
            public Long getAssessmentId() {
                return ClientAssessmentResultSecurityService.ANY_TARGET_TYPE;
            }

            @Override
            public Long getClientId() {
                return clientId;
            }
        });
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canEdit(#dto.assessmentResultId)")
    public boolean updateServicePlanNeedIdentification(@P("dto") ClientAssessmentResultServicePlanNeedIdentificationDto dto) {
        ClientAssessmentResult clientAssessmentResult = clientAssessmentService.findById(dto.getAssessmentResultId());
        clientAssessmentResult.setServicePlanNeedIdentificationExcludedQuestions(dto.getExcludedQuestions());
        clientAssessmentResult.setServicePlanNeedIdentificationExcludedSections(dto.getExcludedSections());
        clientAssessmentService.save(clientAssessmentResult);
        return true;
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canHide(#id)")
    public Long hide(Long id, String comment) {
        validateClientActive(id);
        return clientAssessmentService.hide(id, comment);
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canRestore(#id)")
    public Long restore(Long id, String comment) {
        validateClientActive(id);
        return clientAssessmentService.restore(id, comment);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView() {
        return clientAssessmentResultSecurityService.canViewList();
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canDownloadInTuneReport(#clientId)")
    @Transactional(readOnly = true)
    public void downloadInTuneReport(Long clientId, Integer timezoneOffset, HttpServletResponse httpServletResponse) {
        var report = inTuneReportGenerator.generateSingleClientReport(clientId, permissionFilterService.createPermissionFilterForCurrentUser());
        report.setTimeZoneOffset(timezoneOffset);
        var workbook = inTuneWorkbookGenerator.generateWorkbook(report);
        WriterUtils.copyDocumentContentToResponse(workbook, ReportType.IN_TUNE.toString(), httpServletResponse);
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    @Transactional(readOnly = true)
    public boolean canDownloadInTuneReport(Long clientId) {
        return clientAssessmentResultSecurityService.canDownloadInTuneReport(clientId);
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canDownloadInTuneReport(#clientId)")
    @Transactional(readOnly = true)
    public InTuneReportCanGenerateDto canGenerateInTuneReport(Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var clientInfo = inTuneReportGenerator.getClientInfo(clientId, permissionFilter);
        if (!clientInfo.getHasAssessments()) {
            return new InTuneReportCanGenerateDto(
                    false,
                    BusinessExceptionType.INTUNE_REPORT_NO_DATA.code(),
                    BusinessExceptionType.INTUNE_REPORT_NO_DATA.message()
            );
        }
        if (!clientInfo.getHasChangesInTheLastTwoAssessments()) {
            return new InTuneReportCanGenerateDto(
                    false,
                    BusinessExceptionType.INTUNE_REPORT_NO_TRIGGERS.code(),
                    BusinessExceptionType.INTUNE_REPORT_NO_TRIGGERS.message()
            );
        }
        return new InTuneReportCanGenerateDto(true);
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canViewList()")
    @Transactional(readOnly = true)
    public boolean existsInProcess(Long clientId, Long typeId) {
        return clientAssessmentService.existsInProcess(clientId, typeId);
    }

    private void validateAssessmentStatus(ClientAssessmentResult clientAssessmentResult) {
        var status = clientAssessmentResult.getAssessmentStatus();
        var assessment = clientAssessmentResult.getAssessment();
        if (!isStatusSupported(assessment, status)) {
            throw new ValidationException(assessment.getName() + " doesn't support '" + status.getDisplayName() + "' status");
        }
    }

    private boolean isStatusSupported(Assessment assessment, AssessmentStatus status) {
        if (status == AssessmentStatus.IN_PROCESS) {
            return assessment.getDraftEnabled();
        } else if (status == AssessmentStatus.INACTIVE || status == AssessmentStatus.HIDDEN) {
            return COMPREHENSIVE_TYPE_LIST.contains(assessment.getShortName());
        } else {
            return status == AssessmentStatus.COMPLETED;
        }
    }

    private void validateClientActive(Long assessmentResultId) {
        var clientIdAware = clientAssessmentService.findById(assessmentResultId, ClientIdAware.class);
        clientService.validateActive(clientIdAware.getClientId());
    }

    private void validateHousingInProcess(Long typeId, Long clientId) {
        var assessmentType = assessmentService.findById(typeId).orElseThrow();
        if (Assessment.HOUSING.equals(assessmentType.getShortName()) && clientAssessmentService.existsInProcess(clientId, typeId)) {
            throw new BusinessException(BusinessExceptionType.ASSESSMENT_HOUSING_IN_PROCESS_ALREADY_EXISTS);
        }
    }
}
