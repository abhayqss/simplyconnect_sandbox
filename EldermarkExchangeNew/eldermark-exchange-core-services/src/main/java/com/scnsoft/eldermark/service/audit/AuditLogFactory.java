package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogRelation;
import com.scnsoft.eldermark.entity.audit.AuditLogSearchFilter;

import java.util.Set;

public interface AuditLogFactory {
    AuditLog createClientLog(Set<Long> organizationIds,
                             Set<Long> communityIds,
                             Long employeeId,
                             Set<Long> clientIds,
                             String remoteAddress,
                             AuditLogAction auditLogAction,
                             AuditLogRelation auditLogRelation,
                             AuditLogSearchFilter auditLogSearchFilter,
                             boolean isMobile
    );

    AuditLog createDocumentsLog(Set<Long> organizationIds,
                                Set<Long> communityIds,
                                Long employeeId,
                                Set<Long> clientIds,
                                Set<Long> documentIds,
                                String remoteAddress,
                                AuditLogAction auditLogAction,
                                AuditLogRelation auditLogRelation
    );

    AuditLog createDocumentsLog(Set<Long> organizationIds,
                                Set<Long> communityIds,
                                Long employeeId,
                                Set<Long> clientIds,
                                Set<Long> documentIds,
                                String remoteAddress,
                                AuditLogAction auditLogAction
    );

    AuditLog createProspectLog(Set<Long> organizationIds,
                           Set<Long> communityIds,
                           Long employeeId,
                           Set<Long> prospectIds,
                           String remoteAddress,
                           AuditLogAction action,
                           AuditLogRelation auditLogRelation,
                           AuditLogSearchFilter auditLogSearchFilter,
                           boolean isMobile);
}
