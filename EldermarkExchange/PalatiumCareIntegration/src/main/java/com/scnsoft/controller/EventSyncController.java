package com.scnsoft.controller;

import com.scnsoft.dto.incoming.PalCareEventDto;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.service.PalCareEventService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.net.HttpURLConnection;


@Api(value = "Event Controller", description = "Event Controller")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized")
})
@RestController
@RequestMapping("api-v2/event")
public class EventSyncController {

    private PalCareEventService palCareEventService;

    //@todo: get  rid of it! just a hotfix!
    @Autowired
    private com.scnsoft.eldermark.services.carecoordination.EventService eventService;

    @Autowired
    public void setPalCareEventService(PalCareEventService palCareEventService) {
        this.palCareEventService = palCareEventService;
    }


    @ApiOperation(value = "Creates event")
    @RequestMapping(method = RequestMethod.POST)
    public Response createEvent(@RequestBody PalCareEventDto event) {
        palCareEventService.save(event);
        return Response.successResponse();
    }


    @ApiOperation(value = "Deletes event", notes = "Removes event by event id")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void removeEvent(
            @ApiParam(value = "event id", required = true)
            @PathVariable("id") Long id) {
    }



}
