package com.scnsoft.scansol.web.controller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.scnsoft.eldermark.services.DocumentEncryptionService;
import com.scnsoft.scansol.shared.response.ScanSolResponseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.dao.exceptions.InitialSyncNotCompletedException;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.RoleCode;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.security.ExchangeUserDetailsService;
import com.scnsoft.eldermark.security.ExtraParamAuthenticationFilter;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.SaveDocumentCallbackImpl;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.scansol.facades.ScanSolutionFacade;
import com.scnsoft.scansol.shared.ScanSolBaseEmployeeInfoDto;
import com.scnsoft.scansol.shared.ScanSolDatabaseDto;
import com.scnsoft.scansol.shared.ScanSolDatabasesDto;
import com.scnsoft.scansol.shared.ScanSolDocumentsDto;
import com.scnsoft.scansol.shared.ScanSolDocumentsIdListDto;
import com.scnsoft.scansol.shared.ScanSolEmployeeAuthRequest;
import com.scnsoft.scansol.shared.ScanSolEmployeeAuthRequestForSSO;
import com.scnsoft.scansol.shared.ScanSolEmployeeFilterRequest;
import com.scnsoft.scansol.shared.ScanSolEmployeeInfoDto;
import com.scnsoft.scansol.shared.ScanSolEmployeesDto;
import com.scnsoft.scansol.shared.ScanSolIdsDto;
import com.scnsoft.scansol.shared.ScanSolOrganizationDto;
import com.scnsoft.scansol.shared.ScanSolOrganizationsDto;
import com.scnsoft.scansol.shared.ScanSolResidentDto;
import com.scnsoft.scansol.shared.ScanSolResidentsDto;
import com.scnsoft.scansol.shared.ScanSolUploadDocumentRequest;
import com.scnsoft.scansol.shared.UpdateDocumentRequest;

/**
 * Date: 14.05.15
 * Time: 4:33
 */
@Controller
@RequestMapping (value = "/scan-solution")
@PreAuthorize (SecurityExpressions.IS_ROLE_SCAN_SOL_MANAGER)
public class ScanSolController extends ControllerBase {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ScanSolutionFacade scanSolutionFacade;

    @Autowired
    private DocumentFacade documentFacade;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DatabasesService databasesService;

    @Autowired
    private DatabasePasswordSettingsService dbService;
    
    @Autowired
    private ExchangeUserDetailsService exchangeUserDetailsService;

