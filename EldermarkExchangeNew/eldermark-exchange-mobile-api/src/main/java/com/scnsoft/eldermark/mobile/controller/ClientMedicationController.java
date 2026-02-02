package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationDto;
import com.scnsoft.eldermark.mobile.dto.ccd.medication.MedicationListItemDto;
import com.scnsoft.eldermark.mobile.facade.ClientMedicationFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/medications")
public class ClientMedicationController {

    @Autowired
    private ClientMedicationFacade medicationFacade;

    @GetMapping
    public Response<List<MedicationListItemDto>> find(@PathVariable("clientId") Long clientId,
                                                      @ModelAttribute ClientMedicationFilter filter,
                                                      @PageableDefault(sort = {"medicationStarted"},
                                                              direction = Sort.Direction.DESC, size = Integer.MAX_VALUE)
                                                              Pageable pageRequest) {
        filter.setClientId(clientId);
        return Response.pagedResponse(medicationFacade.find(filter, pageRequest));
    }

    @GetMapping
    @RequestMapping(value = "/count-grouped", method = RequestMethod.GET)
    public Response<List<NamedTitledValueEntityDto<Long>>> countGroupedByStatus(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(medicationFacade.countGroupedByStatus(clientId));
    }

    @GetMapping
    @RequestMapping(value = "/{medicationId}", method = RequestMethod.GET)
    public Response<MedicationDto> findById(@PathVariable("medicationId") Long medicationId) {
        return Response.successResponse(medicationFacade.findById(medicationId));
    }

    @PostMapping
    public Response<Long> save(@PathVariable("clientId") Long clientId,
                               @RequestBody MedicationDto dto) {
        dto.setClientId(clientId);
        return Response.successResponse(medicationFacade.save(dto));
    }

    @PostMapping("/{medicationId}")
    public Response<Long> save(@PathVariable("clientId") Long clientId,
                               @PathVariable("medicationId") Long medicationId,
                               @RequestBody MedicationDto dto) {
        dto.setClientId(clientId);
        dto.setId(medicationId);
        return Response.successResponse(medicationFacade.save(dto));
    }

    @GetMapping
    @RequestMapping(value = "/can-view", method = RequestMethod.GET)
    public Response<Boolean> canView(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(medicationFacade.canView(clientId));
    }

    @GetMapping
    @RequestMapping(value = "/can-add", method = RequestMethod.GET)
    public Response<Boolean> canAdd(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(medicationFacade.canAdd(clientId));
    }
}
