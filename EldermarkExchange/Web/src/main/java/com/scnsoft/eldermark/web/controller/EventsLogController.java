package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by pzhurba on 01-Oct-15.
 */
//@Controller
//@RequestMapping(value = "/events-log")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class EventsLogController {
    @RequestMapping(method = RequestMethod.GET)
    public String getView() {
        return "events.log";
    }

}
