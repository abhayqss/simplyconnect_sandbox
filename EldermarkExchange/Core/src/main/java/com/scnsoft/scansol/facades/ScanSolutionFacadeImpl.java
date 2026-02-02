package com.scnsoft.scansol.facades;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.persistence.NoResultException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.RoleCode;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.OrganizationService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.scansol.shared.ScanSolBaseEmployeeInfoDto;
import com.scnsoft.scansol.shared.ScanSolDatabaseDto;
import com.scnsoft.scansol.shared.ScanSolDocumentDto;
import com.scnsoft.scansol.shared.ScanSolOrganizationDto;
import com.scnsoft.scansol.shared.ScanSolResidentDto;
import com.scnsoft.scansol.shared.ScanSolRoleDto;

/**
 * Date: 15.05.15 Time: 6:55
 */
@Transactional
@Component
public class ScanSolutionFacadeImpl implements ScanSolutionFacade {
    private static final Logger logger = LoggerFactory.getLogger(ScanSolutionFacadeImpl.class);

    @Autowired
    private DozerBeanMapper mapper;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private DatabasesService databasesService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentFacade documentFacade;

    @Override
    public List<ScanSolOrganizationDto> getOrganizationsByEmployee(long employeeId) {
        List<ScanSolOrganizationDto> organizationDtos = new ArrayList<ScanSolOrganizationDto>();
        for (Organization organization : organizationService.getOrganizationsByEmployee(employeeId)) {
            organizationDtos.add(mapper.map(organization, ScanSolOrganizationDto.class));
        }
        return organizationDtos;
    }

    @Override
    public List<ScanSolOrganizationDto> getOrganizationsByEmployee(long employeeId, RoleCode role) {
        List<ScanSolOrganizationDto> organizationDtos = new ArrayList<ScanSolOrganizationDto>();
        for (Organization organization : organizationService.getOrganizationsByEmployee(employeeId, role)) {
            organizationDtos.add(mapper.map(organization, ScanSolOrganizationDto.class));
        }
        return organizationDtos;
    }
    
	@Override
	public void getOrganizationsByRole(long employeeId, List<ScanSolOrganizationDto> listOfOraganization) {
		organizationService.getOrganizationsByRole(employeeId, listOfOraganization);
	}

    @Override
    public ScanSolDatabaseDto getDatabasesByCompanyId(String companyId) {
        Database database = databasesService.getDatabaseByCompanyId(companyId);
        if (database == null) {
            throw new NoResultException("Company not found. Please enter a valid company ID.");
        }
        return mapper.map(database, ScanSolDatabaseDto.class);
    }

    @Override
    public List<ScanSolDatabaseDto> getDatabases() {
        List<Database> databases = databasesService.getDatabases();
        List<ScanSolDatabaseDto> databaseDtoList = new ArrayList<ScanSolDatabaseDto>();
        for (Database database : databases) {
            ScanSolDatabaseDto databaseDto = mapper.map(database, ScanSolDatabaseDto.class);
            databaseDtoList.add(databaseDto);
        }
        return databaseDtoList;
    }

    @Override
    public List<ScanSolBaseEmployeeInfoDto> getEmployeesForDatabase(long databaseId) {
        List<Employee> employees = employeeService.getEmployees(databaseId);
        List<ScanSolBaseEmployeeInfoDto> employeeDtoList = new ArrayList<ScanSolBaseEmployeeInfoDto>();
        for (Employee employee : employees) {
            employeeDtoList.add(mapper.map(employee, ScanSolBaseEmployeeInfoDto.class));
        }
        return employeeDtoList;
    }

    @Override
    public List<ScanSolOrganizationDto> getOrganizationsForDatabase(long databaseId) {
        List<ScanSolOrganizationDto> organizationDtoList = new ArrayList<ScanSolOrganizationDto>();
        for (Organization organization : organizationService.getOrganizationsByDatabase(databaseId)) {
            organizationDtoList.add(mapper.map(organization, ScanSolOrganizationDto.class));
        }
        return organizationDtoList;
    }

    @Override
    public List<Long> getOrganizationIdsForEmployee(long employeeId) {
        List<Long> organizationIds = new ArrayList<Long>();
        for (Organization organization : organizationService.getOrganizationsByEmployee(employeeId)) {
            organizationIds.add(organization.getId());
        }
        return organizationIds;
    }

    @Override
    public List<ScanSolResidentDto> getResidentsByOrganization(long organizationId) {
        List<ScanSolResidentDto> residentDtoList = new ArrayList<ScanSolResidentDto>();
        for (Resident resident : residentService.getResidentsByOrganization(organizationId)) {
            residentDtoList.add(mapper.map(resident, ScanSolResidentDto.class));
        }
        return residentDtoList;
    }
    
    @Override	
    public Date getResidentsByOrganizationArchive(long residentId,long organizationId) {	
    	return residentService.getResidentArchiveDate(residentId,organizationId);	
    }	
    

