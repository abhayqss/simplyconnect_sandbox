package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.beans.audit.AuditLogFilter;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.AuditLogDao;
import com.scnsoft.eldermark.dao.specification.AuditLogSpecificationGenerator;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.service.security.AuditLogSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogDao auditLogDao;

    @Autowired
    private AuditLogSpecificationGenerator auditLogSpecificationGenerator;

    @Autowired
    private AuditLogSecurityService auditLogSecurityService;

    @Override
    public void save(AuditLog auditLog) {
        auditLogDao.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Instant findLastLoginTime(Long employeeId) {
        return auditLogDao.findTop1ByEmployeeIdAndActionOrderByDateDesc(employeeId, AuditLogAction.LOG_IN).map(AuditLog::getDate).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeIdAware> findAllGreatestBeforeDateWithoutSentNotification(EmployeeStatus employeeStatus, Iterable<AuditLogAction> auditLogActions, Instant date) {
        var byEmployeeStatus = auditLogSpecificationGenerator.byEmployeeStatus(employeeStatus);
        var byAction = auditLogSpecificationGenerator.byActions(auditLogActions);
        var hasGreatestBeforeDate = auditLogSpecificationGenerator.greatestBeforeDate(date);
        var withoutSentNotificationAndActivity = auditLogSpecificationGenerator.withoutSentNotificationForDeactivateEmployees();
        var not4d = auditLogSpecificationGenerator.notContact4d();
        var notManuallyActivated = auditLogSpecificationGenerator.notManuallyActivatedAfter(date);
        return auditLogDao.findAll(
            byEmployeeStatus.and(
                byAction.and(
                    hasGreatestBeforeDate.and(withoutSentNotificationAndActivity.and(not4d.and(notManuallyActivated)))
                )
            ), EmployeeIdAware.class
        );
    }

    @Transactional(readOnly = true)
    public List<EmployeeIdAware> findAllWithoutActivityBeforeDate(
        EmployeeStatus employeeStatus, Iterable<AuditLogAction> auditLogActions, Instant date
    ) {
        var byEmployeeStatus = auditLogSpecificationGenerator.byEmployeeStatus(employeeStatus);
        var byAction = auditLogSpecificationGenerator.byActions(auditLogActions);
        var hasGreatestBeforeDate = auditLogSpecificationGenerator.greatestBeforeDate(date);
        var not4d = auditLogSpecificationGenerator.notContact4d();
        var notManuallyActivated = auditLogSpecificationGenerator.notManuallyActivatedAfter(date);
        return auditLogDao.findAll(
            byEmployeeStatus.and(
                byAction.and(hasGreatestBeforeDate.and(not4d.and(notManuallyActivated)))
            ), EmployeeIdAware.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLog> find(AuditLogFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        var hasAccess = auditLogSpecificationGenerator.hasAccess(permissionFilter, filter.getActions());
        var byFilter = auditLogSpecificationGenerator.byFilter(filter);
        return auditLogDao.findAll(hasAccess.and(byFilter), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> findOldestDate(AuditLogFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = auditLogSpecificationGenerator.hasAccess(permissionFilter, filter.getActions());
        var byFilter = auditLogSpecificationGenerator.byFilter(filter);
        return auditLogDao.findMinDate(hasAccess.and(byFilter));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> findNewestDate(AuditLogFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = auditLogSpecificationGenerator.hasAccess(permissionFilter, filter.getActions());
        var byFilter = auditLogSpecificationGenerator.byFilter(filter);
        return auditLogDao.findMaxDate(hasAccess.and(byFilter));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canViewList() {
        return auditLogSecurityService.canViewList();
    }
}
