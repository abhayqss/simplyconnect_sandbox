package com.scnsoft.eldermark.web.controller;


import com.scnsoft.eldermark.dto.TransportationDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.TransportationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/transportation")
public class TransportationController {

    @Autowired
    private TransportationFacade transportationFacade;

    @GetMapping(value = "/rides/request")
    public Response<TransportationDto> rideRequest(@RequestParam Long clientId) {
        return Response.successResponse(transportationFacade.requestNewRide(clientId));
    }

    @GetMapping(value = "/rides/history")
    public Response<TransportationDto> rideHistory(@RequestParam Long clientId) {
        return Response.successResponse(transportationFacade.rideHistory(clientId));
    }

    @GetMapping(value = "/rides/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(transportationFacade.hasAccess());
    }
}
