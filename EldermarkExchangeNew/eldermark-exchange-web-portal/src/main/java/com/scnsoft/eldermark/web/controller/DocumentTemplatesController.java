package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.document.BaseDocumentDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureTemplateAssignToFolderDto;
import com.scnsoft.eldermark.facade.signature.DocumentSignatureTemplateFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/document-templates")
public class DocumentTemplatesController {

    @Autowired
    private DocumentSignatureTemplateFacade facade;

    @GetMapping(value = "/{templateId}", produces = MediaType.APPLICATION_JSON_VALUE)
    Response<BaseDocumentDto> findById(@PathVariable Long templateId, @RequestParam Long communityId) {
        return Response.successResponse(facade.findTemplateDocumentById(templateId, communityId));
    }

    @GetMapping(value = "/{templateId}/download")
    void downloadById(
            @PathVariable Long templateId,
            @RequestParam(required = false) Long communityId,
            HttpServletResponse httpServletResponse
    ) {
        facade.downloadTemplateById(templateId, communityId, httpServletResponse);
    }

    @GetMapping(value = "/download")
    void downloadMultipleByIds(
            @RequestParam(name = "ids", required = false) List<Long> templateIds,
            @RequestParam Long communityId,
            HttpServletResponse httpServletResponse
    ) {
        facade.downloadMultipleTemplateByIds(templateIds, communityId, httpServletResponse);
    }

    @PostMapping(value = "/{templateId}/assign")
    Response<Boolean> assignToFolder(@PathVariable Long templateId, @RequestBody DocumentSignatureTemplateAssignToFolderDto dto) {
        dto.setTemplateId(templateId);
        facade.assignToFolder(dto);
        return Response.successResponse(true);
    }
}
