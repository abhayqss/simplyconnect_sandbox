package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.phr.AccountType;
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
@Api(value = "PHR - Notes for users", description = "Notes for users")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@Validated
@RequestMapping("/phr/users/{userId:\\d+}/notes")
public class UserNotesController {

    @Autowired
    private NotesFacade notesFacade;

    @ApiOperation(value = "Get a filtered list of notes", notes = "<h3>Sorting rules</h3>The data is sorted by notes modification date (from the newest to the oldest)")
    @GetMapping
    public Response<List<NoteListItemDto>> getUserNotesUsingGET(
            @Min(1) @ApiParam(value = "User ID", required = true) @PathVariable("userId") Long userId,
            @Min(1) @ApiParam(value = "Event ID") @RequestParam(value = "eventId", required = false) Long eventId,
            @ApiParam(value = "Current account type. This parameter is used when `userId` = current user id. It might be either `provider` or `consumer` at \"CTM view\" events screen or at \"More options for patient\" events screen accordingly.", required = false,
                    allowableValues = "PROVIDER, CONSUMER, provider, consumer", defaultValue = "consumer")
            @RequestParam(value = "accountType", required = false, defaultValue = "consumer") String type,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of notes), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        AccountType.Type accountType = AccountType.Type.fromValue(type);
        if (eventId == null) {
            return Response.pagedResponse(notesFacade.getNotesPageForUser(userId, accountType, pageable));
        }
        return Response.pagedResponse(notesFacade.getEventRelatedNotes(eventId, pageable));
    }


    @ApiOperation(value = "Get notes list size", notes = "Get total user's notes list size")
    @GetMapping(value = "/count")
    public Response<Long> getUserNotesCountUsingGET(
            @Min(1) @ApiParam(value = "User ID", required = true) @PathVariable("userId") Long userId
    ) {
        return Response.successResponse(notesFacade.getNotesCountForUser(userId));
    }

    @ApiOperation(value = "Create a new note", notes = "Create a new note")
    @PutMapping
    public Response<NoteModifiedDto> createUserNoteUsingPUT(
            @Min(1) @ApiParam(value = "User ID", required = true) @PathVariable("userId") Long userId,
            @ApiParam(value = "Note", required = true) @RequestBody NoteCreateDto body
    ) {
        return Response.successResponse(notesFacade.createNoteForUser(userId, body));
    }

}
