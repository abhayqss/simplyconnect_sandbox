package com.scnsoft.eldermark.h2.cda.consol;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.document.ccd.PlanOfCare;
import com.scnsoft.eldermark.entity.document.ccd.SocialHistory;
import com.scnsoft.eldermark.h2.cda.BaseCdaIT;
import com.scnsoft.eldermark.h2.cda.util.CdaTestUtils;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.cda.parse.consol.ConsolCcdParsingService;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.header.RecordTargetFactory;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.eclipse.mdht.uml.hl7.datatypes.AD;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.TS;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.consol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Simple integration test for {@link ConsolCcdParsingService}
 * <ol>
 *     <li>Add test CDA CCD to {@code /resources/cda/c-cda_r1.1} directory</li>
 *     <li>Run this test by executing {@code `mvn verify -P H2`}</li>
 * </ol>
 *
 * @author phomal
 * Created on 5/4/18.
 *
 * P.S moved and adjusted from old portal by
 * @author sparuchnik
 */
@Transactional
public class ConsolCcdParsingServiceIT extends BaseCdaIT {

    private static final Logger logger = LoggerFactory.getLogger(ConsolCcdParsingServiceIT.class);

    @Autowired
    private ConsolCcdParsingService consolCcdParsingService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private RecordTargetFactory consolRecordTargetFactory;

    private static final String BASE_PATH = "/h2/cda/consol/r1_1";
    private final String targetDatabaseAlternativeId = "AllinaHealth";
    private final String targetCommunityName = "Allina Health";

    @Test
    @DatabaseSetup(value = "/h2/datasets/import.xml", type = DatabaseOperation.REFRESH)
    public void testParsingOfExemplarySampleCCD() throws Exception {
        test("Sample_CCD.xml");
    }

    //@Ignore("FIXME encounters section (with optional values) is not recognized")
    @Test
    @DatabaseSetup(value = "/h2/datasets/import.xml", type = DatabaseOperation.REFRESH)
    public void testParsingOfEhmpCCD() throws Exception {
        test("ehmp_ccda.xml");
    }

    @Test
    @DatabaseSetup(value = "/h2/datasets/import.xml", type = DatabaseOperation.REFRESH)
    public void testParsingOfSimplyConnectCCD() throws Exception {
        test("johnhalamka_from_SimplyConnect_generator.xml");
    }

    @Test
    @DatabaseSetup(value = "/h2/datasets/import.xml", type = DatabaseOperation.REFRESH)
    public void testParsingOfQualifactsCCD() throws Exception {
        test("CCDA28909100003282018_from_Qualifacts.xml");
    }

