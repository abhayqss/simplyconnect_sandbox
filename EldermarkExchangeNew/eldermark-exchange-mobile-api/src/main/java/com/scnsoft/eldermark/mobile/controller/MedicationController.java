package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.medication.MedicationSearchResultDto;
import com.scnsoft.eldermark.mobile.dto.medication.SearchMedicationFilter;
import com.scnsoft.eldermark.mobile.facade.MedicationFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medications")
public class MedicationController {

    @Autowired
    private MedicationFacade medicationFacade;

    @GetMapping
    public Response<List<MedicationSearchResultDto>> find(@ModelAttribute SearchMedicationFilter filter) {
        return Response.successResponse(medicationFacade.find(filter));
    }

    @GetMapping("/{mediSpanId}")
    public Response<MedicationSearchResultDto> findByMediSpanId(@PathVariable String mediSpanId) {
        return Response.successResponse(medicationFacade.findByMediSpanId(mediSpanId));
    }

}
