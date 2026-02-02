package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.dto.UserManualDocumentDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.facade.UserManualFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/help/user-manuals")
public class UserManualController {

    @Autowired
    private UserManualFacade userManualFacade;

    @GetMapping
    public Response<List<UserManualDocumentDto>> find() {
        return Response.successResponse(userManualFacade.find());
    }

    @GetMapping(path = "/{id}")
    public void downloadById(@PathVariable Long id, HttpServletResponse response) {
        userManualFacade.downloadById(id, response);
    }

    @DeleteMapping(path = "/{id}")
    public Response<Boolean> deleteById(@PathVariable Long id) {
        return Response.successResponse(userManualFacade.deleteById(id));
    }

    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST})
    public Response<Long> save(@RequestParam(required = false) Long id, @ModelAttribute UserManualDocumentDto dto) {
        if (id == null) {
            return Response.successResponse(userManualFacade.upload(dto));
        }
        return Response.successResponse(userManualFacade.editById(dto, id));
    }


    @GetMapping(path = "/can-upload")
    public Response<Boolean> canUpload() {
        return Response.successResponse(userManualFacade.canUpload());
    }

    @GetMapping(path = "/can-delete")
    public Response<Boolean> canDelete() {
        return Response.successResponse(userManualFacade.canDelete());
    }

    @GetMapping(path = "/can-view")
    public Response<Boolean> canView() {
        return Response.successResponse(userManualFacade.canView());
    }
}
