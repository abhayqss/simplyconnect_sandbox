package com.scnsoft.eldermark.h2.cda.ccd;

import com.github.springtestdbunit.TransactionDbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.document.ccd.PlanOfCare;
import com.scnsoft.eldermark.entity.document.ccd.Procedure;
import com.scnsoft.eldermark.entity.document.ccd.SocialHistory;
import com.scnsoft.eldermark.h2.TestApplicationH2Config;
import com.scnsoft.eldermark.h2.cda.BaseCdaIT;
import com.scnsoft.eldermark.h2.cda.util.CdaTestUtils;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.cda.parse.ccd.CcdHL7ParsingService;
import com.scnsoft.eldermark.service.document.cda.parse.ccd.CcdHL7ParsingServiceImpl;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.header.RecordTargetFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.TS;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.ccd.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.scnsoft.eldermark.h2.cda.util.CdaTestUtils.assertCEMatches;
import static com.scnsoft.eldermark.h2.cda.util.CdaTestUtils.assertCollectionSizeMatches;
import static junit.framework.TestCase.assertNull;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertNotNull;

/**
 * Simple integration test for {@link CcdHL7ParsingServiceImpl}
 * <ol>
 * <li>Add test CDA CCD to {@code /resources/h2/cda/ccd} directory</li>
 * <li>Run this test by executing {@code `mvn verify -P H2`}</li>
 * </ol>
 *
 * @author phomal
 * Created on 5/15/18.
 * <p>
 * <p>
 * P.S moved and adjusted from old portal by
 * @author sparuchnik
 */
@Transactional
public class CcdHL7ParsingServiceIT extends BaseCdaIT {

    private static final Logger logger = LoggerFactory.getLogger(CcdHL7ParsingServiceIT.class);

    @Autowired
    private CcdHL7ParsingService ccdHL7ParsingService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private RecordTargetFactory consolRecordTargetFactory;

    private static final String BASE_PATH = "/h2/cda/ccd/";

    @Test
    @DatabaseSetup(value = "/h2/datasets/import.xml", type = DatabaseOperation.REFRESH)
    public void testParsingOfExemplarySampleCCD() throws Exception {
        test("SampleCCDDocument.xml");
    }

    private final String targetOrganizationAlternativeId = "AllinaHealth";
    private final String targetCommunityName = "Allina Health";

    private void test(String fileName) throws Exception {
        var community = findCommunity(targetOrganizationAlternativeId, targetCommunityName);

        final ClinicalDocument doc = CdaTestUtils.loadClinicalDocument(BASE_PATH, fileName);
        assertNotNull(doc);
        assertThat(doc, instanceOf(ContinuityOfCareDocument.class));
        final ContinuityOfCareDocument ccd = (ContinuityOfCareDocument) doc;
        final CDAUtil.CDAXPath xpath = CDAUtil.createCDAXPath(ccd);

        final Client resident = consolRecordTargetFactory.parseSection(community, ccd.getPatientRoles().get(0));
        assertNotNull(resident);
        // TODO validate recordTarget

        resident.setActive(true);
        final Client dbResident = clientService.save(resident);
        assertNotNull(dbResident);

        // Execute the method being tested
        final ClinicalDocumentVO result = ccdHL7ParsingService.parse(ccd, dbResident);

        // Validation
        assertNotNull(result);
        validateBody(xpath, result);
        validateHeader(xpath, result);
        validateRecordTarget(xpath, result.getRecordTarget());

        // TODO add more validations for header and body sections
    }

