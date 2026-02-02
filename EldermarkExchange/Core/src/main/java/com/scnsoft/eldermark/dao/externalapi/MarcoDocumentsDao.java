package com.scnsoft.eldermark.dao.externalapi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.inbound.marco.MarcoIntegrationDocument;

@Repository
public interface MarcoDocumentsDao extends JpaRepository<MarcoIntegrationDocument, Long> {

}
