package com.scnsoft.eldermark.facades;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.IncidentReportDtoWrapper;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.services.IncidentReportService;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.util.IncidentReportInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Transactional
public class IncidentFacadeWebImpl implements IncidentFacadeWeb {
    private static final Logger logger = LoggerFactory.getLogger(IncidentFacadeWebImpl.class);
    private static final Marker SECURITY_MARKER = MarkerFactory.getMarker("SECURITY");

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private IncidentReportInitializer incidentReportInitializer;

    @Autowired
    private Converter<IncidentReport, IncidentReportDto> incidentReportEntityToDtoConverter;

    @Autowired
    private Converter<IncidentReportDtoWrapper, IncidentReport> incidentReportDtoWrapperToEntityConverter;

    @Override
    public IncidentReportDto initIncidentReport(Long eventId) {
        validateAccessToIrByEventId(eventId);

        Employee loggedInEmployee = SecurityUtils.getAuthenticatedUser().getEmployee();
        return incidentReportInitializer.initIncidentReport(eventId, loggedInEmployee);
    }

    @Override
    public void getIncidentReportPDF(HttpServletResponse response, Long eventId, Integer timeZoneOffset)
            throws DocumentException, IOException {
        validateAccessToIrByEventId(eventId);

        incidentReportService.writeIncidentReportPDFByEventId(response, eventId, timeZoneOffset);
    }

    @Override
    public IncidentReportDto getIncidentReportDetails(Long incidentReportId) {
        IncidentReport incidentReport = incidentReportService.find(incidentReportId);
        validateAccessToIr(incidentReport);

        IncidentReportDto incidentReportDto = incidentReportEntityToDtoConverter.convert(incidentReport);
        return incidentReportDto;
    }

    @Override
    public Long saveIncidentReportDraft(Long eventId, IncidentReportDto incidentReportDto) {
        validateAccessToIr(eventId, incidentReportDto);

        final Employee loggedInEmployee = SecurityUtils.getAuthenticatedUser().getEmployee();
        final IncidentReport incidentReport = incidentReportDtoWrapperToEntityConverter.convert(
                new IncidentReportDtoWrapper(incidentReportDto, eventId, loggedInEmployee));

        return incidentReportService.saveIncidentReportDraft(incidentReport);
    }

    @Override
    public Long submitIncidentReport(Long eventId, IncidentReportDto incidentReportDto) throws IOException, DocumentException {
        validateAccessToIr(eventId, incidentReportDto);

        Employee loggedInEmployee = SecurityUtils.getAuthenticatedUser().getEmployee();
        IncidentReport incidentReport = incidentReportDtoWrapperToEntityConverter.convert(
                new IncidentReportDtoWrapper(incidentReportDto, eventId, loggedInEmployee)
        );
        return incidentReportService.submitIncidentReport(incidentReport);
    }

    @Override
    public boolean canCurrentUserCreateIncidentReport(Long eventId) {
        return incidentReportService.hasAccessToIrByEventId(SecurityUtils.getAuthenticatedUser().getEmployeeId(), eventId);
    }

    private void validateAccessToIr(Long eventId, IncidentReportDto incidentReportDto) {
        if (incidentReportDto.getId() != null) {
            validateAccessToIr(incidentReportDto.getId());
        } else {
            validateAccessToIrByEventId(eventId);
        }
    }

    private void validateAccessToIr(Long incidentReportId) {
        validateAccessToIr(incidentReportService.find(incidentReportId));
    }

    private void validateAccessToIr(IncidentReport incidentReport) {
        if (!incidentReportService.hasAccessToIr(SecurityUtils.getAuthenticatedUser().getEmployeeId(), incidentReport)) {
            logger.warn(SECURITY_MARKER, "Failed attempt to access ir with id [{}] by employee [{}]",
                    incidentReport.getId(),
                    SecurityUtils.getAuthenticatedUser().getEmployeeId());
            throw new BusinessAccessDeniedException("No access to ir with id [" + incidentReport.getId() + "]");
        }
    }

    private void validateAccessToIrByEventId(Long eventId) {
        if (!incidentReportService.hasAccessToIrByEventId(SecurityUtils.getAuthenticatedUser().getEmployeeId(), eventId)) {
            logger.warn(SECURITY_MARKER, "Failed attempt to access ir for event [{}] by employee [{}]",
                    eventId,
                    SecurityUtils.getAuthenticatedUser().getEmployeeId());
            throw new BusinessAccessDeniedException("No access to ir for event [" + eventId + "]");
        }
    }
}
