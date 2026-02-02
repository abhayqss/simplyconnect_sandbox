package com.scnsoft.eldermark.test.integration.ccd;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.ccd.CcdHL7ConstructingService;
import com.scnsoft.eldermark.services.ccd.CcdHL7ParsingService;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import com.scnsoft.eldermark.services.cda.templates.header.RecordTargetFactory;
import com.scnsoft.eldermark.test.TestApplicationH2Config;
import com.scnsoft.eldermark.test.TestApplicationMsSqlConfig;
import com.scnsoft.eldermark.test.integration.cda.util.TestUtils;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.PatientRole;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
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
 * A simple integration test for {@link CcdHL7ParsingService} and {@link CcdHL7ConstructingService}
 * which verifies that document parsing and document construction are mutually reversible transformations. <br/>
 * This test is intended to be executed only locally (on a developer's machine).
 * <br/><br/>
 * Preparation steps:
 * <ol>
 *     <li>Preconfigure MS SQL database by manually executing {@code `mvn flyway:migrate`} from ExchangeDatabase module</li>
 *     <li>Set connection properties in {@code /test/resources/datasource-test-mssql.properties} file</li>
 *     <li>Add an HL7 CCD for testing to {@code /test/resources/cda/hl7} directory</li>
 * </ol>
 *
 * Run via Run/Debug configuration of IntelliJ IDEA or by executing {@code `mvn test -P Local -Dtest=CcdHL7OperationsIT -DfailIfNoTests=false`}.
 * <br/><br/>
 * When executed this test performs the following steps:
 * <ol>
 *     <li>Load CCD.XML file using MDHT library</li>
 *     <li>Cast the {@link ClinicalDocument} to {@link ContinuityOfCareDocument} and parse it</li>
 *     <li>Parse {@code recordTarget} header section (Transformation {@link PatientRole} -> {@link Resident})</li>
 *     <li>Persist the {@link Resident}</li>
 *     <li>Parse document (Transformation {@link ContinuityOfCareDocument} -> {@link ClinicalDocumentVO})</li>
 *     <li>Construct document (Transformation {@link ClinicalDocumentVO} -> {@link ContinuityOfCareDocument})</li>
 *     <li>Save {@link ContinuityOfCareDocument} to {@code test_output.xml} file using MDHT library</li>
 * </ol>
 * After this, a tester manually compares the original document and the resulting one to find any significant differences.
 * <br/><br/>
 * Note that this test is working with a real database configured in {@link TestApplicationMsSqlConfig}.
 * But it's not a requirement; you are free to use any other database. In order to switch to in-memory H2 database
 * use {@link TestApplicationH2Config} configuration instead and add {@link TransactionDbUnitTestExecutionListener}
 * with {@code DatabaseSetup(value = "/datasets/import.xml", type = DatabaseOperation.REFRESH)}
 * (see {@link CcdHL7ParsingServiceIT} for example).
 * @author phomal
 * Created on 2/13/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestApplicationMsSqlConfig.class })
@TransactionConfiguration(defaultRollback = true)
@Transactional
public class CcdHL7OperationsIT {

    private static final Logger logger = LoggerFactory.getLogger(CcdHL7OperationsIT.class);

    @Autowired
    private CcdHL7ParsingService ccdHL7ParsingService;

    @Autowired
    private CcdHL7ConstructingService ccdHL7ConstructingService;

    @Autowired
    private ResidentService residentService;

    @Autowired
    private DatabasesDao databaseDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private RecordTargetFactory recordTargetFactory;

    private static final String BASE_PATH = Paths.get("cda", "hl7").toString();

    static {
        CDAUtil.loadPackages();
    }

    private static void saveCcdDocument(ClinicalDocument ccd) {
        try {
            CDAUtil.save(ccd, new FileOutputStream("./test_output.xml"));
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
                "SampleCCDDocument.xml"
        };

        final ClinicalDocument doc = TestUtils.loadClinicalDocument(BASE_PATH, fileNames[0]);

        Organization organization = initOrganization();

        if (doc instanceof ContinuityOfCareDocument) {
            ContinuityOfCareDocument ccDocument = (ContinuityOfCareDocument) doc;

            final Resident resident = recordTargetFactory.parseSection(organization, ccDocument.getPatientRoles().get(0));
            Assert.assertNotNull(resident);
            resident.setLegacyTable("TestResident_NWHIN");
            resident.setSocialSecurity("11111111111");
            resident.setSsnLastFourDigits("1111");
            resident.setLegacyId("ALD111013F");
            resident.setActive(true);
            if (resident.getBirthDate() == null) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    resident.setBirthDate(sdf.parse("24.09.1932"));
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

            final ClinicalDocumentVO clinicalDocumentVO = ccdHL7ParsingService.parse(ccDocument, dbResident);
            Assert.assertNotNull(clinicalDocumentVO);
            final ContinuityOfCareDocument constructed = ccdHL7ConstructingService.construct(clinicalDocumentVO);
            Assert.assertNotNull(constructed);

            saveCcdDocument(constructed);
        }
    }

}
