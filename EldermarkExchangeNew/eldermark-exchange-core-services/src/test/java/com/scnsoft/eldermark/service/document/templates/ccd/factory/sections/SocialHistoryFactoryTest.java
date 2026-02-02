package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.PregnancyObservation;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.h2.cda.util.CdaTestUtils;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.TS;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.ccd.SocialHistorySection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SocialHistoryFactoryTest {

    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        SocialHistory socialHistoryMock = new SocialHistory();
        socialHistoryMock.setId(3L);

        PregnancyObservation pregnancyObservationMock = new PregnancyObservation();
        pregnancyObservationMock.setId(4L);
        pregnancyObservationMock.setEffectiveTimeLow(new Date());
        pregnancyObservationMock.setEstimatedDateOfDelivery(new Date());
        pregnancyObservationMock.setSocialHistory(socialHistoryMock);

        List<PregnancyObservation> pregnancyObservationMocks = new ArrayList<>();
        pregnancyObservationMocks.add(pregnancyObservationMock);
        socialHistoryMock.setPregnancyObservations(pregnancyObservationMocks);

        SmokingStatusObservation smokingStatusObservationMock = new SmokingStatusObservation();
        smokingStatusObservationMock.setId(5L);
        smokingStatusObservationMock.setSocialHistory(socialHistoryMock);
        smokingStatusObservationMock.setValue(TestUtil.createCcdCodeMockCodeSystemNot(CodeSystem.SNOMED_CT.getOid()));
        smokingStatusObservationMock.setEffectiveTimeLow(new Date());

        List<SmokingStatusObservation> smokingStatusObservationMocks = new ArrayList<>();
        smokingStatusObservationMocks.add(smokingStatusObservationMock);
        socialHistoryMock.setSmokingStatusObservations(smokingStatusObservationMocks);

        TobaccoUse tobaccoUseMock = new TobaccoUse();
        tobaccoUseMock.setId(7L);
        tobaccoUseMock.setValue(TestUtil.createCcdCodeMockCodeSystemNot(CodeSystem.SNOMED_CT.getOid()));
        tobaccoUseMock.setEffectiveTimeLow(new Date());
        tobaccoUseMock.setSocialHistory(socialHistoryMock);

        List<TobaccoUse> tobaccoUseMocks = new ArrayList<>();
        tobaccoUseMocks.add(tobaccoUseMock);
        socialHistoryMock.setTobaccoUses(tobaccoUseMocks);

        SocialHistoryObservation socialHistoryObservationMock = new SocialHistoryObservation();
        socialHistoryObservationMock.setId(8L);
        socialHistoryObservationMock.setValue(TestUtil.createCcdCodeMock());
        socialHistoryObservationMock.setFreeText("FreeText");
        socialHistoryObservationMock.setType(TestUtil.createCcdCodeMock());
        socialHistoryObservationMock.setSocialHistory(socialHistoryMock);

        List<SocialHistoryObservation> socialHistoryObservationMocks = new ArrayList<>();
        socialHistoryObservationMocks.add(socialHistoryObservationMock);
        socialHistoryMock.setSocialHistoryObservations(socialHistoryObservationMocks);

        List<SocialHistory> socialHistoryMocks = new ArrayList<>();
        socialHistoryMocks.add(socialHistoryMock);

        /*
        daoMock = EasyMock.createMock(SocialHistoryDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(socialHistoryMocks));
        EasyMock.replay(daoMock);*/


        SocialHistoryFactory socialHistoryFactory = new SocialHistoryFactory();
        // dependency on DAO is not required for parsing anymore
        //socialHistoryFactory.setSocialHistoryDao(daoMock);

        // 2. test
        final SocialHistorySection section = socialHistoryFactory.buildTemplateInstance(socialHistoryMocks);

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.1.15", section.getTemplateIds().get(0).getRoot());
        assertEquals("29762-2", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        // test Social History Observation
        Observation observation = section.getEntries().get(0).getObservation();
        assertNotNull(observation);
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.1.33", observation.getTemplateIds().get(0).getRoot());
        assertEquals(socialHistoryObservationMock.getId().toString(), observation.getIds().get(0).getExtension());
        assertEquals(socialHistoryObservationMock.getType().getCode(), observation.getCode().getCode());
        assertEquals("#" + SocialHistoryObservation.class.getSimpleName() + socialHistoryObservationMock.getId(), observation.getCode().getOriginalText().getReference().getValue());
        assertEquals(socialHistoryObservationMock.getType().getCodeSystem(), observation.getCode().getCodeSystem());
        assertEquals("completed", observation.getStatusCode().getCode());
        assertEquals(socialHistoryObservationMock.getValue().getCode(), ((CD) observation.getValues().get(0)).getCode());
//        assertTrue(section.getText().getText().contains(socialHistoryObservationMock.getFreeText()));

        // test Pregnancy Observation
        observation = section.getEntries().get(1).getObservation();
        assertNotNull(observation);
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.1.33", observation.getTemplateIds().get(0).getRoot());
        assertEquals("ASSERTION", observation.getCode().getCode());
        assertEquals("completed", observation.getStatusCode().getCode());
        assertEquals("77386006", ((CD) observation.getValues().get(0)).getCode());
        assertEquals("2.16.840.1.113883.6.96", ((CD) observation.getValues().get(0)).getCodeSystem());
        assertEquals(CcdUtils.formatSimpleDate(pregnancyObservationMock.getEffectiveTimeLow()), observation.getEffectiveTime().getLow().getValue());
        assertEquals("REFR", observation.getEntryRelationships().get(0).getTypeCode().getName());
        observation = observation.getEntryRelationships().get(0).getObservation();
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.1.33", observation.getTemplateIds().get(0).getRoot());
        assertEquals("completed", observation.getStatusCode().getCode());
        assertEquals("11778-8", observation.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", observation.getCode().getCodeSystem());
        assertEquals(CcdUtils.formatSimpleDate(pregnancyObservationMock.getEstimatedDateOfDelivery()), ((TS) observation.getValues().get(0)).getValue());

        // test Smoking Status Observation
        observation = section.getEntries().get(2).getObservation();
        assertNotNull(observation);
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.1.33", observation.getTemplateIds().get(0).getRoot());
        assertEquals("ASSERTION", observation.getCode().getCode());
        assertEquals("completed", observation.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(smokingStatusObservationMock.getEffectiveTimeLow()), observation.getEffectiveTime().getLow().getValue());
        CdaTestUtils.assertCodeTranslation(smokingStatusObservationMock.getValue(), (CD) observation.getValues().get(0), false);

        // test Tobacco Use
        observation = section.getEntries().get(3).getObservation();
        assertNotNull(observation);
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.1.33", observation.getTemplateIds().get(0).getRoot());
        assertEquals("ASSERTION", observation.getCode().getCode());
        assertEquals("completed", observation.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(smokingStatusObservationMock.getEffectiveTimeLow()), observation.getEffectiveTime().getLow().getValue());
        CdaTestUtils.assertCodeTranslation(tobaccoUseMock.getValue(), (CD) observation.getValues().get(0), false);
    }
}
