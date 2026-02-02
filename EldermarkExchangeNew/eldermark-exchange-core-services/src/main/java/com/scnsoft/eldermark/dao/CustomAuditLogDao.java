package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.audit.AuditLog;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.Optional;

public interface CustomAuditLogDao {

    Optional<Instant> findMaxDate(Specification<AuditLog> specification);

    Optional<Instant> findMinDate(Specification<AuditLog> specification);
}
