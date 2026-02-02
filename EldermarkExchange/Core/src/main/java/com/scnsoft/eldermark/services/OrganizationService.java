package com.scnsoft.eldermark.services;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.RoleCode;
import com.scnsoft.scansol.shared.ScanSolOrganizationDto;

import java.util.List;

/**
 * Date: 15.05.15
 * Time: 5:51
 */
public interface OrganizationService {
    List<Organization> getOrganizationsByEmployee(long employeeId);
    List<Organization> getOrganizationsByEmployee(long employeeId, RoleCode role);
    List<Organization> getOrganizationsByDatabase(long databaseId);
    Organization getOrganization(long id);
    Organization getUnaffiliatedOrganization(Long databaseId);
    
    List<ScanSolOrganizationDto> getOrganizationsByRole(long employeeId, List<ScanSolOrganizationDto> listOfOraganization);
}
