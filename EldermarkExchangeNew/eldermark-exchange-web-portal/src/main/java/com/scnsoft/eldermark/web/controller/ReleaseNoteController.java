package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.ReleaseNoteDto;
import com.scnsoft.eldermark.dto.ReleaseNoteListItemDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.ReleaseNoteFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping(value = "/help/release-notes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ReleaseNoteController {

    @Autowired
    private ReleaseNoteFacade releaseNoteFacade;

    @GetMapping
    public Response<List<ReleaseNoteListItemDto>> find() {
        return Response.successResponse(releaseNoteFacade.find());
    }

    @GetMapping(path = "/{id}")
    public Response<ReleaseNoteDto> findById(@PathVariable Long id) {
        return Response.successResponse(releaseNoteFacade.findById(id));
    }

    @GetMapping(path = "/{id}/download")
    public void downloadById(@PathVariable Long id, HttpServletResponse response) {
        releaseNoteFacade.downloadById(id, response);
    }

    @DeleteMapping(path = "/{id}")
    public Response<Boolean> deleteById(@PathVariable Long id) {
        return Response.successResponse(releaseNoteFacade.deleteById(id));
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST})
    public Response<Long> save(@ModelAttribute ReleaseNoteDto dto) {
        return Response.successResponse(releaseNoteFacade.save(dto));
    }

    @GetMapping(path = "/can-upload")
    public Response<Boolean> canUpload() {
        return Response.successResponse(releaseNoteFacade.canUpload());
    }

    @GetMapping(path = "/can-delete")
    public Response<Boolean> canDelete() {
        return Response.successResponse(releaseNoteFacade.canDelete());
    }

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(releaseNoteFacade.canView());
    }
}
