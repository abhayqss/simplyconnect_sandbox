package com.scnsoft.scansol.facades;

import com.scnsoft.eldermark.entity.RoleCode;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.scansol.shared.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * Date: 15.05.15
 * Time: 6:56
 */
public interface ScanSolutionFacade {
    List<ScanSolOrganizationDto> getOrganizationsByEmployee (long employeeId);

    List<ScanSolOrganizationDto> getOrganizationsByEmployee (long employeeId, RoleCode role);

    ScanSolDatabaseDto getDatabasesByCompanyId(String companyLoginId);

    List<ScanSolDatabaseDto> getDatabases();

    List<ScanSolBaseEmployeeInfoDto> getEmployeesForDatabase(long databaseId);

    List<ScanSolOrganizationDto> getOrganizationsForDatabase (long databaseId);

    List<Long> getOrganizationIdsForEmployee (long employeeId);

    List<ScanSolResidentDto> getResidentsByOrganization (long organizationId);

    List<ScanSolDocumentDto> getDocumentsForResident(long residentId, long employeeId);
    
    Date getResidentsByOrganizationArchive (long residentId,long organizationId);

    DocumentBean findDocument(long documentId, long employeeId);

    void updateDocumentTitle(long documentId, long employeeId, String title);

    void updateDocumentSharingPolicy(long documentId, long employeeId, List<Long> databaseIdsToShareWith);
    
    List<ScanSolRoleDto> getRoles(UserDetails authentication);

    List<ScanSolRoleDto> getRoles(Authentication authentication);

    ScanSolOrganizationDto getOrganization(long organizationId);

	void getDocumentsZip(List<Long> recordIdsList, HttpServletResponse response, Long employeeId) throws IOException;
	
	void getOrganizationsByRole(long employeeId,List<ScanSolOrganizationDto> listOfOraganization);
}