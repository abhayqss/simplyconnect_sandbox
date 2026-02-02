package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Employee;

import java.util.Collection;

public interface ChatSecurityService {

    boolean canStart(Collection<Long> employeeIds);

    boolean canStart(Collection<Long> employeeIds, PermissionFilter filter);

    boolean canAddMembers(Collection<Long> employeeIds);

    boolean existsConversationWithClient(Long clientId);

    boolean existsConversationWithEmployee(Long employeeId);

    boolean areChatsAccessibleByEmployee(Employee employeeId);
}
