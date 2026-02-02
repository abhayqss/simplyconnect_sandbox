package com.scnsoft.eldermark.test.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.ccd.templates.sections.FunctionalStatusFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.test.util.TestUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentSubstanceMood;
import org.junit.Assert;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ccd.FunctionalStatusSection;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FunctionalStatusSectionTest {

    private static final double DELTA = 1e-15;
    private static final long RESIDENT_ID = 49L;

    private Random random = new Random();

    @Test
    public void testBuildingTemplate() {

        FunctionalStatus functionalStatusMock = new FunctionalStatus();

        functionalStatusMock.setFunctionalStatusResultOrganizers(generateStatusResultOrganizers());
        functionalStatusMock.setCognitiveStatusResultOrganizers(generateStatusResultOrganizers());
        functionalStatusMock.setFunctionalStatusResultObservations(generateStatusResultObservations());
        functionalStatusMock.setCognitiveStatusResultObservations(generateStatusResultObservations());
        functionalStatusMock.setFunctionalStatusProblemObservations(generateStatusProblemObservations());
        functionalStatusMock.setCognitiveStatusProblemObservations(generateStatusProblemObservations());
        functionalStatusMock.setAssessmentScaleObservations(generateAssessmentScaleObservations());
        functionalStatusMock.setCaregiverCharacteristics(generateCaregiverCharacteristics());
        functionalStatusMock.setNonMedicinalSupplyActivities(generateNonMedicinalSupplyActivities());
        functionalStatusMock.setPressureUlcerObservations(generatePressureUlcerObservation());
        functionalStatusMock.setNumberOfPressureUlcersObservations(generateNumberOfPressureUlcersObservation());
        functionalStatusMock.setHighestPressureUlcerStages(generateHighestPressureUlcerStages());

        /*
        daoMock = EasyMock.createMock(FunctionalStatusDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(Collections.singleton(functionalStatusMock));
        EasyMock.replay(daoMock);*/

        FunctionalStatusFactory functionalStatusFactory = new FunctionalStatusFactory();
        // dependency on DAO is not required for parsing anymore
        //functionalStatusFactory.setFunctionalStatusDao(daoMock);

        // 2. test
        FunctionalStatusSection section = functionalStatusFactory.buildTemplateInstance(Collections.singleton(functionalStatusMock));

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.1.5", section.getTemplateIds().get(0).getRoot());

        assertEquals("47420-5", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());
        assertEquals("LOINC", section.getCode().getCodeSystemName());
        assertEquals("Functional Status Assessment".toLowerCase(), section.getCode().getDisplayName().toLowerCase());

        assertThat(section.getTitle().getText(), containsString("Functional Status"));

        List<Organizer> organizers = section.getOrganizers();
        assertEquals(functionalStatusMock.getFunctionalStatusResultOrganizers().size() +
                functionalStatusMock.getCognitiveStatusResultOrganizers().size(), organizers.size());

        int expectedOrganizersCount = 0;

        if (!CollectionUtils.isEmpty(functionalStatusMock.getFunctionalStatusResultOrganizers())) {
            expectedOrganizersCount += functionalStatusMock.getFunctionalStatusResultOrganizers().size();
            assertStatusResultOrganizers(functionalStatusMock.getFunctionalStatusResultOrganizers(), organizers, true);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getCognitiveStatusResultOrganizers())) {
            expectedOrganizersCount += functionalStatusMock.getCognitiveStatusResultOrganizers().size();
            assertStatusResultOrganizers(functionalStatusMock.getCognitiveStatusResultOrganizers(), organizers, false);
        }

        assertEquals(expectedOrganizersCount, organizers.size());

        int expectedObservationCount = 0;
        EList<Observation> observations = section.getObservations();
//        assertEquals(functionalStatusMock.getFunctionalStatusResultObservations().size() +
//                functionalStatusMock.getCognitiveStatusResultObservations().size(),observations.size());

        if (!CollectionUtils.isEmpty(functionalStatusMock.getFunctionalStatusResultObservations())) {
            expectedObservationCount += functionalStatusMock.getFunctionalStatusResultObservations().size();
            assertStatusResultObservations(functionalStatusMock.getFunctionalStatusResultObservations(), observations, true);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getCognitiveStatusResultObservations())) {
            expectedObservationCount += functionalStatusMock.getCognitiveStatusResultObservations().size();
            assertStatusResultObservations(functionalStatusMock.getCognitiveStatusResultObservations(), observations, false);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getFunctionalStatusProblemObservations())) {
            expectedObservationCount += functionalStatusMock.getFunctionalStatusProblemObservations().size();
            assertStatusProblemObservations(functionalStatusMock.getFunctionalStatusProblemObservations(), observations, true);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getCognitiveStatusProblemObservations())) {
            expectedObservationCount += functionalStatusMock.getCognitiveStatusProblemObservations().size();
            assertStatusProblemObservations(functionalStatusMock.getCognitiveStatusProblemObservations(), observations, false);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getNonMedicinalSupplyActivities())) {
            assertNonMedicinalSupplyActivities(functionalStatusMock.getNonMedicinalSupplyActivities(), section.getSupplies(), false);
        }


        if (!CollectionUtils.isEmpty(functionalStatusMock.getCaregiverCharacteristics())) {
            expectedObservationCount += functionalStatusMock.getCaregiverCharacteristics().size();
            assertCaregiverCharacteristics(functionalStatusMock.getCaregiverCharacteristics(), observations, false);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getAssessmentScaleObservations())) {
            expectedObservationCount += functionalStatusMock.getAssessmentScaleObservations().size();
            assertAssessmentScaleObservations(functionalStatusMock.getAssessmentScaleObservations(), observations, false);
        }


        if (!CollectionUtils.isEmpty(functionalStatusMock.getPressureUlcerObservations())) {
            expectedObservationCount += functionalStatusMock.getPressureUlcerObservations().size();
            assertPressureUlcerObservations(functionalStatusMock.getPressureUlcerObservations(), observations);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getNumberOfPressureUlcersObservations())) {
            expectedObservationCount += functionalStatusMock.getNumberOfPressureUlcersObservations().size();
            assertNumberOfPressureUlcersObservations(functionalStatusMock.getNumberOfPressureUlcersObservations(), observations);
        }

        if (!CollectionUtils.isEmpty(functionalStatusMock.getHighestPressureUlcerStages())) {
            expectedObservationCount += functionalStatusMock.getHighestPressureUlcerStages().size();
            assertHighestPressureUlcerStages(functionalStatusMock.getHighestPressureUlcerStages(), observations);
        }

        assertEquals(expectedObservationCount, observations.size());
    }

    private void assertHighestPressureUlcerStages(List<HighestPressureUlcerStage> highestPressureUlcerStages, EList<Observation> observations) {
        for (HighestPressureUlcerStage observationMock : highestPressureUlcerStages) {
            Observation observation = TestUtil.getObservationById(observationMock.getId(), observations);
            assertNotNull(observation);
            assertEquals("OBS", observation.getClassCode().toString());
            assertEquals("EVN", observation.getMoodCode().toString());

            assertEquals("2.16.840.1.113883.10.20.22.4.77", observation.getTemplateIds().get(0).getRoot());

            assertEquals("420905001", observation.getCode().getCode());
            assertEquals("Highest Pressure Ulcer Stage".toLowerCase(), observation.getCode().getDisplayName().toLowerCase());
            assertEquals("2.16.840.1.113883.6.96", observation.getCode().getCodeSystem());

            CD value = (CD) observation.getValues().get(0);
            if (observationMock.getValue() != null) {
                TestUtil.assertCodes(observationMock.getValue(), value);
//                assertEquals("2.16.840.1.113883.6.96", value.getCodeSystem());
            } else {
                assertEquals(NullFlavor.NI, value.getNullFlavor());
            }
        }
    }

    private void assertNumberOfPressureUlcersObservations(List<NumberOfPressureUlcersObservation> numberOfPressureUlcersObservations, EList<Observation> observations) {
        for (NumberOfPressureUlcersObservation observationMock : numberOfPressureUlcersObservations) {
            Observation observation = TestUtil.getObservationById(observationMock.getId(), observations);
            assertNotNull(observation);
            assertEquals("OBS", observation.getClassCode().toString());
            assertEquals("EVN", observation.getMoodCode().toString());

            assertEquals("2.16.840.1.113883.10.20.22.4.76", observation.getTemplateIds().get(0).getRoot());

            assertEquals("2264892003", observation.getCode().getCode());
            assertEquals("2.16.840.1.113883.6.96", observation.getCode().getCodeSystem());
            assertEquals("number of pressure ulcers", observation.getCode().getDisplayName().toLowerCase());

            assertEquals("completed", observation.getStatusCode().getCode());

            if (observationMock.getEffectiveTime() != null) {
                assertEquals(CcdUtils.formatSimpleDate(observationMock.getEffectiveTime()), observation.getEffectiveTime().getValue());
            }

            if (observationMock.getValue() != null) {
                INT value = (INT) observation.getValues().get(0);
                assertEquals(observationMock.getValue().intValue(), value.getValue().intValue());
            }

            if (observationMock.getAuthor() != null) {
                TestUtil.assertAuthors(observationMock.getAuthor(), observation.getAuthors().get(0));
            }

            Observation intObservation = observation.getObservations().get(0);
            assertEquals("SUBJ", ((EntryRelationship) intObservation.eContainer()).getTypeCode().toString());
            assertEquals("OBS", intObservation.getClassCode().toString());
            assertEquals("EVN", intObservation.getMoodCode().toString());

            CD value = (CD) intObservation.getValues().get(0);
            if (observationMock.getObservationValue() != null) {
                TestUtil.assertCodes(observationMock.getObservationValue(), value);
//                assertEquals("2.16.840.1.113883.6.96", value.getCodeSystem());
            } else {
                assertEquals(NullFlavor.NI, value.getNullFlavor());
            }

        }
    }

    private void assertPressureUlcerObservations(List<PressureUlcerObservation> pressureUlcerObservations, EList<Observation> observations) {
        for (PressureUlcerObservation observationMock : pressureUlcerObservations) {
            Observation observation = TestUtil.getObservationById(observationMock.getId(), observations);
            assertNotNull(observation);
            assertEquals("OBS", observation.getClassCode().toString());
            assertEquals("EVN", observation.getMoodCode().toString());

            if (observationMock.isNegationInd() != null && observationMock.isNegationInd()) {
                assertTrue(observation.getNegationInd());
            }

            assertEquals("2.16.840.1.113883.10.20.22.4.70", observation.getTemplateIds().get(0).getRoot());

            assertEquals("ASSERTION", observation.getCode().getCode());
            assertEquals("2.16.840.1.113883.5.4", observation.getCode().getCodeSystem());

            if (observationMock.getText() != null) {
                if (!observationMock.getText().equals(observation.getText().getText())) {
                    assertEquals("#" + PressureUlcerObservation.class.getSimpleName() + observationMock.getId(), observation.getText().getReference().getValue());
                }
            }

            assertEquals("completed", observation.getStatusCode().getCode());

            if (observationMock.getEffectiveTime() != null) {
                assertEquals(CcdUtils.formatSimpleDate(observationMock.getEffectiveTime()), observation.getEffectiveTime().getValue());
            }

            CD value = (CD) observation.getValues().get(0);
            if (observationMock.getValue() != null) {
                if ("2.16.840.1.113883.6.96".equals(observationMock.getValue().getCodeSystem())) {
                    TestUtil.assertCodes(observationMock.getValue(), value);
                } else {
                    // (CONF:14396)
                    // If the code is something other than SNOMED, @nullFlavor SHOULD be “OTH” and the other code SHOULD be placed in the translation element
                    CD translation = ((CD) observation.getValues().get(0)).getTranslations().get(0);
                    assertEquals(NullFlavor.OTH, value.getNullFlavor());
                    TestUtil.assertCodes(observationMock.getValue(), translation);
                }
            } else {
                assertEquals(NullFlavor.UNK, value.getNullFlavor());
            }

            if (!CollectionUtils.isEmpty(observationMock.getTargetSiteCodes())) {
                List<CD> targetSiteCodes = observation.getTargetSiteCodes();
                assertEquals(observationMock.getTargetSiteCodes().size(), targetSiteCodes.size());

                for (int i = 0; i < observationMock.getTargetSiteCodes().size(); i++) {
                    TargetSiteCode targetSiteCodeMock = observationMock.getTargetSiteCodes().get(i);
                    CD targetSiteCodeCcd = targetSiteCodes.get(i);

                    if (targetSiteCodeMock.getCode() != null) {
                        TestUtil.assertCodes(targetSiteCodeMock.getCode(), targetSiteCodeCcd);
                       // assertEquals("2.16.840.1.113883.6.96", targetSiteCodeCcd.getCodeSystem());
                    }
                    if (targetSiteCodeMock.getValue() != null) {
                        CR qualifier = targetSiteCodeCcd.getQualifiers().get(0);
                        CV name = qualifier.getName();
                        assertEquals("272741003", name.getCode());
                        assertEquals("2.16.840.1.113883.6.96", name.getCodeSystem());
                        CD qualifierValue = qualifier.getValue();
                        TestUtil.assertCodes(targetSiteCodeMock.getValue(), qualifierValue);
//                        assertEquals("2.16.840.1.113883.6.96", qualifierValue.getCodeSystem());
                    }
                }
            } else {
                assertEquals(NullFlavor.NI, observation.getTargetSiteCodes().get(0).getNullFlavor());
            }

            List<Observation> intObservations = observation.getObservations();
            assertPUObservation(observationMock.getLengthOfWoundValue(), "401238003", "Length of Wound", intObservations);
            assertPUObservation(observationMock.getWidthOfWoundValue(), "401239006", "Width of Wound", intObservations);
            assertPUObservation(observationMock.getDepthOfWoundValue(), "425094009", "Depth of Wound", intObservations);


        }
    }

    private void assertPUObservation(Double value, String code, String displayName, List<Observation> observations) {
        if (value != null) {
            Observation observation = findObservationByCode(code, observations);
            Assert.assertNotNull(observation);
            assertEquals("COMP", ((EntryRelationship) observation.eContainer()).getTypeCode().toString());
            assertEquals("OBS", observation.getClassCode().toString());
            assertEquals("EVN", observation.getMoodCode().toString());
            assertEquals(code, observation.getCode().getCode());
            assertEquals("2.16.840.1.113883.6.96", observation.getCode().getCodeSystem());
            assertEquals(displayName, observation.getCode().getDisplayName());
            PQ valueCcd = (PQ) observation.getValues().get(0);
            assertEquals(value, valueCcd.getValue().doubleValue(), DELTA);
        }
    }


    private void assertStatusProblemObservations(List<StatusProblemObservation> statusProblemObservations, EList<Observation> observations, boolean functional) {
        for (StatusProblemObservation observationMock : statusProblemObservations) {
            Observation observation = TestUtil.getObservationById(observationMock.getId(), observations);
            assertNotNull(observation);
            assertEquals("OBS", observation.getClassCode().toString());
            assertEquals("EVN", observation.getMoodCode().toString());

            if (observationMock.isNegationInd() != null && observationMock.isNegationInd()) {
                assertTrue(observation.getNegationInd());
            }

            assertEquals("2.16.840.1.113883.10.20.1.28", observation.getTemplateIds().get(0).getRoot());

            if (functional) {
                assertEquals("248536006", observation.getCode().getCode());
                assertEquals("2.16.840.1.113883.6.96", observation.getCode().getCodeSystem());
            } else {
                assertEquals("373930000", observation.getCode().getCode());
                assertEquals("2.16.840.1.113883.6.96", observation.getCode().getCodeSystem());
            }

            if (observationMock.getText() != null) {
                if (!observationMock.getText().equals(observation.getText().getText())) {
                    assertEquals("#" + StatusProblemObservation.class.getSimpleName() + observationMock.getId(), observation.getText().getReference().getValue());
                }
            }
            assertEquals("completed", observation.getStatusCode().getCode());

            IVL_TS effectiveTime = observation.getEffectiveTime();

            if (observationMock.getTimeLow() != null) {
                assertEquals(CcdUtils.formatSimpleDate(observationMock.getTimeLow()), effectiveTime.getLow().getValue());
            }
            if (observationMock.getTimeHigh() != null) {
                assertEquals(CcdUtils.formatSimpleDate(observationMock.getTimeHigh()), effectiveTime.getHigh().getValue());
            }

            if (observationMock.getTimeHigh() == null && observationMock.isResolved()) {
                assertEquals(NullFlavor.UNK, effectiveTime.getHigh().getNullFlavor());
            }

            CD value = (CD) observation.getValues().get(0);
            if (observationMock.getValue() != null) {
                if ("2.16.840.1.113883.6.96".equals(observationMock.getValue().getCodeSystem()) || !functional) {
                    TestUtil.assertCodes(observationMock.getValue(), value);
                } else {
                    // (CONF:10142)
                    // If the code is something other than SNOMED, @nullFlavor SHOULD be “OTH” and the other code SHOULD be placed in the translation element
                    CD translation = ((CD) observation.getValues().get(0)).getTranslations().get(0);
                    assertEquals(NullFlavor.OTH, value.getNullFlavor());
                    TestUtil.assertCodes(observationMock.getValue(), translation);
                }
            } else {
                assertEquals(NullFlavor.UNK, value.getNullFlavor());
            }

            if (observationMock.getMethodCode() != null) {
                TestUtil.assertCodes(observationMock.getMethodCode(), observation.getMethodCodes().get(0));
            }

            if (!CollectionUtils.isEmpty(observationMock.getNonMedicinalSupplyActivities())) {
                assertNonMedicinalSupplyActivities(observationMock.getNonMedicinalSupplyActivities(), observation.getSupplies(), true);
            }

            int expectedObservationSize = 0;

            EList<Observation> observationList = observation.getObservations();

            if (!CollectionUtils.isEmpty(observationMock.getCaregiverCharacteristics())) {
                expectedObservationSize += observationMock.getCaregiverCharacteristics().size();
                assertCaregiverCharacteristics(observationMock.getCaregiverCharacteristics(), observationList, true);
            }

            if (!CollectionUtils.isEmpty(observationMock.getAssessmentScaleObservations())) {
                expectedObservationSize += observationMock.getAssessmentScaleObservations().size();
                assertAssessmentScaleObservations(observationMock.getAssessmentScaleObservations(), observationList, true);
            }

            assertEquals(expectedObservationSize, observationList.size());

        }
    }

    private void assertStatusResultOrganizers(List<StatusResultOrganizer> mockOrganizers, List<Organizer> organizers, boolean functional) {
        for (StatusResultOrganizer organizerMock : mockOrganizers) {
            Organizer organizer = TestUtil.getOrganizerById(organizerMock.getId(), organizers);
            assertNotNull(organizer);
            assertEquals("CLUSTER", organizer.getClassCode().toString());
            assertEquals("EVN", organizer.getMoodCode().toString());

            assertEquals("2.16.840.1.113883.10.20.1.32", organizer.getTemplateIds().get(0).getRoot());

            if (organizerMock.getCode() != null) {
                TestUtil.assertCodes(organizerMock.getCode(), organizer.getCode());
            } else {
                assertEquals(NullFlavor.NI, organizer.getCode().getNullFlavor());
            }
            assertEquals("completed", organizer.getStatusCode().getCode());

            if (organizerMock.getStatusResultObservations() != null) {
                assertEquals(organizerMock.getStatusResultObservations().size(), organizer.getObservations().size());
                assertStatusResultObservations(organizerMock.getStatusResultObservations(), organizer.getObservations(), functional);
            }
        }
    }

    private void assertStatusResultObservations(List<StatusResultObservation> statusResultObservations, EList<Observation> observations, boolean functional) {

        for (StatusResultObservation observationMock : statusResultObservations) {
            Observation observation = TestUtil.getObservationById(observationMock.getId(), observations);
            assertNotNull(observation);
            assertEquals("OBS", observation.getClassCode().toString());
            assertEquals("EVN", observation.getMoodCode().toString());

            assertEquals("2.16.840.1.113883.10.20.1.31", observation.getTemplateIds().get(0).getRoot());

            if (!functional) {
                assertEquals("373930000", observation.getCode().getCode());
                assertEquals("2.16.840.1.113883.6.96", observation.getCode().getCodeSystem());
            } else {
                if (observationMock.getCode() != null) { //TODO
                    TestUtil.assertCodes(observationMock.getCode(), observation.getCode());
                } else {
                    assertEquals(NullFlavor.NI, observation.getCode().getNullFlavor());            //TODO
                }
            }

            if (observationMock.getText() != null) {
                if (!observationMock.getText().equals(observation.getText().getText())) {
                    assertEquals("#" + StatusResultObservation.class.getSimpleName() + observationMock.getId(), observation.getText().getReference().getValue());
                }
            }

            assertEquals("completed", observation.getStatusCode().getCode());

            if (observationMock.getEffectiveTime() != null) {
                assertEquals(CcdUtils.formatSimpleDate(observationMock.getEffectiveTime()), observation.getEffectiveTime().getValue());
            }

            if (observationMock.getValueCode() != null) {
//                if (observationMock.getValueUnit() == null) {
                    CD value = (CD) observation.getValues().get(0);

                    TestUtil.assertCodes(observationMock.getValueCode(), value);
//                    assertEquals("2.16.840.1.113883.6.96", value.getCodeSystem());
                } else if (!StringUtils.isEmpty(observationMock.getValue())) {
                    PQ value = (PQ) observation.getValues().get(0);
                    assertEquals(Double.parseDouble(observationMock.getValue()), value.getValue().doubleValue(), DELTA);
                    assertEquals(observationMock.getValueUnit(), value.getUnit());
                }
             else {
                assertEquals(NullFlavor.NI, observation.getValues().get(0).getNullFlavor());
            }

            if (observationMock.getInterpretationCodes() != null) {
                assertEquals(observationMock.getInterpretationCodes().size(), observation.getInterpretationCodes().size());
                for (int i = 0; i < observationMock.getInterpretationCodes().size(); i++) {
                    TestUtil.assertCodes(observationMock.getInterpretationCodes().get(i), observation.getInterpretationCodes().get(i));
                }
            }

            if (observationMock.getMethodCode() != null) {
                TestUtil.assertCodes(observationMock.getMethodCode(), observation.getMethodCodes().get(0));
            }

            if (observationMock.getTargetSiteCode() != null) {
                TestUtil.assertCodes(observationMock.getTargetSiteCode(), observation.getTargetSiteCodes().get(0));
            }

            if (observationMock.getAuthor() != null) {
                TestUtil.assertAuthors(observationMock.getAuthor(), observation.getAuthors().get(0));
            }

            if (!CollectionUtils.isEmpty(observationMock.getNonMedicinalSupplyActivities())) {
                assertNonMedicinalSupplyActivities(observationMock.getNonMedicinalSupplyActivities(), observation.getSupplies(), true);
            }

            int expectedObservationSize = 0;

            EList<Observation> observationList = observation.getObservations();

            if (!CollectionUtils.isEmpty(observationMock.getCaregiverCharacteristics())) {
                expectedObservationSize += observationMock.getCaregiverCharacteristics().size();
                assertCaregiverCharacteristics(observationMock.getCaregiverCharacteristics(), observationList, true);
            }

            if (!CollectionUtils.isEmpty(observationMock.getAssessmentScaleObservations())) {
                expectedObservationSize += observationMock.getAssessmentScaleObservations().size();
                assertAssessmentScaleObservations(observationMock.getAssessmentScaleObservations(), observationList, true);
            }

            assertEquals(expectedObservationSize, observationList.size());

            if (observationMock.getReferenceRanges() != null) {
                assertReferenceRanges(observationMock.getReferenceRanges(), observation.getReferenceRanges());
            }

        }


    }

    private void assertReferenceRanges(List<String> mockReferenceRanges, EList<ReferenceRange> referenceRanges) {
        assertEquals(mockReferenceRanges.size(), referenceRanges.size());
        for (int i = 0; i < mockReferenceRanges.size(); i++) {
            assertEquals(mockReferenceRanges.get(i), referenceRanges.get(i).getObservationRange().getText().getText());
        }

    }

    private void assertAssessmentScaleObservations(List<AssessmentScaleObservation> assessmentScaleObservations, EList<Observation> observations, boolean assertTypeCode) {
        for (AssessmentScaleObservation asMock : assessmentScaleObservations) {
            Observation asObservation = TestUtil.getObservationById(asMock.getId(), observations);

            assertNotNull(asObservation);
            if (assertTypeCode)
                assertEquals("COMP", ((EntryRelationship) asObservation.eContainer()).getTypeCode().toString());
            assertEquals("OBS", asObservation.getClassCode().toString());
            assertEquals("EVN", asObservation.getMoodCode().toString());
            assertEquals("2.16.840.1.113883.10.20.22.4.69", asObservation.getTemplateIds().get(0).getRoot());

            if (asMock.getCode() != null) {
                TestUtil.assertCodes(asMock.getCode(), asObservation.getCode());
            } else {
                assertEquals(NullFlavor.NI, asObservation.getCode().getNullFlavor());
            }

            if (asMock.getDerivationExpr() != null) {
                assertEquals(asMock.getDerivationExpr(), asObservation.getDerivationExpr().getText());
            }
            assertEquals("completed", asObservation.getStatusCode().getCode());

            if (asMock.getEffectiveTime() != null) {
                assertEquals(CcdUtils.formatSimpleDate(asMock.getEffectiveTime()), asObservation.getEffectiveTime().getValue());
            } else {
                assertEquals(NullFlavor.NI, asObservation.getEffectiveTime().getNullFlavor());
            }

            if (asMock.getValue() != null) {
                INT value = (INT) asObservation.getValues().get(0);
                assertEquals(asMock.getValue().intValue(), value.getValue().intValue());
            } else {
                assertEquals(NullFlavor.NI, asObservation.getValues().get(0).getNullFlavor());
            }

            if (asMock.getInterpretationCodes() != null) {
                List<CE> codes = asObservation.getInterpretationCodes();
                assertEquals(asMock.getInterpretationCodes().size(), codes.size());
                for (int i = 0; i < asMock.getInterpretationCodes().size(); i++) {
                    TestUtil.assertCodes(asMock.getInterpretationCodes().get(i), codes.get(i));
                }
            }

            if (asMock.getAuthors() != null) {
                List<org.eclipse.mdht.uml.cda.Author> authors = asObservation.getAuthors();
                assertEquals(asMock.getAuthors().size(), authors.size());
                for (int i = 0; i < asMock.getAuthors().size(); i++) {
                    TestUtil.assertAuthors(asMock.getAuthors().get(i), authors.get(i));
                }
            }

            if (asMock.getAssessmentScaleSupportingObservations() != null) {
                assertAssessmentScaleSupportingObservation(asMock.getAssessmentScaleSupportingObservations(), asObservation.getObservations());
            }

            if (asMock.getObservationRanges() != null) {
                assertReferenceRanges(asMock.getObservationRanges(), asObservation.getReferenceRanges());
            }


        }
    }

    private void assertAssessmentScaleSupportingObservation(List<AssessmentScaleSupportingObservation> mockObservations, EList<Observation> observations) {
        assertEquals(mockObservations.size(), observations.size());
        for (AssessmentScaleSupportingObservation observationMock : mockObservations) {
            Observation observationCcd = TestUtil.getObservationById(observationMock.getId(), observations);
            assertNotNull(observationCcd);
            assertEquals("COMP", ((EntryRelationship) observationCcd.eContainer()).getTypeCode().toString());
            assertEquals("OBS", observationCcd.getClassCode().toString()); //TODO tostring?
            assertEquals("EVN", observationCcd.getMoodCode().toString());
            assertEquals("2.16.840.1.113883.10.20.22.4.86", observationCcd.getTemplateIds().get(0).getRoot());

            if (observationMock.getCode() != null) {
                TestUtil.assertCodes(observationMock.getCode(), observationCcd.getCode());
            } else {
                assertEquals(NullFlavor.NI, observationCcd.getCode().getNullFlavor());
            }

            assertEquals("completed", observationCcd.getStatusCode().getCode());

            if (observationMock.getIntValue() != null) {
                INT value = (INT) observationCcd.getValues().get(0);
                assertEquals(observationMock.getIntValue().intValue(), value.getValue().intValue());
            }
            if (observationMock.getValueCode() != null) {
                CD value = (CD) observationCcd.getValues().get(0);
                TestUtil.assertCodes(observationMock.getValueCode(), value);
            }

            if (observationMock.getIntValue() == null && observationMock.getValueCode() == null) {
                assertEquals(NullFlavor.NI, observationCcd.getValues().get(0).getNullFlavor());
            }

        }
    }

    private void assertCaregiverCharacteristics(List<CaregiverCharacteristic> caregiverCharacteristics, EList<Observation> observations, boolean assertTypeCode) {
        for (CaregiverCharacteristic ccMock : caregiverCharacteristics) {
            Observation ccObservation = TestUtil.getObservationById(ccMock.getId(), observations);
            assertNotNull(ccObservation);
            if (assertTypeCode)
                assertEquals("REFR", ((EntryRelationship) ccObservation.eContainer()).getTypeCode().toString());
            assertEquals("OBS", ccObservation.getClassCode().toString()); //TODO tostring?
            assertEquals("EVN", ccObservation.getMoodCode().toString());
            assertEquals("2.16.840.1.113883.10.20.22.4.72", ccObservation.getTemplateIds().get(0).getRoot());
            if (ccMock.getCode() != null) {
                TestUtil.assertCodes(ccMock.getCode(), ccObservation.getCode());
//                assertEquals(ccMock.getCode(), ccObservation.getCode().getCode());
//                assertEquals("2.16.840.1.113883.5.4", ccObservation.getCode().getCodeSystem());
            } else {
                assertEquals(NullFlavor.NI, ccObservation.getCode().getNullFlavor());
            }

            assertEquals("completed", ccObservation.getStatusCode().getCode());

            CD value = (CD) ccObservation.getValues().get(0);
            if (ccMock.getValue() != null) {
//                assertEquals(ccMock.getValue(), value.getCode());
//                assertEquals(ccMock.getValueCodeSystem(), value.getCodeSystem());
                TestUtil.assertCodes(ccMock.getValue(), value);
            } else {
                assertEquals(NullFlavor.NI, value.getNullFlavor());
            }

            Participant2 participant = ccObservation.getParticipants().get(0);
            assertNotNull(participant);
            assertEquals("IND", participant.getTypeCode().toString());
            if (ccMock.getParticipantTimeLow() != null || ccMock.getParticipantTimeHigh() != null || ccMock.getParticipantRoleCode() != null) {
                if (ccMock.getParticipantTimeLow() != null || ccMock.getParticipantTimeHigh() != null) {
                    IVL_TS effectiveTime = participant.getTime();
                    if (ccMock.getParticipantTimeLow() != null) {
                        assertEquals(CcdUtils.formatSimpleDate(ccMock.getParticipantTimeLow()), effectiveTime.getLow().getValue());
                    } else {
                        assertEquals(NullFlavor.NI, effectiveTime.getLow().getNullFlavor());
                    }
                    if (ccMock.getParticipantTimeHigh() != null) {
                        assertEquals(CcdUtils.formatSimpleDate(ccMock.getParticipantTimeHigh()), effectiveTime.getHigh().getValue());
                    }
                }
                ParticipantRole participantRole = participant.getParticipantRole();
                assertEquals("CAREGIVER", participantRole.getClassCode().toString());
                CE code = participantRole.getCode();
                if (ccMock.getParticipantRoleCode() != null) {
//                    assertEquals(ccMock.getParticipantRoleCode(), code.getCode());
//                    assertEquals("2.16.840.1.113883.5.111", code.getCodeSystem());
                    TestUtil.assertCodes(ccMock.getParticipantRoleCode(), code);
                } else {
                    //assertEquals(NullFlavor.NI, code.getNullFlavor());         //TODO tocheck?
                }
            } else {
                assertEquals(NullFlavor.NI, participant.getNullFlavor());
            }
        }
    }


    private void assertNonMedicinalSupplyActivities(List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities, EList<Supply> supplies, boolean assertTypecode) {
        assertEquals(nonMedicinalSupplyActivities.size(), supplies.size());
        for (NonMedicinalSupplyActivity supplyMock : nonMedicinalSupplyActivities) {
            Supply supply = getSupplyById(supplyMock.getId(), supplies);
            assertNotNull(supply);
            if (assertTypecode)
                assertEquals("REFR", ((EntryRelationship) supply.eContainer()).getTypeCode().toString());
            assertEquals("SPLY", supply.getClassCode().toString());
            assertEquals(supplyMock.getMoodCode(), supply.getMoodCode().toString());
            assertEquals("2.16.840.1.113883.10.20.22.4.50", supply.getTemplateIds().get(0).getRoot());
            if (supplyMock.getStatusCode() != null) {
                assertEquals(supplyMock.getStatusCode(), supply.getStatusCode().getCode());   //TODO ask question
            } else {
                //assertEquals(NullFlavor.NI, supply.getStatusCode().getNullFavor()); ///MODIFIED
            }

            if (supplyMock.getEffectiveTimeHigh() != null) {
                IVL_TS effectiveTime = (IVL_TS) supply.getEffectiveTimes().get(0);
                assertEquals(CcdUtils.formatSimpleDate(supplyMock.getEffectiveTimeHigh()), effectiveTime.getHigh().getValue());    //TODO debug
            }

            if (supplyMock.getQuantity() != null) {
                assertEquals(supplyMock.getQuantity(), supply.getQuantity().getValue());
            }

            if (supplyMock.getProductInstance() != null) {
                Participant2 participant = supply.getParticipants().get(0);
                assertNotNull(participant);
                assertEquals("PRD", participant.getTypeCode().toString());
                TestUtil.assertProductInstances(supplyMock.getProductInstance(), participant.getParticipantRole());
            }
        }
    }


    private List<StatusProblemObservation> generateStatusProblemObservations() {
        List<StatusProblemObservation> statusProblemObservations = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            StatusProblemObservation statusProblemObservation = new StatusProblemObservation();
            statusProblemObservation.setId(random.nextLong());
            if (i != 2) statusProblemObservation.setValue(TestUtil.createCcdCodeMock());
            if (i == 1) statusProblemObservation.setAssessmentScaleObservations(generateAssessmentScaleObservations());
            if (i == 2) statusProblemObservation.setCaregiverCharacteristics(generateCaregiverCharacteristics());
            if (i != 0) statusProblemObservation.setMethodCode(TestUtil.createCcdCodeMock());
            if (i != 2) statusProblemObservation.setNegationInd(i % 2 == 0);
            if (i == 2)
                statusProblemObservation.setNonMedicinalSupplyActivities(generateNonMedicinalSupplyActivities());
            if (i != 0) statusProblemObservation.setResolved(i == 1);
            if (i != 1) statusProblemObservation.setTimeLow(new Date());
            if (i == 0) statusProblemObservation.setTimeHigh(new Date());  //TODO
            if (i != 0) statusProblemObservation.setText(TestUtil.getRandomString(12));
            statusProblemObservations.add(statusProblemObservation);
        }

        return statusProblemObservations;  //To change body of created methods use File | Settings | File Templates.
    }

    private List<StatusResultOrganizer> generateStatusResultOrganizers() {

        List<StatusResultOrganizer> statusResultOrganizers = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            StatusResultOrganizer statusResultOrganizer = new StatusResultOrganizer();
            if (i != 2) statusResultOrganizer.setCode(TestUtil.createCcdCodeMock());
//            if (i != 1) statusResultOrganizer.setCodeSystem(TestUtil.getRandomString(10));
            statusResultOrganizer.setId(random.nextLong());
            if (i != 2) statusResultOrganizer.setStatusResultObservations(generateStatusResultObservations());
            statusResultOrganizers.add(statusResultOrganizer);
        }
        return statusResultOrganizers;
    }

    private List<StatusResultObservation> generateStatusResultObservations() {
        List<StatusResultObservation> statusResultObservations = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            StatusResultObservation statusResultObservation = new StatusResultObservation();
            statusResultObservation.setId(random.nextLong());
            if (i != 2)
                statusResultObservation.setCode(TestUtil.createCcdCodeMock());
            if (i == 1)
                statusResultObservation.setAuthor(TestUtil.createAuthorMock());
            if (i != 0)
                statusResultObservation.setEffectiveTime(new Date());
            if (i != 1)
                statusResultObservation.setInterpretationCodes(new ArrayList<>(Arrays.asList(TestUtil.createCcdCodeMock(), TestUtil.createCcdCodeMock())));
            if (i != 0)
                statusResultObservation.setMethodCode(TestUtil.createCcdCodeMock());
            if (i != 2)
                statusResultObservation.setReferenceRanges(TestUtil.generateStringArray(3));
            if (i != 0)
                statusResultObservation.setTargetSiteCode(TestUtil.createCcdCodeMock());
            if (i != 1)
                statusResultObservation.setText(TestUtil.getRandomString(20));
            if (i != 2)
                statusResultObservation.setValue("" + random.nextInt());
            if (i == 1)
                statusResultObservation.setValueUnit(TestUtil.getRandomString(2));

            if (i != 0)
                statusResultObservation.setAssessmentScaleObservations(generateAssessmentScaleObservations());
            if (i != 1)
                statusResultObservation.setCaregiverCharacteristics(generateCaregiverCharacteristics());
            if (i != 2)
                statusResultObservation.setNonMedicinalSupplyActivities(generateNonMedicinalSupplyActivities());

            statusResultObservations.add(statusResultObservation);

        }
        return statusResultObservations;  //To change body of created methods use File | Settings | File Templates.
    }


