package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationDao extends JpaRepository<Organization, Long> {


    Organization getFirstByConsanaOrgIdAndDatabaseConsanaXOwningId(String organizationId, String databaseId);
}
