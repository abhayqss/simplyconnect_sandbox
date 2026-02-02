package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeUpdateRequestDto;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.mobile.dto.employee.FavouriteDto;
import com.scnsoft.eldermark.mobile.facade.EmployeeFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    @Autowired
    private EmployeeFacade employeeFacade;

    @GetMapping
    public Response<List<EmployeeDto>> find(EmployeeSearchWithFavouriteFilter filter,
                                            Pageable pageRequest){
        return Response.pagedResponse(employeeFacade.find(filter, pageRequest));
    }

    @GetMapping("/exists")
    public Response<Boolean> exists(EmployeeSearchWithFavouriteFilter filter){
        return Response.successResponse(employeeFacade.exists(filter));
    }

    @GetMapping("/{employeeId}")
    public Response<EmployeeDto> findById(@PathVariable("employeeId") Long employeeId){
        return Response.successResponse(employeeFacade.findById(employeeId));
    }

    @PutMapping("/{employeeId}")
    public Response<Void> update(
            @PathVariable("employeeId") Long employeeId,
            @RequestBody @Valid EmployeeUpdateRequestDto dto
    ){
        dto.setId(employeeId);
        employeeFacade.update(dto);
        return Response.successResponse();
    }

    @PutMapping("/{employeeId}/favourite")
    public Response<Void> setFavourite(@PathVariable("employeeId") Long employeeId, @Valid @RequestBody FavouriteDto favouriteDto){
        employeeFacade.setFavourite(employeeId, favouriteDto.getFavourite());
        return Response.successResponse();
    }

    @GetMapping("/{employeeId}/can-edit")
    public Response<Boolean> canEdit(@PathVariable("employeeId") Long employeeId) {
        return Response.successResponse(employeeFacade.canEdit(employeeId));
    }
}
