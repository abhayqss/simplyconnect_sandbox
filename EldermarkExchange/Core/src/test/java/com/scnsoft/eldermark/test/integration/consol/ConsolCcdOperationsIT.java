package com.scnsoft.eldermark.test.integration.consol;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import com.scnsoft.eldermark.services.cda.templates.header.RecordTargetFactory;
import com.scnsoft.eldermark.services.consol.ConsolCcdConstructingService;
import com.scnsoft.eldermark.services.consol.ConsolCcdParsingService;
import com.scnsoft.eldermark.test.TestApplicationH2Config;
import com.scnsoft.eldermark.test.TestApplicationMsSqlConfig;
import com.scnsoft.eldermark.test.integration.cda.util.TestUtils;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.PatientRole;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * A simple integration test for {@link ConsolCcdParsingService} and {@link ConsolCcdConstructingService}
 * which verifies that document parsing and document construction are mutually reversible transformations. <br/>
 * This test is intended to be executed only locally (on a developer's machine).
 * <br/><br/>
 * Preparation steps:
 * <ol>
 *     <li>Preconfigure MS SQL database by manually executing {@code `mvn flyway:migrate`} from ExchangeDatabase module</li>
 *     <li>Set connection properties in {@code /test/resources/datasource-test-mssql.properties} file</li>
 *     <li>Add a C-CDA CCD for testing to {@code /test/resources/cda/c-cda_r1.1} directory</li>
 * </ol>
 *
 * Run this test via Run/Debug configuration of IntelliJ IDEA or by executing {@code `mvn test -P Local -Dtest=ConsolCcdOperationsIT`}.
 * <br/><br/>
 * When executed this test performs the following steps:
 * <ol>
 *     <li>Load CCD.XML file using MDHT library</li>
 *     <li>Cast the {@link ClinicalDocument} to {@link ContinuityOfCareDocument} and parse it</li>
 *     <li>Parse {@code recordTarget} header section (Transformation {@link PatientRole} -> {@link Resident})</li>
 *     <li>Persist the {@link Resident}</li>
 *     <li>Parse document (Transformation {@link ContinuityOfCareDocument} -> {@link ClinicalDocumentVO})</li>
 *     <li>Construct document (Transformation {@link ClinicalDocumentVO} -> {@link ContinuityOfCareDocument})</li>
 *     <li>Save {@link ContinuityOfCareDocument} to {@code consol_test_output.xml} file using MDHT library</li>
 * </ol>
 * After this, a tester manually compares the original document and the resulting one to find any significant differences.
 * <br/><br/>
 * Note that this test is working with a real database configured in {@link TestApplicationMsSqlConfig}.
 * But it's not a requirement; you are free to use any other database. In order to switch to in-memory H2 database
 * use {@link TestApplicationH2Config} configuration instead and add {@link TransactionDbUnitTestExecutionListener}
 * with {@code DatabaseSetup(value = "/datasets/import.xml", type = DatabaseOperation.REFRESH)}
 * (see {@link ConsolCcdParsingServiceIT} for example).
 *
 * @author phomal
 * Created on 5/16/18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationMsSqlConfig.class })
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class ConsolCcdOperationsIT {

    private static final Logger logger = LoggerFactory.getLogger(ConsolCcdOperationsIT.class);

    @Autowired
    private ConsolCcdParsingService consolCcdParsingService;

    @Autowired
    private ConsolCcdConstructingService consolCcdConstructingService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private DatabasesDao databaseDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private RecordTargetFactory consolRecordTargetFactory;

    private static final String BASE_PATH = Paths.get("cda", "c-cda_r1.1").toString();

    static {
        CDAUtil.loadPackages();
    }

    private static void saveCcdDocument(ClinicalDocument ccd) {
        try {
            CDAUtil.save(ccd, new FileOutputStream("./consol_test_output.xml"));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    private final String nwhinDatabaseAlternativeId = "AllinaHealth";
    private final String nwhinCommunityName = "Allina Health";

    public Organization initOrganization() {
        final Database nwhinDatabase = databaseDao.getDatabaseByAlternativeId(nwhinDatabaseAlternativeId);
        return organizationDao.getOrganizationByNameAndDatabase(nwhinCommunityName, nwhinDatabase.getId());
    }

    @Test
    public void testParsingSection() throws Exception {
        final String fileNames[] = {
                "Sample_CCD.xml",
                "johnhalamka_from_SimplyConnect_generator.xml",
                "ehmp_ccda.xml",
                "CCDA28909100003282018_from_Qualifacts.xml"
        };

        final ClinicalDocument doc = TestUtils.loadClinicalDocument(BASE_PATH, fileNames[0]);

        Organization organization = initOrganization();

        if (doc instanceof ContinuityOfCareDocument) {
            ContinuityOfCareDocument ccd = (ContinuityOfCareDocument) doc;

            final Resident resident = consolRecordTargetFactory.parseSection(organization, ccd.getPatientRoles().get(0));
            Assert.assertNotNull(resident);
            resident.setLegacyTable("TestResident_CCDA");
            resident.setSocialSecurity("289091000");
            resident.setSsnLastFourDigits("1000");
            resident.setLegacyId("28909-1000");
            resident.setActive(true);
            if (resident.getBirthDate() == null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    resident.setBirthDate(sdf.parse("29.03.1988"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            Resident patient = residentService.getResidentByIdentityFields(organization.getId(),
                    resident.getSocialSecurity(), resident.getBirthDate(), resident.getLastName(),
                    resident.getFirstName());
            Resident dbResident = null;

            if (patient == null) {
                dbResident = residentService.createResident(resident);
            } else {
                resident.setId(patient.getId());
                resident.setAuthors(patient.getAuthors());
                resident.setAdvanceDirectives(patient.getAdvanceDirectives());
                resident.setLanguages(patient.getLanguages());
                resident.setGuardians(patient.getGuardians());
                dbResident = residentService.updateResident(resident);
            }

            final ClinicalDocumentVO clinicalDocumentVO = consolCcdParsingService.parse(ccd, dbResident);
            Assert.assertNotNull(clinicalDocumentVO);
            final ContinuityOfCareDocument constructed = consolCcdConstructingService.construct(clinicalDocumentVO);
            Assert.assertNotNull(constructed);

            saveCcdDocument(constructed);
        }
    }

}
