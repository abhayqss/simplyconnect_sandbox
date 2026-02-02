package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeUpdateRequestDto;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeFacade {

    Page<EmployeeDto> find(EmployeeSearchWithFavouriteFilter filter, Pageable pageRequest);

    boolean exists(EmployeeSearchWithFavouriteFilter filter);

    EmployeeDto findById(Long employeeId);

    void setFavourite(Long employeeId, boolean favourite);

    void update(EmployeeUpdateRequestDto contactDto);

    boolean canEdit(Long employeeId);
}
