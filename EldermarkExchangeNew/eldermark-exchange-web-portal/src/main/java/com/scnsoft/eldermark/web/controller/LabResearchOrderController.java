package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.beans.LabResearchOrderFilter;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.dto.lab.*;
import com.scnsoft.eldermark.exception.HL7ProcessingException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.facade.LabResearchOrderFacade;
import com.scnsoft.eldermark.service.hl7.ApolloOruProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/lab-research/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class LabResearchOrderController {

    @Autowired
    private LabResearchOrderFacade labResearchOrderFacade;

    @PostMapping
    public Response<Long> add(@RequestBody @Valid LabResearchOrderDto dto) {
        return Response.successResponse(labResearchOrderFacade.add(dto));
    }

    @GetMapping
    public Response<List<LabResearchOrderListItemDto>> find(@ModelAttribute @Valid LabResearchOrderFilter filter, Pageable pageable) {
        return Response.pagedResponse(labResearchOrderFacade.find(filter, pageable));
    }

    @GetMapping("/{id}")
    public Response<LabResearchOrderDto> findById(@PathVariable Long id) {
        return Response.successResponse(labResearchOrderFacade.findById(id));
    }

    @GetMapping("/count")
    public Response<Long> count(@ModelAttribute @Valid LabResearchOrderFilter filter) {
        return Response.successResponse(labResearchOrderFacade.count(filter));
    }

    @GetMapping("/collector-sites")
    public Response<List<IdentifiedTitledEntityDto>> findCollectorSites(@RequestParam("communityId") Long communityId) {
        return Response.successResponse(labResearchOrderFacade.findCollectorSites(communityId));
    }

    @PutMapping("/review")
    public Response<Void> review (@RequestBody @Valid LabResearchReviewDto reviewDto) {
        labResearchOrderFacade.review(reviewDto);
        return Response.successResponse();
    }

    @GetMapping(value = "/{orderId}/test-results")
    public Response<List<LabResearchTestResultListItemDto>> findTestResults(@PathVariable("orderId") Long orderId, Pageable pageable) {
        return Response.pagedResponse(labResearchOrderFacade.findTestResults(orderId, pageable));
    }

    @GetMapping(value = "/pending-review")
    public Response<List<LabResearchOrderBulkReviewListItemDto>> findPendingReview(@RequestParam("organizationId") Long organizationId,
                                                                                   @RequestParam(name = "communityIds", required = false) List<Long> communityIds) {
        return Response.successResponse(labResearchOrderFacade.findPendingReview(organizationId, communityIds));
    }

    @GetMapping("/icd-codes")
    public Response<List<LabIcd10GroupDto>> findIcdGroupCodes() {
        return Response.successResponse(labResearchOrderFacade.findIcdGroupCodes());
    }

    @GetMapping("/specimens-types")
    public Response<List<IdentifiedNamedTitledEntityDto>> findSpecimens() {
        return Response.successResponse(labResearchOrderFacade.findSpecimens());
    }

    @GetMapping(path = "/default")
    public Response<LabResearchOrderDto> findDefault(@RequestParam("clientId") Long clientId) {
        return Response.successResponse(labResearchOrderFacade.findDefault(clientId));
    }

    @GetMapping(value = "/can-add")
    public Response<Boolean> canAdd(@RequestParam("communityId") Long communityId) {
        return Response.successResponse(labResearchOrderFacade.canAddToCommunity(communityId));
    }

    @GetMapping(value = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(labResearchOrderFacade.canView());
    }

    @GetMapping(value = "/validate-uniq-in-organization")
    public Response<LabResearchOrderOrganizationUniquenessDto> validateUniqInOrganization(@RequestParam("requisitionNumber") String requisitionNumber,
                                                                                          @RequestParam("communityId") Long communityId) {
        return Response.successResponse(labResearchOrderFacade.validateUniqueInOrganization(requisitionNumber, communityId));
    }

    @GetMapping(value = "/can-review")
    public Response<Boolean> canReview(@RequestParam("organizationId") Long organizationId) {
        return Response.successResponse(labResearchOrderFacade.canReview(organizationId));
    }


    // ============================================= Testing ==============================================
    @Autowired
    private ApolloOruProcessor apolloOruProcessor;

    @PostMapping(value = "/testing/oru", consumes = MediaType.TEXT_PLAIN_VALUE)
    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMINISTRATOR')")
    public Response<Boolean> submitTestOrm(@RequestBody String oruRaw) {
        var orderOru = apolloOruProcessor.processTesting(oruRaw).get();

        var exception = orderOru.getProcessingException();
        if (exception == null) {
            return Response.successResponse(orderOru.isSuccess());
        }

        if (!(exception instanceof InternalServerException)) {
            exception = new HL7ProcessingException(exception.getMessage());
        }

        return Response.errorResponse((InternalServerException) exception);
    }
}
