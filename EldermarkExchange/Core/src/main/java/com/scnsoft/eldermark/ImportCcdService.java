package com.scnsoft.eldermark;

import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.ResidentDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.CdaFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;

/**
 * @author phomal
 * Created on 6/21/2017.
 */
@Transactional
@Service
public class ImportCcdService {

    private static final Logger logger = LoggerFactory.getLogger(ImportCcdService.class);

    @Autowired
    private CdaFacade cdaFacade;

    @Autowired
    private ResidentDao residentDao;

    @Autowired
    private DatabasesDao databaseDao;

    @Autowired
    private OrganizationDao organizationDao;

    private final String DATABASE_ALTERNATIVE_ID = "PhysicianRepo";
    private final String DATABASE_COMPANY_ID = "QSS";
    private final String COMMUNITY_NAME = "QSS Technosoft";

    public Organization initOrganization() {
        //Database database = databaseDao.getDatabaseByAlternativeId(DATABASE_ALTERNATIVE_ID);
        Database database = databaseDao.getDatabaseByCompanyId(DATABASE_COMPANY_ID);
        assert database != null;
        return organizationDao.getOrganizationByNameAndDatabase(COMMUNITY_NAME, database.getId());
    }

    // temporary solution
    public void parseFile(final Long residentId, final String fileName) throws Exception {
        final Organization organization = initOrganization();
        assert organization != null;
        final Resident patient = residentDao.get(residentId);
        final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        cdaFacade.importXml(inputStream, patient, organization, CdaFacade.ImportMode.OVERWRITE);
    }

}
