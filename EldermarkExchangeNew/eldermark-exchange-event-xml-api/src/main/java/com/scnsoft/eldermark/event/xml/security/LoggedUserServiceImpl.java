package com.scnsoft.eldermark.event.xml.security;

import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
//todo remove, because bean is present
//com.scnsoft.eldermark.config.CoreServicesConfiguration.notImplementedLoggedUserService
public class LoggedUserServiceImpl implements LoggedUserService {

    @Override
    public Optional<UserPrincipal> getCurrentUser() {
        return Optional.empty();
    }

    @Override
    public Employee getCurrentEmployee() {
        return null;
    }

    @Override
    public Long getCurrentEmployeeId() {
        return null;
    }

    @Override
    public List<Employee> getCurrentAndLinkedEmployees() {
        return Collections.emptyList();
    }

    @Override
    public void addRecordSearchFoundClientIds(Collection<Long> clientIds) {

    }
}
