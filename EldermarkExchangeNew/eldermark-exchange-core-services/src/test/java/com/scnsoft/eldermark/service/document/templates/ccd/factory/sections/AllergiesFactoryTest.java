package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.ValueSet;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.document.ccd.ReactionObservation;
import com.scnsoft.eldermark.entity.document.ccd.SeverityObservation;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.service.document.cda.generator.CcdConstructionUtils;
import com.scnsoft.eldermark.service.document.cda.generator.CcdHL7Generator;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.SectionFactory;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.Section;
import org.eclipse.mdht.uml.cda.util.CDAUtil;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.ccd.AlertObservation;
import org.openhealthtools.mdht.uml.cda.ccd.AlertStatusObservation;
import org.openhealthtools.mdht.uml.cda.ccd.AlertsSection;
import org.openhealthtools.mdht.uml.cda.ccd.ProblemAct;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class AllergiesFactoryTest {

    @InjectMocks
    private AllergiesFactory allergiesFactory;

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
        adverseEvent.setDisplayName("adverseEventTypeCodeDisplay");
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

        var severityObservationMock = new SeverityObservation();
        severityObservationMock.setId((long) 1);
        CcdCode severityCode = new CcdCode();
        severityCode.setCode("severityCode");
        var vs = new ValueSet();
        vs.setOid(ValueSetEnum.PROBLEM_SEVERITY.getOid());
        severityCode.setValueSets(Collections.singletonList(vs));
        severityObservationMock.setSeverityCode(severityCode);
        severityObservationMock.setSeverityText("severityText");

        allergyObservationMock.setSeverityObservation(severityObservationMock);

        ReactionObservation reactionObservationMock = new ReactionObservation();
        reactionObservationMock.setId((long) 1);
        reactionObservationMock.setTimeLow(new Date());
        reactionObservationMock.setTimeHigh(new Date());
        CcdCode reactionCode = new CcdCode();
        reactionCode.setCode("reactionCode");
        reactionObservationMock.setReactionCode(reactionCode);
        reactionObservationMock.setReactionText("reactionText");

        List<SeverityObservation> severityObservations = new ArrayList<>();
        severityObservations.add(severityObservationMock);
        reactionObservationMock.setSeverityObservations(severityObservations);

        var reactionObservations = new HashSet<ReactionObservation>();
        reactionObservations.add(reactionObservationMock);
        allergyObservationMock.setReactionObservations(reactionObservations);

        Set<AllergyObservation> allergyObservations = new HashSet<>();
        allergyObservations.add(allergyObservationMock);
        allergyMock.setAllergyObservations(allergyObservations);

        Set<Allergy> allergies = new HashSet<>();
        allergies.add(allergyMock);

        final ClinicalDocumentVO clinicalDocumentVO = new ClinicalDocumentVO();
        clinicalDocumentVO.setAllergies(new ArrayList<>(allergies));

        // 2. test
        final CcdHL7Generator ccdGenerator = initCcdGeneratorMock(allergiesFactory);
        var ccd = ccdGenerator.generate(clinicalDocumentVO);

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
        var observations = getSubList(problemAct.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.AlertObservation.class);
        assertEquals(allergyObservations.size(), observations.size());
        assertEquals("SUBJ", problemAct.getEntryRelationships().get(0).getTypeCode().getName());

        AlertObservation alertObservation = (AlertObservation) observations.get(0);
        assertEquals("OBS", alertObservation.getClassCode().toString());
        assertEquals("EVN", alertObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.7", alertObservation.getTemplateIds().get(0).getRoot());
        assertEquals(allergyObservationMock.getId().toString(), alertObservation.getIds().get(0).getExtension());
        assertEquals("ASSERTION", alertObservation.getCode().getCode());
        assertEquals("completed", alertObservation.getStatusCode().getCode());
        assertEquals(adverseEvent.getDisplayName(), ((CD) alertObservation.getValues().get(0)).getDisplayName());
        assertEquals(allergyObservationMock.getAdverseEventTypeText(), ((CD) alertObservation.getValues().get(0)).getOriginalText().getText());
        assertEquals(allergyObservationMock.getAdverseEventTypeCode().getCode(), ((CD) alertObservation.getValues().get(0)).getCode());
        assertEquals(allergyObservationMock.getAdverseEventTypeCode().getCodeSystem(), ((CD) alertObservation.getValues().get(0)).getCodeSystem());
        assertEquals(CcdUtils.formatSimpleDate(allergyObservationMock.getTimeLow()), alertObservation.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(allergyObservationMock.getTimeHigh()), alertObservation.getEffectiveTime().getHigh().getValue());
        assertEquals(allergyObservationMock.getProductCode().getCode(), alertObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity().getCode().getCode());
        assertEquals(allergyObservationMock.getProductCode().getCodeSystem(), alertObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity().getCode().getCodeSystem());
        assertEquals(allergyObservationMock.getProductText(), alertObservation.getParticipants().get(0).getParticipantRole().getPlayingEntity().getNames().get(0).getText());

        // allergy status observation
        var _statusObservations = getSubList(alertObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.AlertStatusObservation.class);
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
        var _severityObservations = getSubList(alertObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation.class);
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
        assertEquals(severityObservationMock.getSeverityCode().getCode(), ((CD) _severityObservation.getValues().get(0)).getCode());
        assertEquals(severityObservationMock.getSeverityText(), _severityObservation.getText().getText());

        // reaction observation
        var _reactionObservations = getSubList(alertObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation.class);
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
        assertEquals(reactionObservationMock.getReactionCode().getCode(), ((CD) _reactionObservation.getValues().get(0)).getCode());
        String refId = org.openhealthtools.mdht.uml.cda.ccd.ReactionObservation.class.getSimpleName() + reactionObservationMock.getId();
//        assertTrue(alertsSection.getText().getText("text").contains(String.format("<content ID=\"%s\">%s</content>", refId, reactionObservationMock.getReactionText())));
        assertEquals("#" + refId, _reactionObservation.getText().getReference().getValue());

        var _reactionSeverityObservations = getSubList(_reactionObservation.getObservations(), org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation.class);
        assertEquals("SUBJ", _reactionObservation.getEntryRelationships().get(0).getTypeCode().getName());
        assertEquals(Boolean.TRUE, _reactionObservation.getEntryRelationships().get(0).getInversionInd());

        assertEquals(reactionObservationMock.getSeverityObservations().size(), _reactionSeverityObservations.size());
        org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation _reactionSeverityObservation = (org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation) _reactionSeverityObservations.get(0);
        assertEquals(severityObservationMock.getSeverityCode().getCode(), ((CD) _reactionSeverityObservation.getValues().get(0)).getCode());
        assertEquals("OBS", _reactionSeverityObservation.getClassCode().toString());
        assertEquals("EVN", _reactionSeverityObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.8", _reactionSeverityObservation.getTemplateIds().get(0).getRoot());
        assertEquals("SEV", _reactionSeverityObservation.getCode().getCode());
        assertEquals("2.16.840.1.113883.5.4", _reactionSeverityObservation.getCode().getCodeSystem());
        assertEquals("completed", _reactionSeverityObservation.getStatusCode().getCode().toString());
        refId = org.openhealthtools.mdht.uml.cda.ccd.SeverityObservation.class.getSimpleName() + severityObservationMock.getId();
//        assertTrue(alertsSection.getText().getText().contains(String.format("<content ID=\"%s\">%s</content>", refId, severityObservationMock.getSeverityText())));
        assertEquals("#" + refId, _reactionSeverityObservation.getText().getReference().getValue());
    }



    private CcdHL7Generator initCcdGeneratorMock(SectionFactory<? extends Section, ? extends BasicEntity> sectionFactory) {
        final CcdHL7Generator generator = new CcdHL7Generator();

        final CcdConstructionUtils ccdConstructionUtils = Mockito.mock(CcdConstructionUtils.class);
        final AdvanceDirectiveFactory advanceDirectiveFactory = Mockito.mock(AdvanceDirectiveFactory.class);
        final AllergiesFactory allergiesFactory = (AllergiesFactory) sectionFactory;
        final EncountersFactory encountersFactory = Mockito.mock(EncountersFactory.class);
        final FamilyHistoryFactory familyHistoryFactory = Mockito.mock(FamilyHistoryFactory.class);
        final FunctionalStatusFactory functionalStatusFactory = Mockito.mock(FunctionalStatusFactory.class);
        final ImmunizationsFactory immunizationsFactory = Mockito.mock(ImmunizationsFactory.class);
        final MedicalEquipmentFactory medicalEquipmentFactory = Mockito.mock(MedicalEquipmentFactory.class);
        final MedicationsFactory medicationsFactory = Mockito.mock(MedicationsFactory.class);
        final PayerFactory payerFactory = Mockito.mock(PayerFactory.class);
        final PlanOfCareFactory planOfCareFactory = Mockito.mock(PlanOfCareFactory.class);
        final ProblemsFactory problemsFactory = Mockito.mock(ProblemsFactory.class);
        final ProceduresFactory proceduresFactory = Mockito.mock(ProceduresFactory.class);
        final ResultsFactory resultsFactory = Mockito.mock(ResultsFactory.class);
        final SocialHistoryFactory socialHistoryFactory = Mockito.mock(SocialHistoryFactory.class);
        final VitalSignFactory vitalSignFactory = Mockito.mock(VitalSignFactory.class);

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

        return generator;
    }

    private <T> List<T> getSubList(List<Observation> list, Class<T> clazz) {
        List<T> subList = new ArrayList<>();
        for (Observation entry : list) {
            if (clazz.isInstance(entry)) {
                subList.add(clazz.cast(entry));
            }
        }
        return subList;
    }
}
