package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.UserAuthenticationContext;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.UserPrincipal;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.security.AuthScope;
import com.scnsoft.eldermark.exception.AuthAccountInactiveException;
import com.scnsoft.eldermark.exception.EmployeeConfirmedStatusException;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LoggedUserDetailsService implements UserDetailsService {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @Autowired
    private ClientService clientService;

    @Value("${employee.allow-login.status.confirmed}")
    private boolean allowConfirmedStatusLogin;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        String[] split = input.split(UserPrincipal.DELIMITER);
        if (split.length < 2) {
            throw new UsernameNotFoundException("Both username and company ID must be selected");
        }

        String companyId = split[0];
        String username = split[1];

        Employee employee = employeeService.getEmployeeThatCanLoginOrInactive(username, companyId);
        if (!allowConfirmedStatusLogin && employee.getStatus() == EmployeeStatus.CONFIRMED) {
            throw new EmployeeConfirmedStatusException("This account is not active yet. Please, sign in to the mobile app to complete your registration");
        }
        var linkedEmployees = employeeService.getEmployeeAndLinkedEmployees(employee);
        var permissionMap = PermissionFilterUtils.getPermissionsOfEmployees(linkedEmployees);
        var employeeRoleMap = PermissionFilterUtils.getEmployeeRoleMap(linkedEmployees);
        var employeeUnlockTime = employeePasswordSecurityService.getEmployeeUnlockTime(employee);
        if (employeeUnlockTime != null) {
            Long currentTime = DateTimeUtils.toEpochMilli(Instant.now());
            boolean locked = currentTime < employeeUnlockTime;
            employee.getEmployeePasswordSecurity().setLocked(locked);
            if (!locked) {
                employeePasswordSecurityService.unlockEmployeeAccount(employee);
            }
        }
        boolean expired = !BooleanUtils.isTrue(employee.getContact4d()) && employeePasswordSecurityService.isPasswordExpired(employee);
        return new UserPrincipal(employee, !expired, permissionMap, employeeRoleMap, AuthScope.FULL);
    }

    private UserPrincipal loadById(Long id, AuthScope authScope) {
        Employee employee = employeeService.getEmployeeById(id);

        if (employee.getStatus() == null || !employee.getStatus().canLogin()) {
            if (EmployeeStatus.INACTIVE.equals(employee.getStatus())) {
                throw new AuthAccountInactiveException();
            } else {
                throw new InsufficientAuthenticationException("Please contact your Administrator for more details");
            }
        }
        var linkedEmployees = employeeService.getEmployeeAndLinkedEmployees(employee);
        var permissionMap = PermissionFilterUtils.getPermissionsOfEmployees(linkedEmployees);
        var employeeRoleMap = PermissionFilterUtils.getEmployeeRoleMap(linkedEmployees);

        return new UserPrincipal(employee, Boolean.TRUE, permissionMap, employeeRoleMap, authScope);
    }

    public UserPrincipal loadById(Long id) {
        return loadById(id, AuthScope.FULL);
    }

    public UserDetails loadByUserAuthCtx(UserAuthenticationContext ctx) {
        var authScope = StringUtils.isEmpty(ctx.getAccessibleRoomSid())
                ? AuthScope.FULL : AuthScope.CONVERSATIONS;
        var principal = loadById(ctx.getId(), authScope);

        principal.addClientRecordSearchFoundIds(clientService.getNotOptedOutClientIds(ctx.getClientRecordSearchFoundIds()));
        principal.setAccessibleRoomSid(ctx.getAccessibleRoomSid());
        return principal;
    }

}
