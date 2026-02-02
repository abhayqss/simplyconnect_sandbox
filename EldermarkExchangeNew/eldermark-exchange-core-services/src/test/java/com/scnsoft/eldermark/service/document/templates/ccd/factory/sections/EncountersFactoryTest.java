package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.Act;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.ccd.EncountersSection;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EncountersFactoryTest {

    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        CcdCode ccdCode = new CcdCode();
        Encounter encounterMock = new Encounter();
        encounterMock.setId(1L);
        encounterMock.setDispositionCode(TestUtil.createCcdCodeMock());
        encounterMock.setEffectiveTime(new Date());
        encounterMock.setEncounterType(TestUtil.createCcdCodeMock());
        encounterMock.setEncounterTypeText("encounterTypeText");

        ProblemObservation problemObservationMock = new ProblemObservation();
        problemObservationMock.setId(1L);
        problemObservationMock.setProblemName("problemName");
        problemObservationMock.setProblemDateTimeHigh(new Date());
        problemObservationMock.setProblemDateTimeLow(new Date());
        problemObservationMock.setProblemCode(createCcdCode("problemCode", "problemCodeSystem"));
        problemObservationMock.setProblemType(createCcdCode("problemType", "problemTypeCodesystem"));
        problemObservationMock.setAgeObservationUnit("a");
        problemObservationMock.setAgeObservationValue(50);
        problemObservationMock.setHealthStatusCode(createCcdCode("healthStatusCode", "healthStatusCodeSystem"));
        problemObservationMock.setHealthStatusObservationText("healthStatusText");
        problemObservationMock.setProblemStatusCode(createCcdCode("problemStatusCode", "problemStatusCodeSystem"));
        problemObservationMock.setProblemStatusText("problemStatusText");
        encounterMock.setProblemObservation(problemObservationMock);

        Indication indicationMock = new Indication();
        indicationMock.setId(5L);
        ccdCode.setCode("IndicationCode");
        indicationMock.setCode(ccdCode);
        ccdCode.setCode("IndicationValue");
        indicationMock.setValue(ccdCode);
        indicationMock.setTimeLow(new Date());
        indicationMock.setTimeHigh(new Date());

        List<Indication> indicationMocks = new ArrayList<>();
        indicationMocks.add(indicationMock);
        encounterMock.setIndications(indicationMocks);

        ServiceDeliveryLocation locationMock = new ServiceDeliveryLocation();
        locationMock.setId((long) 3);
        locationMock.setCode(TestUtil.createCcdCodeMock());
        locationMock.setName("ServiceDeliveryLocation_Name");

        CommunityTelecom tel = new CommunityTelecom();
        tel.setId(1L);
        tel.setValue("TelcomValue");
        tel.setUseCode("HP");

        var telecoms = new ArrayList<CommunityTelecom>();
        telecoms.add(tel);
        locationMock.setTelecoms(telecoms);

        CommunityAddress addr = new CommunityAddress();
        addr.setPostalCode("PostalCode");
        addr.setStreetAddress("StreetAddress");
        addr.setCity("City");
        addr.setState("State");
        addr.setCountry("Country");

        List<CommunityAddress> addrs = new ArrayList<>();
        addrs.add(addr);
        locationMock.setAddresses(addrs);

        List<ServiceDeliveryLocation> locationMocks = new ArrayList<>();
        locationMocks.add(locationMock);
        encounterMock.setServiceDeliveryLocations(locationMocks);

        var encounterPerformerMocks = new ArrayList<EncounterPerformer>();
        EncounterPerformer encounterPerformerMock = TestUtil.createEncounterPerformerMock();
        encounterPerformerMock.setEncounter(encounterMock);
        encounterPerformerMocks.add(encounterPerformerMock);
        encounterMock.setEncounterPerformers(encounterPerformerMocks);

        Set<Encounter> encounterMocks = new HashSet<>();
        encounterMocks.add(encounterMock);

        EncountersFactory encountersFactory = new EncountersFactory();

        // 2. test
        final EncountersSection section = encountersFactory.buildTemplateInstance(encounterMocks);

        // 3. verify
        assertNotNull(section);
        assertEquals("2.16.840.1.113883.10.20.1.3", section.getTemplateIds().get(0).getRoot());
        assertEquals("46240-8", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        var encounters = section.getEncounters();
        assertEquals(encounterMocks.size(), encounters.size());
        var encounter = encounters.get(0);
        assertNotNull(encounter);
        assertEquals("2.16.840.1.113883.10.20.22.4.49", encounter.getTemplateIds().get(0).getRoot());
        assertEquals(encounterMock.getId().toString(), encounter.getIds().get(0).getExtension());
        assertEquals("ENC", encounter.getClassCode().getName());
        assertEquals("EVN", encounter.getMoodCode().getName().toString());
        assertEquals(encounterMock.getEncounterType().getCode(), encounter.getCode().getCode());
        assertEquals(encounterMock.getEncounterType().getCodeSystem(), encounter.getCode().getCodeSystem());
        assertEquals("#" + Encounter.class.getSimpleName() + encounterMock.getId(), encounter.getCode().getOriginalText().getReference().getValue());
//        assertTrue(section.getText().getText().contains(encounterMock.getEncounterTypeText()));
        assertEquals(CcdUtils.formatSimpleDate(encounterMock.getEffectiveTime()), encounter.getEffectiveTime().getCenter().getValue());

        assertEquals(encounterMock.getEncounterPerformers().get(0).getProviderCode().getCode(), encounter.getPerformers().get(0).getAssignedEntity().getCode().getCode());
        assertEquals(encounterMock.getEncounterPerformers().get(0).getProviderCode().getCodeSystem(), encounter.getPerformers().get(0).getAssignedEntity().getCode().getCodeSystem());

        assertNotNull(encounter.getParticipants().get(0));
        assertEquals("LOC", encounter.getParticipants().get(0).getTypeCode().getName());
        assertEquals(1, encounter.getParticipants().get(0).getParticipantRole().getTelecoms().size());
        assertEquals(1, encounter.getParticipants().get(0).getParticipantRole().getAddrs().size());

        assertNotNull(encounter.getEntryRelationships().get(0));
        assertEquals("RSON", encounter.getEntryRelationships().get(0).getTypeCode().getName());

        assertNotNull(encounter.getEntryRelationships().get(1));
        assertEquals("COMP", encounter.getEntryRelationships().get(1).getTypeCode().getName());
        Act encounterDiagnosis = encounter.getEntryRelationships().get(1).getAct();
        assertEquals("ACT", encounterDiagnosis.getClassCode().getName());
        assertEquals("EVN", encounterDiagnosis.getMoodCode().getName().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.80", encounterDiagnosis.getTemplateIds().get(0).getRoot());
        assertEquals("29308-4", encounterDiagnosis.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", encounterDiagnosis.getCode().getCodeSystem());
        assertNotNull(encounterDiagnosis.getEntryRelationships().get(0));
        assertEquals("SUBJ", encounterDiagnosis.getEntryRelationships().get(0).getTypeCode().getName());
    }

    private CcdCode createCcdCode(String code, String codeSystem) {
        CcdCode ccdCode = new CcdCode();
        ccdCode.setCode(code);
        ccdCode.setCodeSystem(codeSystem);
        return ccdCode;
    }
}
