package com.scnsoft.eldermark.dao.inbound.document;

import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentUnassignedStoragePath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentAssignmentUnassignedStoragePathDao extends JpaRepository<DocumentAssignmentUnassignedStoragePath, Long> {

    List<DocumentAssignmentUnassignedStoragePath> findAllByDatabaseNameAndDisabledIsFalse(String databaseName);


}
