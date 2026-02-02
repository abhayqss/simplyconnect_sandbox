package org.openhealthtools.openxds.dao;

import org.openhealthtools.openxds.entity.Organization;


public interface OrganizationDao {
    Organization findFirstByName(String name);

    Organization findFirstByNameAndDatabaseOid(String name, String databaseOid);

    Organization findByUniversalId(String name, Long orgId);

    Organization findDefaultByDatabase(Long databaseId);

    Organization findDefaultByDatabaseOid(String databaseOid);
}
