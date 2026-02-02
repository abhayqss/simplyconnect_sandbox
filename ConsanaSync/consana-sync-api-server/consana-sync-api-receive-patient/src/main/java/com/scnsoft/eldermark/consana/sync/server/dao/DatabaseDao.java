package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Database;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatabaseDao extends JpaRepository<Database, Long> {

    Database getFirstByAlternativeId(String alternativeId);
}
