package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.facade.AvatarFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/avatars")
public class AvatarController {

    @Autowired
    private AvatarFacade avatarFacade;

    @GetMapping(value = "/{avatarId}")
    public void downloadById(@PathVariable("avatarId") Long id, HttpServletResponse response) {
        avatarFacade.downloadById(id, response);
    }

}
