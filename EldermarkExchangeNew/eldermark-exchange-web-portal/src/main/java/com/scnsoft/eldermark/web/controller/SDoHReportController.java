package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.SDoHReportListItemDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.SDoHReportFacade;
import com.scnsoft.eldermark.service.report.SdohReportLogServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/sdoh/reports")
public class SDoHReportController {

    @Autowired
    private SDoHReportFacade sDoHReportFacade;

    @GetMapping
    public Response<List<SDoHReportListItemDto>> find(@RequestParam("organizationId") Long organizationId, Pageable pageable){
        return Response.pagedResponse(sDoHReportFacade.find(organizationId, pageable));
    }

    @GetMapping(path = "/{reportId}/download-xlsx")
    public void downloadXslx(@PathVariable("reportId") Long reportId, HttpServletResponse response) {
        sDoHReportFacade.downloadXlsx(reportId, response);
    }

    @GetMapping(path = "/{reportId}/download-zip")
    public void downloadZip(@PathVariable("reportId") Long reportId, HttpServletResponse response) {
        sDoHReportFacade.downloadZip(reportId, response);
    }

    @PutMapping(path = "/{reportId}/mark-as-sent")
    public Response<Void> markAsSent(@PathVariable("reportId") Long reportId) {
        sDoHReportFacade.markAsSent(reportId);
        return Response.successResponse();
    }

    @GetMapping(path = "/{reportId}/can-mark-as-sent")
    public Response<Boolean> canMarkAsSent(@PathVariable("reportId") Long reportId) {
        return Response.successResponse(sDoHReportFacade.canMarkAsSent(reportId));
    }

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(sDoHReportFacade.canView());
    }


    //============================================== Testing ===========================================================

    @PostMapping("/testing/set-time")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<Boolean> setTime(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestParam("dayOfMonth") int dayOfMonth,
            @RequestParam("hour") int hour,
            @RequestParam("minute") int minute,
            @RequestParam("second") int second,
            @RequestParam("zoneId") String zone
    ) {
        var zoneId = ZoneId.of(zone);
        var dt = LocalDateTime.of(year, month, dayOfMonth, hour, minute, second).atZone(zoneId).toInstant();

        var clock = Clock.fixed(dt, ZoneOffset.UTC);

        SdohReportLogServiceImpl.setCLOCK(clock);
        return Response.successResponse();
    }

    @PostMapping("/testing/reset-time")
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<Boolean> resetTime() {
        SdohReportLogServiceImpl.setCLOCK(Clock.systemUTC());
        return Response.successResponse();
    }
}
