package com.scnsoft.eldermark.service.task;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.service.DeactivateEmployeeNotificationService;
import com.scnsoft.eldermark.service.EmployeeService;
import com.scnsoft.eldermark.service.audit.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "deactivate.employee.enabled", havingValue = "true")
public class ScheduledDeactivateEmployeeServiceImpl implements ScheduledDeactivateEmployeeService {

    @Value("${deactivate.employee.last.activity.minutes}")
    private long deactivateMinutes;

    @Value("${deactivate.employee.prior.email.notification.minutes}")
    private long priorPeriodMinutes;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private DeactivateEmployeeNotificationService deactivateEmployeeNotificationService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    @Scheduled(cron = "${deactivate.employee.cron}")
    @Transactional
    public void process() {
        deactivateEmployees();
        sentDeactivateNotifications();
    }

    private void deactivateEmployees() {
        var beforeDate = Instant.now().minus(deactivateMinutes, ChronoUnit.MINUTES);
        var inactiveEmployeeIds = auditLogService
                .findAllWithoutActivityBeforeDate(EmployeeStatus.ACTIVE, List.of(AuditLogAction.LOG_IN, AuditLogAction.PASSWORD_RESET), beforeDate).stream()
                .map(EmployeeIdAware::getEmployeeId)
                .collect(Collectors.toSet());
        employeeService.updateStatus(EmployeeStatus.INACTIVE, inactiveEmployeeIds, true);
    }

    private void sentDeactivateNotifications() {
        var beforeDate = Instant.now().minus(deactivateMinutes - priorPeriodMinutes, ChronoUnit.MINUTES);
        var employeeIds = auditLogService
                .findAllGreatestBeforeDateWithoutSentNotification(EmployeeStatus.ACTIVE, List.of(AuditLogAction.LOG_IN, AuditLogAction.PASSWORD_RESET), beforeDate).stream()
                .map(EmployeeIdAware::getEmployeeId)
                .collect(Collectors.toSet());
        deactivateEmployeeNotificationService.send(employeeIds);
    }
}
