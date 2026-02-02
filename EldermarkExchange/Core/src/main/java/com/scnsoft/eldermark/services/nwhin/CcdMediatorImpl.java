package com.scnsoft.eldermark.services.nwhin;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.facades.ConnectNhinGateway;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.cda.CdaFacade;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.DocumentRetrieveDto;
import com.scnsoft.eldermark.shared.ResidentDto;
import com.scnsoft.eldermark.shared.SearchScope;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author phomal
 * Created on 2/13/2017.
 */
@Component
public class CcdMediatorImpl implements CcdMediator {

    private static final Logger logger = LoggerFactory.getLogger(CcdMediatorImpl.class);

    @Autowired
    private DatabasesService databaseService;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private CdaFacade cdaFacade;

    @Autowired
    private ConnectNhinGateway connectNhinGateway;

    @Value("${nwhin.database.alternativeId}")
    private String nwhinDatabaseAlternativeId;
    @Value("${nwhin.community.name}")
    private String nwhinCommunityName;
    @Value("${nwhin.integration.enabled}")
    private boolean nwhinEnabled;


    @Override
    public String processCcd(String homeCommunityId, ExchangeUserDetails userDetails, ResidentDto resident) {
        String residentId = null;
        if (!nwhinEnabled) {
            return residentId;
        }

        Database nwhinDatabase = databaseService.getDatabaseByAlternativeId(nwhinDatabaseAlternativeId);
        if (nwhinDatabase == null) {
            return residentId;
        } else {
            resident.setDatabaseId(nwhinDatabase.getId());
            resident.setDatabaseName(nwhinDatabase.getName());
        }
        Organization nwhinOrg = organizationDao.getOrganizationByNameAndDatabase(nwhinCommunityName, nwhinDatabase.getId());
        if (nwhinOrg == null) {
            return residentId;
        } else {
            resident.setOrganizationId(String.valueOf(nwhinOrg.getId()));
            resident.setOrganizationName(nwhinOrg.getName());
        }
        Long communityId = nwhinOrg.getId();

        List<DocumentDto> documentsList = connectNhinGateway.queryForDocuments(resident.getId(), homeCommunityId, userDetails);
        Resident dbResident = null;
        if (CollectionUtils.isNotEmpty(documentsList)) {
            for (DocumentDto document : documentsList) {
                if ("text/xml".equalsIgnoreCase(document.getMimeType())) {
                    DocumentRetrieveDto retrievedCCD = connectNhinGateway.retrieveDocument(document.getId(), homeCommunityId, userDetails);
                    final Resident patient = residentService.getResidentByIdentityFields(communityId, resident.getSsn(),
                            resident.getDateOfBirth(), resident.getLastName(), resident.getFirstName());
                    try {
                        dbResident = cdaFacade.importXml(retrievedCCD.getData().getInputStream(), patient, nwhinOrg, CdaFacade.ImportMode.OVERWRITE);
                        if (dbResident != null) {
                            // if there're more documents in the NwHIN response they're ignored... is it an issue?
                            break;
                        }
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }
        }

        if (dbResident != null) {
            residentId = String.valueOf(dbResident.getId());
            resident.setId(residentId);
            resident.setHashKey(dbResident.getHashKey());
        }
        resident.setSearchScope(SearchScope.NWHIN);

        return residentId;
    }

}
