package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentDao extends JpaRepository<Document, Long> {

    List<Document> findAllByConsanaMapIdIsNotNullAndClientLegacyIdAndClientOrganizationAlternativeId(String clientLegacyId, String clientOrganizationAlternativeId);
}
