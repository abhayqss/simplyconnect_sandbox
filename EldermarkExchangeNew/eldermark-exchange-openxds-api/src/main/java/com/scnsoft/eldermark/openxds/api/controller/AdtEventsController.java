package com.scnsoft.eldermark.openxds.api.controller;

import com.scnsoft.eldermark.openxds.api.dto.AdtDto;
import com.scnsoft.eldermark.openxds.api.facade.AdtEventFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class AdtEventsController {

    private static final Logger logger = LoggerFactory.getLogger(AdtEventsController.class);

    @Autowired
    private AdtEventFacade adtEventFacade;

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "events/adt", method = RequestMethod.POST, consumes = "application/x-www-form-urlencoded")
    @ResponseBody
    public void postAdtEvent(@Valid AdtDto adtDto) {
        logger.info("ADT Message Came to the System. Generate Encounter-ADT event. Dto: " + adtDto);
        adtEventFacade.create(adtDto);
    }

}