    @RequestMapping(value = "/ssoemployees", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolResponseBase authSSOEmployee (@Valid @RequestBody ScanSolEmployeeAuthRequestForSSO authRequest) throws Exception {
        String companyAndUser = authRequest.getCompanyId() + ExtraParamAuthenticationFilter.getDelimiter() + authRequest.getLogin();

        ScanSolEmployeeInfoDto response = new ScanSolEmployeeInfoDto();
        try{
            ScanSolDatabaseDto database = scanSolutionFacade.getDatabasesByCompanyId(authRequest.getCompanyId());

            if(database.getIsInitialSync()!=null && database.getIsInitialSync()) {
            	throw new InitialSyncNotCompletedException("Sync for this organization is not completed yet. Please contact Eldermark support.");
            }
            UserDetails userDetail = exchangeUserDetailsService.loadUserByUsername(companyAndUser);
            Employee employee = ((ExchangeUserDetails) userDetail).getEmployee();
            Long employeeId = employee.getId();
            boolean accessToAll = userDetail.getAuthorities().contains(new SimpleGrantedAuthority(RoleCode.ROLE_CLOUD_STORAGE_USER.getName()));
            boolean accessToSelected = userDetail.getAuthorities().contains(new SimpleGrantedAuthority(RoleCode.ROLE_CLOUD_STORAGE_USER.getName() + "_COMMUNITY"));
            if (accessToAll) {
                response.setOrganizations(scanSolutionFacade.getOrganizationsByEmployee(employeeId));
            } else if (accessToSelected) {
                response.setOrganizations(scanSolutionFacade.getOrganizationsByEmployee(employeeId, RoleCode.ROLE_CLOUD_STORAGE_USER));
            } 
            
            if(response.getOrganizations() == null || response.getOrganizations().isEmpty()){
                throw new AccessDeniedException("You have not been granted access to Cloud Storage or your organization does not have the module enabled.");
            }

            ScanSolBaseEmployeeInfoDto employeeDto = new ScanSolBaseEmployeeInfoDto();
            employeeDto.setId(employeeId);
            employeeDto.setName(employee.getFullName());
            response.setEmployee(employeeDto);
            response.setRoles(scanSolutionFacade.getRoles(userDetail));

            response.setCompany(database);

            return response;
        }
        catch (final Exception e) {
            return createFailureResponse (e.getMessage());
        }

    }
    
    @RequestMapping(value = "/employees", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolResponseBase authEmployee (@Valid @RequestBody ScanSolEmployeeAuthRequest authRequest) throws Exception {
        String companyAndUser = authRequest.getCompanyId() + ExtraParamAuthenticationFilter.getDelimiter() + authRequest.getLogin();

        ScanSolEmployeeInfoDto response = new ScanSolEmployeeInfoDto();
        try{
            ScanSolDatabaseDto database = scanSolutionFacade.getDatabasesByCompanyId(authRequest.getCompanyId());

            if(database.getIsInitialSync()!=null && database.getIsInitialSync()) {
            	throw new InitialSyncNotCompletedException("Sync for this organization is not completed yet. Please contact Eldermark support.");
            }
            
            List<DatabasePasswordSettings> dbPasswordSettings = dbService.getOrganizationPasswordSettings(database.getId());
            if(dbPasswordSettings == null || dbPasswordSettings.isEmpty()) {
                dbService.createDefaultDatabasePasswordSettings(database.getId());
            }
            UsernamePasswordAuthenticationToken authAttemptToken = new UsernamePasswordAuthenticationToken(companyAndUser, authRequest.getPwd());
            Authentication authResult = authenticationManager.authenticate(authAttemptToken);


            Employee employee = ((ExchangeUserDetails) authResult.getPrincipal()).getEmployee();
            Long employeeId = employee.getId();
            List<ScanSolOrganizationDto> listOfOraganization = null;

			boolean accessToAll = authResult.getAuthorities().contains(new SimpleGrantedAuthority(RoleCode.ROLE_CLOUD_STORAGE_USER.getName()));
			boolean accessToSelected = authResult.getAuthorities().contains(new SimpleGrantedAuthority(RoleCode.ROLE_CLOUD_STORAGE_USER.getName() + "_COMMUNITY"));
			if (accessToAll) {
				listOfOraganization=scanSolutionFacade.getOrganizationsByEmployee(employeeId);
				response.setOrganizations(listOfOraganization);
			} else if (accessToSelected) {
				listOfOraganization = scanSolutionFacade.getOrganizationsByEmployee(employeeId,RoleCode.ROLE_CLOUD_STORAGE_USER);
				response.setOrganizations(listOfOraganization);
			}
			scanSolutionFacade.getOrganizationsByRole(employeeId, listOfOraganization);

			if (response.getOrganizations() == null || response.getOrganizations().isEmpty()) {
				throw new AccessDeniedException(
						"You have not been granted access to Cloud Storage or your organization does not have the module enabled.");
			}

            ScanSolBaseEmployeeInfoDto employeeDto = new ScanSolBaseEmployeeInfoDto();
            employeeDto.setId(employeeId);
            employeeDto.setName(employee.getFullName());
            response.setEmployee(employeeDto);
            response.setRoles(scanSolutionFacade.getRoles(authResult));

            response.setCompany(database);

            return response;
        }
        catch (final Exception e) {
            return createFailureResponse (e.getMessage());
        }

    }

    @RequestMapping(value = "/databases", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolDatabasesDto getDatabasesData () throws Exception {
        ScanSolDatabasesDto databases = new ScanSolDatabasesDto ();
        databases.setCompanies (scanSolutionFacade.getDatabases());
        return databases;
    }

    @RequestMapping(value = "/databases/{databaseId}/employees", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolEmployeesDto getEmployeesData (@PathVariable(value = "databaseId") long databaseId) throws Exception {
        ScanSolEmployeesDto employees = new ScanSolEmployeesDto ();
        employees.setEmployees(scanSolutionFacade.getEmployeesForDatabase(databaseId));
        return employees;
    }

    @RequestMapping(value = "/employees/{employeeId}/organizations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolIdsDto getOrganizationsByEmployee (@PathVariable(value = "employeeId") long employeeId) throws Exception {
        ScanSolIdsDto organizationIds = new ScanSolIdsDto ();
        organizationIds.setIds(scanSolutionFacade.getOrganizationIdsForEmployee(employeeId));
        return organizationIds;
    }

    @RequestMapping(value = "/databases/{databaseId}/organizations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolOrganizationsDto getOrganizationsByDatabase (@PathVariable(value = "databaseId") long databaseId) throws Exception {
        ScanSolOrganizationsDto organizations = new ScanSolOrganizationsDto ();
        organizations.setOrganizations(scanSolutionFacade.getOrganizationsForDatabase(databaseId));
        return organizations;
    }

	@RequestMapping(value = "/organizations/{organizationId}/residents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public ScanSolResidentsDto getResidentsByOrganization(@PathVariable(value = "organizationId") long organizationId)
			throws Exception {
		ScanSolResidentsDto residentsDto = new ScanSolResidentsDto();
		List<ScanSolResidentDto> residentList;
		residentList = scanSolutionFacade.getResidentsByOrganization(organizationId);
		residentsDto.setResidents(residentList);
		for (ScanSolResidentDto resident : residentList) {
			resident.setArchiveDate(
					scanSolutionFacade.getResidentsByOrganizationArchive(resident.getId(), organizationId));
		}
		return residentsDto;
	}

    @RequestMapping(value = "/residents/{residentId}/documents", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolDocumentsDto getResidentDocuments (@Valid @RequestBody ScanSolEmployeeFilterRequest employeeDto, @PathVariable(value = "residentId") long residentId) throws Exception {
        ScanSolDocumentsDto documentsDto = new ScanSolDocumentsDto();
        if(employeeDto.getEmployeeId () != null){
            documentsDto.setDocuments (scanSolutionFacade.getDocumentsForResident (residentId, employeeDto.getEmployeeId ()));
        }
        return documentsDto;
    }

    @RequestMapping(value = "/documents/{documentId}/preview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void previewDocument (@Valid @RequestBody ScanSolEmployeeFilterRequest employeeDto, @PathVariable(value = "documentId") long documentId, HttpServletResponse response) throws Exception {
        if(employeeDto.getEmployeeId () != null){
            DocumentBean document = scanSolutionFacade.findDocument (documentId, employeeDto.getEmployeeId ());
            downloadOrViewCustomDocument(document, response, true);
        }
    }
    
    @RequestMapping(value = "/documents/downloadzip", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void downloadDocumentZip(final @RequestBody ScanSolDocumentsIdListDto scanSolDocumentsIdListDto,
            HttpServletResponse response) throws Exception {
        scanSolutionFacade.getDocumentsZip(scanSolDocumentsIdListDto.getDocumentsIdList(), response,
                scanSolDocumentsIdListDto.getEmployeeId());
    }

    @RequestMapping(value = "/residents/{residentId}/documents/upload", method = RequestMethod.POST)
    @ResponseBody
    public Long uploadDocument (@RequestBody final ScanSolUploadDocumentRequest uploadDocument, @PathVariable (value = "residentId") long residentId) throws Exception {
        DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
                .setDocumentTitle(uploadDocument.getTitle ().trim())
                .setFileName(uploadDocument.getOriginalName ().trim())
                .setMimeType(uploadDocument.getMimeType ().trim())
                .build();

        List<Long> sharedWith = new ArrayList<Long> ();
        if (Boolean.TRUE.equals(uploadDocument.isShared())) {
            // share with employee's company
            Employee employee =  employeeService.getEmployee (uploadDocument.getEmployeeId ());
            sharedWith.add(employee.getDatabaseId());
        } else {
            // share with scansol only
            sharedWith.add(databasesService.getDatabaseByCompanyId("service").getId());
        }

        long authorId = uploadDocument.getEmployeeId ();
        return documentFacade.saveDocument(
                documentMetadata,
                residentId,
                authorId,
                false,
                sharedWith, true, new SaveDocumentCallbackImpl() {
                    @Override
                    public void saveToFile(File file) {
                        try {
                            FileCopyUtils.copy(uploadDocument.getContent (), new FileOutputStream(file));
                        } catch (IOException e) {
                            throw new FileIOException("Failed to save file " + uploadDocument.getOriginalName (), e);
                        }
                    }
                },
                true, uploadDocument.getContent()).getId();
    }

    @RequestMapping(value = "/documents/{documentId}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteDocument(@PathVariable(value = "documentId") Long documentId,
                               @RequestParam(value = "couldBeRestored", defaultValue = "false") Boolean couldBeRestored) {
        documentFacade.deleteDocument(documentId, couldBeRestored);
    }

    @RequestMapping(value = "/documents/{documentId}/restore", method = RequestMethod.POST)
    @ResponseBody
    public void restoreDocument(@PathVariable(value = "documentId") Long documentId) {
        documentFacade.restoreDocument(documentId, true);
    }

    @RequestMapping(value = "/documents/{documentId}/update", method = RequestMethod.POST)
    @ResponseBody
    public void updateDocument (@PathVariable(value = "documentId") Long documentId,
                                              @RequestBody final UpdateDocumentRequest updateDocumentRequest) throws Exception {
        if (updateDocumentRequest.getTitle() != null) {
            scanSolutionFacade.updateDocumentTitle(documentId, updateDocumentRequest.getEmployeeId(), updateDocumentRequest.getTitle());
        }

        if (updateDocumentRequest.isShared() != null) {
            List<Long> sharedWithDatabaseIds = new ArrayList<Long> ();
            if (Boolean.TRUE.equals(updateDocumentRequest.isShared())) {
                // share with employee's company
                Employee employee =  employeeService.getEmployee (updateDocumentRequest.getEmployeeId ());
                sharedWithDatabaseIds.add(employee.getDatabaseId());
            } else {
                // share with scansol only
                sharedWithDatabaseIds.add(databasesService.getDatabaseByCompanyId("service").getId());
            }

            scanSolutionFacade.updateDocumentSharingPolicy(documentId, updateDocumentRequest.getEmployeeId(), sharedWithDatabaseIds);
        }
    }

    private void downloadOrViewCustomDocument(DocumentBean document, HttpServletResponse response, boolean isViewMode) {
        String contentType = isViewMode ? document.getMimeType() : "application/x-download";
        String openType = isViewMode ? "inline" : "attachment";

        response.setContentType(contentType);
        response.setHeader("Content-Disposition", openType + ";filename=\"" + document.getOriginalFileName() + "\"");

        try {
            FileCopyUtils.copy(new ByteArrayInputStream(document.getFileContent()), response.getOutputStream ());
        } catch (IOException e) {
            throw new FileIOException ("Failed to save file " + document.getOriginalFileName(), e);
        }
    }
    
    @RequestMapping(value = "/organizations/{organizationId}/details", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ScanSolOrganizationDto getOrganizationDetails (@PathVariable(value = "organizationId") Long organizationId) throws Exception {
        ScanSolOrganizationDto organizationDto = scanSolutionFacade.getOrganization(organizationId);
        return organizationDto;
    }
}
