package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PermissionFilterServiceImpl implements PermissionFilterService {

    private final LoggedUserService loggedUserService;
    private final EmployeeDao employeeDao;

    private final EmployeeService employeeService;

    @Autowired
    public PermissionFilterServiceImpl(LoggedUserService loggedUserService, EmployeeDao employeeDao, EmployeeService employeeService) {
        this.loggedUserService = loggedUserService;
        this.employeeDao = employeeDao;
        this.employeeService = employeeService;
    }

    @Override
    public PermissionFilter createPermissionFilterForCurrentUser() {
        return loggedUserService.getCurrentUser()
                .map(userPrincipal -> {
                    //findAllById doesn't hit 1-level cache therefore loading all needed employees once
                    var loadedEmployees = employeeDao.findAllById(userPrincipal.getEmployeeRoleMap().keySet());

                    Set<Long> clientRecordSearchFoundIds =
                            CollectionUtils.isNotEmpty(userPrincipal.getClientRecordSearchFoundIds()) ?
                                    userPrincipal.getClientRecordSearchFoundIds() :
                                    Collections.emptySet();

                    return createFilter(loadedEmployees,
                            userPrincipal.getLinkedEmployeesPermissions(),
                            userPrincipal.getEmployeeRoleMap(), clientRecordSearchFoundIds);
                })
                .orElseGet(() -> new PermissionFilter(Collections.emptyMap(), Collections.emptyMap(), Collections.emptySet()));
    }

    @Override
    public PermissionFilter createPermissionFilterForUser(Long employeeId) {
        var linkedEmployees = employeeService.getEmployeeAndLinkedEmployees(employeeDao.findById(employeeId).orElseThrow());
        var permissionMap = PermissionFilterUtils.getPermissionsOfEmployees(linkedEmployees);
        var employeeRoleMap = PermissionFilterUtils.getEmployeeRoleMap(linkedEmployees);

        return createFilter(linkedEmployees, permissionMap, employeeRoleMap, Set.of());
    }

    private PermissionFilter createFilter(List<Employee> employees,
                                          Map<Permission, List<Long>> linkedEmployeesPermissions,
                                          Map<Long, Permission> employeeRoleMap,
                                          Set<Long> clientRecordSearchFoundIds) {
        var employeesMap = employees.stream()
                .collect(StreamUtils.toMapOfUniqueKeys(Employee::getId));


        var currentPermissionsMap = linkedEmployeesPermissions.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream().map(employeesMap::get)
                                .collect(Collectors.toList())));

        var currentRolesMap = new TreeMap<Employee, Permission>(Comparator.comparingLong(Employee::getId));
        employeeRoleMap
                .forEach((employeeId, permission) -> currentRolesMap.put(employeesMap.get(employeeId), permission));

        return new PermissionFilter(currentPermissionsMap, currentRolesMap, clientRecordSearchFoundIds);
    }
}
