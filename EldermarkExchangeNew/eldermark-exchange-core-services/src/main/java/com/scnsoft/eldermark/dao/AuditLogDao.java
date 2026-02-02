package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.audit.AuditLogAction;
import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuditLogDao extends AppJpaRepository<AuditLog, Long>, CustomAuditLogDao {
    Optional<AuditLog> findTop1ByEmployeeIdAndActionOrderByDateDesc(Long employeeId, AuditLogAction auditLogAction);
}
