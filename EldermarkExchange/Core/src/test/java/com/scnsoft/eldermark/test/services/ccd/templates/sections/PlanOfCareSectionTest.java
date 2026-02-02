package com.scnsoft.eldermark.test.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Instructions;
import com.scnsoft.eldermark.entity.PlanOfCare;
import com.scnsoft.eldermark.entity.PlanOfCareActivity;
import com.scnsoft.eldermark.services.ccd.templates.sections.PlanOfCareFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.test.util.TestUtil;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.eclipse.mdht.uml.hl7.vocab.x_DocumentActMood;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ccd.PlanOfCareSection;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlanOfCareSectionTest {
    private static final long RESIDENT_ID = 49L;

    private Random random = new Random();

    @Test
    public void testBuildingTemplate() {

        PlanOfCare planOfCareMock = new PlanOfCare();

        planOfCareMock.setPlanOfCareActivityActList(generatePlanOfCareActivityList());
        planOfCareMock.setPlanOfCareActivityEncounterList(generatePlanOfCareActivityList());
        planOfCareMock.setPlanOfCareActivityObservationList(generatePlanOfCareActivityList());
        planOfCareMock.setPlanOfCareActivityProcedureList(generatePlanOfCareActivityList());
        planOfCareMock.setPlanOfCareActivitySubstanceAdministrationList(generatePlanOfCareActivityList());
        planOfCareMock.setPlanOfCareActivitySupplyList(generatePlanOfCareActivityList());

        List<Instructions>instructionsList = new ArrayList<>();


        for (int i=0; i<3; i++) {
            Instructions instructions = new Instructions();
            CcdCode ccdCode = new CcdCode();
            ccdCode.setCode(TestUtil.getRandomString(5));
            instructions.setCode(ccdCode);
            instructions.setId(random.nextLong());
            instructions.setText(TestUtil.getRandomString(15));
            instructionsList.add(instructions);
        }
        planOfCareMock.setInstructions(instructionsList);

        /*
        daoMock = EasyMock.createMock(PlanOfCareActivityDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(Collections.singleton(planOfCareMock));
        EasyMock.replay(daoMock);*/


        PlanOfCareFactory planOfCareFactory = new PlanOfCareFactory();
        // dependency on DAO is not required for parsing anymore
        //planOfCareFactory.setPlanOfCareDao(daoMock);

        // 2. test
        final PlanOfCareSection section = planOfCareFactory.buildTemplateInstance(Collections.singleton(planOfCareMock));

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.1.10", section.getTemplateIds().get(0).getRoot());

        assertEquals("18776-5", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());
        assertEquals("LOINC", section.getCode().getCodeSystemName());
        assertEquals("Plan of Care".toLowerCase(), section.getCode().getDisplayName().toLowerCase());

        List<Act> planOfCareActivityActList = section.getActs();
        assertEquals(planOfCareMock.getPlanOfCareActivityActList().size() + planOfCareMock.getInstructions().size(), planOfCareActivityActList.size());

        for (PlanOfCareActivity planOfCareActivityMock : planOfCareMock.getPlanOfCareActivityActList()) {
            Act planOfCareActivityAct = getActById(planOfCareActivityMock.getId(), planOfCareActivityActList);
            assertNotNull(planOfCareActivityAct);
            assertEquals("ACT", planOfCareActivityAct.getClassCode().getName());
            assertEquals(planOfCareActivityMock.getMoodCode(), planOfCareActivityAct.getMoodCode().getName());
            assertEquals("2.16.840.1.113883.10.20.22.4.39", planOfCareActivityAct.getTemplateIds().get(0).getRoot());
            assertEquals(planOfCareActivityMock.getId().toString(), planOfCareActivityAct.getIds().get(0).getExtension());

            if (planOfCareActivityMock.getCode() != null) {
                assertEquals(planOfCareActivityMock.getCode().getCode(),planOfCareActivityAct.getCode().getCode());
                assertEquals(planOfCareActivityMock.getCode().getCodeSystem(),planOfCareActivityAct.getCode().getCodeSystem());
            }

            if (planOfCareActivityMock.getEffectiveTime()!= null) {
                assertEquals(CcdUtils.formatSimpleDate(planOfCareActivityMock.getEffectiveTime()), planOfCareActivityAct.getEffectiveTime().getCenter().getValue());
            }

        }

        List<Encounter> planOfCareActivityEncounterList = section.getEncounters();
        assertEquals(planOfCareMock.getPlanOfCareActivityEncounterList().size(), planOfCareActivityEncounterList.size());

        for (PlanOfCareActivity planOfCareActivityMock : planOfCareMock.getPlanOfCareActivityEncounterList()) {
            Encounter planOfCareActivityEncounter = getEncounterById(planOfCareActivityMock.getId(), planOfCareActivityEncounterList);
            assertNotNull(planOfCareActivityEncounter);
            assertEquals("ENC", planOfCareActivityEncounter.getClassCode().getName());
            assertEquals(planOfCareActivityMock.getMoodCode(), planOfCareActivityEncounter.getMoodCode().getName());
            assertEquals("2.16.840.1.113883.10.20.22.4.40", planOfCareActivityEncounter.getTemplateIds().get(0).getRoot());
            assertEquals(planOfCareActivityMock.getId().toString(), planOfCareActivityEncounter.getIds().get(0).getExtension());

        }


        List<Observation> planOfCareActivityObservationList = section.getObservations();
        assertEquals(planOfCareMock.getPlanOfCareActivityObservationList().size(), planOfCareActivityObservationList.size());

        for (PlanOfCareActivity planOfCareActivityMock : planOfCareMock.getPlanOfCareActivityObservationList()) {
            Observation planOfCareActivityObservation = TestUtil.getObservationById(planOfCareActivityMock.getId(), planOfCareActivityObservationList);
            assertNotNull(planOfCareActivityObservation);
            assertEquals("OBS", planOfCareActivityObservation.getClassCode().getName());
            assertEquals(planOfCareActivityMock.getMoodCode(), planOfCareActivityObservation.getMoodCode().getName());
            assertEquals("2.16.840.1.113883.10.20.22.4.44", planOfCareActivityObservation.getTemplateIds().get(0).getRoot());
            assertEquals(planOfCareActivityMock.getId().toString(), planOfCareActivityObservation.getIds().get(0).getExtension());

            if (planOfCareActivityMock.getCode() != null) {
                assertEquals(planOfCareActivityMock.getCode().getCode(),planOfCareActivityObservation.getCode().getCode());
                assertEquals(planOfCareActivityMock.getCode().getCodeSystem(),planOfCareActivityObservation.getCode().getCodeSystem());
            }

            if (planOfCareActivityMock.getEffectiveTime()!= null) {
                assertEquals(CcdUtils.formatSimpleDate(planOfCareActivityMock.getEffectiveTime()), planOfCareActivityObservation.getEffectiveTime().getCenter().getValue());
            }

        }

        List<Procedure> planOfCareActivityProcedureList = section.getProcedures();
        assertEquals(planOfCareMock.getPlanOfCareActivityProcedureList().size(), planOfCareActivityProcedureList.size());

        for (PlanOfCareActivity planOfCareActivityMock : planOfCareMock.getPlanOfCareActivityProcedureList()) {
            Procedure planOfCareActivityProcedure = getProcedureById(planOfCareActivityMock.getId(), planOfCareActivityProcedureList);
            assertNotNull(planOfCareActivityProcedure);
            assertEquals("PROC", planOfCareActivityProcedure.getClassCode().getName());
            assertEquals(planOfCareActivityMock.getMoodCode(), planOfCareActivityProcedure.getMoodCode().getName());
            assertEquals("2.16.840.1.113883.10.20.22.4.41", planOfCareActivityProcedure.getTemplateIds().get(0).getRoot());
            assertEquals(planOfCareActivityMock.getId().toString(), planOfCareActivityProcedure.getIds().get(0).getExtension());

            if (planOfCareActivityMock.getCode() != null) {
                assertEquals(planOfCareActivityMock.getCode().getCode(),planOfCareActivityProcedure.getCode().getCode());
                assertEquals(planOfCareActivityMock.getCode().getCodeSystem(),planOfCareActivityProcedure.getCode().getCodeSystem());
            }

            if (planOfCareActivityMock.getEffectiveTime()!= null) {
                assertEquals(CcdUtils.formatSimpleDate(planOfCareActivityMock.getEffectiveTime()), planOfCareActivityProcedure.getEffectiveTime().getCenter().getValue());
            }

        }


        List<SubstanceAdministration> planOfCareActivitySubstanceAdministrations = section.getSubstanceAdministrations();
        assertEquals(planOfCareMock.getPlanOfCareActivitySubstanceAdministrationList().size(), planOfCareActivitySubstanceAdministrations.size());

        for (PlanOfCareActivity planOfCareActivityMock : planOfCareMock.getPlanOfCareActivitySubstanceAdministrationList()) {
            SubstanceAdministration planOfCareActivitySubstanceAdministration = getSubstanceAdministrationById(planOfCareActivityMock.getId(), planOfCareActivitySubstanceAdministrations);
            assertNotNull(planOfCareActivitySubstanceAdministration);
            assertEquals("SBADM", planOfCareActivitySubstanceAdministration.getClassCode().getName());
            assertEquals(planOfCareActivityMock.getMoodCode(), planOfCareActivitySubstanceAdministration.getMoodCode().getName());
            assertEquals("2.16.840.1.113883.10.20.22.4.42", planOfCareActivitySubstanceAdministration.getTemplateIds().get(0).getRoot());
            assertEquals(planOfCareActivityMock.getId().toString(), planOfCareActivitySubstanceAdministration.getIds().get(0).getExtension());

            CE code = planOfCareActivitySubstanceAdministration.getConsumable().getManufacturedProduct().getManufacturedLabeledDrug().getCode();
            if (planOfCareActivityMock.getCode() != null) {
                assertEquals(planOfCareActivityMock.getCode().getCode(),code.getCode());
                assertEquals(planOfCareActivityMock.getCode().getCodeSystem(),code.getCodeSystem());
            }
            else {
                assertEquals(NullFlavor.NI,code.getNullFlavor());
            }

        }

        List<Supply> planOfCareActivitySupplies = section.getSupplies();
        assertEquals(planOfCareMock.getPlanOfCareActivitySupplyList().size(), planOfCareActivitySupplies.size());

        for (PlanOfCareActivity planOfCareActivityMock : planOfCareMock.getPlanOfCareActivitySupplyList()) {
            Supply planOfCareActivitySupply = getSupplyById(planOfCareActivityMock.getId(), planOfCareActivitySupplies);
            assertNotNull(planOfCareActivitySupply);
            assertEquals("SPLY", planOfCareActivitySupply.getClassCode().getName());
            assertEquals(planOfCareActivityMock.getMoodCode(), planOfCareActivitySupply.getMoodCode().getName());
            assertEquals("2.16.840.1.113883.10.20.22.4.43", planOfCareActivitySupply.getTemplateIds().get(0).getRoot());
            assertEquals(planOfCareActivityMock.getId().toString(), planOfCareActivitySupply.getIds().get(0).getExtension());

        }

        List<Act> acts = section.getActs();
        //assertEquals(planOfCareMock.getPlanOfCareActivityActList().size(), planOfCareActivityActList.size());

        for (Instructions instructionsMock : planOfCareMock.getInstructions()) {
            Act instruction = getActByCode(instructionsMock.getCode().getCode(), acts);
            assertNotNull(instruction);
            //assertEquals(planOfCareActivityMock.getId().toString(), planOfCareActivityAct.getIds().get(0).getExtension());
            assertEquals("ACT", instruction.getClassCode().getName());
            assertEquals("INT", instruction.getMoodCode().getName());
            assertEquals("2.16.840.1.113883.10.20.22.4.20", instruction.getTemplateIds().get(0).getRoot());
            assertEquals(instructionsMock.getCode().getCode(),instruction.getCode().getCode());
            //assertEquals("",instruction.getText().getText()); TODO
            assertEquals("completed",instruction.getStatusCode().getCode());

        }


    }

    private Supply getSupplyById(Long id, List<Supply> planOfCareActivitySupplies) {
        for (Supply supply : planOfCareActivitySupplies) {
            II adId = supply.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return supply;
            }
        }
        return null;
    }

    private SubstanceAdministration getSubstanceAdministrationById(Long id, List<SubstanceAdministration> substanceAdministrations) {
        for (SubstanceAdministration substanceAdministration : substanceAdministrations) {
            II adId = substanceAdministration.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return substanceAdministration;
            }
        }
        return null;
    }

    private List<PlanOfCareActivity> generatePlanOfCareActivityList() {
        List<PlanOfCareActivity> planOfCareActivityMockList = new ArrayList<>();
        int n = 1 + random.nextInt(3);
        for (int i = 0; i < n; i++) {
            PlanOfCareActivity planOfCareActivity = new PlanOfCareActivity();
            planOfCareActivity.setId(random.nextLong());
            int rndInt = random.nextInt(4)+4;
            //if (rndInt == 2) rndInt++;
            planOfCareActivity.setMoodCode(x_DocumentActMood.get(rndInt).toString());
            planOfCareActivityMockList.add(planOfCareActivity);

            if (i==0) {
                planOfCareActivity.setCode(TestUtil.createCcdCodeMock());
                planOfCareActivity.setEffectiveTime(new Date());
            }
        }
        return planOfCareActivityMockList;
    }

    public static Encounter getEncounterById(Long id, List<Encounter> encounters) {
        for (Encounter encounter : encounters) {
            II adId = encounter.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return encounter;
            }
        }
        return null;
    }

    public static Procedure getProcedureById(Long id, List<Procedure> procedures) {
        for (Procedure procedure : procedures) {
            II adId = procedure.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return procedure;
            }
        }
        return null;
    }

    public static Act getActById(Long id, List<Act> acts) {
        for (Act act : acts) {
            if (CollectionUtils.isEmpty(act.getIds())) continue;
            II adId = act.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return act;
            }
        }
        return null;
    }

    public static Act getActByCode(String code, List<Act> acts) {
        for (Act act : acts) {
            if (!CollectionUtils.isEmpty(act.getIds())) continue;
            CD cd = act.getCode();
            if (cd.getCode().equals(code)) {
                return act;
            }
        }
        return null;
    }

}

