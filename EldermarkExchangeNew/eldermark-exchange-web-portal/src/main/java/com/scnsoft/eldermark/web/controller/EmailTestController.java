package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailTestController {

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<Void> test(@RequestParam("sleep") long sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Response.successResponse();
    }
}
