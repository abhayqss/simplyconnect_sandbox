package com.scnsoft.eldermark.beans.security;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.security.AuthScope;
import com.scnsoft.eldermark.entity.security.Permission;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserPrincipal extends User {

    private static final long serialVersionUID = 1L;

    public static final String DELIMITER = "/";

    private final Long employeeId;
    private Map<Permission, List<Long>> linkedEmployeesPermissions;
    private Map<Long, Permission> employeeRoleMap;
    private final Set<Long> clientRecordSearchFoundIds;
    private String accessibleRoomSid;

    public UserPrincipal(Employee employee, Boolean credentialsNonExpired,
                         Map<Permission, List<Long>> linkedEmployeesPermissions,
                         Map<Long, Permission> employeeRoleMap, AuthScope authScope) {
        super(employee.getLoginName(), employee.getPassword(),
                employee.getStatus().canLogin(),
                true, credentialsNonExpired,
                employee.getEmployeePasswordSecurity() == null || !employee.getEmployeePasswordSecurity().getLocked(),
                Stream.concat(
                        linkedEmployeesPermissions.keySet().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.toString())),
                        Stream.of(new SimpleGrantedAuthority(authScope.toString())))
                    .collect(Collectors.toSet()));
        this.employeeId = employee.getId();
        this.linkedEmployeesPermissions = linkedEmployeesPermissions;
        this.employeeRoleMap = employeeRoleMap;
        this.clientRecordSearchFoundIds = new HashSet<>();
    }

    public Map<Permission, List<Long>> getLinkedEmployeesPermissions() {
        return linkedEmployeesPermissions;
    }

    public void setLinkedEmployeesPermissions(Map<Permission, List<Long>> linkedEmployeesPermissions) {
        this.linkedEmployeesPermissions = linkedEmployeesPermissions;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public Map<Long, Permission> getEmployeeRoleMap() {
        return employeeRoleMap;
    }

    public void setEmployeeRoleMap(Map<Long, Permission> employeeRoleMap) {
        this.employeeRoleMap = employeeRoleMap;
    }

    public Set<Long> getClientRecordSearchFoundIds() {
        return new HashSet<>(clientRecordSearchFoundIds);
    }

    public boolean addClientRecordSearchFoundIds(Collection<Long> clientIds) {
        return clientRecordSearchFoundIds.addAll(clientIds);
    }

    public String getAccessibleRoomSid() {
        return accessibleRoomSid;
    }

    public void setAccessibleRoomSid(String accessibleRoomSid) {
        this.accessibleRoomSid = accessibleRoomSid;
    }
}
