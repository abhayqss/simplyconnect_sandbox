package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.service.CareteamService;
import com.scnsoft.eldermark.api.external.service.CommunitiesService;
import com.scnsoft.eldermark.api.external.service.EmployeesService;
import com.scnsoft.eldermark.api.external.service.ResidentsService;
import com.scnsoft.eldermark.api.external.web.dto.*;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.api.shared.validation.Phone;
import com.scnsoft.eldermark.api.shared.validation.Ssn;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import com.scnsoft.eldermark.api.shared.web.dto.ResponseErrorDto;
import io.swagger.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
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
@Api(value = "Communities", description = "Communities")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/communities")
public class CommunitiesController {

    final Logger logger = Logger.getLogger(CommunitiesController.class.getName());

    private final CommunitiesService communitiesService;
    private final CareteamService careteamService;
    private final EmployeesService employeesService;
    private final ResidentsService residentsService;

    @Autowired
    public CommunitiesController(CommunitiesService communitiesService, CareteamService careteamService, EmployeesService employeesService,
                                 ResidentsService residentsService) {
        this.communitiesService = communitiesService;
        this.careteamService = careteamService;
        this.employeesService = employeesService;
        this.residentsService = residentsService;
    }

    @ApiOperation(value = "Create employee", tags = {"not-implemented"},
            notes = "Create new inactive employee (contact) that needs to be activated via the regular registration process in Web S.C. <h3>Required privileges</h3> <pre>COMMUNITY_READ, EMPLOYEE_CREATE</pre>")
    @PostMapping(value = "/{communityId:\\d+}/employees")
    public Response<EmployeeDto> createEmployee(
            @Min(1)
            @ApiParam(value = "community id", required = true)
            @PathVariable("communityId") Long communityId,
            @Phone
            @ApiParam(value = "mobile phone number", required = true)
            @RequestParam(value = "phone", required = true) String phone,
            @Email
            @ApiParam(value = "email", required = true)
            @RequestParam(value = "email", required = true) String email,
            @ApiParam(value = "unique login, it can be the same as email (credentials to Web Simply Connect)", required = true)
            @RequestParam(value = "login", required = true) String login,
            @ApiParam(value = "first name", required = true)
            @RequestParam(value = "firstName", required = true) String firstName,
            @ApiParam(value = "last name", required = true)
            @RequestParam(value = "lastName", required = true) String lastName,
            @ApiParam(value = "external id <h3>Additional required privilege</h3> <pre>SPECIAL_NUCLEUS</pre>")
            @RequestParam(value = "nucleusUserId", required = false) String nucleusUserId
    ) {
        final EmployeeDto dto = employeesService.create(communityId, phone, email, login, firstName, lastName, nucleusUserId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "Create resident", tags = {"not-implemented"},
            notes = "Create a new resident (patient). <h3>Required privileges</h3> <pre>COMMUNITY_READ, RESIDENT_CREATE</pre>")
    @PostMapping(value = "/{communityId:\\d+}/residents")
    public Response<ResidentDto> createResident(
            @Min(1)
            @ApiParam(value = "community id", required = true)
            @PathVariable("communityId") Long communityId,
            @Phone
            @ApiParam(value = "mobile phone number", required = true)
            @RequestParam(value = "phone", required = true) String phone,
            @Email
            @ApiParam(value = "email address", required = true)
            @RequestParam(value = "email", required = true) String email,
            @ApiParam(value = "first name", required = true)
            @RequestParam(value = "firstName", required = true) String firstName,
            @ApiParam(value = "last name", required = true)
            @RequestParam(value = "lastName", required = true) String lastName,
            @Ssn
            @NotBlank
            @ApiParam(value = "social security number (example 123456789)")
            @RequestParam(value = "ssn", required = false) String ssn,
            @ApiParam(value = "last name")
            @RequestParam(value = "middleName", required = false) String middleName,
            @ApiParam(value = "external id <h3>Additional required privilege</h3> <pre>SPECIAL_NUCLEUS</pre>")
            @RequestParam(value = "nucleusUserId", required = false) String nucleusUserId
    ) {
        final ResidentDto dto = residentsService.create(communityId, phone, email, firstName, lastName, ssn, middleName, nucleusUserId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List community care team members", tags = {"not-implemented"},
            notes = "<h3>Sorting rules</h3><ul><li>?</li></ul><h3>Required privileges</h3> <pre>COMMUNITY_READ</pre>")
    @GetMapping(value = "/{communityId:\\d+}/careteam")
    public Response<List<CareteamMemberBriefDto>> getCommunityCareteamMembers(
            @Min(1)
            @ApiParam(value = "community id", required = true)
            @PathVariable("communityId") Long communityId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return unlimited number of care team members), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<CareteamMemberBriefDto> ctms = careteamService.listCommunityCTMs(communityId, pageable);
        return Response.pagedResponse(ctms);
    }

    @ApiOperation(value = "List employees of community",
            notes = "List employees from the specified community. <h3>Required privileges</h3> <pre>COMMUNITY_READ</pre>")
    @GetMapping(value = "/{communityId:\\d+}/employees")
    public Response<List<EmployeeListItemDto>> getCommunityEmployees(
            @Min(1)
            @ApiParam(value = "community id", required = true)
            @PathVariable("communityId") Long communityId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all employees), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<EmployeeListItemDto> employees = employeesService.listByCommunity(communityId, pageable);
        return Response.pagedResponse(employees);
    }

    @ApiOperation(value = "List residents of community", notes = "<h3>Required privileges</h3> <pre>COMMUNITY_READ</pre>")
    @GetMapping(value = "/{communityId:\\d+}/residents")
    public Response<List<ResidentListItemDto>> getCommunityResidents(
            @Min(1)
            @ApiParam(value = "community id", required = true)
            @PathVariable("communityId") Long communityId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all residents), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<ResidentListItemDto> residents = residentsService.listByCommunity(communityId, pageable);
        return Response.pagedResponse(residents);
    }

    @ApiOperation(value = "Get community details", notes = "<h3>Required privileges</h3> <pre>COMMUNITY_READ</pre>")
    @GetMapping(value = "/{communityId:\\d+}")
    public Response<CommunityDto> getCommunity(
            @Min(1)
            @ApiParam(value = "community id", required = true)
            @PathVariable("communityId") Long communityId
    ) {
        final CommunityDto dto = communitiesService.get(communityId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List all communities", notes = "List accessible communities from all organizations")
    @GetMapping
    public Response<List<CommunityDto>> getCommunities() {
        final List<CommunityDto> dto = communitiesService.listAllAccessible();
        return Response.successResponse(dto);
    }

}
