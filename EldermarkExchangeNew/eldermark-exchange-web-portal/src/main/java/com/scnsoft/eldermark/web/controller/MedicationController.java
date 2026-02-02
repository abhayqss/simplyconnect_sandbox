package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.ClientMedicationFilter;
import com.scnsoft.eldermark.dto.MedicationDto;
import com.scnsoft.eldermark.dto.MedicationListItemDto;
import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledValueEntityDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.ClientMedicationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/medications")
public class MedicationController {

    @Autowired
    private ClientMedicationFacade medicationFacade;

    @GetMapping
    public Response<List<MedicationListItemDto>> find(@PathVariable("clientId") Long clientId,
                                                      @ModelAttribute ClientMedicationFilter filter,
                                                      @PageableDefault(sort = {"medicationStarted"}, direction = Sort.Direction.DESC, size = Integer.MAX_VALUE) Pageable pageRequest) {
        filter.setClientId(clientId);
        return Response.pagedResponse(medicationFacade.find(filter, pageRequest));
    }

    @GetMapping
    @RequestMapping(value = "/{medicationId}", method = RequestMethod.GET)
    public Response<MedicationDto> findById(@PathVariable("medicationId") Long medicationId) {
        return Response.successResponse(medicationFacade.findById(medicationId));
    }

    @GetMapping(value = "/statistics")
    public Response<List<NamedTitledValueEntityDto<Long>>> countGroupedByStatus(@PathVariable("clientId") Long clientId) {
        var filter = new ClientMedicationFilter();
        filter.setClientId(clientId);
        return Response.successResponse(medicationFacade.countGroupedByStatus(filter));
    }

}
