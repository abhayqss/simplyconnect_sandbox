package com.scnsoft.eldermark.test.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.FamilyHistory;
import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import com.scnsoft.eldermark.services.ccd.templates.sections.FamilyHistoryFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.test.util.TestUtil;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.RelatedSubject;
import org.eclipse.mdht.uml.cda.SubjectPerson;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.PQ;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ccd.AgeObservation;
import org.openhealthtools.mdht.uml.cda.ccd.FamilyHistoryOrganizer;
import org.openhealthtools.mdht.uml.cda.ccd.FamilyHistorySection;

import java.util.*;

import static org.junit.Assert.*;

public class FamilyHistorySectionTest {

    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() throws Exception {
        Random random = new Random();

        List<FamilyHistory> familyHistoryList = new ArrayList<>();

        //TODO find the way to test multiple objects without ids
        for (int n = 0; n < 1; n++) {
            FamilyHistory familyHistoryMock = new FamilyHistory();
            familyHistoryMock.setId(random.nextLong());
            if (n != 2)
                familyHistoryMock.setAdministrativeGenderCode(TestUtil.createCcdCodeMock());
            familyHistoryMock.setBirthTime(new Date());
            familyHistoryMock.setDeceasedInd((n % 2) == 0);
            familyHistoryMock.setRelatedSubjectCode(TestUtil.createCcdCodeMock());
            familyHistoryMock.setDeceasedTime(new Date());
            familyHistoryMock.setPersonInformationId(UUID.randomUUID().toString());
            familyHistoryMock.setRelatedSubjectCode(TestUtil.createCcdCodeMock());

            List<FamilyHistoryObservation> familyHistoryObservationList = new ArrayList<>();
            familyHistoryMock.setFamilyHistoryObservations(familyHistoryObservationList);

            for (int i = 0; i < 3; i++) {
                FamilyHistoryObservation fhObsMock = new FamilyHistoryObservation();
                fhObsMock.setId(random.nextLong());
                if (i != 1)
                    fhObsMock.setProblemTypeCode(TestUtil.createCcdCodeMock());
                if (i != 1)
                    fhObsMock.setProblemValue(TestUtil.createCcdCodeMock());
                fhObsMock.setEffectiveTime(new Date());
                if (i != 3)
                    fhObsMock.setAgeObservationValue(random.nextInt(100));
                if (i != 2)
                    fhObsMock.setAgeObservationUnit(TestUtil.getRandomString(2));
                if (i != 1)
                    fhObsMock.setDeceased(familyHistoryMock.getDeceasedInd() == Boolean.TRUE && i == 2);
                familyHistoryObservationList.add(fhObsMock);
            }
            familyHistoryList.add(familyHistoryMock);
        }

        /*
        daoMock = EasyMock.createMock(FamilyHistoryDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(familyHistoryList));
        EasyMock.replay(daoMock);*/

        FamilyHistoryFactory familyHistoryFactory = new FamilyHistoryFactory();
        // dependency on DAO is not required for parsing anymore
        //familyHistoryFactory.setFamilyHistoryDao(daoMock);

        FamilyHistorySection familyHistorySection = familyHistoryFactory.buildTemplateInstance(familyHistoryList);

        assertNotNull(familyHistorySection);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.1.4", familyHistorySection.getTemplateIds().get(0).getRoot());
        assertEquals("10157-6", familyHistorySection.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", familyHistorySection.getCode().getCodeSystem());
        assertEquals("Family history", familyHistorySection.getTitle().getText());

        List<FamilyHistoryOrganizer> familyHistoryOrganizerList = familyHistorySection.getFamilyHistoryOrganizers();
        assertEquals(familyHistoryList.size(), familyHistoryOrganizerList.size());

        for (int i = 0; i < familyHistoryList.size(); i++) {
            FamilyHistoryOrganizer familyHistoryOrganizer = familyHistoryOrganizerList.get(i);
            FamilyHistory familyHistoryMock = familyHistoryList.get(i);

            assertEquals("CLUSTER", familyHistoryOrganizer.getClassCode().getLiteral());
            assertEquals("EVN", familyHistoryOrganizer.getMoodCode().getLiteral());
            assertEquals("2.16.840.1.113883.10.20.22.4.45", familyHistoryOrganizer.getTemplateIds().get(0).getRoot());
            assertEquals("completed", familyHistoryOrganizer.getStatusCode().getCode());

            RelatedSubject relatedSubject = familyHistoryOrganizer.getSubject().getRelatedSubject();
            assertEquals("PRS", relatedSubject.getClassCode().getName());
            TestUtil.assertCodes(familyHistoryMock.getRelatedSubjectCode(), relatedSubject.getCode());

            SubjectPerson subjectPerson = relatedSubject.getSubject();

            if (familyHistoryMock.getAdministrativeGenderCode() != null) {
                TestUtil.assertCodes(familyHistoryMock.getAdministrativeGenderCode(), subjectPerson.getAdministrativeGenderCode());
//                assertEquals("2.16.840.1.113883.5.1", subjectPerson.getAdministrativeGenderCode().getCodeSystem());
                assertEquals(CcdUtils.formatSimpleDate(familyHistoryMock.getBirthTime()), subjectPerson.getBirthTime().getValue());

//              assertEquals(familyHistoryMock.getPersonInformationId().toString(), subjectPerson.getSDTCIds().get(0).getRoot());
//              if (familyHistoryMock.getDeceasedInd() != null)
//                  assertEquals(familyHistoryMock.getDeceasedInd(), subjectPerson.getSDTCDeceasedInd().getValue());
//              assertEquals(CcdUtils.formatSimpleDate(familyHistoryMock.getDeceasedTime()), subjectPerson.getSDTCDeceasedTime().getValue());
            }

            List<org.openhealthtools.mdht.uml.cda.ccd.FamilyHistoryObservation> familyHistoryObservationList = familyHistoryOrganizer.getFamilyHistoryObservations();
            assertEquals(familyHistoryMock.getFamilyHistoryObservations().size(), familyHistoryObservationList.size());

            for (FamilyHistoryObservation observationMock : familyHistoryMock.getFamilyHistoryObservations()) {
                org.openhealthtools.mdht.uml.cda.ccd.FamilyHistoryObservation observationCcd = (org.openhealthtools.mdht.uml.cda.ccd.FamilyHistoryObservation) TestUtil.getObservationById(observationMock.getId(), familyHistoryObservationList);
                assertNotNull(observationCcd);
                assertEquals("OBS", observationCcd.getClassCode().getName());
                assertEquals("EVN", observationCcd.getMoodCode().getName());
                assertEquals("2.16.840.1.113883.10.20.22.4.46", observationCcd.getTemplateIds().get(0).getRoot());
                assertEquals(observationMock.getId().toString(), observationCcd.getIds().get(0).getExtension());

                //assertEquals(observationMock.getProblemTypeCode().getCode(), observationCcd.getCode().getCode());

                if (observationMock.getProblemTypeCode() != null) {
                    TestUtil.assertCodes(observationMock.getProblemTypeCode(),observationCcd.getCode());
//                    assertEquals("2.16.840.1.113883.6.96", observationCcd.getCode().getCodeSystem());
//                    assertEquals("SNOMED CT", observationCcd.getCode().getCodeSystemName());
                } else {
                    assertEquals("NI", observationCcd.getCode().getNullFlavor().getName());
                }

                assertEquals("completed", observationCcd.getStatusCode().getCode());

//                assertEquals(CcdUtils.formatSimpleDate(observationMock.getEffectiveTime()), observationCcd.getEffectiveTime().getCenter().getValue());
                assertEquals(CcdUtils.formatSimpleDate(observationMock.getEffectiveTime()), observationCcd.getEffectiveTime().getValue());

                CD value = (CD) observationCcd.getValues().get(0);
                if (observationMock.getProblemValue() != null) {
//                    assertEquals(observationMock.getProblemValue(), value.getCode());
//                    assertEquals("2.16.840.1.113883.6.96", value.getCodeSystem());
                    TestUtil.assertCodes(observationMock.getProblemValue(),value);
                } else {
                    assertEquals("NI", value.getNullFlavor().getName());
                }

                if (observationMock.getAgeObservationValue() != null) {
                    AgeObservation ageObservation = observationCcd.getAgeObservation();

                    assertEquals("SUBJ", ((EntryRelationship) ageObservation.eContainer()).getTypeCode().getName());
                    assertTrue(((EntryRelationship) ageObservation.eContainer()).getInversionInd());

                    assertEquals("OBS", ageObservation.getClassCode().getName());
                    assertEquals("EVN", ageObservation.getMoodCode().getName());

                    assertEquals("2.16.840.1.113883.10.20.22.4.31", ageObservation.getTemplateIds().get(0).getRoot());

                    assertEquals("445518008", ageObservation.getCode().getCode());
                    assertEquals("2.16.840.1.113883.6.96", ageObservation.getCode().getCodeSystem());

                    assertEquals("completed", ageObservation.getStatusCode().getCode());

                    PQ ageValue = (PQ) ageObservation.getValues().get(0);
                    assertEquals(observationMock.getAgeObservationValue().toString(), ageValue.getValue().toString());
                    if (observationMock.getAgeObservationUnit() != null)
                        assertEquals(observationMock.getAgeObservationUnit(), ageValue.getUnit());
                    else
                        assertEquals("a", ageValue.getUnit());
                }
                if (observationMock.getDeceased()!=null &&observationMock.getDeceased()) {
                    List<Observation> observations = observationCcd.getObservations();
                    Observation deathObservation = null;
                    for (Observation observation : observations) {
                        if (((EntryRelationship) observation.eContainer()).getTypeCode().getName().equals("CAUS")) {
                            deathObservation = observation;
                            break;
                        }
                    }

                    assertNotNull(deathObservation);

                    assertEquals("OBS", deathObservation.getClassCode().getName());
                    assertEquals("EVN", deathObservation.getMoodCode().getName());

                    assertEquals("2.16.840.1.113883.10.20.22.4.47", deathObservation.getTemplateIds().get(0).getRoot());

                    assertEquals("ASSERTION", deathObservation.getCode().getCode());
                    assertEquals("2.16.840.1.113883.5.4", deathObservation.getCode().getCodeSystem());

                    assertEquals("completed", deathObservation.getStatusCode().getCode());

                    CD deathObservationValue = (CD) deathObservation.getValues().get(0);
                    assertEquals("419099009", deathObservationValue.getCode());
                    assertEquals("2.16.840.1.113883.6.96", deathObservationValue.getCodeSystem());
                    assertEquals("Dead", deathObservationValue.getDisplayName());

                }


            }


        }


    }

}
