package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamUpdateReadDto;
import com.scnsoft.eldermark.mobile.dto.home.HomeSectionType;
import com.scnsoft.eldermark.mobile.dto.home.HomeSectionsDto;
import com.scnsoft.eldermark.mobile.facade.HomeFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private HomeFacade homeFacade;

    @GetMapping(value = "/sections", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<HomeSectionsDto> getSections(@RequestParam("sectionTypes") Set<HomeSectionType> sectionTypes) {
        return Response.successResponse(homeFacade.getSections(sectionTypes));
    }

    @PostMapping(value = "/sections/careteam-updates/read", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Void> readCareTeamNotification(@RequestBody CareTeamUpdateReadDto careTeamUpdateReadDto) {
        homeFacade.readCareTeamMemberUpdates(careTeamUpdateReadDto.getCareTeamMemberId());
        return Response.successResponse();
    }
}
