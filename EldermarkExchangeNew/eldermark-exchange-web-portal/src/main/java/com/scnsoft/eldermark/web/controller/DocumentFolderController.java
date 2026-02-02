package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.document.folder.DocumentFolderDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderFilterDto;
import com.scnsoft.eldermark.dto.document.folder.DocumentFolderItemDto;
import com.scnsoft.eldermark.dto.document.folder.PermissionContactDto;
import com.scnsoft.eldermark.facade.document.folder.DocumentFolderFacade;
import com.scnsoft.eldermark.service.document.folder.PermissionContactFilter;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/document-folders")
public class DocumentFolderController {

    @Autowired
    private DocumentFolderFacade folderFacade;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> addFolder(@Valid @ModelAttribute DocumentFolderDto folder) {
        return Response.successResponse(folderFacade.add(folder));
    }

    @PutMapping(value = "/{folderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> editFolder(
            @Valid @ModelAttribute DocumentFolderDto folder,
            @PathVariable Long folderId
    ) {
        folder.setId(folderId);
        return Response.successResponse(folderFacade.edit(folder));
    }

    @GetMapping(value = "/{folderId}")
    public Response<DocumentFolderDto> findById(@PathVariable Long folderId) {
        return Response.successResponse(folderFacade.findById(folderId));
    }

    @GetMapping(value = "/default", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<DocumentFolderDto> getDefaultFolder(
        @RequestParam(value = "folderId", required = false) Long folderId,
        @RequestParam(value = "parentFolderId", required = false) Long parentFolderId,
        @RequestParam(value = "communityId") Long communityId,
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "isSecurityEnabled", required = false) Boolean isSecurityEnabled
    ) {
        return Response.successResponse(folderFacade.getDefaultFolder(
            folderId,
            parentFolderId,
            communityId,
            name != null ? name : "",
            isSecurityEnabled != null ? isSecurityEnabled : false
        ));
    }

    @GetMapping(value = "/validate-uniq", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> validateUniq(
        @RequestParam(value = "id", required = false) Long id,
        @RequestParam(value = "parentId", required = false) Long parentId,
        @RequestParam(value = "communityId") Long communityId,
        @RequestParam(value = "name") String name
    ) {
        return Response.successResponse(folderFacade.validateUniqueness(id, parentId, communityId, name));
    }

    @GetMapping(value = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<PermissionContactDto>> getContacts(
        @RequestParam(value = "folderId", required = false) Long folderId,
        @RequestParam(value = "parentFolderId", required = false) Long parentFolderId,
        @RequestParam(value = "communityId") Long communityId,
        PermissionContactFilter filter,
        Pageable pageable
    ) {
        return Response.pagedResponse(folderFacade.getContacts(folderId, parentFolderId, communityId, filter, pageable));
    }

    @GetMapping(value = "/{folderId}/download")
    public void download(@PathVariable(value = "folderId") Long folderId, HttpServletResponse httpResponse) {
        folderFacade.download(folderId, httpResponse);
    }

    @GetMapping(value = "/can-add", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canAdd(
        @RequestParam(value = "parentFolderId", required = false) Long parentFolderId,
        @RequestParam(value = "communityId") Long communityId
    ) {
        return Response.successResponse(folderFacade.canAdd(parentFolderId, communityId));
    }

    @GetMapping(value = "/can-view", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Boolean> canView(
        @RequestParam(value = "folderId", required = false) Long folderId,
        @RequestParam(value = "communityId") Long communityId
    ) {
        return Response.successResponse(folderFacade.canView(folderId, communityId));
    }

    @DeleteMapping(value = "/{folderId}")
    public Response<Void> deleteById(@PathVariable(value = "folderId") Long folderId, @RequestParam(value = "isTemporaryDeletion") boolean isTemporary) {
        folderFacade.delete(folderId, isTemporary);
        return Response.successResponse();
    }

    @PostMapping(value = "/{folderId}/restore")
    public Response<Void> restoreById(@PathVariable(value = "folderId") Long folderId) {
        folderFacade.restore(folderId);
        return Response.successResponse();
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentFolderItemDto>> getList(@Valid DocumentFolderFilterDto filterDto) {
        return Response.successResponse(folderFacade.getList(filterDto));
    }
}
