package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.AppointmentContactFilter;
import com.scnsoft.eldermark.beans.ClientAppointmentFilter;
import com.scnsoft.eldermark.dto.NameRoleStatusCommunityDto;
import com.scnsoft.eldermark.dto.client.appointment.*;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.facade.ClientAppointmentFacade;
import com.scnsoft.eldermark.facade.ContactFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private ClientAppointmentFacade clientAppointmentFacade;

    @Autowired
    private ContactFacade contactFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientAppointmentListItemDto>> find(@ModelAttribute @Valid ClientAppointmentFilter filter,
                                                             Pageable pageable) {
        var page = clientAppointmentFacade.find(filter, pageable);
        return Response.pagedResponse(page);
    }

    @GetMapping(value = "/{appointmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ClientAppointmentDto> findById(@PathVariable("appointmentId") Long appointmentId) {
        return Response.successResponse(clientAppointmentFacade.findById(appointmentId));
    }

    @GetMapping(value = "/unarchived-id", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> findUnarchivedIdByChainId(@RequestParam Long appointmentChainId) {
        return Response.successResponse(clientAppointmentFacade.findUnarchivedIdByChainId(appointmentChainId));
    }

    @PostMapping(value = "/{appointmentId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> cancel(@PathVariable("appointmentId") Long appointmentId,
                                    @Valid @RequestBody CancelClientAppointmentDto cancelClientAppointmentDto) {
        clientAppointmentFacade.cancel(appointmentId, cancelClientAppointmentDto);
        return Response.successResponse(true);
    }

    @GetMapping(value = "/{appointmentId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientAppointmentHistoryListItemDto>> findHistory(@PathVariable("appointmentId") Long appointmentId, Pageable pageRequest) {
        var page = clientAppointmentFacade.findHistory(appointmentId, pageRequest);
        return Response.pagedResponse(page);
    }

    @GetMapping(value = "/availability", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<AppointmentTimeSlotUnavailabilityDto> availability(@RequestParam("dateFrom") Long dateFrom,
                                                                       @RequestParam("dateTo") Long dateTo,
                                                                       @RequestParam("clientId") Long clientId,
                                                                       @RequestParam(value = "serviceProviderIds", required = false) List<Long> serviceProviderIds,
                                                                       @RequestParam(value = "appointmentId", required = false) Long appointmentId,
                                                                       @RequestParam(value = "isExternalProviderServiceProvider", required = false) Boolean isExternalProviderServiceProvider,
                                                                       @RequestHeader("timeZoneOffset") Integer timeZoneOffset) {
        var availability = clientAppointmentFacade.findParticipantsAvailability(dateFrom, dateTo, clientId, serviceProviderIds, appointmentId, isExternalProviderServiceProvider, timeZoneOffset);
        return Response.successResponse(availability);
    }

    @GetMapping(value = "/export")
    public void export(@ModelAttribute @Valid ClientAppointmentFilter filter,
                       @RequestHeader("TimezoneOffset") Integer timeZoneOffset,
                       HttpServletResponse response) {
        clientAppointmentFacade.export(filter, response, timeZoneOffset);
    }

    @GetMapping(value = "/count")
    public Response<Long> count(@RequestParam Long organizationId) {
        return Response.successResponse(clientAppointmentFacade.count(organizationId));
    }

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(clientAppointmentFacade.canView());
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> find(@RequestParam Long organizationId) {
        return Response.successResponse(clientAppointmentFacade.canAddInOrganization(organizationId));
    }

    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<NameRoleStatusCommunityDto>> findContactNames(
            @RequestParam Long organizationId,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) List<EmployeeStatus> statuses,
            @RequestParam(required = false) Boolean withAccessibleCreatedAppointments,
            @RequestParam(required = false) Boolean withAccessibleScheduledAppointments,
            @RequestParam(required = false) Long accessibleClientId
    ) {
        var filter = populateContactFilter(
                organizationId,
                roles,
                statuses,
                withAccessibleCreatedAppointments,
                withAccessibleScheduledAppointments,
                accessibleClientId
        );
        return Response.successResponse(contactFacade.findAppointmentContacts(filter));
    }

    @GetMapping(value = "/participation")
    public Response<AppointmentParticipationDto> hasExternalProvidersNoProviders(@RequestParam Long organizationId) {
        return Response.successResponse(clientAppointmentFacade.hasAccessibleExternalProviderNoProviderAppointments(organizationId));
    }

    private AppointmentContactFilter populateContactFilter(
            Long organizationId,
            List<String> roles,
            List<EmployeeStatus> statuses,
            Boolean withAppointmentsCreated,
            Boolean withAccessibleAppointmentsScheduled,
            Long withAccessToClientId
    ) {
        var filter = new AppointmentContactFilter();
        filter.setOrganizationId(organizationId);
        filter.setRoles(
                Stream.ofNullable(roles)
                        .flatMap(Collection::stream)
                        .map(CareTeamRoleCode::getByCode)
                        .collect(Collectors.toList())
        );
        filter.setStatuses(statuses);
        filter.setWithAppointmentsCreated(withAppointmentsCreated);
        filter.setWithAppointmentsScheduled(withAccessibleAppointmentsScheduled);
        filter.setAccessibleClientId(withAccessToClientId);
        return filter;
    }
}
