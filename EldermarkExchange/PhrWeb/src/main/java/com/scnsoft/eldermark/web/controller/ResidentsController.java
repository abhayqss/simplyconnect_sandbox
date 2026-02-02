package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facade.AdmitIntakeHistoryFacade;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.AdmitDateDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.List;

import static com.scnsoft.eldermark.shared.utils.PaginationUtils.buildPageable;

@Api(value = "PHR - Notes for users", description = "Notes for users")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@RestController
@Validated
@RequestMapping("/phr/residents/{residentId:\\d+}")
public class ResidentsController {

    @Autowired
    private AdmitIntakeHistoryFacade admitIntakeHistoryFacade;

    @ApiOperation(value = "Get admit dates of resident", notes = "Get admit dates of resident", tags={ "residents-controller", })
    @GetMapping(value = "/admit-dates")
    public Response<List<AdmitDateDto>>  getResidentAdmitDates(
            @ApiParam(value = "Resident id", required = true) @PathVariable("residentId") Long residentId,
            @Min(1) @ApiParam(value = "Maximum results to appear in response (if not specified, system will return " +
                    "all encounters), ≥ 1") @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0) @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0") @RequestParam(value = "page", required = false, defaultValue="0") Integer page) {
        final Pageable pageable = buildPageable(pageSize, page);
        return Response.pagedResponse(admitIntakeHistoryFacade.getAdmitIntakeDatesForResident(residentId, pageable));
    }

}
