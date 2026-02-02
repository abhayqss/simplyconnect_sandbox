package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.dto.client.appointment.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface ClientAppointmentFacade {

    Long create(ClientAppointmentDto clientAppointmentDto);

    Long edit(ClientAppointmentDto clientAppointmentDto);

    Page<ClientAppointmentListItemDto> find(ClientAppointmentFilter filter, Pageable pageable);

    ClientAppointmentDto findById(Long appointmentId);

    Long findUnarchivedIdByChainId(Long appointmentChainId);

    void cancel(Long appointmentId, CancelClientAppointmentDto cancelClientAppointmentDto);

    Page<ClientAppointmentHistoryListItemDto> findHistory(Long appointmentId, Pageable pageRequest);

    AppointmentTimeSlotUnavailabilityDto findParticipantsAvailability(Long dateFrom, Long dateTo, Long clientId, List<Long> serviceProviderIds, Long appointmentId, Boolean isExternalProviderServiceProvider, Integer timeZoneOffset);

    void export(ClientAppointmentFilter filter, HttpServletResponse response, Integer timeZoneOffset);

    boolean canAdd(Long clientId);

    Long count(Long organizationId);

    boolean canView();

    boolean canAddInOrganization(Long organizationId);

    AppointmentParticipationDto hasAccessibleExternalProviderNoProviderAppointments(Long organizationId);
}
