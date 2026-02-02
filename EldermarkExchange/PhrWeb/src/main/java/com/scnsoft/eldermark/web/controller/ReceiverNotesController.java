package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facade.NotesFacade;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.notes.NoteCreateDto;
import com.scnsoft.eldermark.web.entity.notes.NoteListItemDto;
import com.scnsoft.eldermark.web.entity.notes.NoteModifiedDto;
import io.swagger.annotations.*;
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
 * Created on 24/08/2018.
 */
@Api(value = "PHR - Notes for receivers", description = "Notes for receivers")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@Validated
@RequestMapping("/phr/care-receivers/{receiverId:\\d+}/notes")
public class ReceiverNotesController {

    @Autowired
    private NotesFacade notesFacade;

    @ApiOperation(value = "Get a filtered list of notes", notes = "<h3>Sorting rules</h3>The data is sorted by notes modification date (from the newest to the oldest)")
    @GetMapping
    public Response<List<NoteListItemDto>> getReceiverNotesUsingGET(
            @Min(1) @ApiParam(value = "Receiver id from ResidentCareTeamMember table", required = true) @PathVariable("receiverId") Long receiverId,
            @Min(1)  @ApiParam(value = "Event ID") @RequestParam(value = "eventId", required = false) Long eventId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of notes), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        if (eventId == null) {
            return Response.pagedResponse(notesFacade.getNotesPageForReceiver(receiverId, pageable));
        }
        return Response.pagedResponse(notesFacade.getEventRelatedNotes(eventId, pageable));
    }

    @ApiOperation(value = "Get notes list size", notes = "Get total patient's notes list size")
    @GetMapping(value = "/count")
    public Response<Long> getReceiverNotesCountUsingGET(
            @Min(1) @ApiParam(value = "id from ResidentCareTeamMember table", required = true) @PathVariable("receiverId") Long receiverId
    ) {
        return Response.successResponse(notesFacade.getNotesCountForReceiver(receiverId));
    }

    @ApiOperation(value = "Create a new note", notes = "Create a new note")
    @PutMapping
    public Response<NoteModifiedDto> createReceiverNoteUsingPUT(
            @Min(1) @ApiParam(value = "Receiver id from ResidentCareTeamMember table", required = true) @PathVariable("receiverId") Long receiverId,
            @ApiParam(value = "Note" , required = true ) @RequestBody NoteCreateDto body
    ) {
        return Response.successResponse(notesFacade.createNoteForReceiver(receiverId, body));
    }
}