//    private List<String> generateInterpretationCodes() {
//        return null;  //To change body of created methods use File | Settings | File Templates.
//    }

    private List<NonMedicinalSupplyActivity> generateNonMedicinalSupplyActivities() {
        List<NonMedicinalSupplyActivity> nonMedicinalSupplyActivities = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            NonMedicinalSupplyActivity nonMedicinalSupplyActivity = new NonMedicinalSupplyActivity();
            nonMedicinalSupplyActivity.setId(random.nextLong());
            if (i != 0) nonMedicinalSupplyActivity.setEffectiveTimeHigh(new Date());
            nonMedicinalSupplyActivity.setMoodCode(x_DocumentSubstanceMood.get(random.nextInt(4)).toString());          //x_DocumentActMood.get(rndInt).toString()
            if (i != 1) nonMedicinalSupplyActivity.setQuantity(BigDecimal.valueOf(random.nextFloat()));
            if (i != 2) nonMedicinalSupplyActivity.setStatusCode(TestUtil.getRandomString(8));

            if (i != 1) {
                ProductInstance productInstanceMock = new ProductInstance();
                productInstanceMock.setId(random.nextLong());
                productInstanceMock.setDeviceCode(TestUtil.createCcdCodeMock());
                productInstanceMock.setScopingEntityId(TestUtil.getRandomString(12));
                nonMedicinalSupplyActivity.setProductInstance(productInstanceMock);
            }
            nonMedicinalSupplyActivities.add(nonMedicinalSupplyActivity);
        }
        return nonMedicinalSupplyActivities;
    }

    private List<CaregiverCharacteristic> generateCaregiverCharacteristics() {
        List<CaregiverCharacteristic> caregiverCharacteristics = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            CaregiverCharacteristic caregiverCharacteristic = new CaregiverCharacteristic();
            caregiverCharacteristic.setId(random.nextLong());
            if (i != 2) caregiverCharacteristic.setCode(TestUtil.createCcdCodeMock());
            if (i != 1) caregiverCharacteristic.setValue(TestUtil.createCcdCodeMock());
            if (i != 0) caregiverCharacteristic.setParticipantRoleCode(TestUtil.createCcdCodeMock());
            if (i != 2) caregiverCharacteristic.setParticipantTimeHigh(new Date());
            if (i == 0) caregiverCharacteristic.setParticipantTimeLow(new Date());
//            if (i != 2) caregiverCharacteristic.setValueCodeSystem(TestUtil.getRandomString(8));
            caregiverCharacteristics.add(caregiverCharacteristic);
        }

        return caregiverCharacteristics;
    }

    private List<AssessmentScaleObservation> generateAssessmentScaleObservations() {

        List<AssessmentScaleObservation> assessmentScaleObservations = new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            AssessmentScaleObservation assessmentScaleObservation = new AssessmentScaleObservation();
            assessmentScaleObservation.setId(random.nextLong());
            if (i != 1) assessmentScaleObservation.setCode(TestUtil.createCcdCodeMock());
            if (i != 2) assessmentScaleObservation.setValue(random.nextInt());
            if (i != 0) assessmentScaleObservation.setDerivationExpr(TestUtil.getRandomString(15));
            if (i != 1) assessmentScaleObservation.setEffectiveTime(new Date());
            if (i != 0) assessmentScaleObservation.setInterpretationCodes(new ArrayList<>(Arrays.asList(TestUtil.createCcdCodeMock(), TestUtil.createCcdCodeMock())));
            if (i != 2) assessmentScaleObservation.setObservationRanges(TestUtil.generateStringArray(2));
            if (i == 0)
                assessmentScaleObservation.setAuthors(Arrays.asList(TestUtil.createAuthorMock(), TestUtil.createAuthorMock()));
            if (i != 1)
                assessmentScaleObservation.setAssessmentScaleSupportingObservations(generateAssessmentScaleSupportingObservations());
            assessmentScaleObservations.add(assessmentScaleObservation);
        }
        return assessmentScaleObservations;
    }

    private List<AssessmentScaleSupportingObservation> generateAssessmentScaleSupportingObservations() {
        List<AssessmentScaleSupportingObservation> assessmentScaleSupportingObservations = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            AssessmentScaleSupportingObservation assessmentScaleSupportingObservation = new AssessmentScaleSupportingObservation();
            assessmentScaleSupportingObservation.setId(random.nextLong());
            if (i != 1) assessmentScaleSupportingObservation.setCode(TestUtil.createCcdCodeMock());
            if (i == 2) assessmentScaleSupportingObservation.setIntValue(random.nextInt());
            if (i != 2) assessmentScaleSupportingObservation.setValueCode(TestUtil.createCcdCodeMock());
            assessmentScaleSupportingObservations.add(assessmentScaleSupportingObservation);
        }
        return assessmentScaleSupportingObservations;
    }

    private List<PressureUlcerObservation> generatePressureUlcerObservation() {
        List<PressureUlcerObservation> pressureUlcerObservations = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            PressureUlcerObservation pressureUlcerObservation = new PressureUlcerObservation();
            pressureUlcerObservation.setId(random.nextLong());
            if (i != 1) pressureUlcerObservation.setEffectiveTime(new Date());
            if (i != 1) pressureUlcerObservation.setNegationInd(i == 0);
            if (i != 2) pressureUlcerObservation.setValue(TestUtil.createCcdCodeMock());
            if (i != 0) pressureUlcerObservation.setText(TestUtil.getRandomString(15));
            if (i != 1) pressureUlcerObservation.setLengthOfWoundValue(random.nextDouble());
            if (i != 2) pressureUlcerObservation.setWidthOfWoundValue(random.nextDouble());
            if (i != 0) pressureUlcerObservation.setDepthOfWoundValue(random.nextDouble());
            if (i == 0) pressureUlcerObservation.setTargetSiteCodes(createTargetSiteCodes());
            pressureUlcerObservations.add(pressureUlcerObservation);
        }
        return pressureUlcerObservations;  //To change body of created methods use File | Settings | File Templates.
    }

    private List<TargetSiteCode> createTargetSiteCodes() {
        List<TargetSiteCode> targetSiteCodes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            TargetSiteCode targetSiteCode = new TargetSiteCode();
            targetSiteCode.setId(random.nextLong());
            if (i != 1) targetSiteCode.setCode(TestUtil.createCcdCodeMock());
            if (i != 2) targetSiteCode.setValue(TestUtil.createCcdCodeMock());
            targetSiteCodes.add(targetSiteCode);
        }
        return targetSiteCodes;  //To change body of created methods use File | Settings | File Templates.
    }


    private List<HighestPressureUlcerStage> generateHighestPressureUlcerStages() {
        List<HighestPressureUlcerStage> highestPressureUlcerStages = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            HighestPressureUlcerStage highestPressureUlcerStage = new HighestPressureUlcerStage();
            highestPressureUlcerStage.setId(random.nextLong());
            if (i != 1) highestPressureUlcerStage.setValue(TestUtil.createCcdCodeMock());
            highestPressureUlcerStages.add(highestPressureUlcerStage);
        }
        return highestPressureUlcerStages;
    }

    private List<NumberOfPressureUlcersObservation> generateNumberOfPressureUlcersObservation() {
        List<NumberOfPressureUlcersObservation> numberOfPressureUlcersObservations = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            NumberOfPressureUlcersObservation numberOfPressureUlcersObservation = new NumberOfPressureUlcersObservation();
            numberOfPressureUlcersObservation.setId(random.nextLong());
            if (i == 0) numberOfPressureUlcersObservation.setAuthor(TestUtil.createAuthorMock());
            if (i != 1) numberOfPressureUlcersObservation.setEffectiveTime(new Date());
            if (i != 1) numberOfPressureUlcersObservation.setValue(random.nextInt());
            if (i != 2) numberOfPressureUlcersObservation.setObservationValue(TestUtil.createCcdCodeMock());
            numberOfPressureUlcersObservations.add(numberOfPressureUlcersObservation);
        }
        return numberOfPressureUlcersObservations;
    }


    private Supply getSupplyById(Long id, EList<Supply> supplies) {
        for (Supply supply : supplies) {
            II adId = supply.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return supply;
            }
        }
        return null;
    }


    private Observation findObservationByCode(String code, List<Observation> observations) {
        for (Observation observation : observations) {
            if (observation.getCode().getCode().equals(code)) {
                return observation;
            }
        }
        return null;
    }


}

