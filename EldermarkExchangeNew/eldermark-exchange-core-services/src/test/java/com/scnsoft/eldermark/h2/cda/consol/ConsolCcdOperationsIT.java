package com.scnsoft.eldermark.h2.cda.consol;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.h2.cda.BaseCdaIT;
import com.scnsoft.eldermark.h2.cda.util.CdaTestUtils;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.cda.CdaGenerator;
import com.scnsoft.eldermark.service.document.cda.generator.CcdaR11CcdGenerator;
import com.scnsoft.eldermark.service.document.cda.parse.consol.ConsolCcdParsingService;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.header.RecordTargetFactory;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.PatientRole;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * A simple integration test for {@link ConsolCcdParsingService} and {@link CcdaR11CcdGenerator}
 * which verifies that document parsing and document construction are mutually reversible transformations. <br/>
 * This test is intended to be executed only locally (on a developer's machine).
 * <p>
 * Run this test via Run/Debug configuration of IntelliJ IDEA or by executing {@code `mvn test -P Local -Dtest=ConsolCcdOperationsIT`}.
 * <br/><br/>
 * When executed this test performs the following steps:
 * <ol>
 * <li>Load CCD.XML file using MDHT library</li>
 * <li>Cast the {@link ClinicalDocument} to {@link ContinuityOfCareDocument} and parse it</li>
 * <li>Parse {@code recordTarget} header section (Transformation {@link PatientRole} -> {@link Client})</li>
 * <li>Persist the {@link Client}</li>
 * <li>Parse document (Transformation {@link ContinuityOfCareDocument} -> {@link ClinicalDocumentVO})</li>
 * <li>Construct document (Transformation {@link ClinicalDocumentVO} -> {@link ContinuityOfCareDocument})</li>
 * <li>Save {@link ContinuityOfCareDocument} to {@code consol_test_output.xml} file using MDHT library</li>
 * </ol>
 * After this, a tester manually compares the original document and the resulting one to find any significant differences.
 * <br/><br/>
 * <p>
 * Note that old portal supported running this test against real database. Check out details in old portal if needed
 *
 * @author phomal
 * Created on 5/16/18.
 * <p>
 * <p>
 * P.S moved and adjusted from old portal by
 * @author sparuchnik
 */
@Transactional
public class ConsolCcdOperationsIT extends BaseCdaIT {

    private static final Logger logger = LoggerFactory.getLogger(ConsolCcdOperationsIT.class);

    @Autowired
    private ConsolCcdParsingService consolCcdParsingService;

    @Autowired
    private CdaGenerator<ContinuityOfCareDocument> consolCcdGenerator;

    @Autowired
    private ClientService clientService;

    @Autowired
    private RecordTargetFactory consolRecordTargetFactory;

    private static final String BASE_PATH = "/h2/cda/consol/r1_1";
    private final String nwhinOrganizationAlternativeId = "AllinaHealth";
    private final String nwhinCommunityName = "Allina Health";

    @Test
    @DatabaseSetup(value = "/h2/datasets/import.xml", type = DatabaseOperation.REFRESH)
    public void testParsingSection() throws Exception {
        final String fileNames[] = {
                "Sample_CCD.xml",
                "johnhalamka_from_SimplyConnect_generator.xml",
                "ehmp_ccda.xml",
                "CCDA28909100003282018_from_Qualifacts.xml"
        };

        final ClinicalDocument doc = CdaTestUtils.loadClinicalDocument(BASE_PATH, fileNames[0]);

        Community community = findCommunity(nwhinOrganizationAlternativeId, nwhinCommunityName);

        if (doc instanceof ContinuityOfCareDocument) {
            ContinuityOfCareDocument ccd = (ContinuityOfCareDocument) doc;

            final Client resident = consolRecordTargetFactory.parseSection(community, ccd.getPatientRoles().get(0));
            Assert.assertNotNull(resident);
            resident.setLegacyTable("TestResident_CCDA");
            resident.setSocialSecurity("289091000");
            resident.setSsnLastFourDigits("1000");
            resident.setLegacyId("28909-1000");
            resident.setActive(true);
            resident.setBirthDate(LocalDate.of(1988, 3, 29));

            var patientOpt = clientService.findByIdentityFields(community.getId(),
                    resident.getSocialSecurity(), resident.getBirthDate(), resident.getLastName(),
                    resident.getFirstName());
            Client dbResident = null;

            if (patientOpt.isEmpty()) {
                dbResident = clientService.save(resident);
            } else {
                var patient = patientOpt.get();
                resident.setId(patient.getId());
                resident.setAuthors(patient.getAuthors());
                resident.setAdvanceDirectives(patient.getAdvanceDirectives());
                resident.setLanguages(patient.getLanguages());
                resident.setGuardians(patient.getGuardians());
                dbResident = clientService.save(resident);
            }

            final ClinicalDocumentVO clinicalDocumentVO = consolCcdParsingService.parse(ccd, dbResident);
            Assert.assertNotNull(clinicalDocumentVO);
            final ContinuityOfCareDocument constructed = consolCcdGenerator.generate(clinicalDocumentVO);
            Assert.assertNotNull(constructed);

            CdaTestUtils.saveCcdDocument(constructed, "./consol_test_output.xml");
        }
    }

}
