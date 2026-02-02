package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.web.commons.dto.FileBytesDto;
import com.scnsoft.eldermark.facade.AvatarFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/avatars")
public class AvatarController {

    @Autowired
    private AvatarFacade avatarFacade;

    @GetMapping(value = "/{avatarId}")
    public Response<byte[]> downloadById(@PathVariable("avatarId") Long id) {
        FileBytesDto avatar = avatarFacade.downloadById(id);
        return Response.successResponse(new Response.Body<>(avatar.getBytes(), avatar.getMediaType()));
    }
}
