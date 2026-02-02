package com.scnsoft.eldermark.test.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.Author;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Result;
import com.scnsoft.eldermark.entity.ResultObservation;
import com.scnsoft.eldermark.services.ccd.templates.sections.ResultsFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.test.util.TestUtil;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.cda.Organizer;
import org.eclipse.mdht.uml.hl7.datatypes.PQ;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.ccd.ResultsSection;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ResultsSectionTest {

    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() {
        // 1. init
        Result resultMock = new Result();
        resultMock.setId(2L);
        resultMock.setCode(TestUtil.createCcdCodeMock());
        resultMock.setStatusCode("resultStatusCode");
        resultMock.setClassCode("BATTERY");

        ResultObservation resultObservationMock = new ResultObservation();
        resultObservationMock.setText("ResultObservationTest");
        resultObservationMock.setStatusCode("ResultObservationStatusCode");
        resultObservationMock.setId(4L);
        resultObservationMock.setEffectiveTime(new Date());
        resultObservationMock.setValue(5);
        resultObservationMock.setValueUnit("valueUnit");
        resultObservationMock.setTargetSiteCode(TestUtil.createCcdCodeMock());
        resultObservationMock.setResultTypeCode(TestUtil.createCcdCodeMock());
        List<CcdCode> interpretationCodes = new ArrayList<>();
        interpretationCodes.add(TestUtil.createCcdCodeMock());
        resultObservationMock.setInterpretationCodes(interpretationCodes);
        List<String> referenceRanges = new ArrayList<>();
        referenceRanges.add("referenceRange");
        resultObservationMock.setReferenceRanges(referenceRanges);
        resultObservationMock.setAuthor(new Author());
        resultObservationMock.setMethodCode(TestUtil.createCcdCodeMock());
        resultObservationMock.setTargetSiteCode(TestUtil.createCcdCodeMock());

        List<ResultObservation> resultObservationMocks = new ArrayList<>();
        resultObservationMocks.add(resultObservationMock);
        resultMock.setObservations(resultObservationMocks);

        List<Result> resultMocks = new ArrayList<>();
        resultMocks.add(resultMock);

        /*
        daoMock = EasyMock.createMock(ResultDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<Result>(resultMocks));
        EasyMock.replay(daoMock);*/

        ResultsFactory resultsFactory = new ResultsFactory();
        // dependency on DAO is not required for parsing anymore
        //resultsFactory.setResultDao(daoMock);

        // 2. test
        final ResultsSection section = resultsFactory.buildTemplateInstance(resultMocks);

        // 3. verify
        assertNotNull(section);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.1.14", section.getTemplateIds().get(0).getRoot());
        assertEquals("30954-2", section.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", section.getCode().getCodeSystem());

        // test Result Organizer
        Organizer organizer = section.getEntries().get(0).getOrganizer();
        assertNotNull(organizer);
        assertEquals(resultMock.getClassCode(), organizer.getClassCode().getName());
        assertEquals("EVN", organizer.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.1", organizer.getTemplateIds().get(0).getRoot());
        assertEquals(resultMock.getId().toString(), organizer.getIds().get(0).getExtension());
        assertEquals(resultMock.getStatusCode(), organizer.getStatusCode().getCode());
        assertEquals(resultMock.getCode().getCode(), organizer.getCode().getCode());
        assertEquals(resultMock.getCode().getCodeSystem(), organizer.getCode().getCodeSystem());

        // test Result Observation
        Observation observation = organizer.getComponents().get(0).getObservation();
        assertNotNull(observation);
        assertEquals("OBS", observation.getClassCode().toString());
        assertEquals("EVN", observation.getMoodCode().toString());
        assertEquals("2.16.840.1.113883.10.20.22.4.2", observation.getTemplateIds().get(0).getRoot());
        assertEquals(resultObservationMock.getId().toString(), observation.getIds().get(0).getExtension());
        assertEquals(resultObservationMock.getResultTypeCode().getCode(), observation.getCode().getCode());
        assertEquals(resultObservationMock.getResultTypeCode().getCodeSystem(), observation.getCode().getCodeSystem());
        assertEquals("#" + ResultObservation.class.getSimpleName() + resultObservationMock.getId(), observation.getCode().getOriginalText().getReference().getValue());
//        assertTrue(section.getText().getText().contains(resultObservationMock.getText()));
        assertEquals(resultObservationMock.getStatusCode(), observation.getStatusCode().getCode());
        assertEquals(CcdUtils.formatSimpleDate(resultObservationMock.getEffectiveTime()), observation.getEffectiveTime().getCenter().getValue());
        assertEquals(resultObservationMock.getValue().intValue(), ((PQ) observation.getValues().get(0)).getValue().intValue());
        assertEquals(resultObservationMock.getValueUnit(), ((PQ) observation.getValues().get(0)).getUnit());
        assertEquals(resultObservationMock.getMethodCode().getCode(), (observation.getMethodCodes().get(0).getCode()));
        assertEquals(resultObservationMock.getTargetSiteCode().getCode(), (observation.getTargetSiteCodes().get(0).getCode()));
        assertNotNull(observation.getAuthors().get(0));
        assertEquals(resultObservationMock.getReferenceRanges().get(0), observation.getReferenceRanges().get(0).getObservationRange().getText().getText());
        assertEquals(resultObservationMock.getInterpretationCodes().get(0).getCode(), observation.getInterpretationCodes().get(0).getCode());
    }
}
