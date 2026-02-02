package com.scnsoft.eldermark.dao.signature;

import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentSignatureRequestNotificationDao extends JpaRepository<DocumentSignatureRequestNotification, Long> {
}
