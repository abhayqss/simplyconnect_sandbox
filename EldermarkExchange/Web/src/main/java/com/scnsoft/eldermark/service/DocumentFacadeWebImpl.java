package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Document;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.DocumentService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.carecoordination.CommunityCrudService;
import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import com.scnsoft.eldermark.services.cda.CdaFacade;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Lazy
public class DocumentFacadeWebImpl implements DocumentFacadeWeb {

    @Autowired
    private OrganizationService careCoordinationOrganizationService;

    @Autowired
    private CommunityCrudService communityCrudService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private CdaFacade cdaFacade;

    @Autowired
    private ResidentService residentService;

    @Override
    public String getCdaHtmlViewForDocument(Long documentId) {
        if (!currentEmployeeHasAccessToCdaDocumentView(documentId)) {
            throw new BusinessAccessDeniedException("No access to the document.");
        }
        return cdaFacade.getCdaHtmlViewForDocument(documentId);
    }

    /**
     * checking if document's resident or any merged is in accesible organization and community
     *
     * @param documentId
     * @return
     */
    private boolean currentEmployeeHasAccessToCdaDocumentView(Long documentId) {
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return true;
        }

        final Document document = documentService.findDocument(documentId);
        final Resident resident = documentService.getResident(document);
        final Set<Resident> residents = residentService.getDirectMergedResidents(resident);
        residents.add(resident);

        final Set<Long> employeeDatabaseIds = SecurityUtils.getAuthenticatedUser().getCurrentAndLinkedDatabaseIds();

        for (final Resident res : residents) {
            if (employeeDatabaseIds.contains(res.getDatabaseId()) || careCoordinationOrganizationService.checkDatabaseAccess(res.getDatabaseId(), employeeDatabaseIds)) {
                try {
                    communityCrudService.checkViewAccessToCommunitiesOrThrow(res.getFacility().getId(), res.getDatabaseId());
                    return true;
                } catch (BusinessAccessDeniedException e) {
                    // no access, trying other residents
                }
            }
        }
        return false;
    }
}
