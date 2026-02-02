package com.scnsoft.simplyconnect.bulkfileencrypt.dao;

import com.scnsoft.simplyconnect.bulkfileencrypt.entity.EncryptionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptionKeyDao extends JpaRepository<EncryptionKey, Long> {
}
