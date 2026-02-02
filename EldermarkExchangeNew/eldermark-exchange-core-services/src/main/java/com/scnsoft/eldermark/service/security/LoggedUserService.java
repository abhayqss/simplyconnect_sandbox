package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.entity.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LoggedUserService {

    Optional<UserPrincipal> getCurrentUser();

    Employee getCurrentEmployee();

    Long getCurrentEmployeeId();

    List<Employee> getCurrentAndLinkedEmployees();

    void addRecordSearchFoundClientIds(Collection<Long> clientIds);
}
