package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ServicePlanFilter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.dto.serviceplan.*;
import com.scnsoft.eldermark.facade.ClientServicePlanFacade;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients/{clientId:\\d+}/service-plans")
public class ClientServicePlanController {

    @Autowired
    private ClientServicePlanFacade clientServicePlanFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientServicePlanListItemDto>> find(@ModelAttribute ServicePlanFilter clientServicePlanFilterDto, @PathVariable Long clientId, Pageable pageRequest) {
        clientServicePlanFilterDto.setClientId(clientId);
        var servicePlan = clientServicePlanFacade.find(clientServicePlanFilterDto, pageRequest);
        return Response.pagedResponse(servicePlan.getContent(), servicePlan.getTotalElements());
    }

    @GetMapping(value = "/{servicePlanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ServicePlanDto> findById(@PathVariable("servicePlanId") Long servicePlanId) {
        return Response.successResponse(clientServicePlanFacade.findById(servicePlanId));
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> save(@PathVariable Long clientId, @Valid @RequestBody ServicePlanDto servicePlanDto) {
        servicePlanDto.setClientId(clientId);
        if (servicePlanDto.getId() == null) {
            return Response.successResponse(clientServicePlanFacade.add(servicePlanDto));
        }
        return Response.successResponse(clientServicePlanFacade.edit(servicePlanDto));
    }

    @GetMapping(value = "/{servicePlanId:\\d+}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ServicePlanHistoryDto>> history(@PathVariable Long servicePlanId, Pageable pageRequest) {
        var servicePlan = clientServicePlanFacade.findHistoryById(servicePlanId, pageRequest);
        return Response.pagedResponse(servicePlan.getContent(), servicePlan.getTotalElements());
    }

    @GetMapping(value = "/any-in-development", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> anyInDevelopment(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientServicePlanFacade.existsUnarchivedInDevelopment(clientId));
    }

    @GetMapping(value = "/in-development", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ServicePlanDto> findInDevelopment(@PathVariable Long clientId) {
        return Response.successResponse(clientServicePlanFacade.findInDevelopment(clientId));
    }

    @GetMapping(value = "/{servicePlanId}/download")
    public void downloadById(@PathVariable("servicePlanId") Long servicePlanId, HttpServletResponse response,
                             @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset,
                             @RequestParam (required = false) List<Long> domainIds) {
        clientServicePlanFacade.writeServicePlanPDFToResponse(servicePlanId, domainIds, response, DateTimeUtils.generateZoneOffset(timeZoneOffset));
    }

    @GetMapping(value = "/{servicePlanId}/domains")
    public Response<List<IdentifiedNamedEntityDto>> domains(@PathVariable Long servicePlanId) {
        return Response.successResponse(clientServicePlanFacade.getDomains(servicePlanId));
    }

    @GetMapping(value = "/count")
    public Response<Long> count(@ModelAttribute ServicePlanFilter clientServicePlanFilter, @PathVariable("clientId") Long clientId) {
        clientServicePlanFilter.setClientId(clientId);
        Long count = clientServicePlanFacade.count(clientServicePlanFilter);
        return Response.successResponse(count);
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientServicePlanFacade.canAdd(clientId));
    }

    @GetMapping(value = "/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canView(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientServicePlanFacade.canView());
    }

    @GetMapping(value = "/controlled", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<ServicePlanDateDto> findControlled(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientServicePlanFacade.findServicePlanForStatusCheck(clientId));
    }

    @GetMapping(value = "/controlled/resource-names", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ServicePlanResourceNameDto>> findControlledResourceNames(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientServicePlanFacade.findServicePlanForStatusCheckResourceNames(clientId));
    }

}
