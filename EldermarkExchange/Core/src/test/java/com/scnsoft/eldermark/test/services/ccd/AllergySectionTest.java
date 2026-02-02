package com.scnsoft.eldermark.test.services.ccd;

import com.scnsoft.eldermark.dao.AllergyDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.ccd.CcdHL7ConstructingService;
import com.scnsoft.eldermark.services.ccd.CcdHL7ConstructingServiceImpl;
import com.scnsoft.eldermark.services.ccd.templates.sections.*;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdConstructionUtils;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import com.scnsoft.eldermark.services.cda.templates.header.*;
import org.easymock.EasyMock;
import org.easymock.EasyMockRule;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.eclipse.mdht.uml.cda.Section;
import org.junit.Rule;
import org.junit.Test;
import org.eclipse.mdht.uml.cda.Observation;
import org.openhealthtools.mdht.uml.cda.ccd.*;
import org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation;
import org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AllergySectionTest {

    @Rule
    public EasyMockRule rule = new EasyMockRule(this);

    @Mock
    private AllergyDao daoMock;

/*    @Mock
    private CcdTransform ccdTransformMock;

    @Mock
    private SectionEntryParseFactory sectionEntryParseFactory;*/

    @TestSubject
    private AllergiesFactory allergiesFactory = new AllergiesFactory();

    private static final long RESIDENT_ID = 123;

    @Test
    public void testBuildingTemplate() throws Exception {
        // 1. set up mocks
        Allergy allergyMock = new Allergy();
        allergyMock.setId((long) 1);
        allergyMock.setStatusCode("allergyStatusCode");
        allergyMock.setTimeLow(new Date());
        allergyMock.setTimeHigh(new Date());

        AllergyObservation allergyObservationMock = new AllergyObservation();
        allergyObservationMock.setId((long) 1);
        CcdCode adverseEvent = new CcdCode();
        adverseEvent.setCode("adverseEventTypeCode");
        allergyObservationMock.setAdverseEventTypeCode(adverseEvent);
        allergyObservationMock.setAdverseEventTypeText("adverseEventTypeText");
        allergyObservationMock.setAllergy(allergyMock);
        allergyObservationMock.setTimeLow(new Date());
        allergyObservationMock.setTimeHigh(new Date());
        CcdCode observationStatusCode = new CcdCode();
        observationStatusCode.setCode("observationStatusCode");
        allergyObservationMock.setObservationStatusCode(observationStatusCode);
        CcdCode productCode = new CcdCode();
        productCode.setCode("productCode");
        productCode.setCodeSystem("productCodeSystem");
        allergyObservationMock.setProductCode(productCode);
        allergyObservationMock.setProductText("productName");

        com.scnsoft.eldermark.entity.SeverityObservation severityObservationMock = new com.scnsoft.eldermark.entity.SeverityObservation();
        severityObservationMock.setId((long) 1);
        CcdCode severityCode = new CcdCode();
        severityCode.setCode("severityCode");
        severityObservationMock.setSeverityCode(severityCode);
        severityObservationMock.setSeverityText("severityText");

        allergyObservationMock.setSeverityObservation(severityObservationMock);

        com.scnsoft.eldermark.entity.ReactionObservation reactionObservationMock = new com.scnsoft.eldermark.entity.ReactionObservation();
        reactionObservationMock.setId((long) 1);
        reactionObservationMock.setTimeLow(new Date());
        reactionObservationMock.setTimeHigh(new Date());
        CcdCode reactionCode = new CcdCode();
        reactionCode.setCode("reactionCode");
        reactionObservationMock.setReactionCode(reactionCode);
        reactionObservationMock.setReactionText("reactionText");

        List<com.scnsoft.eldermark.entity.SeverityObservation> severityObservations = new ArrayList<>();
        severityObservations.add(severityObservationMock);
        reactionObservationMock.setSeverityObservations(severityObservations);

        Set<com.scnsoft.eldermark.entity.ReactionObservation> reactionObservations = new HashSet<>();
        reactionObservations.add(reactionObservationMock);
        allergyObservationMock.setReactionObservations(reactionObservations);

        Set<com.scnsoft.eldermark.entity.AllergyObservation> allergyObservations = new HashSet<>();
        allergyObservations.add(allergyObservationMock);
        allergyMock.setAllergyObservations(allergyObservations);

        Set<Allergy> allergies = new HashSet<>();
        allergies.add(allergyMock);

        /*
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(allergies);
        EasyMock.replay(daoMock);*/

        final ClinicalDocumentVO clinicalDocumentVO = new ClinicalDocumentVO();
        clinicalDocumentVO.setAllergies(new ArrayList<>(allergies));

        // 2. test
        final CcdHL7ConstructingService ccdGenerator = initCcdGeneratorMock(allergiesFactory);
        final ContinuityOfCareDocument ccd = ccdGenerator.construct(clinicalDocumentVO);

        CDAUtil.CDAXPath xpath = new CDAUtil.CDAXPath(ccd);

        // FIXME use HL7 CCD templateId for Alerts section
        AlertsSection alertsSection = xpath.selectSingleNode("//cda:section[cda:templateId[@root='2.16.840.1.113883.10.20.1.2']]", AlertsSection.class);

        // 3. verify
        assertNotNull(alertsSection);
        //EasyMock.verify(daoMock);
        assertEquals("2.16.840.1.113883.10.20.1.2", alertsSection.getTemplateIds().get(0).getRoot());
        assertEquals("48765-2", alertsSection.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", alertsSection.getCode().getCodeSystem());

        List<ProblemAct> problemActs = alertsSection.getProblemActs();
        assertEquals(allergies.size(), problemActs.size());

        // allergy problem act
        ProblemAct problemAct = problemActs.get(0);
        assertEquals("ACT", problemAct.getClassCode().toString());
        assertEquals("EVN", problemAct.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.30", problemAct.getTemplateIds().get(0).getRoot());
        assertEquals("48765-2", problemAct.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", problemAct.getCode().getCodeSystem());
        assertEquals(allergyMock.getId().toString(), problemAct.getIds().get(0).getExtension());
        assertEquals(allergyMock.getStatusCode(), problemAct.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(allergyMock.getTimeLow()), problemAct.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(allergyMock.getTimeHigh()), problemAct.getEffectiveTime().getHigh().getValue());

        // allergy observation
        List<Observation> observations = getSubList(problemAct.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.AlertObservation.class);
        assertEquals(allergyObservations.size(), observations.size());
        assertEquals("SUBJ", problemAct.getEntryRelationships().get(0).getTypeCode().getName());

        AlertObservation alertObservation = (AlertObservation) observations.get(0);
        assertEquals("OBS", alertObservation.getClassCode().toString());
        assertEquals("EVN", alertObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.7", alertObservation.getTemplateIds().get(0).getRoot());
        assertEquals(allergyObservationMock.getId().toString(), alertObservation.getIds().get(0).getExtension());
        assertEquals("ASSERTION", alertObservation.getCode().getCode());
        assertEquals("completed", alertObservation.getStatusCode().getCode());
        assertEquals(allergyObservationMock.getAdverseEventTypeText(), ((CE) alertObservation.getValues().get(0)).getDisplayName());
        assertEquals(allergyObservationMock.getAdverseEventTypeCode().getCode(), ((CE) alertObservation.getValues().get(0)).getCode());
        assertEquals(allergyObservationMock.getAdverseEventTypeCode().getCodeSystem(), ((CE) alertObservation.getValues().get(0)).getCodeSystem());
        assertEquals(CcdUtils.formatSimpleDate(allergyObservationMock.getTimeLow()), alertObservation.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(allergyObservationMock.getTimeHigh()), alertObservation.getEffectiveTime().getHigh().getValue());
        assertEquals(allergyObservationMock.getProductCode().getCode(), alertObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity().getCode().getCode());
        assertEquals(allergyObservationMock.getProductCode().getCodeSystem(), alertObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity().getCode().getCodeSystem());
        assertEquals(allergyObservationMock.getProductText(), alertObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity().getNames().get(0).getText());

        // allergy status observation
        List<Observation> _statusObservations = getSubList(alertObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.AlertStatusObservation.class);
        assertEquals(1, _statusObservations.size());
        assertEquals("SUBJ", alertObservation.getEntryRelationships().get(0).getTypeCode().getName());
        assertEquals(Boolean.TRUE, alertObservation.getEntryRelationships().get(0).getInversionInd());

        AlertStatusObservation alertStatusObservation = (AlertStatusObservation) _statusObservations.get(0);
        assertEquals("OBS", alertStatusObservation.getClassCode().toString());
        assertEquals("EVN", alertStatusObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.28", alertStatusObservation.getTemplateIds().get(0).getRoot());
        assertEquals("33999-4", alertStatusObservation.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", alertStatusObservation.getCode().getCodeSystem());
        assertEquals("completed", alertStatusObservation.getStatusCode().getCode().toString());
        assertEquals(allergyObservationMock.getObservationStatusCode().getCode(), ((CE) alertStatusObservation.getValues().get(0)).getCode());

        // severity observation
        List<Observation> _severityObservations = getSubList(alertObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation.class);
        assertEquals(1, _severityObservations.size());
        assertEquals("SUBJ", alertObservation.getEntryRelationships().get(2).getTypeCode().getName());
        assertEquals(Boolean.TRUE, alertObservation.getEntryRelationships().get(2).getInversionInd());

        org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation _severityObservation = (org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation) _severityObservations.get(0);
        assertEquals("OBS", _severityObservation.getClassCode().toString());
        assertEquals("EVN", _severityObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.8", _severityObservation.getTemplateIds().get(0).getRoot());
        assertEquals("SEV", _severityObservation.getCode().getCode());
        assertEquals("2.16.840.1.113883.5.4", _severityObservation.getCode().getCodeSystem());
        assertEquals("completed", _severityObservation.getStatusCode().getCode().toString());
        assertEquals(severityObservationMock.getSeverityCode().getCode(), ((CE) _severityObservation.getValues().get(0)).getCode());
        assertEquals(severityObservationMock.getSeverityText(), _severityObservation.getText().getText());

        // reaction observation
        List<Observation> _reactionObservations = getSubList(alertObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation.class);
        assertEquals(allergyObservationMock.getReactionObservations().size(), _reactionObservations.size());
        assertEquals("MFST", alertObservation.getEntryRelationships().get(1).getTypeCode().getName());
        assertEquals(Boolean.TRUE, alertObservation.getEntryRelationships().get(1).getInversionInd());

        org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation _reactionObservation = (org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation) _reactionObservations.get(0);
        assertEquals("OBS", _reactionObservation.getClassCode().toString());
        assertEquals("EVN", _reactionObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.9", _reactionObservation.getTemplateIds().get(0).getRoot());
        assertEquals(reactionObservationMock.getId().toString(), _reactionObservation.getIds().get(0).getExtension());
        assertEquals(NullFlavor.NI, _reactionObservation.getCode().getNullFlavor());
        assertEquals("completed", _reactionObservation.getStatusCode().getCode().toString());
        assertEquals(CcdUtils.formatSimpleDate(reactionObservationMock.getTimeLow()), _reactionObservation.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(reactionObservationMock.getTimeHigh()), _reactionObservation.getEffectiveTime().getHigh().getValue());
        assertEquals(reactionObservationMock.getReactionCode().getCode(), ((CE) _reactionObservation.getValues().get(0)).getCode());
        String refId = ReactionObservation.class.getSimpleName() + reactionObservationMock.getId();
//        assertTrue(alertsSection.getText().getText("text").contains(String.format("<content ID=\"%s\">%s</content>", refId, reactionObservationMock.getReactionText())));
        assertEquals("#" + refId, _reactionObservation.getText().getReference().getValue());

        List<Observation> _reactionSeverityObservations = getSubList(_reactionObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation.class);
        assertEquals("SUBJ", _reactionObservation.getEntryRelationships().get(0).getTypeCode().getName());
        assertEquals(Boolean.TRUE, _reactionObservation.getEntryRelationships().get(0).getInversionInd());

        assertEquals(reactionObservationMock.getSeverityObservations().size(), _reactionSeverityObservations.size());
        org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation _reactionSeverityObservation = (org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation) _reactionSeverityObservations.get(0);
        assertEquals(severityObservationMock.getSeverityCode().getCode(), ((CE) _reactionSeverityObservation.getValues().get(0)).getCode());
        assertEquals("OBS", _reactionSeverityObservation.getClassCode().toString());
        assertEquals("EVN", _reactionSeverityObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.8", _reactionSeverityObservation.getTemplateIds().get(0).getRoot());
        assertEquals("SEV", _reactionSeverityObservation.getCode().getCode());
        assertEquals("2.16.840.1.113883.5.4", _reactionSeverityObservation.getCode().getCodeSystem());
        assertEquals("completed", _reactionSeverityObservation.getStatusCode().getCode().toString());
        refId = SeverityObservation.class.getSimpleName() + severityObservationMock.getId();
//        assertTrue(alertsSection.getText().getText().contains(String.format("<content ID=\"%s\">%s</content>", refId, severityObservationMock.getSeverityText())));
        assertEquals("#" + refId, _reactionSeverityObservation.getText().getReference().getValue());
    }

    private CcdHL7ConstructingService initCcdGeneratorMock(SectionFactory<? extends Section, ? extends BasicEntity> sectionFactory) {
        final CcdHL7ConstructingServiceImpl generator = new CcdHL7ConstructingServiceImpl();

        RecordTargetFactory recordTargetFactoryMock = EasyMock.createNiceMock(RecordTargetFactory.class);
        AuthorFactory authorFactory = EasyMock.createNiceMock(AuthorFactory.class);
        DataEntererFactory dataEntererFactory = EasyMock.createNiceMock(DataEntererFactory.class);
        InformantFactory informantFactory = EasyMock.createNiceMock(InformantFactory.class);
        CustodianFactory custodianFactory = EasyMock.createNiceMock(CustodianFactory.class);
        InformationRecipientFactory informationRecipientFactory = EasyMock.createNiceMock(InformationRecipientFactory.class);
        LegalAuthenticatorFactory legalAuthenticatorFactory = EasyMock.createNiceMock(LegalAuthenticatorFactory.class);
        AuthenticatorFactory authenticatorFactory = EasyMock.createNiceMock(AuthenticatorFactory.class);
        ParticipantFactory participantFactory = EasyMock.createNiceMock(ParticipantFactory.class);
        InFulfillmentOfFactory inFulfillmentOfFactory = EasyMock.createNiceMock(InFulfillmentOfFactory.class);
        DocumentationOfFactory documentationOfFactory = EasyMock.createNiceMock(DocumentationOfFactory.class);
        AuthorizationFactory authorizationFactory = EasyMock.createNiceMock(AuthorizationFactory.class);
        ComponentFactory componentFactory = EasyMock.createNiceMock(ComponentFactory.class);

        final CcdConstructionUtils ccdConstructionUtils = EasyMock.createNiceMock(CcdConstructionUtils.class);
        final AdvanceDirectiveFactory advanceDirectiveFactory = EasyMock.createNiceMock(AdvanceDirectiveFactory.class);
        final AllergiesFactory allergiesFactory = (AllergiesFactory) sectionFactory;
        final EncountersFactory encountersFactory = EasyMock.createNiceMock(EncountersFactory.class);
        final FamilyHistoryFactory familyHistoryFactory = EasyMock.createNiceMock(FamilyHistoryFactory.class);
        final FunctionalStatusFactory functionalStatusFactory = EasyMock.createNiceMock(FunctionalStatusFactory.class);
        final ImmunizationsFactory immunizationsFactory = EasyMock.createNiceMock(ImmunizationsFactory.class);
        final MedicalEquipmentFactory medicalEquipmentFactory = EasyMock.createNiceMock(MedicalEquipmentFactory.class);
        final MedicationsFactory medicationsFactory = EasyMock.createNiceMock(MedicationsFactory.class);
        final PayerFactory payerFactory = EasyMock.createNiceMock(PayerFactory.class);
        final PlanOfCareFactory planOfCareFactory = EasyMock.createNiceMock(PlanOfCareFactory.class);
        final ProblemsFactory problemsFactory = EasyMock.createNiceMock(ProblemsFactory.class);
        final ProceduresFactory proceduresFactory = EasyMock.createNiceMock(ProceduresFactory.class);
        final ResultsFactory resultsFactory = EasyMock.createNiceMock(ResultsFactory.class);
        final SocialHistoryFactory socialHistoryFactory = EasyMock.createNiceMock(SocialHistoryFactory.class);
        final VitalSignFactory vitalSignFactory = EasyMock.createNiceMock(VitalSignFactory.class);

        generator.setCcdConstructionUtils(ccdConstructionUtils);
        generator.setAdvanceDirectiveFactory(advanceDirectiveFactory);
        generator.setAllergiesFactory(allergiesFactory);
        generator.setEncountersFactory(encountersFactory);
        generator.setFamilyHistoryFactory(familyHistoryFactory);
        generator.setFunctionalStatusFactory(functionalStatusFactory);
        generator.setImmunizationsFactory(immunizationsFactory);
        generator.setMedicalEquipmentFactory(medicalEquipmentFactory);
        generator.setMedicationsFactory(medicationsFactory);
        generator.setPayerFactory(payerFactory);
        generator.setPlanOfCareFactory(planOfCareFactory);
        generator.setProblemsFactory(problemsFactory);
        generator.setProceduresFactory(proceduresFactory);
        generator.setResultsFactory(resultsFactory);
        generator.setSocialHistoryFactory(socialHistoryFactory);
        generator.setVitalSignFactory(vitalSignFactory);

        EasyMock.replay(recordTargetFactoryMock);
        EasyMock.replay(authorFactory);
        EasyMock.replay(dataEntererFactory);
        EasyMock.replay(informantFactory);
        EasyMock.replay(custodianFactory);
        EasyMock.replay(informationRecipientFactory);
        EasyMock.replay(legalAuthenticatorFactory);
        EasyMock.replay(authenticatorFactory);
        EasyMock.replay(participantFactory);
        EasyMock.replay(inFulfillmentOfFactory);
        EasyMock.replay(documentationOfFactory);
        EasyMock.replay(authorizationFactory);
        EasyMock.replay(componentFactory);

        EasyMock.replay(ccdConstructionUtils);
        EasyMock.replay(advanceDirectiveFactory);
        EasyMock.replay(encountersFactory);
        EasyMock.replay(familyHistoryFactory);
        EasyMock.replay(functionalStatusFactory);
        EasyMock.replay(immunizationsFactory);
        EasyMock.replay(medicalEquipmentFactory);
        EasyMock.replay(medicationsFactory);
        EasyMock.replay(payerFactory);
        EasyMock.replay(planOfCareFactory);
        EasyMock.replay(problemsFactory);
        EasyMock.replay(proceduresFactory);
        EasyMock.replay(resultsFactory);
        EasyMock.replay(socialHistoryFactory);
        EasyMock.replay(vitalSignFactory);

        return generator;
    }

    private List<Observation> getSubList(List<Observation> list, Class clazz) {
        List<Observation> subList = new ArrayList<>();
        for(Observation entry : list) {
            if(clazz.isInstance(entry)) {
                subList.add(entry);
            }
        }
        return subList;
    }
}