    @Override
    public List<ScanSolDocumentDto> getDocumentsForResident(long residentId, long employeeId) {
        List<ScanSolDocumentDto> documentDtoList = new ArrayList<ScanSolDocumentDto>();
        Employee employee = employeeService.getEmployee(employeeId);
        Resident resident = residentService.getResident(residentId, true);
        Map<AuthorKey, Employee> authorIds = new HashMap<AuthorKey, Employee>();
        List<Document> documents = documentService.queryForDocuments(resident, employee, sharedWithScanSol(), false);
        for (Document document : documents) {
            ScanSolDocumentDto documentDto = mapper.map(document, ScanSolDocumentDto.class);
            AuthorKey authorKey = new AuthorKey(document.getAuthorLegacyId(),
                    document.getAuthorDatabaseAlternativeId());
            Employee author = authorIds.get(authorKey);
            if (author == null) {
                author = documentService.getAuthor(document);
                authorIds.put(authorKey, author);
            }
            if (author != null) {
                documentDto
                        .setAuthor(mapper.map(author, ScanSolBaseEmployeeInfoDto.class, "employee-id-and-name-only"));
            }
            // TODO add property with default scan sol database
            documentDto.setShared(!document.getSharedWithDatabasesIds()
                    .contains(databasesService.getDatabaseByCompanyId("service").getId()));

            documentDtoList.add(documentDto);
        }
        return documentDtoList;
    }

    @Override
    public DocumentBean findDocument(long documentId, long employeeId) {
        return documentFacade.findDocument(documentId, employeeId, sharedWithScanSol(), false, true);
    }

    private List<Long> sharedWithScanSol() {
        List<Long> sharedWith = new ArrayList<Long>();
        // sharedWith.add(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId());
        // TODO add property with default scan sol database
        sharedWith.add(databasesService.getDatabaseByCompanyId("service").getId());

        return sharedWith;
    }

    @Override
    public void updateDocumentTitle(long documentId, long employeeId, String title) {
        documentFacade.updateDocumentTitle(documentId, employeeId, sharedWithScanSol(), title, true);
    }

    @Override
    public void updateDocumentSharingPolicy(long documentId, long employeeId, List<Long> databaseIdsToShareWith) {
        documentFacade.updateDocumentSharingPolicy(documentId, employeeId, sharedWithScanSol(), databaseIdsToShareWith,
                true);
    }

    @Override
    public List<ScanSolRoleDto> getRoles(UserDetails authentication) {
        List<ScanSolRoleDto> roles = new ArrayList<ScanSolRoleDto>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority instanceof SimpleGrantedAuthority) {
                ScanSolRoleDto role = new ScanSolRoleDto();
                role.setName(authority.getAuthority());
                roles.add(role);
            }
        }
        return roles;
    }
    
    @Override
    public List<ScanSolRoleDto> getRoles(Authentication authentication) {
        List<ScanSolRoleDto> roles = new ArrayList<ScanSolRoleDto>();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority instanceof SimpleGrantedAuthority) {
                ScanSolRoleDto role = new ScanSolRoleDto();
                role.setName(authority.getAuthority());
                roles.add(role);
            }
        }
        return roles;
    }

    @Override
    public ScanSolOrganizationDto getOrganization(long organizationId) {
        Organization organization = organizationService.getOrganization(organizationId);
        return mapper.map(organization, ScanSolOrganizationDto.class);
    }

    private class AuthorKey {
        protected String legacyId;
        protected String databaseAlternativeId;
        private int hashCode;

        public AuthorKey(String legacyId, String databaseAlternativeId) {
            this.legacyId = legacyId;
            this.databaseAlternativeId = databaseAlternativeId;
            this.hashCode = (legacyId + databaseAlternativeId).hashCode();
        }

        @Override
        public boolean equals(Object object) {
            if (object == null || !(object instanceof AuthorKey)) {
                return false;
            }
            AuthorKey authorKey = (AuthorKey) object;
            if (StringUtils.equals(authorKey.legacyId, this.legacyId)
                    && StringUtils.equals(authorKey.databaseAlternativeId, this.databaseAlternativeId)) {
                return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hashCode;
        }
    }

    @Override
    public void getDocumentsZip(List<Long> recordIdsList, HttpServletResponse response, Long employeeId)
            throws IOException {
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"Documents.zip");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        Map<String, Integer> fileNames = new HashMap<>();

        for (Long documentId : recordIdsList) {
            try {
                DocumentBean document = findDocument(documentId, employeeId);
                String originalFileName = document.getOriginalFileName().trim();
                Integer counter = fileNames.containsKey(originalFileName) ? fileNames.get(originalFileName) : 0;
                fileNames.put(originalFileName, counter + 1);
                zos.putNextEntry(
                        new ZipEntry(fileNames.get(originalFileName) > 1
                                ? originalFileName.substring(0, originalFileName.lastIndexOf(".")) + " ("
                                        + (fileNames.get(originalFileName) - 1) + ")"
                                        + originalFileName.substring(originalFileName.lastIndexOf("."))
                                : originalFileName));
                zos.write(document.getFileContent());
                zos.closeEntry();
            } catch (com.scnsoft.eldermark.shared.exceptions.ResidentOptedOutException e) {
                logger.error("Resident was opted out, doc #" + documentId, e);
            } catch (com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException e) {
                logger.error("Document not found, doc #" + documentId, e);
            } catch (Exception e) {
                logger.error("Document download failed, doc #" + documentId, e);
            }
        }

        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        ServletOutputStream sos = response.getOutputStream();
        sos.write(baos.toByteArray());
        sos.flush();
    }
}
