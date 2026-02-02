package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facade.NotesFacade;
import com.scnsoft.eldermark.service.NoteService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.notes.*;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.List;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;

/**
 * @author sparuchnik
 * Created on 5/4/2018.
 */
@Api(value = "PHR - Notes", description = "Notes")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@Validated
@RequestMapping("/phr")
public class NotesController {

    @Autowired
    private NoteService noteService;

    @Autowired
    private NotesFacade notesFacade;

    @ApiOperation(value = "Does current user can add note.", notes = "Does current user has access to adding note.")
    @RequestMapping(value = "/notes/canAdd", method = RequestMethod.GET)
    public Response<Boolean> canAddUsingGet() {
        return Response.successResponse(noteService.canAddNote());
    }

    @ApiOperation(value = "Edit an existing note", notes = "Edit an existing note")
    @PostMapping("/notes")
    public Response<NoteModifiedDto> editNoteUsingPOST(
            @ApiParam(value = "Note" , required = true ) @RequestBody NoteEditDto body,
            @RequestHeader(value = "X-App-Ver", required = false) String appVersion
    ) {
        return Response.successResponse(notesFacade.editNote(body));
    }

    @ApiOperation(value = "Get note details", notes = "Returns details of the note with given Id")
    @GetMapping(value = "/notes/{noteId}")
    public Response<NoteDetailsDto> getNoteDetailsUsingGET(
            @Min(1) @ApiParam(value = "Note ID", required = true) @PathVariable("noteId") Long noteId
    ) {
        return Response.successResponse(notesFacade.getNoteDetails(noteId));
    }

    //=================== [deprecated] since next after 2.34.23 release version of frontend ==========================

    @ApiOperation(value = "Create a new note", notes = "Create a new note")
    @PutMapping("/notes")
    @Deprecated
    public Response<NoteModifiedDto> createNoteUsingPUT(
            @ApiParam(value = "Note" , required = true ) @RequestBody NoteCreateDto body
    ) {
        return Response.successResponse(noteService.createNote(body));
    }

    @ApiOperation(value = "Get a filtered list of notes", notes = "<h3>Sorting rules</h3>The data is sorted by notes modification date (from the newest to the oldest)")
    @GetMapping("/{userId:\\d+}/notes")
    @Deprecated
    public Response<List<NoteListItemDto>> getNotesUsingGET(
            @Min(1) @ApiParam(value = "User ID", required = true) @PathVariable("userId") Long userId,
            @Min(1)  @ApiParam(value = "Event ID") @RequestParam(value = "eventId", required = false) Long eventId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of notes), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        if (eventId == null) {
            return Response.pagedResponse(noteService.getUserListNotes(userId, pageable));
        }
        return Response.pagedResponse(noteService.getRelatedUserEventNotes(userId, eventId, pageable));
    }

    @ApiOperation(value = "Get notes list size", notes = "Get total patient's notes list size")
    @RequestMapping(value = "/{userId}/notes/count", method = RequestMethod.GET)
    @Deprecated
    public Response<Long> getNotesCountUsingGET(
            @Min(1) @ApiParam(value = "User ID", required = true) @PathVariable("userId") Long userId
    ) {
        return Response.successResponse(noteService.getUserListNotesCount(userId));
    }

    // ============================================ [deprecated] ==========================================================

}
