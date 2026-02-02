package com.scnsoft.eldermark.test.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.ccd.templates.sections.ProceduresFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.test.util.TestUtil;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ccd.ProceduresSection;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProcedureSectionTest {

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

        OrganizationTelecom tel = new OrganizationTelecom();
        tel.setId(1L);
        tel.setValue("TelcomValue");
        tel.setUseCode("HP");

        List<OrganizationTelecom> telecoms = new ArrayList<>();
        locationMock.setTelecoms(telecoms);

        OrganizationAddress addr = new OrganizationAddress();
        addr.setPostalCode("PostalCode");
        addr.setStreetAddress("StreetAddress");
        addr.setCity("City");
        addr.setState("State");
        addr.setCountry("Country");

        List<OrganizationAddress> addrs = new ArrayList<>();
        addrs.add(addr);
        locationMock.setAddresses(addrs);

        Set<ServiceDeliveryLocation> locationMocks = new HashSet<>();
        locationMocks.add(locationMock);
        procedureActivityMock.setServiceDeliveryLocations(locationMocks);

        Organization organizationMock = new Organization();
        organizationMock.setId(2L);
        organizationMock.setName("PerformerName");
        organizationMock.setTelecom(tel);
        organizationMock.setAddresses(addrs);
        Set<Organization> performersMocks = new HashSet<>();
        performersMocks.add(organizationMock);
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
        assertEquals("2.16.840.1.113883.10.20.1.12", section.getTemplateIds().get(0).getRoot());
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
        assertNotNull(procedure.getParticipants().get(1).getParticipantRole());
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
