package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.Informant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InformantDao extends JpaRepository<Informant, Long> {
    List<Informant> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
