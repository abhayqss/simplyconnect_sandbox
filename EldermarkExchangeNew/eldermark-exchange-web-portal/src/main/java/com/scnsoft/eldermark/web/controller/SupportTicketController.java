package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.config.StringCrlfToLfFormatter;
import com.scnsoft.eldermark.dto.support.SupportTicketDto;
import com.scnsoft.eldermark.facade.SupportTicketFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/support-tickets")
public class SupportTicketController {

    @Autowired
    private SupportTicketFacade supportTicketFacade;

    @InitBinder
    private void initBinder(WebDataBinder dataBinder) {
        dataBinder.addCustomFormatter(new StringCrlfToLfFormatter(), String.class);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> create(@Valid @ModelAttribute SupportTicketDto dto) {
        return Response.successResponse(supportTicketFacade.create(dto));
    }
}
