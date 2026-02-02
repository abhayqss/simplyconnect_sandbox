package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.RoleDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderItemDto;
import com.scnsoft.eldermark.dto.signature.*;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.facade.document.folder.DocumentFolderFacade;
import com.scnsoft.eldermark.facade.signature.DocumentSignatureRequestFacade;
import com.scnsoft.eldermark.facade.signature.DocumentSignatureTemplateFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/documents/e-sign/")
public class ESignController {

    @Autowired
    private DocumentSignatureTemplateFacade documentSignatureTemplateFacade;

    @Autowired
    private DocumentSignatureRequestFacade documentSignatureRequestFacade;

    @Autowired
    private DocumentFolderFacade documentFolderFacade;

    @GetMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentSignatureTemplateListItemDto>> fetchAllTemplates(
            @RequestParam("communityIds") List<Long> communityIds,
            @RequestParam(value = "isManuallyCreated", required = false) Boolean isManuallyCreated
    ) {
        var templates = documentSignatureTemplateFacade.getAllTemplates(communityIds, isManuallyCreated);
        return Response.successResponse(templates);
    }

    @GetMapping(value = "/organizations", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedTitledEntityDto>> getAvailableOrganizations() {
        return Response.successResponse(documentSignatureRequestFacade.findOrganizationsAvailableForSignatureRequest());
    }

    @GetMapping(value = "/communities", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedTitledEntityDto>> getAvailableCommunities(@RequestParam Long organizationId) {
        return Response.successResponse(documentSignatureRequestFacade.findCommunitiesAvailableForSignatureRequest(organizationId));
    }

    @PostMapping(value = "/templates", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> createTemplate(@ModelAttribute @Valid UploadDocumentSignatureTemplateDto dto) {
        return Response.successResponse(documentSignatureTemplateFacade.uploadTemplate(dto));
    }

    @PutMapping(value = "/templates/{templateId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> updateTemplate(
        @PathVariable("templateId") Long templateId,
        @ModelAttribute @Valid UpdateDocumentSignatureTemplateDto dto
    ) {
        return Response.successResponse(documentSignatureTemplateFacade.updateTemplate(templateId, dto));
    }

    @DeleteMapping(value = "/templates/{templateId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> deleteTemplate(@PathVariable("templateId") Long templateId) {
        documentSignatureTemplateFacade.deleteTemplate(templateId);
        return Response.successResponse();
    }

    @GetMapping(value = "/templates/{templateId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<DocumentSignatureTemplateDto> findById(
            @PathVariable("templateId") Long templateId,
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset
    ) {
        return Response.successResponse(
                documentSignatureTemplateFacade.findByIdWithDefaults(templateId, clientId, timeZoneOffset)
        );
    }

    @PostMapping(value = "/templates/{templateId}/preview")
    public Response<DocumentSignatureTemplatePreviewResponseDto> preview(
            @PathVariable("templateId") Long templateId,
            @RequestBody @Valid DocumentSignatureTemplatePreviewRequestDto dto,
            @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset
    ) {
        dto.setTemplateId(templateId);
        dto.setTimezoneOffset(timeZoneOffset);
        return Response.successResponse(documentSignatureTemplateFacade.getTemplatePreview(dto));
    }

    @GetMapping(value = "/templates/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@RequestParam("communityId") Long communityId) {
        return Response.successResponse(documentSignatureTemplateFacade.countByCommunityId(communityId));
    }

    @PostMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<Long>> submitSignatureRequest(
            @RequestBody @Valid SubmitTemplateSignatureRequestsDto dto,
            @RequestHeader(value = "TimezoneOffset", required = false) Integer timeZoneOffset
    ) {
        dto.setTimezoneOffset(timeZoneOffset);
        return Response.successResponse(documentSignatureRequestFacade.submitRequests(dto));
    }

    @GetMapping(value = "/requests/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<DocumentSignatureRequestInfoDto> findById(@PathVariable("requestId") Long requestId) {
        return Response.successResponse(documentSignatureRequestFacade.findInfoById(requestId));
    }

    @PostMapping(value = "/requests/{requestId}/renew", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> renew(
            @PathVariable("requestId") Long requestId,
            @RequestBody @Valid DocumentSignatureRequestRenewDto dto
    ) {
        dto.setRequestId(requestId);
        return Response.successResponse(documentSignatureRequestFacade.renewRequest(dto));
    }

    @PostMapping(value = "/requests/{requestId}/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> cancelRequest(@PathVariable("requestId") Long requestId) {
        documentSignatureRequestFacade.cancelRequest(requestId);
        return Response.successResponse();
    }

    @PostMapping(value = "/requests/{requestId}/resend-pin", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<DocumentSignatureResendPinResponseDto> resendPin(@PathVariable("requestId") Long requestId) {
        return Response.successResponse(documentSignatureRequestFacade.resendPin(requestId));
    }

    @GetMapping(value = "/requests/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(@Valid DocumentSignatureRequestFilterDto filterDto) {
        return Response.successResponse(documentSignatureRequestFacade.count(filterDto));
    }

    @GetMapping(value = "/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentSignatureHistoryDto>> getHistory(
            @RequestParam Long documentId,
            Pageable pageable,
            @RequestHeader(value = "TimezoneOffset", required = false) Integer timezoneOffset
    ) {
        return Response.pagedResponse(documentSignatureRequestFacade.findHistoryByDocumentId(documentId, pageable, timezoneOffset));
    }

    @GetMapping(value = "/allowed-request-recipient-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<RoleDto>> getAllowedRequestRecipientRoles() {
        return Response.successResponse(documentSignatureRequestFacade.getAllowedRecipientRoles());
    }

    @GetMapping(value = "/default-template-folders", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentFolderItemDto>> getDefaultTemplateFolders(
            @RequestParam(required = false) Long organizationId,
            @RequestParam(required = false) List<Long> communityIds
    ) {
        if (organizationId == null && CollectionUtils.isNotEmpty(communityIds)) {
            throw new ValidationException("At least one of the fields: organizationId, communityIds shouldn't be empty");
        }

        return Response.successResponse(documentFolderFacade.getDefaultTemplateFolders(
                organizationId,
                communityIds
        ));
    }

    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<IdentifiedNamedEntityDto>> getAllowedRequestRecipientContacts(
            @RequestParam Long clientId,
            @RequestParam(required = false) Long documentId
    ) {
        return Response.successResponse(documentSignatureRequestFacade.getAllowedRequestRecipientContacts(clientId, documentId));
    }

    @GetMapping(value = "/requests/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "organizationId", required = false) Long organizationId
    ) {
        if (organizationId != null) {
            return Response.successResponse(documentSignatureRequestFacade.canAddForOrganization(organizationId));
        }
        return Response.successResponse(clientId != null && documentSignatureRequestFacade.canAdd(clientId));
    }

    @GetMapping(value = "/templates/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAddTemplate(@RequestParam(value = "organizationId") Long organizationId) {
        return Response.successResponse(documentSignatureTemplateFacade.canAdd(organizationId));
    }
}
