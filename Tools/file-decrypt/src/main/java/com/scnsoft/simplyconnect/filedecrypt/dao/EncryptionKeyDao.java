package com.scnsoft.simplyconnect.filedecrypt.dao;

import com.scnsoft.simplyconnect.filedecrypt.entity.EncryptionKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncryptionKeyDao extends JpaRepository<EncryptionKey, Long> {
}
