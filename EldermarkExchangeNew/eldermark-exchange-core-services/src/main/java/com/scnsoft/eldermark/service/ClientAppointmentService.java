package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientAppointmentSecurityFieldsAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointment;
import com.scnsoft.eldermark.entity.client.appointment.ClientAppointmentStatus;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;
import com.scnsoft.eldermark.service.excel.appointments.AppointmentExcelExportDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface ClientAppointmentService extends AuditableEntityService<ClientAppointment>, ProjectingService<Long>, SecurityAwareEntityService<ClientAppointmentSecurityFieldsAware, Long> {

    Page<ClientAppointment> find(ClientAppointmentFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    void cancel(Long appointmentId, String cancellationReason, Employee currentEmployee);

    Page<ClientAppointment> findHistory(Long appointmentId, Pageable pageRequest);

    boolean validForCancel(ClientAppointmentStatus status, Instant dateTo);

    boolean validForEdit(ClientAppointmentStatus status);

    boolean validForDuplicate(ClientAppointmentStatus status);

    List<ClientAppointment> findByDatesOverlapsAndAnyParticipants(Long dateFrom, Long dateTo, Long clientId, Collection<Long> employeeIds, Long appointmentId);

    AppointmentExcelExportDto getExcel(
            ClientAppointmentFilter appointmentFilter, PermissionFilter permissionFilter, Integer timeZoneOffset
    );

    <P> List<P> findAllFutureByOrganizationId(Long organizationId, Class<P> projectionClass);

    Long count(Long organizationId);

    Long create(ClientAppointment clientAppointment);

    Long edit(ClientAppointment clientAppointment);

    Pair<Boolean, Boolean> hasAccessibleExternalProviderNoProviderAppointments(Long organizationId);

    Long findUnarchivedIdByChainId(Long chainId);
}
