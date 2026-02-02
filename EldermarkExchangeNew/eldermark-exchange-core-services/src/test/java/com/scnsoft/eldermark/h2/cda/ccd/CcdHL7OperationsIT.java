package com.scnsoft.eldermark.h2.cda.ccd;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.h2.TestApplicationH2Config;
import com.scnsoft.eldermark.h2.cda.BaseCdaIT;
import com.scnsoft.eldermark.h2.cda.util.CdaTestUtils;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.cda.CdaGenerator;
import com.scnsoft.eldermark.service.document.cda.generator.CcdHL7Generator;
import com.scnsoft.eldermark.service.document.cda.parse.ccd.CcdHL7ParsingService;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.header.RecordTargetFactory;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.PatientRole;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * A simple integration test for {@link CcdHL7ParsingService} and {@link CcdHL7Generator}
 * which verifies that document parsing and document construction are mutually reversible transformations. <br/>
 * This test is intended to be executed only locally (on a developer's machine).
 * <br/><br/>
 * <p>
 * Run via Run/Debug configuration of IntelliJ IDEA or by executing {@code `mvn test -P Local -Dtest=CcdHL7OperationsIT -DfailIfNoTests=false`}.
 * <br/><br/>
 * When executed this test performs the following steps:
 * <ol>
 * <li>Load CCD.XML file using MDHT library</li>
 * <li>Cast the {@link ClinicalDocument} to {@link ContinuityOfCareDocument} and parse it</li>
 * <li>Parse {@code recordTarget} header section (Transformation {@link PatientRole} -> {@link Client})</li>
 * <li>Persist the {@link Client}</li>
 * <li>Parse document (Transformation {@link ContinuityOfCareDocument} -> {@link ClinicalDocumentVO})</li>
 * <li>Construct document (Transformation {@link ClinicalDocumentVO} -> {@link ContinuityOfCareDocument})</li>
 * <li>Save {@link ContinuityOfCareDocument} to {@code test_output.xml} file using MDHT library</li>
 * </ol>
 * After this, a tester manually compares the original document and the resulting one to find any significant differences.
 * <br/><br/>
 * <p>
 * Note that old portal supported running this test against real database. Check out details in old portal if needed
 *
 * @author phomal
 * Created on 2/13/17.
 * <p>
 * <p>
 * P.S moved and adjusted from old portal by
 * @author sparuchnik
 */
@Transactional
public class CcdHL7OperationsIT extends BaseCdaIT {

    private static final Logger logger = LoggerFactory.getLogger(CcdHL7OperationsIT.class);

    @Autowired
    private CcdHL7ParsingService ccdHL7ParsingService;

    @Autowired
    private CdaGenerator<ContinuityOfCareDocument> ccdHL7Generator;

    @Autowired
    private ClientService clientService;

    @Autowired
    private RecordTargetFactory recordTargetFactory;

    private static final String BASE_PATH = "/h2/cda/ccd/";

    private final String nwhinOrganizationAlternativeId = "AllinaHealth";
    private final String nwhinCommunityName = "Allina Health";

    @Test
    @DatabaseSetup(value = "/h2/datasets/import.xml", type = DatabaseOperation.REFRESH)
    public void testParsingSection() throws Exception {
        final String fileNames[] = {
                "SampleCCDDocument.xml"
        };

        final ClinicalDocument doc = CdaTestUtils.loadClinicalDocument(BASE_PATH, fileNames[0]);

        Community community = findCommunity(nwhinOrganizationAlternativeId, nwhinCommunityName);


        if (doc instanceof ContinuityOfCareDocument) {
            ContinuityOfCareDocument ccDocument = (ContinuityOfCareDocument) doc;

            var client = recordTargetFactory.parseSection(community, ccDocument.getPatientRoles().get(0));
            Assert.assertNotNull(client);
            client.setLegacyTable("TestResident_NWHIN");
            client.setSocialSecurity("11111111111");
            client.setSsnLastFourDigits("1111");
            client.setLegacyId("ALD111013F");
            client.setActive(true);
            client.setBirthDate(LocalDate.of(1932, 9, 24));

            var patientOpt = clientService.findByIdentityFields(community.getId(),
                    client.getSocialSecurity(), client.getBirthDate(), client.getLastName(),
                    client.getFirstName());
            Client dbResident = null;

            if (patientOpt.isEmpty()) {
                dbResident = clientService.save(client);
            } else {
                var patient = patientOpt.get();
                client.setId(patient.getId());
                client.setAuthors(patient.getAuthors());
                client.setAdvanceDirectives(patient.getAdvanceDirectives());
                client.setLanguages(patient.getLanguages());
                client.setGuardians(patient.getGuardians());
                dbResident = clientService.save(client);
            }

            final ClinicalDocumentVO clinicalDocumentVO = ccdHL7ParsingService.parse(ccDocument, dbResident);
            Assert.assertNotNull(clinicalDocumentVO);
            final ContinuityOfCareDocument constructed = ccdHL7Generator.generate(clinicalDocumentVO);
            Assert.assertNotNull(constructed);

            CdaTestUtils.saveCcdDocument(constructed, "./test_output.xml");
        }
    }

}
