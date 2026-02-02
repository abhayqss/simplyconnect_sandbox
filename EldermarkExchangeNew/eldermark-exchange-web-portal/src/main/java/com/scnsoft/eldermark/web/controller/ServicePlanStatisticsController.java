package com.scnsoft.eldermark.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.scnsoft.eldermark.dto.StatusCountDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.ServicePlanStatisticsFacade;

@RestController
@RequestMapping("/service-plans")
public class ServicePlanStatisticsController {

    @Autowired
    private ServicePlanStatisticsFacade servicePlanFacade;

    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Response<Long> count() {
        return Response.successResponse(servicePlanFacade.count());
    }

    @RequestMapping(value = "/status-count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<StatusCountDto>> countGroupedByStatus() {
        return Response.successResponse(servicePlanFacade.countGroupedByStatus());
    }

}
