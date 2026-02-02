package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.RoleCode;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.scansol.shared.ScanSolOrganizationDto;

import java.util.List;

/**
 * Date: 14.05.15
 * Time: 15:18
 */
public interface OrganizationDao extends BaseDao<Organization> {
    List<Organization> getOrganizationsByEmployee(long employeeId);

    //List<Integer> getOrganizationIdsByEmployee(long employeeId);
    List<Organization> getOrganizationsByEmployee(long employeeId, RoleCode role);

    List<Organization> getOrganizationsByDatabase(long databaseId);

    Long getOrganizationsByDatabaseCount(long databaseId);

    Organization getOrganization(long id);

    Pair<String, String> getOrganizationLogos(long id);

    Organization getOrganizationByName(final String name);

    Organization getOrganizationByNameAndDatabase(final String name, Long databaseId);

    Organization getOrganizationByOid(String oid, Long databaseId);

    List<Organization> getOrganizationByOidAndDatabaseOid(String oid, String databaseOid);
    List<Organization> getOrganizationByNameAndDatabaseOid(String name, String databaseOid);
    List<Organization> getOrganizationByOidAndNameAndDatabaseOid(String oid, String name, String databaseOid);

    List<Long> getDatabasesByOrganizationIds(List<Long> organizationIds);

    List<Organization> getPharmacyByResidentId(Long residentId);

	void getOrganizationsByRole(long employeeId, ScanSolOrganizationDto organization);
	void getOrganizationsByGroupRole(long employeeId, ScanSolOrganizationDto organization);
	void getOrganizationsByEmployeeGroup(long employeeId, ScanSolOrganizationDto organization);
	
}
