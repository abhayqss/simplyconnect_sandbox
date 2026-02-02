package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.beans.audit.AuditLogFilter;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AuditLogService {

    void save(AuditLog auditLog);

    Instant findLastLoginTime(Long employeeId);

    List<EmployeeIdAware> findAllGreatestBeforeDateWithoutSentNotification(EmployeeStatus employeeStatus, Iterable<AuditLogAction> auditLogActions, Instant date);

    List<EmployeeIdAware> findAllWithoutActivityBeforeDate(EmployeeStatus employeeStatus, Iterable<AuditLogAction> auditLogActions, Instant date);

    Page<AuditLog> find(AuditLogFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    Optional<Instant> findOldestDate(AuditLogFilter auditLogFilter, PermissionFilter permissionFilter);

    Optional<Instant> findNewestDate(AuditLogFilter auditLogFilter, PermissionFilter permissionFilter);

    boolean canViewList();
}
