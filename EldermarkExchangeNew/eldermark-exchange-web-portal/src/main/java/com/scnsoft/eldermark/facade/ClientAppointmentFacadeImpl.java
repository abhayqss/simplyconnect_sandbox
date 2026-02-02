package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentClientOnlySecurityFieldsAwareImpl;
import com.scnsoft.eldermark.dto.client.appointment.*;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentReminder;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentStatus;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.ClientAppointmentService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.report.converter.WriterUtils;
import com.scnsoft.eldermark.service.security.ClientAppointmentSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ClientAppointmentFacadeImpl implements ClientAppointmentFacade {

    private static final Long ONE_WEEK_MILLIS = 7L * 24L * 60L * 60L *1000L;
    private static final Long HALF_HOUR_MILLIS = 30L * 60L *1000L;

    @Autowired
    private ClientAppointmentService clientAppointmentService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ClientAppointmentSecurityService clientAppointmentSecurityService;

    @Autowired
    private Converter<ClientAppointmentDto, ClientAppointment> clientAppointmentEntityConverter;

    @Autowired
    private Converter<ClientAppointment, ClientAppointmentListItemDto> clientAppointmentListItemDtoConverter;

    @Autowired
    private Converter<ClientAppointment, ClientAppointmentDto> clientAppointmentDtoConverter;

    @Autowired
    private Converter<ClientAppointment, ClientAppointmentHistoryListItemDto> clientAppointmentHistoryListItemDtoConverter;

    @Override
    @PreAuthorize("@clientAppointmentSecurityService.canAdd(#dto)")
    public Long create(@P("dto") ClientAppointmentDto clientAppointmentDto) {
        var clientAppointment = clientAppointmentEntityConverter.convert(clientAppointmentDto);
        validateAppointment(clientAppointment);
        return clientAppointmentService.create(clientAppointment);
    }

    @Override
    @PreAuthorize("@clientAppointmentSecurityService.canEdit(#dto.id)")
    public Long edit(@P("dto") ClientAppointmentDto clientAppointmentDto) {
        var clientAppointment = clientAppointmentEntityConverter.convert(clientAppointmentDto);
        validateAppointment(clientAppointment);
        return clientAppointmentService.edit(clientAppointment);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAppointmentSecurityService.canViewList()")
    public Page<ClientAppointmentListItemDto> find(ClientAppointmentFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var appointments = clientAppointmentService.find(filter, permissionFilter, pageable);
        return appointments.map(clientAppointmentListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAppointmentSecurityService.canView(#appointmentId)")
    public ClientAppointmentDto findById(Long appointmentId) {
        var clientAppointment = clientAppointmentService.findById(appointmentId);
        return clientAppointmentDtoConverter.convert(clientAppointment);
    }

    @Override
    @PreAuthorize("@clientAppointmentSecurityService.canView(#appointmentChainId)")
    public Long findUnarchivedIdByChainId(Long appointmentChainId) {
        return clientAppointmentService.findUnarchivedIdByChainId(appointmentChainId);
    }

    @Override
    @PreAuthorize("@clientAppointmentSecurityService.canEdit(#appointmentId)")
    public void cancel(Long appointmentId, CancelClientAppointmentDto cancelClientAppointmentDto) {
        clientAppointmentService.cancel(appointmentId, cancelClientAppointmentDto.getCancellationReason(), loggedUserService.getCurrentEmployee());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAppointmentSecurityService.canView(#appointmentId)")
    public Page<ClientAppointmentHistoryListItemDto> findHistory(Long appointmentId, Pageable pageRequest) {
        var appointmentHistory = clientAppointmentService.findHistory(appointmentId, PaginationUtils.setHistorySort(pageRequest));
        return appointmentHistory.map(clientAppointmentHistoryListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAppointmentSecurityService.canAdd(new com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentClientOnlySecurityFieldsAwareImpl(#clientId))")
    public AppointmentTimeSlotUnavailabilityDto findParticipantsAvailability(Long dateFrom, Long dateTo, Long clientId, List<Long> serviceProviderIds, Long appointmentId, Boolean isExternalProviderServiceProvider, Integer timeZoneOffset) {
        Long currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        List<Long> employeeIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(serviceProviderIds)) {
            employeeIds = serviceProviderIds;
        } else if(BooleanUtils.isNotTrue(isExternalProviderServiceProvider)) {
            employeeIds = List.of(currentEmployeeId);
        }
        var overlappingAppointments = clientAppointmentService.findByDatesOverlapsAndAnyParticipants(dateFrom, dateTo, clientId, employeeIds, appointmentId);
        List<TimeSlotDto> suggestedTimeSlots = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(overlappingAppointments)) {
            Long dateFromToCheckAvailability = dateFrom - ONE_WEEK_MILLIS;
            Long dateToToCheckAvailability = dateTo + ONE_WEEK_MILLIS;
            Long minDateFromToCheckAvailability = Instant.now().toEpochMilli() + HALF_HOUR_MILLIS;
            if (dateFromToCheckAvailability < minDateFromToCheckAvailability) {
                dateFromToCheckAvailability = minDateFromToCheckAvailability;
            }
            var twoWeekAppointments = clientAppointmentService.findByDatesOverlapsAndAnyParticipants(dateFromToCheckAvailability, dateToToCheckAvailability, clientId, employeeIds, appointmentId);
            suggestedTimeSlots = suggestTimeSlots(twoWeekAppointments, dateFromToCheckAvailability, dateToToCheckAvailability, dateFrom, dateTo, timeZoneOffset, 3);
        }
        var unavailabilityDto = checkParticipantsAvailability(overlappingAppointments, clientId, serviceProviderIds, currentEmployeeId);
        unavailabilityDto.setSuggestedTimeSlots(suggestedTimeSlots);
        return unavailabilityDto;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAppointmentSecurityService.canViewList()")
    public void export(ClientAppointmentFilter filter, HttpServletResponse response, Integer timeZoneOffset) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var excel = clientAppointmentService.getExcel(filter, permissionFilter, timeZoneOffset);
        WriterUtils.copyDocumentContentToResponse(excel.getFileName(), excel.getFile(), WriterUtils.XSLX_MIME_TYPE, false, response);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long clientId) {
        return clientAppointmentSecurityService.canAdd(new ClientAppointmentClientOnlySecurityFieldsAwareImpl(clientId));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientAppointmentSecurityService.canViewList()")
    public Long count(Long organizationId) {
        return clientAppointmentService.count(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView() {
        return clientAppointmentSecurityService.canViewList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAddInOrganization(Long organizationId) {
        return clientAppointmentSecurityService.canAddInOrganization(organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentParticipationDto hasAccessibleExternalProviderNoProviderAppointments(Long organizationId) {
        var hasExternalProviderNoProvider =  clientAppointmentService.hasAccessibleExternalProviderNoProviderAppointments(organizationId);
        return new AppointmentParticipationDto(hasExternalProviderNoProvider.getFirst(), hasExternalProviderNoProvider.getSecond());
    }

    private AppointmentTimeSlotUnavailabilityDto checkParticipantsAvailability(List<ClientAppointment> overlappingAppointments, Long clientId, List<Long> serviceProviderIds, Long currentEmployeeId) {
        var result = new AppointmentTimeSlotUnavailabilityDto();
        if (CollectionUtils.isNotEmpty(overlappingAppointments)) {
            result.setServiceProviders(new ArrayList<>());
            var clientUnavailable = overlappingAppointments.stream().anyMatch(clientAppointment -> clientAppointment.getClientId().equals(clientId));
            if (clientUnavailable) {
                result.setClient(clientService.findById(clientId, NamesAware.class).getFullName());
            }
            if (CollectionUtils.isEmpty(serviceProviderIds)) {
                var creatorUnavailable = overlappingAppointments.stream().anyMatch(clientAppointment -> clientAppointment.getCreatorId().equals(currentEmployeeId)
                        || clientAppointment.getServiceProviderIds().contains(currentEmployeeId));
                if (creatorUnavailable) {
                    result.setCreator(employeeService.findById(currentEmployeeId, NamesAware.class).getFullName());
                }
            } else {
                for (Long serviceProviderId : serviceProviderIds) {
                    var serviceProviderUnavailable = overlappingAppointments.stream().anyMatch(clientAppointment -> clientAppointment.getCreatorId().equals(serviceProviderId)
                            || clientAppointment.getServiceProviderIds().contains(serviceProviderId));
                    if (serviceProviderUnavailable) {
                        result.getServiceProviders().add(employeeService.findById(serviceProviderId, NamesAware.class).getFullName());
                    }
                }
            }

        }
        return result;
    }

    private List<TimeSlotDto> suggestTimeSlots(List<ClientAppointment> overlappingAppointments, Long minDateFrom,
                                               Long maxDateTo, Long dateFrom, Long dateTo, Integer timeZoneOffset,
                                               int suggestedTimeSlotsCount) {
        List<TimeSlotDto> suggestedTimeSlots = new ArrayList<>();
        Long suggestedDateFromInThePast = dateFrom - HALF_HOUR_MILLIS;
        Long suggestedDateToInThePast = dateTo - HALF_HOUR_MILLIS;
        Long suggestedDateFromInTheFuture = dateFrom + HALF_HOUR_MILLIS;
        Long suggestedDateToInTheFuture = dateTo + HALF_HOUR_MILLIS;
        while (suggestedDateFromInThePast >= minDateFrom || suggestedDateToInTheFuture <= maxDateTo) {
            if (suggestedDateFromInThePast >= minDateFrom
                    && isWorkTime(suggestedDateFromInThePast, timeZoneOffset) && isWorkTime(suggestedDateToInThePast, timeZoneOffset)
                    && !isAnyAppointmentOverlapsPeriod(overlappingAppointments, suggestedDateFromInThePast, suggestedDateToInThePast)) {
                TimeSlotDto timeSlotDto = createTimeSlotDto(suggestedDateFromInThePast, suggestedDateToInThePast);
                suggestedTimeSlots.add(timeSlotDto);
                if (suggestedTimeSlots.size() >= suggestedTimeSlotsCount) {
                    break;
                }
            }
            if (suggestedDateToInTheFuture <= maxDateTo
                    && isWorkTime(suggestedDateFromInTheFuture, timeZoneOffset) && isWorkTime(suggestedDateToInTheFuture, timeZoneOffset)
                    && !isAnyAppointmentOverlapsPeriod(overlappingAppointments, suggestedDateFromInTheFuture, suggestedDateToInTheFuture)) {
                TimeSlotDto timeSlotDto = createTimeSlotDto(suggestedDateFromInTheFuture, suggestedDateToInTheFuture);
                suggestedTimeSlots.add(timeSlotDto);
                if (suggestedTimeSlots.size() >= suggestedTimeSlotsCount) {
                    break;
                }
            }
            suggestedDateFromInThePast = suggestedDateFromInThePast - HALF_HOUR_MILLIS;
            suggestedDateToInThePast = suggestedDateToInThePast - HALF_HOUR_MILLIS;
            suggestedDateFromInTheFuture = suggestedDateFromInTheFuture + HALF_HOUR_MILLIS;
            suggestedDateToInTheFuture = suggestedDateToInTheFuture + HALF_HOUR_MILLIS;
        }
        return suggestedTimeSlots;
    }

    private TimeSlotDto createTimeSlotDto(Long dateFrom, Long suggestedDateToInThePast) {
        var timeSlotDto = new TimeSlotDto();
        timeSlotDto.setStartDate(dateFrom);
        timeSlotDto.setEndDate(suggestedDateToInThePast);
        return timeSlotDto;
    }

    private boolean isAnyAppointmentOverlapsPeriod(List<ClientAppointment> appointments, Long dateFrom, Long dateTo) {
        return appointments.stream().anyMatch(clientAppointment -> isAppointmentOverlapsPeriod(clientAppointment, dateFrom, dateTo));
    }

    private boolean isWorkTime(Long time, Integer timeZoneOffset) {
        var localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), DateTimeUtils.generateZoneOffset(timeZoneOffset));
        var day = DayOfWeek.of(localDateTime.get(ChronoField.DAY_OF_WEEK));
        var hour = localDateTime.get(ChronoField.HOUR_OF_DAY);
        var minute = localDateTime.get(ChronoField.MINUTE_OF_HOUR);
        if (day == DayOfWeek.SUNDAY || day == DayOfWeek.SATURDAY || hour < 8 || (hour == 17 && minute > 0) || hour > 17) {
            return false;
        }
        return true;
    }

    private boolean isAppointmentOverlapsPeriod(ClientAppointment appointment, Long dateFrom, Long dateTo) {
        Long appointmentDateFrom = appointment.getDateFrom().toEpochMilli();
        Long appointmentDateTo = appointment.getDateTo().toEpochMilli();
        return (appointmentDateFrom <= dateFrom && appointmentDateTo > dateFrom)
                || (appointmentDateFrom < dateTo && appointmentDateTo >=  dateTo)
                || (appointmentDateFrom >= dateFrom && appointmentDateTo <= dateTo);
    }

    private void validateAppointment(ClientAppointment clientAppointment) {
        if (!clientAppointment.getReminders().contains(ClientAppointmentReminder.NEVER) && CollectionUtils.isEmpty(clientAppointment.getNotificationMethods())) {
            throw new ValidationException("Notification methods are required");
        }
        if (clientAppointment.getId() != null && clientAppointment.getStatus() == ClientAppointmentStatus.COMPLETED) {
            if (!clientAppointmentSecurityService.canComplete(clientAppointment.getId())) {
                throw new BusinessException(BusinessExceptionType.APPOINTMENT_COMPLETE_NOT_AVAILABLE);
            }
            if (clientAppointment.getDateFrom().isAfter(Instant.now())) {
                throw new BusinessException(BusinessExceptionType.APPOINTMENT_COMPLETE_NOT_AVAILABLE_IN_FUTURE);
            }
        }
    }

}
