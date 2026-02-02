package com.scnsoft.eldermark.web.controller.carecoordination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.NoteSubType;
import com.scnsoft.eldermark.services.carecoordination.EncounterNoteTypeService;
import com.scnsoft.eldermark.services.carecoordination.EventService;
import com.scnsoft.eldermark.services.carecoordination.NoteDetailsService;
import com.scnsoft.eldermark.services.carecoordination.NoteService;
import com.scnsoft.eldermark.services.carecoordination.NoteSubTypeService;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteEventDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.notes.NoteSubTypeDto;

//@Controller
//@RequestMapping(value = "/care-coordination/notes")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationNotesController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteDetailsService noteDetailsService;

    @Autowired
    private NoteSubTypeService noteSubTypeService;

    @Autowired
    private EventService eventService;
    
    @Autowired
    private EncounterNoteTypeService encounterNoteTypeService;

    @RequestMapping(value = "/patient/{patientId}", method = RequestMethod.POST)
    @ResponseBody
    public Page<NoteListItemDto> getNotes(@PathVariable("patientId") Long patientId, Pageable pageRequest) {
        return noteService.listPatientNotes(patientId, pageRequest);
    }

    @ModelAttribute("noteSubTypes")
    public List<NoteSubTypeDto> getNoteSubTypes() {
        final List<NoteSubTypeDto> result = new ArrayList<>();
        result.add(new NoteSubTypeDto(null, "-- Select --"));
        result.addAll(noteSubTypeService.getAllSubTypes());
        return result;
    }

    @ModelAttribute("encounterNoteTypes")
    public List<KeyValueDto> getEncounterTypes() {
        final List<KeyValueDto> result = new ArrayList<>();
        result.addAll(encounterNoteTypeService.getAllEncounterNoteTypes());
        return result;
    }

    @RequestMapping(value = "/patient/{patientId}/new-note", method = RequestMethod.GET)
    public String addNewNotePatientView(final Model model, @PathVariable("patientId") Long patientId) {
        final NoteDto noteDto = new NoteDto();
        noteDto.setPersonSubmittingNote(SecurityUtils.getAuthenticatedUser().getEmployee().getFullName());
        model.addAttribute("noteDto", noteDto);
        model.addAttribute("modalTitle", "Add a Note");
        model.addAttribute("buttonTitle", "SUBMIT");
        model.addAttribute("dateModificationAllowed", true);
        model.addAttribute("admitDates", noteService.getNoteAdmittanceHistoryForResidentWithIntakeDate(patientId));
        return "note.modal";
    }

    @RequestMapping(value = "/event/{eventId}/new-note", method = RequestMethod.GET)
    public String addNewNoteEventView(final Model model, @PathVariable("eventId") Long eventId) {
        eventService.checkAccess(eventId);
        final NoteDto noteDto = new NoteDto();
        noteDto.setPersonSubmittingNote(SecurityUtils.getAuthenticatedUser().getEmployee().getFullName());
        model.addAttribute("noteDto", noteDto);
        model.addAttribute("modalTitle", "Add a Note");
        model.addAttribute("buttonTitle", "SUBMIT");
        model.addAttribute("dateModificationAllowed", true);
        model.addAttribute("admitDates", noteService.getNoteAdmittanceHistoryForEventWithIntakeDate(eventId));
        return "note.modal";
    }

    @RequestMapping(value = "/patient/{patientId}/{noteId}/page-number", method = RequestMethod.GET)
    @ResponseBody
    public Integer getPageNumber(@PathVariable("patientId") Long patientId, @PathVariable("noteId") Long noteId) {
        return noteService.getPageNumber(noteId, patientId);
    }

    @RequestMapping(value = "/patient/{patientId}/new-note", method = RequestMethod.POST)
    @ResponseBody
    public Long savePatientNote(@ModelAttribute("noteDto") NoteDto noteDto, @PathVariable("patientId") Long patientId) {
        noteDto.setPatientId(patientId);
        return noteService.createPatientNote(noteDto);
    }

    @RequestMapping(value = "/event/{eventId}/new-note", method = RequestMethod.POST)
    @ResponseBody
    public Long saveEventNote(@ModelAttribute("noteDto") NoteDto noteDto, @PathVariable("eventId") Long eventId) {
        eventService.checkAccess(eventId);
        final NoteEventDto noteEventDto = new NoteEventDto();
        noteEventDto.setId(eventId);
        noteDto.setEvent(noteEventDto);
        return noteService.createEventNote(noteDto);
    }

    @RequestMapping(value = "/{noteId}/edit", method = RequestMethod.GET)
    public String editNoteView(@PathVariable("noteId") Long noteId, Model model, @RequestParam("timeZoneOffset") Integer timeZoneOffset) {
        noteService.checkAddedBySelfOrThrow(noteId);
        final NoteDto noteDto = noteDetailsService.getNoteDetails(noteId, false,timeZoneOffset );
        noteDto.setLastModifiedDate(new Date());
        model.addAttribute("noteDto", noteDto);
        model.addAttribute("modalTitle", "Edit Note");
        model.addAttribute("buttonTitle", "SAVE");
        model.addAttribute("dateModificationAllowed", false);
        model.addAttribute("disableSubType", true);
        model.addAttribute("disableAdmit", true);
        model.addAttribute("admitDates", Collections.singletonList(noteDto.getNoteResidentAdmittanceHistoryDto()));
        return "note.modal";
    }

    @RequestMapping(value = "/{noteId}/edit", method = RequestMethod.POST)
    @ResponseBody
    public Long editNote(@PathVariable("noteId") Long noteId, @ModelAttribute("noteDto") NoteDto noteDto) {
        noteService.checkAddedBySelfOrThrow(noteId);
        noteDto.setId(noteId);
        return noteService.editNote(noteDto);
    }

    @RequestMapping(value = "/{noteId}/view", method = RequestMethod.GET)
    public String noteView(@PathVariable("noteId") Long noteId, Model model, @RequestParam("timeZoneOffset") Integer timeZoneOffset) {
        final NoteDto noteDto = noteDetailsService.getNoteDetails(noteId, false,timeZoneOffset);
        model.addAttribute("noteDto", noteDto);
        model.addAttribute("modalTitle", "View Note");
        model.addAttribute("readOnly", true);
        model.addAttribute("dateModificationAllowed", false);
        model.addAttribute("admitDates", Collections.singletonList(noteDto.getNoteResidentAdmittanceHistoryDto()));

        return "note.modal";
    }

    @ResponseBody
    @RequestMapping(value = "/patient/{patientId}/total", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Long getNotesTotal(@PathVariable("patientId") Long patientId) {
        return noteService.count(patientId);
    }

    @RequestMapping(value = "/{noteId}/note-details", method = RequestMethod.GET)
    public String initNoteDetails(@PathVariable("noteId") Long noteId, Model model, @RequestParam("timeZoneOffset") Integer timeZoneOffset) {
        final NoteDto noteDto = noteDetailsService.getNoteDetails(noteId, true,timeZoneOffset);
        model.addAttribute("note", noteDto);
        model.addAttribute("addedBySelf", noteService.isAddedBySelf(noteId));
        return "patient.note.details";
    }

    @RequestMapping(value = "/{noteId}/latest", method = RequestMethod.GET)
    @ResponseBody
    public Long getLatestForNote(@PathVariable("noteId") Long noteId) {
        return noteService.getLatestForNote(noteId);
    }

    @ResponseBody
    @RequestMapping(value = "/patient/{patientId}/admit/{admitId}/getTaken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NoteSubType.FollowUpCode> getTakenFollowUp(@PathVariable("patientId") Long patientId, @PathVariable("admitId") Long admitId) {
        return noteSubTypeService.getTakenFollowUpForAdmitDate(patientId, admitId);
    }

    @ResponseBody
    @RequestMapping(value = "/patient/{patientId}/followUp/{followUpCode}/getTaken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Long> getTakenFollowUp(@PathVariable("patientId") Long patientId, @PathVariable("followUpCode") String followUp) {
        return noteSubTypeService.getTakenAdmitIntakeHistoryIdForSubType(patientId, NoteSubType.FollowUpCode.getByCode(followUp));
    }

    @ResponseBody
    @RequestMapping(value = "/event/{eventId}/admit/{admitId}/getTaken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<NoteSubType.FollowUpCode> getTakenFollowUpForEvent(@PathVariable("eventId") Long eventId, @PathVariable("admitId") Long admitId) {
        eventService.checkAccess(eventId);
        return noteSubTypeService.getTakenFollowUpForAdmitDateForEvent(eventId, admitId);
    }

    @ResponseBody
    @RequestMapping(value = "/event/{eventId}/followUp/{followUpCode}/getTaken", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public  List<Long> getTakenFollowUpForEvent(@PathVariable("eventId") Long eventId, @PathVariable("followUpCode") String followUp) {
        eventService.checkAccess(eventId);
        return noteService.getTakenAdmitIntakeHistoryIdForSubTypeForEvent(eventId, NoteSubType.FollowUpCode.getByCode(followUp));
    }
}