    private void test(String fileName) throws Exception {
        var community = findCommunity(targetDatabaseAlternativeId, targetCommunityName);

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
        final ClinicalDocumentVO result = consolCcdParsingService.parse(ccd, dbResident);

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
        final List<AdvanceDirectiveObservation> advanceDirectiveObservations =
                xpath.selectNodes("//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.48'] and not(@nullFlavor)]", AdvanceDirectiveObservation.class);
        assertCollectionSizeMatches("AdvanceDirectives", result.getAdvanceDirectives(), advanceDirectiveObservations);

        final List<AllergyProblemAct> allergyProblemActs =
                xpath.selectNodes("//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.30'] and not(@nullFlavor)]", AllergyProblemAct.class);
        assertCollectionSizeMatches("Allergies", result.getAllergies(), allergyProblemActs);

        final String XPATH_ENCOUNTERS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.22.2.22' or @root='2.16.840.1.113883.10.20.22.2.22.1'] and not(@nullFlavor)]";
        final List<EncounterActivities> encounterActivities =
                xpath.selectNodes(XPATH_ENCOUNTERS_SECTION + "//cda:encounter[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.49'] and not(@nullFlavor)]", EncounterActivities.class);
        assertCollectionSizeMatches("Encounters", result.getEncounters(), encounterActivities);

        final List<FamilyHistoryOrganizer> familyHistoryOrganizers =
                xpath.selectNodes("//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.45'] and not(@nullFlavor)]", FamilyHistoryOrganizer.class);
        assertCollectionSizeMatches("FamilyHistories", result.getFamilyHistories(), familyHistoryOrganizers);

        // TODO add more validations for Functional Status; should we test non organized FS Results?
        final String XPATH_FUNCTIONAL_STATUS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.22.2.14'] and not(@nullFlavor)]";
        final FunctionalStatusSection functionalStatusSection = xpath.selectSingleNode(XPATH_FUNCTIONAL_STATUS_SECTION, FunctionalStatusSection.class);
        final List<FunctionalStatusResultOrganizer> functionalStatusResultOrganizers =
                xpath.selectNodes("//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.66'] and not(@nullFlavor)]", FunctionalStatusResultOrganizer.class);
        final List<CognitiveStatusResultOrganizer> cognitiveStatusResultOrganizers = xpath.selectNodes("//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.75'] and not(@nullFlavor)]", CognitiveStatusResultOrganizer.class);
        if (functionalStatusSection != null) {
            assertThat("Functional Status", result.getFunctionalStatuses(), hasSize(1));
            final FunctionalStatus functionalStatus = result.getFunctionalStatuses().get(0);
            assertCollectionSizeMatches("Functional Status Result Organizers", functionalStatus.getFunctionalStatusResultOrganizers(), functionalStatusResultOrganizers);
            assertCollectionSizeMatches("Cognitive Status Result Organizers", functionalStatus.getCognitiveStatusResultOrganizers(), cognitiveStatusResultOrganizers);
        } else {
            assertCollectionSizeMatches("Functional Status", result.getFunctionalStatuses(), null);
        }

        final List<ImmunizationActivity> immunizationActivities =
                xpath.selectNodes("//cda:substanceAdministration[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.52'] and not(@nullFlavor)]", ImmunizationActivity.class);
        assertCollectionSizeMatches("Immunizations", result.getImmunizations(), immunizationActivities);

        final List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities =
                xpath.selectNodes("//cda:supply[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.50'] and not(@nullFlavor)]", NonMedicinalSupplyActivity.class);
        assertThat("MedicalEquipments", result.getMedicalEquipments(), hasSize(0));

        final String XPATH_MEDICATIONS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.22.2.1' or @root='2.16.840.1.113883.10.20.22.2.1.1'] and not(@nullFlavor)]";
        final List<MedicationActivity> medicationActivities =
                xpath.selectNodes(XPATH_MEDICATIONS_SECTION + "//cda:substanceAdministration[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.16'] and not(@nullFlavor)]", MedicationActivity.class);
        assertCollectionSizeMatches("Medications", result.getMedications(), medicationActivities);

        final List<CoverageActivity> coverageActivities =
                xpath.selectNodes("//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.60'] and not(@nullFlavor)]", CoverageActivity.class);
        assertCollectionSizeMatches("Payers", result.getPayers(), coverageActivities);

        // plan of care
        final String XPATH_PLAN_OF_CARE_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.22.2.10'] and not(@nullFlavor)]";
        final PlanOfCareSection planOfCareSection = xpath.selectSingleNode(XPATH_PLAN_OF_CARE_SECTION, PlanOfCareSection.class);
        final List<PlanOfCareActivityAct> planOfCareActivityActs =
                xpath.selectNodes(XPATH_PLAN_OF_CARE_SECTION + "//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.39'] and not(@nullFlavor)]", PlanOfCareActivityAct.class);
        final List<PlanOfCareActivityEncounter> planOfCareActivityEncounters =
                xpath.selectNodes(XPATH_PLAN_OF_CARE_SECTION + "//cda:encounter[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.40'] and not(@nullFlavor)]", PlanOfCareActivityEncounter.class);
        final List<PlanOfCareActivityProcedure> planOfCareActivityProcedures =
                xpath.selectNodes(XPATH_PLAN_OF_CARE_SECTION + "//cda:procedure[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.41'] and not(@nullFlavor)]", PlanOfCareActivityProcedure.class);
        if (planOfCareSection != null) {
            assertThat("Plan of Care", result.getPlanOfCares(), hasSize(1));
            final PlanOfCare planOfCare = result.getPlanOfCares().get(0);
            assertCollectionSizeMatches("Plan of Care Activity Acts", planOfCare.getPlanOfCareActivityActList(), planOfCareActivityActs);
            assertCollectionSizeMatches("Plan of Care Activity Encounters", planOfCare.getPlanOfCareActivityEncounterList(), planOfCareActivityEncounters);
            assertCollectionSizeMatches("Plan of Care Activity Procedures", planOfCare.getPlanOfCareActivityProcedureList(), planOfCareActivityProcedures);
        } else {
            assertCollectionSizeMatches("Plan of Care", result.getPlanOfCares(), null);
        }

        final List<ProblemConcernAct> problemConcernActs =
                xpath.selectNodes("//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.3'] and not(@nullFlavor)]", ProblemConcernAct.class);//TODO ggavrysh change xpath and uncomment assert
//        assertCollectionSizeMatches("Problems", result.getProblems(), problemConcernActs);

        // procedures
        final List<ProcedureActivityAct> procedureActivityActs =
                xpath.selectNodes("//cda:act[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.12'] and not(@nullFlavor)]", ProcedureActivityAct.class);
        final List<ProcedureActivityObservation> procedureActivityObservations =
                xpath.selectNodes("//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.13'] and not(@nullFlavor)]", ProcedureActivityObservation.class);
        final List<ProcedureActivityProcedure> procedureActivityProcedures =
                xpath.selectNodes("//cda:procedure[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.14'] and not(@nullFlavor)]", ProcedureActivityProcedure.class);
        /* FIXME this check is valid but failing
        assertThat(result.getProcedures(), hasSize(1));
        final Procedure procedure = result.getProcedures().get(0);
        assertThat(procedure.getActs(), hasSize(procedureActivityActs.size()));
        assertThat(procedure.getActivities(), hasSize(procedureActivityProcedures.size()));
        assertThat(procedure.getObservations(), hasSize(procedureActivityObservations.size()));
        */

        final String XPATH_RESULTS_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.22.2.3' or @root='2.16.840.1.113883.10.20.22.2.3.1'] and not(@nullFlavor)]";
        final List<ResultOrganizer> resultOrganizers =
                xpath.selectNodes(XPATH_RESULTS_SECTION + "//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.1'] and not(@nullFlavor)]", ResultOrganizer.class);
        var resultObservations = xpath.selectNodes(XPATH_RESULTS_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.2'] and not(@nullFlavor)]", ResultObservation.class);//TODO ggavrysh change xpath and uncomment assert
//        assertCollectionSizeMatches("Results", result.getResults(), resultOrganizers);
        if (resultOrganizers.size() == 1) {
//            assertThat(result.getResults().get(0).getObservations(), hasSize(resultObservations.size()));
        }

        final String XPATH_SOCIAL_HISTORY_SECTION = "//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.22.2.17'] and not(@nullFlavor)]";
        final SocialHistorySection socialHistorySection = xpath.selectSingleNode(XPATH_SOCIAL_HISTORY_SECTION, SocialHistorySection.class);
        final List<SocialHistoryObservation> socialHistoryObservations =
                xpath.selectNodes(XPATH_SOCIAL_HISTORY_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.38'] and not(@nullFlavor)]", SocialHistoryObservation.class);
        final List<SmokingStatusObservation> smokingStatusObservations =
                xpath.selectNodes(XPATH_SOCIAL_HISTORY_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.78'] and not(@nullFlavor)]", SmokingStatusObservation.class);
        var pregnancyObservations =
                xpath.selectNodes(XPATH_SOCIAL_HISTORY_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.15.3.8'] and not(@nullFlavor)]", PregnancyObservation.class);
        final List<TobaccoUse> tobaccoUses =
                xpath.selectNodes(XPATH_SOCIAL_HISTORY_SECTION + "//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.85'] and not(@nullFlavor)]", TobaccoUse.class);
        if (socialHistorySection != null) {
            assertThat("Social History", result.getSocialHistories(), hasSize(1));
            final SocialHistory socialHistory = result.getSocialHistories().get(0);
            assertThat("Social History Observations", socialHistory.getSocialHistoryObservations(), hasSize(socialHistoryObservations.size()));
            assertThat("Social History > Smoking Status Observations", socialHistory.getSmokingStatusObservations(), hasSize(smokingStatusObservations.size()));
            assertThat("Social History > Pregnancy Observations", socialHistory.getPregnancyObservations(), hasSize(pregnancyObservations.size()));
            assertThat("Social History > Tobacco Use", socialHistory.getTobaccoUses(), hasSize(tobaccoUses.size()));
        } else {
            assertThat("Social History", result.getResults(), hasSize(2));
        }

        final List<VitalSignsOrganizer> vitalSignsOrganizers =
                xpath.selectNodes("//cda:organizer[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.26'] and not(@nullFlavor)]", VitalSignsOrganizer.class);
        final List<VitalSignObservation> vitalSignObservations =
                xpath.selectNodes("//cda:observation[cda:templateId[@root='2.16.840.1.113883.10.20.22.4.27'] and not(@nullFlavor)]", VitalSignObservation.class);
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
        final AD adBirthplace = xpath.selectSingleNode("//cda:recordTarget/cda:patientRole[1]/cda:patient/cda:birthplace/cda:place/cda:addr", AD.class);

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
        if (CcdParseUtils.hasContent(adBirthplace)) {
            assertNotNull(recordTarget.getBirthplaceAddress());
            // TODO more assertions
        } else {
            assertNull(recordTarget.getBirthplaceAddress());
        }
    }
}
