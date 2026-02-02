package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.service.CareteamService;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseErrorDto;
import com.scnsoft.eldermark.api.external.web.dto.CareteamMemberBriefDto;
import com.scnsoft.eldermark.api.external.web.dto.CareteamMemberDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
@Api(value = "Care Team", description = "Care Team Member information")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_NOT_FOUND, message = "Not Found", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/residents/{residentId:\\d+}/careteam")
public class CareteamController {

    final Logger logger = Logger.getLogger(CareteamController.class.getName());

    private final CareteamService careteamService;

    @Autowired
    public CareteamController(CareteamService careteamService) {
        this.careteamService = careteamService;
    }

    @ApiOperation(value = "Get a specific care team member", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping(value = "/{contactId:\\d+}")
    public Response<CareteamMemberDto> getCareteamMember(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @Min(1)
            @ApiParam(value = "contact id", required = true)
            @PathVariable("contactId") Long contactId
    ) {
        final CareteamMemberDto dto = careteamService.get(residentId, contactId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List care team members", notes = "<h3>Sorting rules</h3><ul><li>?</li></ul><h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping
    public Response<List<CareteamMemberBriefDto>> getCareteamMembers(
            @Min(1)
            @ApiParam(value = "resident id", required = true)
            @PathVariable("residentId") Long residentId,
            @ApiParam(value = "<li>`FAMILY` = role \"Parent/Guardian\"</li> <li>`CARE_PROVIDER` = any other role</li></ul>",
                    allowableValues = "FAMILY, CARE_PROVIDER")
            @RequestParam(value = "directory", required = false) String directory,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of care team members), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<CareteamMemberBriefDto> ctms = careteamService.listResidentCTMs(residentId, directory, pageable);
        return Response.pagedResponse(ctms);
    }

}
