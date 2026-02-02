package com.scnsoft.eldermark.beans.security;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.security.Permission;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PermissionFilter {

    private Map<Permission, List<Employee>> permissionsMap;
    private Map<Employee, Permission> employeeRoleMap;
    private Map<Employee, Set<Permission>> employeePermissionMap;
    private final Set<Long> clientRecordSearchFoundIds;

    public PermissionFilter(Map<Permission, List<Employee>> permissionsMap, Map<Employee, Permission> employeeRoleMap, Set<Long> clientRecordSearchFoundIds) {
        this.permissionsMap = permissionsMap;
        this.employeeRoleMap = employeeRoleMap;
        this.clientRecordSearchFoundIds = clientRecordSearchFoundIds;
    }

    public boolean hasPermission(Permission permission) {
        if (permission.hasPredicate()) {
            return Optional.ofNullable(getEmployees(permission)).map(employees -> employees.stream().anyMatch(permission.getPermissionConfirmationPredicate())).orElse(false);
        } else {
            return permissionsMap.containsKey(permission);
        }
    }

    public boolean hasAnyPermission(Collection<Permission> permissions) {
        return permissions.stream().anyMatch(this::hasPermission);
    }

    public List<Employee> getEmployees(Permission permission) {
        var employees = permissionsMap.get(permission);
        if (CollectionUtils.isNotEmpty(employees) && permission.hasPredicate()) {
            return employees.stream().filter(permission.getPermissionConfirmationPredicate()).collect(Collectors.toList());
        }
        return employees;
    }

    public List<Employee> getEmployees(Predicate<Employee> predicate) {
        return getAllEmployees()
                .stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    public List<Employee> getEmployeesWithAny(Collection<Permission> permissions) {
        return permissions.stream().map(this::getEmployees).filter(Objects::nonNull).flatMap(List::stream).filter(distinctByKey(Employee::getId)).collect(Collectors.toList());
    }

    public Permission getEmployeeRole(Employee employee) {
        return employeeRoleMap.get(employee);
    }

    public Map<Permission, List<Employee>> getPermissionsMap() {
        return permissionsMap;
    }

    public Map<Employee, Permission> getEmployeeRoleMap() {
        return employeeRoleMap;
    }

    public Set<Long> getAllEmployeeIds() {
        return getAllEmployees().stream().map(IdAware::getId).collect(Collectors.toSet());
    }

    public Set<Employee> getAllEmployees() {
        return getEmployeeRoleMap().keySet();
    }

    public Set<Permission> getEmployeePermissions(Employee employee) {
        if (employeePermissionMap == null) {
            initEmployeePermissionMap();
        }
        return employeePermissionMap.get(employee);
    }

    public Set<Long> getClientRecordSearchFoundIds() {
        return clientRecordSearchFoundIds;
    }

    public boolean containsClientRecordSearchFoundId(Long clientId) {
        return clientRecordSearchFoundIds.contains(clientId);
    }

    public boolean containsAnyClientRecordSearchFoundIds(Collection<Long> clientIds) {
        return org.apache.commons.collections4.CollectionUtils.containsAny(clientRecordSearchFoundIds, clientIds);
    }

    private void initEmployeePermissionMap() {
        employeePermissionMap = new TreeMap<>(Comparator.comparingLong(Employee::getId));
        permissionsMap.forEach((permission, employees) ->
                employees.forEach(employee -> {
                            var permissions = employeePermissionMap.computeIfAbsent(employee, e -> EnumSet.noneOf(Permission.class));
                            permissions.add(permission);
                        }
                ));
    }

    public List<Employee> getEmployees() {
        return permissionsMap.entrySet().stream().map(Map.Entry::getValue).flatMap(List::stream).filter(distinctByKey(Employee::getId)).collect(Collectors.toList());
    }

    //TODO temporary copied from StreamUtils, resolve visibility and remove
    protected static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
