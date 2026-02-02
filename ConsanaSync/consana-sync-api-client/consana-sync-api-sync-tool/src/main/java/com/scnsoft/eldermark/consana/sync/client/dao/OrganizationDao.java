package com.scnsoft.eldermark.consana.sync.client.dao;

import com.scnsoft.eldermark.consana.sync.client.entities.IdAware;
import com.scnsoft.eldermark.consana.sync.client.entities.Organization;
import com.scnsoft.eldermark.consana.sync.client.entities.Resident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationDao extends JpaRepository<Organization, Long>, JpaSpecificationExecutor<Resident> {

    List<IdAware> findAllByIsConsanaInitialSyncIsTrue();

}
