package com.scnsoft.exchange.audit.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/version")
public class BuildNumberController {

    public @Value("${buildNumber}") String buildNumber;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public String getBuildNumber() {
        return buildNumber;
    }
}
