package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.assessment.*;
import com.scnsoft.eldermark.dto.report.InTuneReportCanGenerateDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.ClientAssessmentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/clients/{clientId}/assessments")
public class ClientAssessmentController {

    @Autowired
    private ClientAssessmentFacade clientAssessmentFacade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientAssessmentListItemDto>> find(@PathVariable("clientId") Long clientId,
                                                            @RequestParam(value = "name", required = false) String name,
                                                            Pageable pageRequest) {
        var pageable = clientAssessmentFacade.find(clientId, name, pageRequest);
        return Response.pagedResponse(pageable);
    }

    @GetMapping(value = "/{assessmentId}")
    public Response<ClientAssessmentResultDto> findById(@PathVariable("assessmentId") final Long assessmentId) {
        ClientAssessmentResultDto residentAssessmentResultDto = clientAssessmentFacade
                .findClientAssessmentById(assessmentId);
        return Response.successResponse(residentAssessmentResultDto);
    }

    @GetMapping(value = "/{assessmentId}/export")
    public void export(@PathVariable("assessmentId") final Long assessmentId,
                       @RequestHeader("TimezoneOffset") Integer timeZoneOffset,
                       HttpServletResponse response) {
        clientAssessmentFacade.export(assessmentId, response, timeZoneOffset);
    }

    @GetMapping(value = "/{assessmentId:\\d+}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<ClientAssessmentResultHistoryItemDto>> getAssessmentHistoryList(@PathVariable Long assessmentId,
                                                                                         Pageable pageRequest) {
        return Response.pageResponse(clientAssessmentFacade.findHistoryById(assessmentId, pageRequest));
    }

    @RequestMapping(method = {RequestMethod.POST, RequestMethod.PUT})
    public Response<Long> save(@PathVariable("clientId") Long clientId, @Valid @RequestBody ClientAssessmentResultDto dto) {
        dto.setClientId(clientId);
        if (dto.getId() == null) {
            return Response.successResponse(clientAssessmentFacade.add(dto));
        }
        return Response.successResponse(clientAssessmentFacade.edit(dto));
    }

    @GetMapping(value = "/count")
    public Response<Long> count(@PathVariable("clientId") Long clientId) {
        Long count = clientAssessmentFacade.count(clientId);
        return Response.successResponse(count);
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> find(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientAssessmentFacade.canAdd(clientId));
    }

    @PutMapping(value = "/{assessmentId}/service-plan-need-identification", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> updateServicePlanNeedIdentification(@PathVariable("assessmentId") Long assessmentId,
                                                                 @RequestBody ClientAssessmentResultServicePlanNeedIdentificationDto clientAssessmentResultServicePlanNeedIdentificationDto) {
        clientAssessmentResultServicePlanNeedIdentificationDto.setAssessmentResultId(assessmentId);
        return Response.successResponse(clientAssessmentFacade.updateServicePlanNeedIdentification(clientAssessmentResultServicePlanNeedIdentificationDto));
    }

    @PutMapping(value = "/{assessmentId}/hide", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> hide(@PathVariable("assessmentId") Long assessmentId,
                               @RequestBody CommentDto dto) {
        return Response.successResponse(clientAssessmentFacade.hide(assessmentId, dto.getComment()));
    }

    @PutMapping(value = "/{assessmentId}/restore", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> restore(@PathVariable("assessmentId") Long assessmentId,
                                  @RequestBody CommentDto dto) {
        return Response.successResponse(clientAssessmentFacade.restore(assessmentId, dto.getComment()));
    }

    @GetMapping(value = "/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canView(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientAssessmentFacade.canView());
    }

    @GetMapping("/in-tune-report/download")
    public void downloadInTuneReport(
        @PathVariable("clientId") Long clientId,
        @RequestHeader("TimezoneOffset") Integer timeZoneOffset,
        HttpServletResponse response
    ) {
        clientAssessmentFacade.downloadInTuneReport(clientId, timeZoneOffset, response);
    }

    @GetMapping(value = "/in-tune-report/can-download", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canDownloadInTuneReport(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientAssessmentFacade.canDownloadInTuneReport(clientId));
    }

    @GetMapping(value = "/in-tune-report/can-generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<InTuneReportCanGenerateDto> canGenerateInTuneReport(@PathVariable("clientId") Long clientId) {
        return Response.successResponse(clientAssessmentFacade.canGenerateInTuneReport(clientId));
    }

    @GetMapping(value = "/any-in-process", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> anyInProcess(@PathVariable("clientId") Long clientId, @RequestParam("typeId") Long typeId) {
        return Response.successResponse(clientAssessmentFacade.existsInProcess(clientId, typeId));
    }
}
