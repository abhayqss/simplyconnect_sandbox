package com.scnsoft.eldermark.test.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.services.ccd.templates.sections.ProblemsFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.PQ;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ccd.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ProblemSectionTest {

    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        Problem problemMock = new Problem();
        problemMock.setId(1L);
        problemMock.setStatusCode("problemStatusCode");
        problemMock.setTimeLow(new Date());
        problemMock.setTimeHigh(new Date());

        com.scnsoft.eldermark.entity.ProblemObservation problemObservationMock = new com.scnsoft.eldermark.entity.ProblemObservation();
        problemObservationMock.setId(1L);
        problemObservationMock.setProblem(problemMock);
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

        List<com.scnsoft.eldermark.entity.ProblemObservation> problemObservationMocks = new ArrayList<>();
        problemObservationMocks.add(problemObservationMock);
        problemMock.setProblemObservations(problemObservationMocks);

        List<Problem>problems = new ArrayList<>();
        problems.add(problemMock);

        /*daoMock = EasyMock.createMock(ProblemDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(problems));
        EasyMock.replay(daoMock);*/


        ProblemsFactory problemsFactory = new ProblemsFactory();
        // dependency on DAO is not required for parsing anymore
        //problemsFactory.setProblemDao(daoMock);

        // 2. test
        final ProblemSection section = problemsFactory.buildTemplateInstance(problems);

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);
        assertEquals("2.16.840.1.113883.10.20.1.11", section.getTemplateIds().get(0).getRoot());
        assertEquals("11450-4", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        List<ProblemAct> problemActs = section.getProblemActs();
        assertEquals(problems.size(), problemActs.size());

        // problem problem act
        ProblemAct problemAct = problemActs.get(0);
        assertEquals("ACT", problemAct.getClassCode().getName());
        assertEquals("EVN", problemAct.getMoodCode().getName());
        assertEquals("2.16.840.1.113883.10.20.22.4.3", problemAct.getTemplateIds().get(0).getRoot());
        assertEquals("CONC", problemAct.getCode().getCode());
        assertEquals("2.16.840.1.113883.5.6", problemAct.getCode().getCodeSystem());
        assertEquals(problemMock.getId().toString(), problemAct.getIds().get(0).getExtension());
        assertEquals(problemMock.getStatusCode(), problemAct.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(problemMock.getTimeLow()), problemAct.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(problemMock.getTimeHigh()), problemAct.getEffectiveTime().getHigh().getValue());

        // problem observation
        List<Observation> problemObservations = getSubList(problemAct.getObservations(), ProblemObservation.class);
        assertEquals(problemObservationMocks.size(), problemObservations.size());
        assertEquals("SUBJ", problemAct.getEntryRelationships().get(0).getTypeCode().getName());

        ProblemObservation problemObservation = (ProblemObservation) problemObservations.get(0);
        assertEquals("OBS", problemObservation.getClassCode().toString());
        assertEquals("EVN", problemObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.4", problemObservation.getTemplateIds().get(0).getRoot());
        assertEquals("completed", problemObservation.getStatusCode().getCode());
        assertEquals(problemObservationMock.getId().toString(), problemObservation.getIds().get(0).getExtension());
        String refId = ProblemObservation.class.getSimpleName() + problemObservationMock.getId();
//        assertTrue(section.getText().getText().contains(String.format("<content ID=\"%s\">%s</content>", refId, problemObservationMock.getProblemName())));
        assertEquals("#" + refId, problemObservation.getText().getReference().getValue());
        assertEquals(CcdUtils.formatSimpleDate(problemObservationMock.getProblemDateTimeLow()), problemObservation.getEffectiveTime().getLow().getValue());
        assertEquals(CcdUtils.formatSimpleDate(problemObservationMock.getProblemDateTimeHigh()), problemObservation.getEffectiveTime().getHigh().getValue());
        assertEquals(problemObservationMock.getProblemCode().getCode(), ((CD) problemObservation.getValues().get(0)).getCode());
        assertEquals(problemObservationMock.getProblemCode().getCodeSystem(), ((CD) problemObservation.getValues().get(0)).getCodeSystem());
        assertEquals(problemObservationMock.getProblemType().getCode(), problemObservation.getCode().getCode());
        assertEquals(problemObservationMock.getProblemType().getCodeSystem(), problemObservation.getCode().getCodeSystem());

        // age observation
        List<Observation> ageObservations = getSubList(problemObservation.getObservations(), AgeObservation.class);
        assertEquals(1, ageObservations.size());
        assertEquals("SUBJ", problemObservation.getEntryRelationships().get(0).getTypeCode().getName());
        assertEquals(Boolean.TRUE, problemObservation.getEntryRelationships().get(0).getInversionInd());

        AgeObservation ageObservation = (AgeObservation) ageObservations.get(0);
        assertEquals("OBS", ageObservation.getClassCode().toString());
        assertEquals("EVN", ageObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.31", ageObservation.getTemplateIds().get(0).getRoot());
        assertEquals("completed", ageObservation.getStatusCode().getCode());
        assertEquals("445518008", ageObservation.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.96", ageObservation.getCode().getCodeSystem());
        assertEquals(problemObservationMock.getAgeObservationUnit(), ((PQ) ageObservation.getValues().get(0)).getUnit());
        assertEquals(problemObservationMock.getAgeObservationValue().toString(), ((PQ) ageObservation.getValues().get(0)).getValue().toString());

        // health status observation
        List<Observation> healthStatusObservations = getSubList(problemObservation.getObservations(), ProblemHealthStatusObservation.class);
        assertEquals(1, healthStatusObservations.size());
        assertEquals("REFR", problemObservation.getEntryRelationships().get(2).getTypeCode().getName());

        ProblemHealthStatusObservation healthStatusObservation = (ProblemHealthStatusObservation) healthStatusObservations.get(0);
        assertEquals("OBS", healthStatusObservation.getClassCode().toString());
        assertEquals("EVN", healthStatusObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.5", healthStatusObservation.getTemplateIds().get(0).getRoot());
        assertEquals("completed", healthStatusObservation.getStatusCode().getCode());
        assertEquals("11323-3", healthStatusObservation.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", healthStatusObservation.getCode().getCodeSystem());
        assertEquals(problemObservationMock.getHealthStatusCode().getCode(), ((CD) healthStatusObservation.getValues().get(0)).getCode());
        assertEquals(problemObservationMock.getHealthStatusCode().getCodeSystem(), ((CD) healthStatusObservation.getValues().get(0)).getCodeSystem());
        assertEquals(problemObservationMock.getHealthStatusObservationText(), healthStatusObservation.getText().getText());

        // problem status
        List<Observation> problemStatusObservations = getSubList(problemObservation.getObservations(), ProblemStatusObservation.class);
        assertEquals(1, problemStatusObservations.size());
        assertEquals("REFR", problemObservation.getEntryRelationships().get(1).getTypeCode().getName());

        ProblemStatusObservation problemStatusObservation = (ProblemStatusObservation) problemStatusObservations.get(0);
        assertEquals("OBS", problemStatusObservation.getClassCode().toString());
        assertEquals("EVN", problemStatusObservation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.6", problemStatusObservation.getTemplateIds().get(0).getRoot());
        assertEquals("completed", problemStatusObservation.getStatusCode().getCode());
        assertEquals("33999-4", problemStatusObservation.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", problemStatusObservation.getCode().getCodeSystem());
        assertEquals(problemObservationMock.getProblemStatusCode().getCode(), ((CD) problemStatusObservation.getValues().get(0)).getCode());
        assertEquals(problemObservationMock.getProblemStatusCode().getCodeSystem(), ((CD) problemStatusObservation.getValues().get(0)).getCodeSystem());
        refId = ProblemObservation.class.getSimpleName() + "Status" + problemObservationMock.getId();
//        assertTrue(section.getText().getText().contains(String.format("<content ID=\"%s\">%s</content>", refId, problemObservationMock.getProblemStatusText())));
        assertEquals("#" + refId, problemStatusObservation.getText().getReference().getValue());
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

    private CcdCode createCcdCode(String code, String codeSystem) {
        CcdCode ccdCode = new CcdCode();
        ccdCode.setCode(code);
        ccdCode.setCodeSystem(codeSystem);
        return ccdCode;
    }
}
