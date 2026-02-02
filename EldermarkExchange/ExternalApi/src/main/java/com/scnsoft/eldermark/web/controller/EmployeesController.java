package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.service.EmployeesService;
import com.scnsoft.eldermark.shared.web.entity.Response;
import com.scnsoft.eldermark.shared.web.entity.ResponseErrorDto;
import com.scnsoft.eldermark.web.entity.EmployeeDto;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
@Api(value = "Employees", description = "Employees")
@ApiResponses({
        @ApiResponse(code = HttpURLConnection.HTTP_UNAUTHORIZED, message = "Unauthorized", response = ResponseErrorDto.class),
        @ApiResponse(code = HttpURLConnection.HTTP_FORBIDDEN, message = "Access Forbidden", response = ResponseErrorDto.class)
})
@Validated
@RestController
@RequestMapping("/employees")
public class EmployeesController {

    final Logger logger = Logger.getLogger(EmployeesController.class.getName());

    private final EmployeesService employeesService;

    @Autowired
    public EmployeesController(EmployeesService employeesService) {
        this.employeesService = employeesService;
    }

    @ApiOperation(value = "Get employee details", notes = "General information about employee, without partner-specific info <h3>Required privileges</h3> <pre>ORGANIZATION_READ or COMMUNITY_READ</pre>")
    @GetMapping(value = "/{employeeId:\\d+}")
    public Response<EmployeeDto> getEmployee(
            @Min(1)
            @ApiParam(value = "employee id", required = true)
            @PathVariable("employeeId") Long employeeId
    ) {
        final EmployeeDto dto = employeesService.get(employeeId);
        return Response.successResponse(dto);
    }

}
