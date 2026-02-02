package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.CommunitiesService;
import com.scnsoft.eldermark.service.EmployeesService;
import com.scnsoft.eldermark.service.OrgsService;
import com.scnsoft.eldermark.service.ResidentsService;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.shared.web.entity.ResponseValidationErrorDto;
import com.scnsoft.eldermark.web.entity.CommunityDto;
import com.scnsoft.eldermark.web.entity.EmployeeListItemDto;
import com.scnsoft.eldermark.web.entity.OrgDto;
import com.scnsoft.eldermark.web.entity.ResidentListItemDto;
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

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T13:26:20.222+03:00")
@Api(value = "Orgs", description = "Organizations")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/orgs")
public class OrgsController {

    final Logger logger = Logger.getLogger(OrgsController.class.getName());

    private final OrgsService orgsService;
    private final CommunitiesService communitiesService;
    private final EmployeesService employeesService;
    private final ResidentsService residentsService;

    @Autowired
    public OrgsController(OrgsService orgsService, CommunitiesService communitiesService, EmployeesService employeesService, ResidentsService residentsService) {
        this.orgsService = orgsService;
        this.communitiesService = communitiesService;
        this.employeesService = employeesService;
        this.residentsService = residentsService;
    }

    @ApiOperation(value = "List communities of organization",
            notes = "List accessible communities of the specified organization. <h3>Required privileges</h3> <pre>ORGANIZATION_READ</pre>")
    @GetMapping(value = "/{orgId:\\d+}/communities")
    public Response<List<CommunityDto>> getOrganizationCommunities(
            @Min(1)
            @ApiParam(value = "organization id", required = true)
            @PathVariable("orgId") Long orgId
    ) {
        final List<CommunityDto> dto = communitiesService.listByOrganization(orgId);
        return Response.successResponse(dto);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "List employees of organization",
            notes = "List employees from accessible communities of the specified organization. <h3>Required privileges</h3> <pre>ORGANIZATION_READ</pre>")
    @GetMapping(value = "/{orgId:\\d+}/employees")
    public Response<List<EmployeeListItemDto>> getOrganizationEmployees(
            @Min(1)
            @ApiParam(value = "organization id", required = true)
            @PathVariable("orgId") Long orgId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all employees), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<EmployeeListItemDto> employees = employeesService.listByOrganization(orgId, pageable);
        return Response.pagedResponse(employees);
    }

    @ApiResponses({
            @ApiResponse(code = HttpURLConnection.HTTP_BAD_REQUEST, message = "Bad Request", response = ResponseValidationErrorDto.class)
    })
    @ApiOperation(value = "List residents of organization", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ</pre>")
    @GetMapping(value = "/{orgId:\\d+}/residents")
    public Response<List<ResidentListItemDto>> getOrganizationResidents(
            @Min(1)
            @ApiParam(value = "organization id", required = true)
            @PathVariable("orgId") Long orgId,
            @Min(1)
            @ApiParam(value = "Maximum results to appear in response (if not specified, system will return all residents), ≥ 1")
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @Min(0)
            @ApiParam(value = "Results page, e.g. 0, 1, 2, etc.", defaultValue = "0")
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page
    ) {
        final Pageable pageable = PaginationUtils.buildPageable(pageSize, page);
        final Page<ResidentListItemDto> residents = residentsService.listByOrganization(orgId, pageable);
        return Response.pagedResponse(residents);
    }

    @ApiOperation(value = "Get organization details", notes = "<h3>Required privileges</h3> <pre>ORGANIZATION_READ</pre>")
    @GetMapping(value = "/{orgId:\\d+}")
    public Response<OrgDto> getOrganization(
            @Min(1)
            @ApiParam(value = "organization id", required = true)
            @PathVariable("orgId") Long orgId
    ) {
        final OrgDto dto = orgsService.get(orgId);
        return Response.successResponse(dto);
    }

    @ApiOperation(value = "List organizations", notes = "List accessible organizations")
    @GetMapping
    public Response<List<OrgDto>> getOrganizations() {
        final List<OrgDto> dto = orgsService.listAllAccessible();
        return Response.successResponse(dto);
    }

}
