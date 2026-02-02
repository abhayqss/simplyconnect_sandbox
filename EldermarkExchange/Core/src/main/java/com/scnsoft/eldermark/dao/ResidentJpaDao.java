package com.scnsoft.eldermark.dao;

import com.google.common.base.Optional;
import com.scnsoft.eldermark.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentJpaDao extends JpaRepository<Resident, Long> {

    Optional<Resident> findFirstByConsanaXrefIdAndDatabaseOidAndFacilityOid(String consanaXrefId, String databaseOid, String organizationOid);
}
