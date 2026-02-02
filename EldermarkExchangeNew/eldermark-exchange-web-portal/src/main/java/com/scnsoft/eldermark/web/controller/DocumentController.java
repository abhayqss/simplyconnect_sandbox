package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.EditCommunityDocumentDto;
import com.scnsoft.eldermark.dto.UploadDocumentDto;
import com.scnsoft.eldermark.dto.document.DocumentDto;
import com.scnsoft.eldermark.facade.DocumentFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentFacade facade;

    @PostMapping
    public Response<Long> upload(@Valid @ModelAttribute UploadDocumentDto uploadDto) {
        return Response.successResponse(facade.save(uploadDto));
    }

    @GetMapping("/can-add")
    public Response<Boolean> canAdd(
        @RequestParam(value = "communityId", required = false) Long communityId,
        @RequestParam(value = "folderId", required = false) Long folderId,
        @RequestParam(value = "clientId", required = false) Long clientId
    ) {
        return Response.successResponse(facade.canAdd(communityId, folderId, clientId));
    }

    @GetMapping(value = "/{documentId}")
    public Response<DocumentDto> findById(@PathVariable(value = "documentId") Long documentId) {
        return Response.successResponse(facade.findById(documentId));
    }

    @GetMapping(value = "/{documentId}/download")
    public void download(@PathVariable(value = "documentId") Long documentId, HttpServletResponse response) {
        facade.download(documentId, response, false);
    }

    @DeleteMapping(value = "/{documentId}")
    public Response<Void> deleteById(
        @PathVariable(value = "documentId") Long documentId,
        @RequestParam(value = "isTemporaryDeletion", required = false) boolean isTemporary
    ) {
        facade.deleteById(documentId, isTemporary);
        return Response.successResponse();
    }

    @PostMapping(value = "/{documentId}/restore")
    public Response<Void> restoreById(@PathVariable(value = "documentId") Long documentId) {
        facade.restoreById(documentId);
        return Response.successResponse();
    }

    @PutMapping
    public Response<Long> edit(@ModelAttribute @Valid EditCommunityDocumentDto editDocumentDto) {
        return Response.successResponse(facade.edit(editDocumentDto));
    }

}
