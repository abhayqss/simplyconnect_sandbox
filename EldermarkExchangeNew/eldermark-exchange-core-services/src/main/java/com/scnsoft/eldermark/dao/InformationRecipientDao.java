package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.InformationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InformationRecipientDao extends JpaRepository<InformationRecipient, Long> {
    List<InformationRecipient> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
