package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.document.CommunityDocumentFilterDto;
import com.scnsoft.eldermark.dto.document.DocumentAndFolderItemDto;
import com.scnsoft.eldermark.facade.document.CommunityDocumentAndFolderFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@RestController
@RequestMapping("/documents-&-folders")
public class DocumentAndFolderController {

    @Autowired
    private CommunityDocumentAndFolderFacade facade;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocumentAndFolderItemDto>> find(
        @ModelAttribute @Valid CommunityDocumentFilterDto documentFilter,
        Pageable pageRequest
    ) {
        return Response.pagedResponse(facade.find(documentFilter, pageRequest));
    }

    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> count(
            @ModelAttribute @Valid CommunityDocumentFilterDto documentFilter
    ) {
        return Response.successResponse(facade.count(documentFilter));
    }

    @GetMapping("download")
    public void downloadMultiple(
        @RequestParam("ids") @NotEmpty List<String> ids,
        HttpServletResponse httpResponse
    ) {
        facade.download(ids, httpResponse);
    }

    @GetMapping("oldest/date")
    public Response<Long> getOldestDate(@RequestParam("communityId") Long communityId) {
        return Response.successResponse(facade.getOldestDate(communityId));
    }

    @GetMapping("/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(facade.canViewList());
    }
}
