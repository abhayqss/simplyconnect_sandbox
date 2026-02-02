package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentSecurityFieldsAware;
import com.scnsoft.eldermark.dao.ClientAppointmentDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.specification.ClientAppointmentSpecificationGenerator;
import com.scnsoft.eldermark.dto.appointment.ClientAppointmentExportAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentStatus;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment_;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.basic.BaseAuditableService;
import com.scnsoft.eldermark.service.excel.EntityExportExcelGenerator;
import com.scnsoft.eldermark.service.excel.appointments.AppointmentExcelExportDto;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientAppointmentServiceImpl extends BaseAuditableService<ClientAppointment> implements ClientAppointmentService {

    @Autowired
    private ClientAppointmentDao clientAppointmentDao;

    @Autowired
    private ClientAppointmentSpecificationGenerator clientAppointmentSpecificationGenerator;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private EntityExportExcelGenerator<List<ClientAppointmentExportAware>, ClientAppointmentFilter, AppointmentExcelExportDto> exportExcelGenerator;

    @Autowired
    private ClientAppointmentEventService clientAppointmentEventService;

    private static final Sort APPOINTMENTS_SORT = Sort.by(
            Sort.Order.desc(ClientAppointment_.DATE_FROM),
            Sort.Order.desc(ClientAppointment_.DATE_TO)
    );

    private static final Set<ClientAppointmentStatus> RESCHEDULABLE_STATUSES =
            EnumSet.complementOf(EnumSet.of(
                    ClientAppointmentStatus.ENTERED_IN_ERROR,
                    ClientAppointmentStatus.COMPLETED
            ));

    @Override
    public ClientAppointment save(ClientAppointment clientAppointment) {
        return clientAppointmentDao.save(clientAppointment);
    }

    @Override
    public ClientAppointment findById(Long id) {
        return clientAppointmentDao.findById(id)
                .orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));
    }

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return clientAppointmentDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientAppointmentDao.findByIdIn(ids, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientAppointment> find(ClientAppointmentFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        var byFilter = clientAppointmentSpecificationGenerator.byFilter(filter);
        var unarchived = clientAppointmentSpecificationGenerator.isUnarchived();
        var hasAccess = clientAppointmentSpecificationGenerator.hasAccess(permissionFilter);
        return clientAppointmentDao.findAll(byFilter.and(unarchived).and(hasAccess), pageable);
    }

    @Override
    @Transactional
    public void cancel(Long appointmentId, String cancellationReason, Employee currentEmployee) {
        var clientAppointment = findById(appointmentId);
        validateForCancel(clientAppointment);
        var clone = createTransientClone(clientAppointment);
        clone.setStatus(ClientAppointmentStatus.CANCELLED);
        clone.setCancellationReason(cancellationReason);
        clone.setCanceledBy(currentEmployee);
        var id = updateAuditableEntity(clone);
        clientAppointmentEventService.createAppointmentCanceledEvent(findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientAppointment> findHistory(Long appointmentId, Pageable pageRequest) {
        var historyById = clientAppointmentSpecificationGenerator.historyById(appointmentId);
        return clientAppointmentDao.findAll(historyById, pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientAppointment> findByDatesOverlapsAndAnyParticipants(Long dateFrom, Long dateTo, Long clientId, Collection<Long> employeeIds, Long appointmentId) {
        var unarchived = clientAppointmentSpecificationGenerator.isUnarchived();
        var overlapsPeriod = clientAppointmentSpecificationGenerator.overlapsPeriod(dateFrom, dateTo);
        var byStatuses = clientAppointmentSpecificationGenerator.byStatusIn(List.of(ClientAppointmentStatus.PLANNED, ClientAppointmentStatus.RESCHEDULED, ClientAppointmentStatus.TRIAGED));
        var byClientOrByEmployees = clientAppointmentSpecificationGenerator.byClient(clientId);
        var byIdNot = clientAppointmentSpecificationGenerator.byIdNot(appointmentId);
        if (CollectionUtils.isNotEmpty(employeeIds)) {
            byClientOrByEmployees = byClientOrByEmployees.or(clientAppointmentSpecificationGenerator.byCreatorOrServiceProvidersIds(employeeIds));
        }
        return clientAppointmentDao.findAll(unarchived.and(overlapsPeriod).and(byStatuses).and(byClientOrByEmployees).and(byIdNot));
    }

    @Override
    public AppointmentExcelExportDto getExcel(
            ClientAppointmentFilter filter, PermissionFilter permissionFilter, Integer timeZoneOffset
    ) {
        var byFilter = clientAppointmentSpecificationGenerator.byFilter(filter);
        var hasAccess = clientAppointmentSpecificationGenerator.hasAccessAndPrivateAccess(permissionFilter);
        var isUnarchived = clientAppointmentSpecificationGenerator.isUnarchived();
        var appointments =
                clientAppointmentDao.findAll(byFilter.and(isUnarchived).and(hasAccess), ClientAppointmentExportAware.class, APPOINTMENTS_SORT);
        return exportExcelGenerator.exportToExcel(appointments, filter, timeZoneOffset);
    }

    @Override
    public ClientAppointment createTransientClone(ClientAppointment source) {
        var clone = new ClientAppointment();
        clone.setId(source.getId());
        clone.setTitle(source.getTitle());
        clone.setStatus(source.getStatus());
        clone.setIsPublic(source.getIsPublic());
        clone.setLocation(source.getLocation());
        clone.setType(source.getType());
        clone.setServiceCategory(source.getServiceCategory());
        clone.setReferralSource(source.getReferralSource());
        clone.setReasonForVisit(source.getReasonForVisit());
        clone.setDirectionsInstructions(source.getDirectionsInstructions());
        clone.setNotes(source.getNotes());
        clone.setClient(source.getClient());
        clone.setClientId(source.getClientId());
        clone.setCreator(source.getCreator());
        clone.setCreatorId(source.getCreatorId());
        clone.setServiceProviders(cloneServiceProviders(source.getServiceProviders()));
        clone.setDateFrom(source.getDateFrom());
        clone.setDateTo(source.getDateTo());
        clone.setReminders(new HashSet<>(source.getReminders()));
        clone.setNotificationMethods(new HashSet<>(source.getNotificationMethods()));
        clone.setEmail(source.getEmail());
        clone.setPhone(source.getPhone());
        clone.setCancellationReason(source.getCancellationReason());
        clone.setIsExternalProviderServiceProvider(source.getIsExternalProviderServiceProvider());
        return clone;
    }

    @Override
    public ClientAppointmentSecurityFieldsAware findSecurityAwareEntity(Long appointmentId) {
        return clientAppointmentDao.findById(appointmentId, ClientAppointmentSecurityFieldsAware.class).orElseThrow();
    }

    @Override
    public List<ClientAppointmentSecurityFieldsAware> findSecurityAwareEntities(Collection<Long> appointmentIds) {
        return clientAppointmentDao.findByIdIn(appointmentIds, ClientAppointmentSecurityFieldsAware.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validForCancel(ClientAppointmentStatus status, Instant dateTo) {
        return (status == ClientAppointmentStatus.PLANNED || status == ClientAppointmentStatus.RESCHEDULED)
                && dateTo.isAfter(Instant.now());
    }

    @Override
    public boolean validForEdit(ClientAppointmentStatus status) {
        return status != ClientAppointmentStatus.CANCELLED;
    }

    @Override
    public boolean validForDuplicate(ClientAppointmentStatus status) {
        return status != ClientAppointmentStatus.CANCELLED;
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(Long organizationId) {
        var byClientOrganizationId =
                clientAppointmentSpecificationGenerator.byOrganizationId(organizationId);
        var byStatusIn = clientAppointmentSpecificationGenerator.byStatusIn(List.of(ClientAppointmentStatus.PLANNED, ClientAppointmentStatus.TRIAGED));
        var dateFrom = clientAppointmentSpecificationGenerator.dateFrom(Instant.now());
        var unarchived = clientAppointmentSpecificationGenerator.isUnarchived();
        var specification = byClientOrganizationId.and(byStatusIn).and(dateFrom).and(unarchived);
        return clientAppointmentDao.count(specification);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllFutureByOrganizationId(Long organizationId, Class<P> projectionClass) {
        var byClientOrganizationId =
                clientAppointmentSpecificationGenerator.byOrganizationId(organizationId);
        var byStatusIn = clientAppointmentSpecificationGenerator.byStatusIn(List.of(ClientAppointmentStatus.PLANNED, ClientAppointmentStatus.TRIAGED));
        var dateFrom = clientAppointmentSpecificationGenerator.dateFrom(Instant.now());
        var specification = byClientOrganizationId.and(byStatusIn.and(dateFrom));

        return clientAppointmentDao.findAll(specification, projectionClass);
    }

    @Override
    public Long findUnarchivedIdByChainId(Long chainId) {

        return clientAppointmentDao.findFirst(
                        clientAppointmentSpecificationGenerator.historyByChainId(chainId)
                                .and(clientAppointmentSpecificationGenerator.isUnarchived()),
                        IdAware.class
                )
                .map(IdAware::getId)
                .orElse(null);
    }

    @Override
    public Long create(ClientAppointment clientAppointment) {
        validateForCreate(clientAppointment);
        validateAvailability(clientAppointment);
        var id = createAuditableEntity(clientAppointment);
        clientAppointmentEventService.createAppointmentCreatedEvent(findById(id));
        return id;
    }

    @Override
    public Long edit(ClientAppointment clientAppointment) {
        var previousId = clientAppointment.getId();
        var dbAppointment = findById(previousId);
        validateForEdit(dbAppointment, clientAppointment.getStatus());
        if (clientAppointment.getStatus() != ClientAppointmentStatus.COMPLETED) {
            validateAvailability(clientAppointment);
        }
        if (isRescheduled(dbAppointment, clientAppointment)) {
            clientAppointment.setStatus(ClientAppointmentStatus.RESCHEDULED);
        }
        var newId = updateAuditableEntity(clientAppointment);
        clientAppointmentEventService.createAppointmentUpdatedEvent(dbAppointment, findById(newId));
        if (clientAppointment.getStatus() == ClientAppointmentStatus.COMPLETED
                && dbAppointment.getStatus() != ClientAppointmentStatus.COMPLETED) {
            clientAppointmentEventService.createAppointmentCompletedEvent(clientAppointment);
        }
        return newId;
    }

    @Override
    @Transactional(readOnly = true)
    public Pair<Boolean, Boolean> hasAccessibleExternalProviderNoProviderAppointments(Long organizationId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var byClientOrganizationId = clientAppointmentSpecificationGenerator.byOrganizationId(organizationId);
        var unarchived = clientAppointmentSpecificationGenerator.isUnarchived();
        var hasAccess = clientAppointmentSpecificationGenerator.hasAccess(permissionFilter);
        var specification = byClientOrganizationId.and(hasAccess).and(unarchived);

        var withExternal = clientAppointmentSpecificationGenerator.withExternalServiceProvider();
        var hasExternal = clientAppointmentDao.exists(specification.and(withExternal));

        var withNoProviders = clientAppointmentSpecificationGenerator.withNoServiceProviders();
        var hasNoProviders = clientAppointmentDao.exists(specification.and(withNoProviders));

        return new Pair<>(hasExternal, hasNoProviders);
    }

    private List<Employee> cloneServiceProviders(List<Employee> serviceProviders) {
        return serviceProviders.stream().map(Employee::getId).map(employeeDao::getOne).collect(Collectors.toList());
    }

    private void validateForCancel(ClientAppointment clientAppointment) {
        if (!validForCancel(clientAppointment.getStatus(), clientAppointment.getDateTo())) {
            throw new BusinessException(BusinessExceptionType.APPOINTMENT_CANCEL_NOT_AVAILABLE);
        }
    }

    private void validateForEdit(ClientAppointment clientAppointment, ClientAppointmentStatus updatedStatus) {
        if (!validForEdit(clientAppointment.getStatus()) || updatedStatus == ClientAppointmentStatus.CANCELLED) {
            throw new BusinessException(BusinessExceptionType.APPOINTMENT_EDIT_NOT_AVAILABLE);
        }
    }

    private void validateAvailability(ClientAppointment clientAppointment) {
        var employeeIds = CollectionUtils.isEmpty(clientAppointment.getServiceProviderIds()) ? Set.of(clientAppointment.getCreatorId()) : clientAppointment.getServiceProviderIds();
        var overlappedAppointments = findByDatesOverlapsAndAnyParticipants(DateTimeUtils.toEpochMilli(clientAppointment.getDateFrom()),
                DateTimeUtils.toEpochMilli(clientAppointment.getDateTo()),
                clientAppointment.getClientId(),
                employeeIds,
                clientAppointment.getId());
        if (!((CollectionUtils.isEmpty(overlappedAppointments)) || (overlappedAppointments.size() == 1 && overlappedAppointments.get(0).getId().equals(clientAppointment.getId())))) {
            throw new BusinessException(BusinessExceptionType.APPOINTMENT_OVERLAPS_EXISTING);
        }
    }

    private void validateForCreate(ClientAppointment clientAppointment) {
        var status = clientAppointment.getStatus();
        if (!(status == ClientAppointmentStatus.PLANNED || status == ClientAppointmentStatus.RESCHEDULED || status == ClientAppointmentStatus.TRIAGED)) {
            throw new BusinessException(BusinessExceptionType.APPOINTMENT_CREATE_NOT_AVAILABLE);
        }
    }

    private boolean isRescheduled(ClientAppointment dbAppointment, ClientAppointment clientAppointment) {
        var isAppointmentDateChanged = !dbAppointment.getDateTo().equals(clientAppointment.getDateTo())
                || !dbAppointment.getDateFrom().equals(clientAppointment.getDateFrom());
        var isStatusChanged = !dbAppointment.getStatus().equals(clientAppointment.getStatus());
        var isStatusReschedulable = RESCHEDULABLE_STATUSES.contains(dbAppointment.getStatus());

        return isAppointmentDateChanged && !isStatusChanged && isStatusReschedulable;
    }
}
