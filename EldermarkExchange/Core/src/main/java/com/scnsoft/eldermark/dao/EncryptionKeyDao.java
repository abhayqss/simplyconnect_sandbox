package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EncryptionKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EncryptionKeyDao extends JpaRepository<EncryptionKey, Long> {
}
