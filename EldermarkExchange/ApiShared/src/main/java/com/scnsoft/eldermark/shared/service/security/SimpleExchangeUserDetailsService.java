package com.scnsoft.eldermark.shared.service.security;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.UsernameBuilder;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class SimpleExchangeUserDetailsService implements UserDetailsService {

    private final EmployeeService employeeService;
    private final EmployeePasswordSecurityService employeePasswordSecurityService;

    @Autowired
    public SimpleExchangeUserDetailsService(EmployeeService employeeService, EmployeePasswordSecurityService employeePasswordSecurityService) {
        this.employeeService = employeeService;
        this.employeePasswordSecurityService = employeePasswordSecurityService;
    }

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException, DataAccessException {
        String[] split = input.split(UsernameBuilder.getDelimiter());
        if (split.length < 2) {
            throw new UsernameNotFoundException("Both username and company ID must be selected");
        }

        String companyId = split[0];
        String username = split[1];

        final Employee employee = employeeService.getActiveEmployee(username, companyId);

        Set<GrantedAuthority> authorities = new HashSet<>();
        Map<Long, Set<GrantedAuthority>> authoritiesMap = new HashMap<>();
        Map<Long, Set<GrantedAuthority>> employeeAuthoritiesMap = new HashMap<>();
        authoritiesMap.put(employee.getDatabaseId(), new HashSet<>(authorities));
        employeeAuthoritiesMap.put(employee.getId(), new HashSet<>(authorities));
        Boolean expired = employeePasswordSecurityService.isPasswordExpired(employee);

        return new ExchangeUserDetails(employee, authorities, null, null, authoritiesMap, employeeAuthoritiesMap, !expired);
    }

}
