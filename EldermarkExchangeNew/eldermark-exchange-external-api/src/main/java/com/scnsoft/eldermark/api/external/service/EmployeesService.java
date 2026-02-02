package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.EmployeeDto;
import com.scnsoft.eldermark.api.external.web.dto.EmployeeListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeesService {

    Page<EmployeeListItemDto> listByOrganization(long orgId, Pageable pageable);

    Page<EmployeeListItemDto> listByCommunity(long communityId, Pageable pageable);

    EmployeeDto create(Long communityId, String phone, String email, String login, String firstName, String lastName, String nucleusUserId);

    EmployeeDto get(Long employeeId);
}
