package com.scnsoft.eldermark.dao.inbound.document;

import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentAssignmentLogDao extends JpaRepository<DocumentAssignmentLog, Long> {
}
