package com.scnsoft.eldermark.mobile.security;

import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class LoggedUserServiceImpl implements LoggedUserService {

    @Autowired
    private EmployeeService employeeService;

    @Override
    public Optional<UserPrincipal> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (isAuthenticated(authentication)) {
            return Optional.of((UserPrincipal) authentication.getPrincipal());
        }
        return Optional.empty();
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication.getPrincipal() instanceof UserPrincipal;
    }

    @Override
    public Employee getCurrentEmployee() {
        return employeeService.getEmployeeById(getCurrentEmployeeId());
    }

    @Override
    public Long getCurrentEmployeeId() {
        return getCurrentUser().orElseThrow().getEmployeeId();
    }

    @Override
    public List<Employee> getCurrentAndLinkedEmployees() {
        return employeeService.getEmployeeAndLinkedEmployees(getCurrentEmployee());
    }

    @Override
    public void addRecordSearchFoundClientIds(Collection<Long> clientIds) {
        getCurrentUser().ifPresent(userPrincipal -> userPrincipal.addClientRecordSearchFoundIds(clientIds));
    }
}
