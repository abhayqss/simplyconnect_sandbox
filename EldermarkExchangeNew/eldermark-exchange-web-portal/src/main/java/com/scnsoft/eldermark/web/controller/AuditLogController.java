package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.audit.AuditLogFilterDto;
import com.scnsoft.eldermark.dto.AuditLogListItemDto;
import com.scnsoft.eldermark.facade.AuditLogFacade;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    @Autowired
    private AuditLogFacade auditLogFacade;

//    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public Response<List<AuditLogListItemDto>> find(@RequestBody @Valid AuditLogFilterDto filter, Pageable pageRequest, @RequestHeader("timeZoneOffset") Integer timeZoneOffset) {
//        filter.setZoneId(DateTimeUtils.generateZoneOffset(timeZoneOffset));
//        var pageable = auditLogFacade.find(filter, pageRequest);
//        return Response.pagedResponse(pageable.getContent(), pageable.getTotalElements());
//    }

//    @GetMapping(value = "/oldest-date", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Response<Long> findOldestDate(@RequestParam(value = "organizationId") Long organizationId) {
//        return Response.successResponse(auditLogFacade.findOldestDateByOrganization(organizationId));
//    }

//    @GetMapping(value = "/newest-date", produces = MediaType.APPLICATION_JSON_VALUE)
//    public Response<Long> findNewestDate(@RequestParam(value = "organizationId") Long organizationId) {
//        return Response.successResponse(auditLogFacade.findNewestDateByOrganization(organizationId));
//    }

//    @GetMapping(value = "/can-view")
//    public Response<Boolean> canView() {
//        return Response.successResponse(auditLogFacade.canViewList());
//    }

    @GetMapping(value = "/export")
    public void export(@ModelAttribute @Valid AuditLogFilterDto filter, @RequestHeader("timeZoneOffset") Integer timeZoneOffset, HttpServletResponse response) {
        filter.setZoneId(DateTimeUtils.generateZoneOffset(timeZoneOffset));
        auditLogFacade.export(filter, response);
    }
}
