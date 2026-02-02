package com.scnsoft.eldermark.dao.inbound.marco;

import com.scnsoft.eldermark.entity.inbound.marco.MarcoUnassignedStoragePath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarcoUnassignedStoragePathDao extends JpaRepository<MarcoUnassignedStoragePath, Long> {

    List<MarcoUnassignedStoragePath> findAllByDatabaseNameAndDisabledIsFalse(String databaseName);

}
