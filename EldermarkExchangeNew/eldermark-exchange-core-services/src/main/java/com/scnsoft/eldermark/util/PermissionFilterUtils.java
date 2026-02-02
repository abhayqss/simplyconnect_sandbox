package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.security.CareTeamRolePermissionMapping;
import com.scnsoft.eldermark.entity.security.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PermissionFilterUtils {
    private static final Logger logger = LoggerFactory.getLogger(PermissionFilterUtils.class);

    public static PermissionFilter excludePermissions(PermissionFilter originalFilter, Permission... permissions) {
        var map = new HashMap<>(originalFilter.getPermissionsMap());
        Stream.of(permissions).forEach(map::remove);

        var roleMap = CareCoordinationUtils.<Employee, Permission>idsComparingMap();
        map.values().stream().flatMap(List::stream)
                .forEach(employee -> roleMap.putIfAbsent(employee, originalFilter.getEmployeeRole(employee)));

        return new PermissionFilter(map, roleMap, originalFilter.getClientRecordSearchFoundIds());
    }

    public static PermissionFilter filterWithEmployeesOnly(PermissionFilter originalFilter, Collection<Employee> employees) {
        var ids = CareCoordinationUtils.toIdsSet(employees);

        return filteredPermissionFilterByEmployees(originalFilter, employee -> ids.contains(employee.getId()));
    }

    private static PermissionFilter filteredPermissionFilterByEmployees(PermissionFilter originalFilter, Predicate<Employee> filterPredicate) {
        var newPermissionMap = new HashMap<Permission, List<Employee>>();
        originalFilter.getPermissionsMap().forEach((permission, employees) -> {
                    var newEmployees = employees.stream().filter(filterPredicate).
                            collect(Collectors.toList());

                    if (newEmployees.size() > 0) {
                        newPermissionMap.put(permission, employees);
                    }
                }
        );

        var newRoleMap = CareCoordinationUtils.<Employee, Permission>idsComparingMap();
        originalFilter.getEmployeeRoleMap().forEach((employee, permission) -> {
            if (filterPredicate.test(employee)) {
                newRoleMap.put(employee, permission);
            }
        });

        return new PermissionFilter(newPermissionMap, newRoleMap, originalFilter.getClientRecordSearchFoundIds());
    }

    public static Map<Permission, List<Long>> getPermissionsOfEmployees(List<Employee> linkedEmployees) {
        Map<Permission, List<Long>> permissionMap = new EnumMap<>(Permission.class);

        linkedEmployees.forEach(employee -> {
            if (employee.getCareTeamRole() == null) {
                logger.info("Null care team role of employee {}", employee.getId());
            } else if (employee.getCareTeamRole().getCode() == null) {
                logger.info("Null care team role code of employee {}", employee.getId());
            }
            var permissions = Optional.ofNullable(employee.getCareTeamRole())
                    .map(CareTeamRole::getCode)
                    .map(CareTeamRolePermissionMapping::getPermissions)
                    .orElseGet(Set::of);

            permissions.forEach(p -> permissionMap.compute(p, (key, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                v.add(employee.getId());
                return v;
            }));
        });
        return permissionMap;
    }

    public static Map<Long, Permission> getEmployeeRoleMap(List<Employee> linkedEmployees) {
        return linkedEmployees
                .stream()
                .collect(Collectors.toMap(BasicEntity::getId,
                        e -> Optional.ofNullable(e.getCareTeamRole())
                                .map(CareTeamRole::getCode)
                                .map(CareTeamRolePermissionMapping::getRole).orElse(Permission.ROLE_UNKNOWN)
                ));
    }
}
