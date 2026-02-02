package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.NotesService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.NoteListItemDto;
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
 * Created on 5/4/2018.
 */
@Api(value = "Notes", description = "Notes")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)

})
@RestController
@Validated
@RequestMapping("/notes")
public class NotesController {

    @Autowired
    private NotesService notesService;

    @ApiOperation(value = "Get a filtered list of notes", notes = "<h3>Sorting rules</h3>The data is sorted by notes modification date (from the newest to the oldest)")
    @GetMapping()
    public Response<List<NoteListItemDto>> getNotes(
            @Min(1)  @ApiParam(value = "Resident Id") @RequestParam(value = "residentId") Long residentId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of notes), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page
    ) {
        final Pageable pageable = buildPageable(pageSize, page);
        return Response.pagedResponse(notesService.getListNotes(residentId, pageable));
    }

    @ApiOperation(value = "Get notes list size", notes = "Get total patient's notes list size")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    public Response<Long> getNotesCount(
            @Min(1) @ApiParam(value = "Resident Id", required = true) @RequestParam("residentId") Long residentId
    ) {
        return Response.successResponse(notesService.getListNotesCount(residentId));
    }
}
