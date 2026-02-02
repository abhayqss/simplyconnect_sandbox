package com.scnsoft.eldermark.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.scnsoft.eldermark.beans.NoteStatisticsFilterDto;
import com.scnsoft.eldermark.web.commons.dto.Response;
import com.scnsoft.eldermark.dto.notes.EntityStatisticsDto;
import com.scnsoft.eldermark.facade.NoteFacade;

//@RestController
//@RequestMapping("/clients/{clientId}/note-statistics")
//implement with proper security
public class ClientNoteStatisticsController {

    @Autowired
    private NoteFacade noteFacade;

    @GetMapping("/encounter")
    public Response<List<EntityStatisticsDto>> findByEncounter(@ModelAttribute NoteStatisticsFilterDto filter,
            @PathVariable("clientId") Long clientId) {
        filter.setClientId(clientId);
        return Response.successResponse(noteFacade.getEncounterNoteCount(filter));
    }
}
