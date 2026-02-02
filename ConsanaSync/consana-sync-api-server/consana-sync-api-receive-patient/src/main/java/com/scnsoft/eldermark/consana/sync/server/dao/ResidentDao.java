package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ResidentDao extends JpaRepository<Resident, Long>, JpaSpecificationExecutor<Resident> {

    Optional<Resident> findByConsanaXrefIdAndFacilityId(String consanaXrefId, Long communityId);

}
