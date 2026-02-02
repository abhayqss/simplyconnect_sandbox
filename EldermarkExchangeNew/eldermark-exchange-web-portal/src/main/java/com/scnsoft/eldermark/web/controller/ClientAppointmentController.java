package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.client.appointment.ClientAppointmentDto;
import com.scnsoft.eldermark.facade.ClientAppointmentFacade;
import com.scnsoft.eldermark.validation.ValidationGroups;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients/{clientId}/appointments")
public class ClientAppointmentController {

    @Autowired
    private ClientAppointmentFacade clientAppointmentFacade;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> create(@PathVariable("clientId") Long clientId,
                                 @Validated(ValidationGroups.Create.class) @RequestBody ClientAppointmentDto clientAppointmentDto) {
        clientAppointmentDto.setClientId(clientId);
        return Response.successResponse(clientAppointmentFacade.create(clientAppointmentDto));
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> update(@PathVariable("clientId") Long clientId,
                                 @Validated(ValidationGroups.Update.class) @RequestBody ClientAppointmentDto clientAppointmentDto) {
        clientAppointmentDto.setClientId(clientId);
        return Response.successResponse(clientAppointmentFacade.edit(clientAppointmentDto));
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> find(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientAppointmentFacade.canAdd(clientId));
    }

}
