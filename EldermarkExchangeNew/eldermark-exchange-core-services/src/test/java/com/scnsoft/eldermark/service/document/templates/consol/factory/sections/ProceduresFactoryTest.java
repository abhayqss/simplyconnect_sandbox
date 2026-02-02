package com.scnsoft.eldermark.service.document.templates.consol.factory.sections;

import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.consol.ProceduresSection;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProceduresFactoryTest {

    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        ProcedureActivity procedureActivityMock = new ProcedureActivity();
        procedureActivityMock.setId((long)1);
        procedureActivityMock.setValue(TestUtil.createCcdCodeMock(
                TestUtil.getRandomString(8), TestUtil.getRandomString(8),
                TestUtil.getRandomString(8), TestUtil.getRandomString(8)));
        procedureActivityMock.setProcedureStarted(new Date());
        procedureActivityMock.setProcedureStopped(new Date());
        procedureActivityMock.setStatusCode("completed");
        procedureActivityMock.setMethodCode(TestUtil.createCcdCodeMock());
        procedureActivityMock.setMoodCode("EVN");
        procedureActivityMock.setPriorityCode(TestUtil.createCcdCodeMock(
                "CSP", "2.16.840.1.113883.5.7", "ActPriority", "Callback placer for scheduling"));

        procedureActivityMock.setProcedureType(TestUtil.createCcdCodeMock(
                "397394009", "2.16.840.1.113883.6.96", "SNOMEDCT", "Bronchoalveolar lavage"));
        procedureActivityMock.setProcedureTypeText(TestUtil.getRandomString(10));

        Medication medicationMock = new Medication();
        procedureActivityMock.setMedication(medicationMock);

        Instructions instructionsMock = new Instructions();
        instructionsMock.setId((long)3);
        procedureActivityMock.setInstructions(instructionsMock);

        Indication indicationMock = new Indication();
        Set<Indication> indicationMocks = new HashSet<>();
        indicationMocks.add(indicationMock);
        procedureActivityMock.setIndications(indicationMocks);

        ProductInstance productInstanceMock = new ProductInstance();
        Set<ProductInstance> productInstanceMocks = new HashSet<>();
        productInstanceMocks.add(productInstanceMock);
        procedureActivityMock.setProductInstances(productInstanceMocks);

        String specimentIdMock = "specimentIdMock";
        Set<String> specimentIdMocks = new HashSet<>();
        specimentIdMocks.add(specimentIdMock);
        procedureActivityMock.setSpecimenIds(specimentIdMocks);

        String encounterIdMock = "encounterIdMock";
        Set<String> encounterIdMocks = new HashSet<>();
        encounterIdMocks.add(encounterIdMock);
        procedureActivityMock.setEncounterIds(encounterIdMocks);

        String bodySiteCodeMock = "bodySiteCodeMock";
        Set<CcdCode> bodySiteCodeMocks = new HashSet<>();
        bodySiteCodeMocks.add(TestUtil.createCcdCodeMock());
        procedureActivityMock.setBodySiteCodes(bodySiteCodeMocks);

        ServiceDeliveryLocation locationMock = new ServiceDeliveryLocation();
        locationMock.setId((long) 3);
        locationMock.setCode(TestUtil.createCcdCodeMock());
        locationMock.setName("ServiceDeliveryLocation_Name");

        CommunityTelecom tel = new CommunityTelecom();
        tel.setId(1L);
        tel.setValue("TelcomValue");
        tel.setUseCode("HP");

        List<CommunityTelecom> telecoms = new ArrayList<>();
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

        Set<ServiceDeliveryLocation> locationMocks = new HashSet<>();
        locationMocks.add(locationMock);
        procedureActivityMock.setServiceDeliveryLocations(locationMocks);

        Community communityMock = new Community();
        communityMock.setId(2L);
        communityMock.setName("PerformerName");
        communityMock.setTelecom(tel);
        communityMock.setAddresses(addrs);
        Set<Community> performersMocks = new HashSet<>();
        performersMocks.add(communityMock);
        procedureActivityMock.setPerformers(performersMocks);

        Set<ProcedureActivity> procedureActivities = new HashSet<>();
        procedureActivities.add(procedureActivityMock);

        Procedure procedureMock = new Procedure();
        procedureMock.setActivities(procedureActivities);

        List<Procedure> procedureMocks = new ArrayList<>();
        procedureMocks.add(procedureMock);

        /*
        daoMock = EasyMock.createMock(ProcedureDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(procedureMocks));
        EasyMock.replay(daoMock);*/

        ProceduresFactory proceduresFactory = new ProceduresFactory();
        // dependency on DAO is not required for parsing anymore
        //proceduresFactory.setProceduresDao(daoMock);

        // 2. test
        final ProceduresSection section = proceduresFactory.buildTemplateInstance(procedureMocks);

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);
        assertEquals("2.16.840.1.113883.10.20.22.2.7.1", section.getTemplateIds().get(0).getRoot());
        assertEquals("47519-4", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        org.eclipse.mdht.uml.cda.Procedure procedure = section.getEntries().get(0).getProcedure();
        assertNotNull(procedure);
        assertEquals("2.16.840.1.113883.10.20.22.4.14", procedure.getTemplateIds().get(0).getRoot());
        assertEquals(procedureActivityMock.getId().toString(), procedure.getIds().get(0).getExtension());
        assertEquals("PROC", procedure.getClassCode().getName());
        assertEquals(procedureActivityMock.getMoodCode(), procedure.getMoodCode().getName());
        assertEquals(procedureActivityMock.getStatusCode(), procedure.getStatusCode().getCode());
        assertEquals("#" + ProcedureActivity.class.getSimpleName() + procedureActivityMock.getId(), procedure.getCode().getOriginalText().getReference().getValue());
        assertEquals(procedureActivityMock.getProcedureType().getCode(), procedure.getCode().getCode());
        assertEquals(procedureActivityMock.getProcedureType().getCodeSystem(), procedure.getCode().getCodeSystem());
        assertEquals(CcdUtils.formatSimpleDate(procedureActivityMock.getProcedureStarted()), procedure.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(procedureActivityMock.getProcedureStopped()), procedure.getEffectiveTime().getHigh().getValue());
        assertEquals(procedureActivityMock.getPriorityCode().getCode(), procedure.getPriorityCode().getCode());
        assertEquals(procedureActivityMock.getPriorityCode().getCodeSystem(), procedure.getPriorityCode().getCodeSystem());
        assertEquals(procedureActivityMock.getPriorityCode().getDisplayName(), procedure.getPriorityCode().getDisplayName());
        assertEquals(procedureActivityMock.getMethodCode().getCode(), procedure.getMethodCodes().get(0).getCode());
        CcdCode targetSiteCode = new ArrayList<>(procedureActivityMock.getBodySiteCodes()).get(0);
        assertEquals(targetSiteCode.getCode(), procedure.getTargetSiteCodes().get(0).getCode());
        assertEquals(targetSiteCode.getCodeSystem(), procedure.getTargetSiteCodes().get(0).getCodeSystem());
        assertEquals(new ArrayList(procedureActivityMock.getSpecimenIds()).get(0), procedure.getSpecimens().get(0).getSpecimenRole().getIds().get(0).getExtension());
        assertEquals(1, procedure.getPerformers().size());
        assertEquals("DEV", procedure.getParticipants().get(0).getTypeCode().getName());
        assertNotNull(procedure.getParticipants().get(0).getParticipantRole());
        assertEquals("LOC", procedure.getParticipants().get(1).getTypeCode().getName());
        var locationParticipantRole = procedure.getParticipants().get(1).getParticipantRole();
        assertNotNull(locationParticipantRole);
        assertEquals(1, locationParticipantRole.getTelecoms().size());
        TestUtil.assertTelecom(tel, locationParticipantRole.getTelecoms().get(0));
        assertEquals(1, locationParticipantRole.getAddrs().size());
        TestUtil.assertAddresses(addr, locationParticipantRole.getAddrs().get(0));
        assertNotNull("COMP", procedure.getEntryRelationships().get(0).getTypeCode().getName());
        assertEquals(true, procedure.getEntryRelationships().get(0).getInversionInd().booleanValue());
        assertNotNull(procedure.getEntryRelationships().get(0).getEncounter());
        assertEquals("ENC", procedure.getEntryRelationships().get(0).getEncounter().getClassCode().getName());
        assertEquals("EVN", procedure.getEntryRelationships().get(0).getEncounter().getMoodCode().getName());
        assertEquals(new ArrayList(procedureActivityMock.getEncounterIds()).get(0), procedure.getEntryRelationships().get(0).getEncounter().getIds().get(0).getExtension());
        assertNotNull("SUBJ", procedure.getEntryRelationships().get(1).getTypeCode().getName());
        assertEquals(true, procedure.getEntryRelationships().get(1).getInversionInd().booleanValue());
        assertNotNull(procedure.getEntryRelationships().get(1).getAct());
        assertNotNull("RSON", procedure.getEntryRelationships().get(2).getTypeCode().getName());
        assertNotNull(procedure.getEntryRelationships().get(2).getObservation());
        assertNotNull("COMP", procedure.getEntryRelationships().get(3).getTypeCode().getName());
        assertNotNull(procedure.getEntryRelationships().get(3).getSubstanceAdministration());
    }
}
