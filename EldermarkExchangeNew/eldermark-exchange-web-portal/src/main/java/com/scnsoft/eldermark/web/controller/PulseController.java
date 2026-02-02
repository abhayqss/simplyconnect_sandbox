package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PulseController {

    //use to notify backend that user is still active and performs action on frontend
    //as a result, backend will generate new application token in cookies if X-Auth-With-Cookie
    //header is present
    @GetMapping(path = "/session-pulse")
    public Response<Void> pulse(){
        return Response.successResponse();
    }
}
