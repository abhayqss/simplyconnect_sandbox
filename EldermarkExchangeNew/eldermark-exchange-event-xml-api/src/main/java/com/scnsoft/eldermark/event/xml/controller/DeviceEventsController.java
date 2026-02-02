package com.scnsoft.eldermark.event.xml.controller;

import com.scnsoft.eldermark.event.xml.dto.DeviceEventProcessingResultDto;
import com.scnsoft.eldermark.event.xml.facade.EventsFacade;
import com.scnsoft.eldermark.event.xml.response.DeviceEventResponseDetails;
import com.scnsoft.eldermark.event.xml.response.DeviceEventsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/device-events")
public class DeviceEventsController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceEventsController.class);

    private final EventsFacade eventsFacade;

    @Autowired
    public DeviceEventsController(EventsFacade eventsFacade) {
        this.eventsFacade = eventsFacade;
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public DeviceEventsResponse receiveDeviceEvents(HttpServletRequest request) throws Exception {
        logger.info("New Device Events come to the system");
        var deviceEventDto = eventsFacade.processDeviceEvents(request);
        return createResponse(deviceEventDto);
    }

    private DeviceEventsResponse createResponse(DeviceEventProcessingResultDto resultDto) {
        final DeviceEventsResponse response = new DeviceEventsResponse();
        response.setCode(0);
        List<DeviceEventResponseDetails> detailsList = new ArrayList<>();
        for (String deviceId : resultDto.getProcessed()) {
            detailsList.add(new DeviceEventResponseDetails(deviceId, "New event was created"));
        }
        for (String deviceId : resultDto.getFailed()) {
            detailsList.add(new DeviceEventResponseDetails(deviceId, "Patient not found"));
        }
        response.setDetails(detailsList);
        return response;
    }
}
