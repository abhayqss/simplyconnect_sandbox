package com.scnsoft.simplyconnect.bulkfileencrypt.dao;

import com.scnsoft.simplyconnect.bulkfileencrypt.entity.EncryptedDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptedDocumentDao extends JpaRepository<EncryptedDocument, Long> {
    boolean existsByPath(String path);
}
