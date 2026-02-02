package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.reports.enums.ReportType;
import com.scnsoft.eldermark.dto.report.ReportFilterDto;
import com.scnsoft.eldermark.facade.ReportsFacade;
import com.scnsoft.eldermark.service.security.ReportSecurityService;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportsFacade reportsFacade;

    @Autowired
    private ReportSecurityService reportSecurityService;

    @GetMapping(path = "/{reportType}")
    public void download(@Valid @ModelAttribute ReportFilterDto filter, HttpServletResponse response,
                         @PathVariable("reportType") ReportType reportType,
                         @RequestHeader("TimezoneOffset") Integer timeZoneOffset) {
        filter.setReportType(reportType);
        filter.setTimezoneOffset(timeZoneOffset);
        reportsFacade.downloadReport(filter, response);
    }

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(reportSecurityService.canGenerate());
    }
}
