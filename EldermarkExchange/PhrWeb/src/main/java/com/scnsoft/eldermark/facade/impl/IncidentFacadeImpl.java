package com.scnsoft.eldermark.facade.impl;

import com.itextpdf.text.DocumentException;
import com.scnsoft.eldermark.dto.IncidentReportDto;
import com.scnsoft.eldermark.dto.IncidentReportDtoWrapper;
import com.scnsoft.eldermark.entity.incident.IncidentReport;
import com.scnsoft.eldermark.entity.phr.AccountType;
import com.scnsoft.eldermark.entity.phr.UserAccountType;
import com.scnsoft.eldermark.facade.BasePhrFacade;
import com.scnsoft.eldermark.facade.IncidentFacade;
import com.scnsoft.eldermark.services.IncidentReportService;
import com.scnsoft.eldermark.services.phr.UserService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.util.IncidentReportInitializer;
import com.scnsoft.eldermark.web.security.PhrSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@Transactional
public class IncidentFacadeImpl extends BasePhrFacade implements IncidentFacade {

    @Autowired
    private IncidentReportService incidentReportService;

    @Autowired
    private IncidentReportInitializer incidentReportInitializer;

    @Autowired
    private Converter<IncidentReport, IncidentReportDto> incidentReportEntityToDtoConverter;

    @Autowired
    private Converter<IncidentReportDtoWrapper, IncidentReport> incidentReportDtoWrapperToEntityConverter;

    @Autowired
    private UserService userService;

    @Override
    @Transactional(readOnly = true)
    public IncidentReportDto initIncidentReport(Long eventId, Long userId) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        validateAccessToIrByEventId(eventId);

        return incidentReportInitializer.initIncidentReport(eventId, getCareTeamSecurityUtils().getCurrentEmployeeOrThrow());

    }

    @Override
    @Transactional(readOnly = true)
    public void getIncidentReportPDF(HttpServletResponse response, Long eventId, Long userId, Integer timeZoneOffset)
            throws DocumentException, IOException {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        validateAccessToIrByEventId(eventId);

        incidentReportService.writeIncidentReportPDFByEventId(response, eventId, timeZoneOffset);
    }

    @Override
    @Transactional(readOnly = true)
    public IncidentReportDto getIncidentReportDetails(Long incidentReportId) {
        IncidentReport incidentReport = incidentReportService.find(incidentReportId);
        validateAccessToIr(incidentReportId);

        return incidentReportEntityToDtoConverter.convert(incidentReport);
    }

    @Override
    public Long saveIncidentReportDraft(Long userId, Long eventId, IncidentReportDto incidentReportDto) {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        validateAccessToIr(eventId, incidentReportDto);

        IncidentReport incidentReport = incidentReportDtoWrapperToEntityConverter.convert(
                new IncidentReportDtoWrapper(incidentReportDto, eventId, getCareTeamSecurityUtils().getCurrentEmployeeOrThrow()));

        return incidentReportService.saveIncidentReportDraft(incidentReport);
    }

    @Override
    public Long submitIncidentReport(Long userId, Long eventId, IncidentReportDto incidentReportDto) throws IOException, DocumentException {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        validateAccessToIr(eventId, incidentReportDto);

        IncidentReport incidentReport = incidentReportDtoWrapperToEntityConverter.convert(
                new IncidentReportDtoWrapper(incidentReportDto, eventId, getCareTeamSecurityUtils().getCurrentEmployeeOrThrow()));

        return incidentReportService.submitIncidentReport(incidentReport);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean canCurrentUserCreateIncidentReport(Long eventId) {
        return isCurrentUserProvider() && incidentReportService.hasAccessToIrByEventId(
                getCareTeamSecurityUtils().getCurrentEmployeeOrThrow().getId(), eventId);
    }

    @Override
    @Transactional(readOnly = true)
    public void getIncidentReportPDFById(HttpServletResponse response, Long userId, Long incidentReportId,
                                         Integer timeZoneOffset) throws DocumentException, IOException {
        PhrSecurityUtils.checkAccessToUserInfoOrThrow(userId);
        validateAccessToIr(incidentReportId);
        incidentReportService.writeIncidentReportPDFById(response, incidentReportId, timeZoneOffset);
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
        validateCurrentUserIsProvider();
        if (!incidentReportService.hasAccessToIr(getCareTeamSecurityUtils().getCurrentEmployeeOrThrow().getId(), incidentReport)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    private void validateAccessToIrByEventId(Long eventId) {
        validateCurrentUserIsProvider();
        if (!incidentReportService.hasAccessToIrByEventId(getCareTeamSecurityUtils().getCurrentEmployeeOrThrow().getId(), eventId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    private boolean isCurrentUserProvider() {
        for (UserAccountType userAccountType : getCareTeamSecurityUtils().getCurrentUser().getAccountTypes()) {
            if (AccountType.Type.PROVIDER.equals(userAccountType.getAccountType().getType())) {
                return true;
            }
        }
        return false;
    }

    private void validateCurrentUserIsProvider() {
        if (!isCurrentUserProvider()) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);

        }
    }
}
