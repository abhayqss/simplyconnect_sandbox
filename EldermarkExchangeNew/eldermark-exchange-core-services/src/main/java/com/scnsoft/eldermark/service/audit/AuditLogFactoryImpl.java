package com.scnsoft.eldermark.service.audit;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogSearchFilter;
import com.scnsoft.eldermark.entity.audit.AuditLogRelation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Set;

@Service
@Transactional(propagation = Propagation.SUPPORTS)
public class AuditLogFactoryImpl implements AuditLogFactory {

    @Override
    public AuditLog createClientLog(Set<Long> organizationIds, Set<Long> communityIds, Long employeeId, Set<Long> clientIds, String remoteAddress, AuditLogAction auditLogAction, AuditLogRelation auditLogRelation, AuditLogSearchFilter auditLogSearchFilter, boolean isMobile) {
        var result = new AuditLog();
        result.setAction(auditLogAction);
        result.setClientIds(clientIds);
        result.setDate(Instant.now());
        result.setEmployeeId(employeeId);
        result.setRemoteAddress(remoteAddress);
        result.setAuditLogRelation(auditLogRelation);
        result.setAuditLogSearchFilter(auditLogSearchFilter);
        result.setOrganizationIds(organizationIds);
        result.setCommunityIds(communityIds);
        result.setMobile(isMobile);
        return result;
    }

    @Override
    public AuditLog createDocumentsLog(Set<Long> organizationIds, Set<Long> communityIds, Long employeeId, Set<Long> clientIds, Set<Long> documentIds, String remoteAddress, AuditLogAction auditLogAction) {
        var result = new AuditLog();
        result.setAction(auditLogAction);
        result.setClientIds(clientIds);
        result.setDocumentIds(!CollectionUtils.isEmpty(documentIds) ? documentIds : null);
        result.setDate(Instant.now());
        result.setEmployeeId(employeeId);
        result.setRemoteAddress(remoteAddress);
        result.setOrganizationIds(organizationIds);
        result.setCommunityIds(communityIds);
        return result;
    }

    @Override
    public AuditLog createProspectLog(Set<Long> organizationIds, Set<Long> communityIds, Long employeeId, Set<Long> prospectIds, String remoteAddress, AuditLogAction action, AuditLogRelation auditLogRelation, AuditLogSearchFilter auditLogSearchFilter, boolean isMobile) {
        var result = new AuditLog();
        result.setAction(action);
        result.setProspectIds(prospectIds);
        result.setDate(Instant.now());
        result.setEmployeeId(employeeId);
        result.setRemoteAddress(remoteAddress);
        result.setAuditLogRelation(auditLogRelation);
        result.setAuditLogSearchFilter(auditLogSearchFilter);
        result.setOrganizationIds(organizationIds);
        result.setCommunityIds(communityIds);
        result.setMobile(isMobile);
        return result;
    }

    @Override
    public AuditLog createDocumentsLog(Set<Long> organizationIds, Set<Long> communityIds, Long employeeId, Set<Long> clientIds, Set<Long> documentIds, String remoteAddress, AuditLogAction auditLogAction, AuditLogRelation auditLogRelation) {
        var result = new AuditLog();
        result.setAction(auditLogAction);
        result.setClientIds(clientIds);
        result.setDocumentIds(!CollectionUtils.isEmpty(documentIds) ? documentIds : null);
        result.setDate(Instant.now());
        result.setEmployeeId(employeeId);
        result.setRemoteAddress(remoteAddress);
        result.setAuditLogRelation(auditLogRelation);
        result.setOrganizationIds(organizationIds);
        result.setCommunityIds(communityIds);
        return result;
    }
}
