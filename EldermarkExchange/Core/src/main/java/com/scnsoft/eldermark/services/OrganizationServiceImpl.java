package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.RoleCode;
import com.scnsoft.scansol.shared.ScanSolOrganizationDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Date: 15.05.15
 * Time: 5:51
 */

@Component("nhinOrganiztionService")
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationDao organizationDao;

    @Value("${unaffiliated.community.oid}")
    private String unaffiliatedCommunityOid;

    @Override
    public List<Organization> getOrganizationsByEmployee(long employeeId) {
        return organizationDao.getOrganizationsByEmployee(employeeId);
    }

    @Override
    public List<Organization> getOrganizationsByEmployee(long employeeId, RoleCode role) {
        return organizationDao.getOrganizationsByEmployee(employeeId, role);
    }

    @Override
    public List<Organization> getOrganizationsByDatabase(long databaseId) {
        return organizationDao.getOrganizationsByDatabase(databaseId);
    }

    @Override
    public Organization getOrganization(long id) {
        return organizationDao.getOrganization(id);
    }


    @Override
    public Organization getUnaffiliatedOrganization(Long databaseId) {
        return organizationDao.getOrganizationByOid(unaffiliatedCommunityOid, databaseId);
    }
    
	@Override
	public List<ScanSolOrganizationDto> getOrganizationsByRole(long employeeId,List<ScanSolOrganizationDto> listOfOraganization) {
		for (ScanSolOrganizationDto organization : listOfOraganization) {
			organizationDao.getOrganizationsByEmployeeGroup(employeeId, organization);
		}
		return listOfOraganization;
	}
}
