package com.scnsoft.eldermark.test.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.Author;
import com.scnsoft.eldermark.entity.VitalSign;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.services.ccd.templates.sections.VitalSignFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.test.util.TestUtil;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.PQ;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ccd.VitalSignsOrganizer;
import org.openhealthtools.mdht.uml.cda.ccd.VitalSignsSection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VitalSignSectionTest {

    private static final long RESIDENT_ID = 49L;

    private Random random = new Random();

    @Test
    public void testBuildingTemplate() {

        List<VitalSign> vitalSignList = new ArrayList<>();

        for (int n = 0; n < 2; n++) {
            VitalSign vitalSignMock = new VitalSign();
            vitalSignList.add(vitalSignMock);
            vitalSignMock.setId(random.nextLong());
            if (n != 1)
                vitalSignMock.setEffectiveTime(new Date());

            List<VitalSignObservation> vitalSignObservationList = new ArrayList<>();
            vitalSignMock.setVitalSignObservations(vitalSignObservationList);

            for (int i = 0; i < 4; i++) {
                VitalSignObservation vitalSignObservationMock = new VitalSignObservation();
                vitalSignObservationList.add(vitalSignObservationMock);

                vitalSignObservationMock.setId(random.nextLong());
                if (i != 1)
                    vitalSignObservationMock.setEffectiveTime(new Date());
                if (i != 2)
                    vitalSignObservationMock.setUnit(TestUtil.getRandomString(4));
                if (i % 2 != 0)
                    vitalSignObservationMock.setResultTypeCode(TestUtil.createCcdCodeMock());
                if (i != 3)
                    vitalSignObservationMock.setValue(random.nextDouble());
                if (i != 2)
                    vitalSignObservationMock.setInterpretationCode(TestUtil.createCcdCodeMock());
                if (i == 0)
                    vitalSignObservationMock.setMethodCode(TestUtil.createCcdCodeMock());
                if (i == 1)
                    vitalSignObservationMock.setTargetSiteCode(TestUtil.createCcdCodeMock());

                if (i % 2 != 0) {
                    vitalSignObservationMock.setAuthor(TestUtil.createAuthorMock());
                }
            }

        }

        /*
        daoMock = EasyMock.createMock(VitalSignDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(vitalSignList));
        EasyMock.replay(daoMock);*/

        VitalSignFactory vitalSignFactory = new VitalSignFactory();
        // dependency on DAO is not required for parsing anymore
        //vitalSignFactory.setVitalSignDao(daoMock);

        final VitalSignsSection vitalSignsSection = vitalSignFactory.buildTemplateInstance(vitalSignList);

        assertNotNull(vitalSignsSection);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.1.16", vitalSignsSection.getTemplateIds().get(0).getRoot());

        assertEquals("8716-3", vitalSignsSection.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", vitalSignsSection.getCode().getCodeSystem());
        assertEquals("LOINC", vitalSignsSection.getCode().getCodeSystemName());
        assertEquals("Vital Signs".toLowerCase(), vitalSignsSection.getCode().getDisplayName().toLowerCase());

        assertEquals("Vital Signs", vitalSignsSection.getTitle().getText());

        List<VitalSignsOrganizer> vitalSignsOrganizerList = vitalSignsSection.getVitalSignsOrganizers();
        assertEquals(vitalSignList.size(), vitalSignsOrganizerList.size());

        for (VitalSign vitalSignMock : vitalSignList) {
            VitalSignsOrganizer vitalSignsOrganizer = (VitalSignsOrganizer) TestUtil.getOrganizerById(vitalSignMock.getId(), vitalSignsOrganizerList);
            //VitalSign vitalSignMock = vitalSignList.get(i);

            assertEquals("CLUSTER", vitalSignsOrganizer.getClassCode().getLiteral());
            assertEquals("EVN", vitalSignsOrganizer.getMoodCode().getLiteral());
            assertEquals("2.16.840.1.113883.10.20.1.35", vitalSignsOrganizer.getTemplateIds().get(0).getRoot());
            assertEquals(vitalSignMock.getId().toString(), vitalSignsOrganizer.getIds().get(0).getExtension());

            CD organizerCode = vitalSignsOrganizer.getCode();
            assertEquals("46680005", organizerCode.getCode());
            assertEquals("2.16.840.1.113883.6.96", organizerCode.getCodeSystem());
            assertEquals("SNOMED-CT", organizerCode.getCodeSystemName());
            assertEquals("Vital Signs", organizerCode.getDisplayName());

            assertEquals("completed", vitalSignsOrganizer.getStatusCode().getCode());

            if (vitalSignMock.getEffectiveTime() != null) {
                assertEquals(CcdUtils.formatSimpleDate(vitalSignMock.getEffectiveTime()), vitalSignsOrganizer.getEffectiveTime().getValue());
            } else {
                assertEquals(NullFlavor.NI, vitalSignsOrganizer.getEffectiveTime().getNullFlavor());
            }

            List<Observation> observationList = vitalSignsOrganizer.getObservations();
            assertEquals(vitalSignMock.getVitalSignObservations().size(), observationList.size());

            for (VitalSignObservation observationMock : vitalSignMock.getVitalSignObservations()) {
                Observation observationCcd = TestUtil.getObservationById(observationMock.getId(), observationList);
                assertNotNull(observationCcd);

                assertEquals("OBS", observationCcd.getClassCode().getName());
                assertEquals("EVN", observationCcd.getMoodCode().getName());
                assertEquals("2.16.840.1.113883.10.20.1.31", observationCcd.getTemplateIds().get(0).getRoot());
                assertEquals(observationMock.getId().toString(), observationCcd.getIds().get(0).getExtension());

                if (observationMock.getResultTypeCode() != null) {
                    assertEquals(observationMock.getResultTypeCode().getCode(), observationCcd.getCode().getCode());
                    assertEquals(observationMock.getResultTypeCode().getCodeSystem(), observationCcd.getCode().getCodeSystem());
                } else {
                    assertEquals("NI", observationCcd.getCode().getNullFlavor().getName());
                }

                if (observationMock.getValue() != null) {
                    assertEquals("#vit" + observationMock.getId(), observationCcd.getText().getReference().getValue());
                }

                assertEquals("completed", observationCcd.getStatusCode().getCode());


                if (observationMock.getEffectiveTime() != null) {
                    assertEquals(CcdUtils.formatSimpleDate(observationMock.getEffectiveTime()), observationCcd.getEffectiveTime().getValue());
                } else {
                    assertEquals(NullFlavor.NI, observationCcd.getEffectiveTime().getNullFlavor());
                }

                PQ value = (PQ) observationCcd.getValues().get(0);
                if (observationMock.getValue() != null && value.getValue() != null) {
                    assertEquals(BigDecimal.valueOf(observationMock.getValue()), value.getValue());
                }
                if (observationMock.getValue() != null) {
                    assertEquals(observationMock.getUnit(), value.getUnit());
                } else {
                    assertEquals(NullFlavor.NI, value.getNullFlavor());
                }

                if (observationMock.getInterpretationCode() != null) {
                    assertEquals(observationMock.getInterpretationCode().getCode(), observationCcd.getInterpretationCodes().get(0).getCode());
                }

                if (observationMock.getMethodCode() != null) {
                    assertEquals(observationMock.getMethodCode().getCode(), observationCcd.getMethodCodes().get(0).getCode());
                }

                if (observationMock.getTargetSiteCode() != null) {
                    assertEquals(observationMock.getTargetSiteCode().getCode(), observationCcd.getTargetSiteCodes().get(0).getCode());
                }

                if (observationMock.getAuthor() != null) {
                    Author authorMock = observationMock.getAuthor();
                    org.eclipse.mdht.uml.cda.Author author = observationCcd.getAuthors().get(0);

                    TestUtil.assertAuthors(authorMock, author);

                }
            }
        }
    }



}