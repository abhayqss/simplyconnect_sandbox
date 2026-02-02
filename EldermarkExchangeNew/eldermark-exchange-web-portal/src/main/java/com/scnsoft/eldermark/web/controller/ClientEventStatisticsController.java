package com.scnsoft.eldermark.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.scnsoft.eldermark.dto.EventStatisticsDto;
import com.scnsoft.eldermark.dto.EventStatisticsFilterDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.ClientEventStatisticsFacade;

@RestController
@RequestMapping("/clients/{clientId}/event-statistics")
public class ClientEventStatisticsController {

    @Autowired
    private ClientEventStatisticsFacade clientEventStatisticsFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Response<List<EventStatisticsDto>> findByAllGroups(@PathVariable Long clientId,
            @RequestBody EventStatisticsFilterDto filter) {
        return Response.successResponse(clientEventStatisticsFacade.findEventGroupCountByClientId(clientId, filter));
    }

}