    /**
     * shallow validation of body sections
     */
    private static void validateBody(CDAUtil.CDAXPath xpath, ClinicalDocumentVO result) throws Exception {
        final String XPATH_ADVANCE_DIRECTIVES_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.1'] and not(@nullFlavor)]";
        final List<AdvanceDirectiveObservation> advanceDirectiveObservations =
                xpath.selectNodes(XPATH_ADVANCE_DIRECTIVES_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.1.17'] and not(@nullFlavor)]", AdvanceDirectiveObservation.class);
        assertCollectionSizeMatches("Advance Directives", result.getAdvanceDirectives(), advanceDirectiveObservations);

        final String XPATH_ALLERGIES_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.2'] and not(@nullFlavor)]";
        final List<ProblemAct> allergyProblemActs =
                xpath.selectNodes(XPATH_ALLERGIES_SECTION + "//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.1.27'] and not(@nullFlavor)]", ProblemAct.class);
        assertCollectionSizeMatches("Allergies", result.getAllergies(), allergyProblemActs);

        final String XPATH_ENCOUNTERS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.3'] and not(@nullFlavor)]";
        final List<Encounter> encounters =
                xpath.selectNodes(XPATH_ENCOUNTERS_SECTION + "//cda:encounter[cda:templateId[@root='2.16.840.1.113883.10.20.1.21'] and not(@nullFlavor)]", Encounter.class);
        assertCollectionSizeMatches("Encounters", result.getEncounters(), encounters);

        final String XPATH_FAMILY_HISTORY_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.4'] and not(@nullFlavor)]";
        final List<FamilyHistoryOrganizer> familyHistoryOrganizers =
                xpath.selectNodes(XPATH_FAMILY_HISTORY_SECTION + "//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.1.23'] and not(@nullFlavor)]", FamilyHistoryOrganizer.class);
        assertCollectionSizeMatches("FamilyHistories", result.getFamilyHistories(), familyHistoryOrganizers);

        final String XPATH_FUNCTIONAL_STATUS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.5'] and not(@nullFlavor)]";
        final FunctionalStatusSection functionalStatusSection = xpath.selectSingleNode(XPATH_FUNCTIONAL_STATUS_SECTION, FunctionalStatusSection.class);
        // organized FSO
        final List<ResultOrganizer> functionalStatusResultOrganizers =
                xpath.selectNodes(XPATH_FUNCTIONAL_STATUS_SECTION + "//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.1.32'] and not(@nullFlavor)]", ResultOrganizer.class);
        // non-organized FSO
        final List<ProblemAct> functionalStatusProblemActs =
                xpath.selectNodes(XPATH_FUNCTIONAL_STATUS_SECTION + "//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.1.27'] and not(@nullFlavor)]", ProblemAct.class);
        final List<ProblemObservation> functionalStatusProblemObservations = xpath.selectNodes(XPATH_FUNCTIONAL_STATUS_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.1.28'] and not(@nullFlavor)]", ProblemObservation.class);
        if (functionalStatusSection != null) {
            assertThat("Functional Status", result.getFunctionalStatuses(), hasSize(1));
            final FunctionalStatus functionalStatus = result.getFunctionalStatuses().get(0);
            assertCollectionSizeMatches("Functional Status Organizers", functionalStatus.getFunctionalStatusResultOrganizers(), functionalStatusResultOrganizers);
            assertCollectionSizeMatches("Functional Status Problem Observations", functionalStatus.getFunctionalStatusProblemObservations(), functionalStatusProblemObservations);
        } else {
            assertCollectionSizeMatches("Functional Status", result.getFunctionalStatuses(), null);
        }

        final String XPATH_IMMUNIZATIONS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.6'] and not(@nullFlavor)]";
        final List<SubstanceAdministration> immunizationMedicationActivities =
                xpath.selectNodes(XPATH_IMMUNIZATIONS_SECTION + "//cda:substanceAdministration[cda:templateId[@root='2.16.840.1.113883.10.20.1.24'] and not(@nullFlavor)]", SubstanceAdministration.class);
        assertCollectionSizeMatches("Immunizations", result.getImmunizations(), immunizationMedicationActivities);

        final String XPATH_MEDICAL_EQUIPMENT_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.7'] and not(@nullFlavor)]";
        final List<SupplyActivity> supplyActivities =
                xpath.selectNodes(XPATH_MEDICAL_EQUIPMENT_SECTION + "//cda:supply[cda:templateId[@root='2.16.840.1.113883.10.20.1.34'] and not(@nullFlavor)]", SupplyActivity.class);
        assertCollectionSizeMatches("MedicalEquipments", result.getMedicalEquipments(), supplyActivities);

        final String XPATH_MEDICATIONS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.8'] and not(@nullFlavor)]";
        final List<MedicationActivity> medicationActivities =
                xpath.selectNodes(XPATH_MEDICATIONS_SECTION + "//cda:substanceAdministration[cda:templateId[@root='2.16.840.1.113883.10.20.1.24'] and not(@nullFlavor)]", MedicationActivity.class);
        assertCollectionSizeMatches("Medications", result.getMedications(), medicationActivities);

        final String XPATH_PAYERS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.9'] and not(@nullFlavor)]";
        final List<CoverageActivity> coverageActivities =
                xpath.selectNodes(XPATH_PAYERS_SECTION + "//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.1.20'] and not(@nullFlavor)]", CoverageActivity.class);
        assertCollectionSizeMatches("Payers", result.getPayers(), coverageActivities);

        // plan of care
        final String XPATH_PLAN_OF_CARE_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.10'] and not(@nullFlavor)]";
        final PlanOfCareSection planOfCareSection = xpath.selectSingleNode(XPATH_PLAN_OF_CARE_SECTION, PlanOfCareSection.class);
        final List<PlanOfCareActivityAct> planOfCareActivityActs =
                xpath.selectNodes(XPATH_PLAN_OF_CARE_SECTION + "//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.1.25'] and not(@nullFlavor)]", PlanOfCareActivityAct.class);
        final List<PlanOfCareActivityEncounter> planOfCareActivityEncounters =
                xpath.selectNodes(XPATH_PLAN_OF_CARE_SECTION + "//cda:encounter[cda:templateId[@root='2.16.840.1.113883.10.20.1.25'] and not(@nullFlavor)]", PlanOfCareActivityEncounter.class);
        final List<PlanOfCareActivityProcedure> planOfCareActivityProcedures =
                xpath.selectNodes(XPATH_PLAN_OF_CARE_SECTION + "//cda:procedure[cda:templateId[@root='2.16.840.1.113883.10.20.1.25'] and not(@nullFlavor)]", PlanOfCareActivityProcedure.class);
        if (planOfCareSection != null) {
            assertThat("Plan of Care", result.getPlanOfCares(), hasSize(1));
            final PlanOfCare planOfCare = result.getPlanOfCares().get(0);
            assertCollectionSizeMatches("Plan of Care Activity Acts", planOfCare.getPlanOfCareActivityActList(), planOfCareActivityActs);
            assertCollectionSizeMatches("Plan of Care Activity Encounters", planOfCare.getPlanOfCareActivityEncounterList(), planOfCareActivityEncounters);
            assertCollectionSizeMatches("Plan of Care Activity Procedures", planOfCare.getPlanOfCareActivityProcedureList(), planOfCareActivityProcedures);
        } else {
            assertCollectionSizeMatches("Plan of Care", result.getPlanOfCares(), null);
        }

        final String XPATH_PROBLEMS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.11'] and not(@nullFlavor)]";
        final List<ProblemAct> problemActs =
                xpath.selectNodes(XPATH_PROBLEMS_SECTION + "//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.1.27'] and not(@nullFlavor)]", ProblemAct.class);
        assertCollectionSizeMatches("Problems", result.getProblems(), problemActs);

        // procedures
        final String XPATH_PROCEDURES_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.12'] and not(@nullFlavor)]";
        final List<ProcedureActivityAct> procedureActivityActs =
                xpath.selectNodes(XPATH_PROCEDURES_SECTION + "//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.1.29'] and not(@nullFlavor)]", ProcedureActivityAct.class);
        final List<ProcedureActivityObservation> procedureActivityObservations =
                xpath.selectNodes(XPATH_PROCEDURES_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.1.29'] and not(@nullFlavor)]", ProcedureActivityObservation.class);
        final List<ProcedureActivityProcedure> procedureActivityProcedures =
                xpath.selectNodes(XPATH_PROCEDURES_SECTION + "//cda:procedure[cda:templateId[@root='2.16.840.1.113883.10.20.1.29'] and not(@nullFlavor)]", ProcedureActivityProcedure.class);
        // FIXME this check is valid but failing
        assertThat("Procedure", result.getProcedures(), hasSize(1));
        final Procedure procedure = result.getProcedures().get(0);
        assertThat("Procedure Activity Acts", procedure.getActs(), hasSize(procedureActivityActs.size()));
        assertThat("Procedure Activity Procedures", procedure.getActivities(), hasSize(procedureActivityProcedures.size()));
        assertThat("Procedure Activity Observations", procedure.getObservations(), hasSize(procedureActivityObservations.size()));

        // Purpose section (template 2.16.840.1.113883.10.20.1.13) is ignored during parsing so it's not tested here

        final String XPATH_RESULTS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.14'] and not(@nullFlavor)]";
        final List<ResultOrganizer> resultOrganizers =
                xpath.selectNodes(XPATH_RESULTS_SECTION + "//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.1.32'] and not(@nullFlavor)]", ResultOrganizer.class);
        final List<ResultObservation> resultObservations =
                xpath.selectNodes(XPATH_RESULTS_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.1.31'] and not(@nullFlavor)]", ResultObservation.class);
        assertCollectionSizeMatches("Results", result.getResults(), resultOrganizers);
        if (resultOrganizers.size() == 1) {
            assertThat(result.getResults().get(0).getObservations(), hasSize(resultObservations.size()));
        }

        final String XPATH_SOCIAL_HISTORY_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.15'] and not(@nullFlavor)]";
        final SocialHistorySection socialHistorySection = xpath.selectSingleNode(XPATH_SOCIAL_HISTORY_SECTION, SocialHistorySection.class);
        final List<SocialHistoryObservation> socialHistoryObservations =
                xpath.selectNodes(XPATH_SOCIAL_HISTORY_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.1.33'] and not(@nullFlavor)]", SocialHistoryObservation.class);
        if (socialHistorySection != null) {
            assertThat("Social History", result.getSocialHistories(), hasSize(1));
            final SocialHistory socialHistory = result.getSocialHistories().get(0);
            assertThat("Social History Observations", socialHistory.getSocialHistoryObservations(), hasSize(socialHistoryObservations.size()));
            assertNull("Social History > Smoking Status Observations", socialHistory.getSmokingStatusObservations());
            assertNull("Social History > Pregnancy Observations", socialHistory.getPregnancyObservations());
            assertNull("Social History > Tobacco Use", socialHistory.getTobaccoUses());
        } else {
            assertCollectionSizeMatches("Social History", result.getResults(), null);
        }

        final String XPATH_VITAL_SIGNS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.16'] and not(@nullFlavor)]";
        final List<VitalSignsOrganizer> vitalSignsOrganizers =
                xpath.selectNodes(XPATH_VITAL_SIGNS_SECTION + "//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.1.35'] and not(@nullFlavor)]", VitalSignsOrganizer.class);
        final List<ResultObservation> vitalSignObservations =
                xpath.selectNodes(XPATH_VITAL_SIGNS_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.1.31'] and not(@nullFlavor)]", ResultObservation.class);
        assertCollectionSizeMatches("VitalSigns", result.getVitalSigns(), vitalSignsOrganizers);
        if (vitalSignsOrganizers.size() == 1) {
            assertThat(result.getVitalSigns().get(0).getVitalSignObservations(), hasSize(vitalSignObservations.size()));
        }
    }

    /**
     * shallow validation of header sections
     */
    private static void validateHeader(CDAUtil.CDAXPath xpath, ClinicalDocumentVO result) throws Exception {
        final List<PatientRole> patientRoles = xpath.selectNodes("//cda:recordTarget/cda:patientRole", PatientRole.class);
        if (!CollectionUtils.isEmpty(patientRoles)) {
            assertNotNull("Record Target", result.getRecordTarget());
        }

        final Custodian custodian = xpath.selectSingleNode("//cda:ClinicalDocument/cda:custodian", Custodian.class);
        if (CcdParseUtils.hasContent(custodian)) {
            assertNotNull("Custodian", result.getCustodian());
            // TODO more assertions
        } else {
            assertNull("Custodian", result.getCustodian());
        }

        final DataEnterer dataEnterer = xpath.selectSingleNode("//cda:ClinicalDocument/cda:dataEnterer", DataEnterer.class);
        if (CcdParseUtils.hasContent(dataEnterer)) {
            assertNotNull("Data Enterer", result.getDataEnterer());
            // TODO more assertions
        } else {
            assertNull("Data Enterer", result.getDataEnterer());
        }

        final LegalAuthenticator legalAuthenticator = xpath.selectSingleNode("//cda:ClinicalDocument/cda:legalAuthenticator", LegalAuthenticator.class);
        // TODO nothing to validate : the header is ignored
        final Component2 component2 = xpath.selectSingleNode("//cda:ClinicalDocument/cda:componentOf", Component2.class);
        // TODO nothing to validate : the header is ignored

        final List<Author> authors = xpath.selectNodes("//cda:ClinicalDocument/cda:author", Author.class);
        assertCollectionSizeMatches("Authors", result.getAuthors(), authors);

        final List<Informant12> informant12s = xpath.selectNodes("//cda:ClinicalDocument/cda:informant", Informant12.class);
        assertCollectionSizeMatches("Informants", result.getInformants(), informant12s);

        final List<InformationRecipient> informationRecipients = xpath.selectNodes("//cda:ClinicalDocument/cda:informationRecipient", InformationRecipient.class);
        assertCollectionSizeMatches("Information Recipients", result.getInformationRecipients(), informationRecipients);

        final List<Authenticator> authenticators = xpath.selectNodes("//cda:ClinicalDocument/cda:authenticator", Authenticator.class);
        assertCollectionSizeMatches("Authenticators", result.getAuthenticators(), authenticators);

        final List<DocumentationOf> documentationOfs = xpath.selectNodes("//cda:ClinicalDocument/cda:documentationOf", DocumentationOf.class);
        assertCollectionSizeMatches("Documentation Of", result.getDocumentationOfs(), documentationOfs);

        final List<InFulfillmentOf> inFulfillmentOfs = xpath.selectNodes("//cda:ClinicalDocument/cda:inFulfillmentOf", InFulfillmentOf.class);
        assertCollectionSizeMatches("In Fulfillment Of", result.getInFulfillmentOfs(), inFulfillmentOfs);

        final List<Authorization> authorizations = xpath.selectNodes("//cda:ClinicalDocument/cda:authorization", Authorization.class);
        assertCollectionSizeMatches("Authorizations", result.getAuthorizations(), authorizations);

        final List<Participant1> participant1s = xpath.selectNodes("//cda:ClinicalDocument/cda:participant", Participant1.class);
        assertCollectionSizeMatches("Participants", result.getParticipants(), participant1s);
    }

    /**
     * deep validation of RecordTarget header
     */
    private static void validateRecordTarget(CDAUtil.CDAXPath xpath, Client recordTarget) throws Exception {
        //final List<PN> pns = xpath.selectNodes("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:name[@use='L']", PN.class);
        final CE ceGender = xpath.selectSingleNode("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:administrativeGenderCode", CE.class);
        final CE ceMaritalStatus = xpath.selectSingleNode("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:maritalStatusCode", CE.class);
        final CE ceReligiousAffiliation = xpath.selectSingleNode("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:religiousAffiliationCode", CE.class);
        final CE ceRace = xpath.selectSingleNode("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:raceCode", CE.class);
        final CE ceEthnicity = xpath.selectSingleNode("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:ethnicGroupCode", CE.class);
        final TS tsBirth = xpath.selectSingleNode("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:birthTime", TS.class);

        assertCEMatches("RecordTarget Religion", ceReligiousAffiliation, recordTarget.getReligion());
        assertCEMatches("RecordTarget MaritalStatus", ceMaritalStatus, recordTarget.getMaritalStatus());
        assertCEMatches("RecordTarget Gender", ceGender, recordTarget.getGender());
        assertCEMatches("RecordTarget Race", ceRace, recordTarget.getRace());
        assertCEMatches("RecordTarget EthnicGroup", ceEthnicity, recordTarget.getEthnicGroup());

        if (CcdParseUtils.hasContent(tsBirth)) {
            assertNotNull(recordTarget.getBirthDate());
            // TODO more assertions
        } else {
            assertNull(recordTarget.getBirthDate());
        }
    }
}
