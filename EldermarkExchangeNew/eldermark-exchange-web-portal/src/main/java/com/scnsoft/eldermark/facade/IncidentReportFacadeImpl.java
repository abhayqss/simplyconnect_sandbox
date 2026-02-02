package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.IncidentReportFilter;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.event.incident.IncidentReport;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.IncidentPictureService;
import com.scnsoft.eldermark.service.IncidentReportService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.IncidentReportSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.util.DocumentUtils;
import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class IncidentReportFacadeImpl implements IncidentReportFacade {

    private final Set<String> ALLOWED_EXTENSIONS = Set.of("TIFF", "TIF", "PDF", "JPEG", "GIF", "PNG", "JPG");

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private IncidentReportSecurityService incidentReportSecurityService;

    @Autowired
    private IncidentReportInitializer incidentReportInitializer;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private Converter<IncidentReport, IncidentReportDto> incidentReportDtoConverter;

    @Autowired
    private Converter<IncidentReportDtoWrapper, IncidentReport> incidentReportEntityConverter;

    @Autowired
    private IncidentPictureService incidentPictureService;

    @Autowired
    private Converter<IncidentReport, IncidentReportListItemDto> incidentReportListItemDtoConverter;

    @Autowired
    private Converter<IncidentReport, IncidentReportHistoryListItemDto> incidentReportHistoryListItemDtoConverter;

    @Autowired
    private ChatService twilioChatService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EventService eventService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.canViewList()")
    public Page<IncidentReportListItemDto> find(IncidentReportFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return incidentReportService.find(filter, permissionFilter, PaginationUtils.applyEntitySort(pageable, IncidentReportListItemDto.class))
                .map(incidentReport -> incidentReportListItemDtoConverter.convert(incidentReport));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.canView(#id)")
    public void downloadById(@P("id") Long id, HttpServletResponse response, ZoneId zoneId) {
        var report = incidentReportService.writePDFById(id, response, zoneId);
        WriterUtils.copyDocumentContentToResponse(response, report);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.hasAccessByEventId(#eventId)")
    public IncidentReportDto findDefault(@P("eventId") Long eventId) {
        Employee loggedInEmployee = loggedUserService.getCurrentEmployee();
        return incidentReportInitializer.initIncidentReport(eventId, loggedInEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.canView(#incidentReportId)")
    public IncidentReportDto findById(@P("incidentReportId") Long incidentReportId) {
        IncidentReport incidentReport = incidentReportService.findById(incidentReportId);
        return incidentReportDtoConverter.convert(incidentReport);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.canView(#id)")
    public Page<IncidentReportHistoryListItemDto> findHistoryById(@P("id") Long id, Pageable pageable) {
        return incidentReportService.findHistoryById(id, PaginationUtils.applyEntitySort(pageable, IncidentReportHistoryListItemDto.class))
                .map(incidentReport -> incidentReportHistoryListItemDtoConverter.convert(incidentReport));
    }

    @Override
    @PreAuthorize("@incidentReportSecurityService.hasAccessByEventId(#dto.eventId)")
    public Long saveDraft(@P("dto") IncidentReportDto incidentReportDto) {
        validateActiveClient(incidentReportDto.getEventId());
        incidentReportDto.setIncidentPictureFiles(validateAndReducePictureFiles(incidentReportDto.getIncidentPictureFiles()));
        var loggedInEmployee = loggedUserService.getCurrentEmployee();
        var incidentReport = incidentReportEntityConverter.convert(new IncidentReportDtoWrapper(incidentReportDto, loggedInEmployee));
        return incidentReportService.saveDraft(incidentReport);
    }

    @Override
    @PreAuthorize("@incidentReportSecurityService.hasAccessByEventId(#dto.eventId)")
    public Long submit(@P("dto") IncidentReportDto incidentReportDto) {
        validateActiveClient(incidentReportDto.getEventId());
        incidentReportDto.setIncidentPictureFiles(validateAndReducePictureFiles(incidentReportDto.getIncidentPictureFiles()));
        var loggedInEmployee = loggedUserService.getCurrentEmployee();
        var incidentReport = incidentReportEntityConverter.convert(new IncidentReportDtoWrapper(incidentReportDto, loggedInEmployee));
        return incidentReportService.submit(incidentReport);
    }

    private void validateActiveClient(Long eventId) {
        var clientIdAware = eventService.findById(eventId, ClientIdAware.class);
        clientService.validateActive(clientIdAware.getClientId());
    }

    private List<MultipartFile> validateAndReducePictureFiles(List<MultipartFile> files) {
        return Stream.ofNullable(files)
                .flatMap(List::stream)
                .filter(Objects::nonNull)
                .peek(file -> DocumentUtils.validateUploadedFile(file, ALLOWED_EXTENSIONS))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.canViewIncidentPicture(#pictureId)")
    public FileBytesDto downloadIncidentPictureById(@P("pictureId") Long pictureId) {
        return Optional.ofNullable(incidentPictureService.downloadById(pictureId))
                .map(pair -> new FileBytesDto(pair.getFirst(), pair.getSecond())).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canViewList() {
        return incidentReportSecurityService.canViewList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canViewByClientId(Long clientId) {
        return incidentReportSecurityService.canViewByClient(clientId);
    }

    @Override
    @PreAuthorize("@incidentReportSecurityService.canDelete(#id)")
    public void deleteById(@P("id") Long id) {
        incidentReportService.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.canViewList()")
    public Long findOldestDateByOrganization(Long orgId) {
        var filter = new IncidentReportFilter();
        filter.setOrganizationId(orgId);
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return incidentReportService.findOldestDateByOrganization(filter, permissionFilter).map(Instant::toEpochMilli).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@incidentReportSecurityService.canViewList()")
    public Long findNewestDateByOrganization(Long orgId) {
        var filter = new IncidentReportFilter();
        filter.setOrganizationId(orgId);
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return incidentReportService.findNewestDateByOrganization(filter, permissionFilter).map(Instant::toEpochMilli).orElse(null);
    }

    @Override
    @PreAuthorize("@incidentReportSecurityService.canView(#id)")
    public void joinConversation(@P("id") Long id) {
        var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        twilioChatService.joinIncidentReportConversation(id, currentEmployeeId);
    }
}
