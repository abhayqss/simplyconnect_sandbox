package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.InstructionsFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PlanOfCareActivityFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.PlanOfCareParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.PlanOfCareSection;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlanOfCareParserTest {
    @Mock
    private InstructionsFactory instructionsFactory;
    @Mock
    private PlanOfCareActivityFactory planOfCareActivityFactory;
    @InjectMocks
    private PlanOfCareParser planOfCareParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final PlanOfCareSection section = ConsolFactory.eINSTANCE.createPlanOfCareSection();

        /*when(instructionsFactory.parseInstructions(any(), any())).thenReturn(Arrays.<Instructions>asList(new Instructions(Long.valueOf(1))));
        when(planOfCareActivityFactory.parseSupplies(any(), any())).thenReturn(Arrays.<PlanOfCareActivity>asList(new PlanOfCareActivity(Long.valueOf(1))));
        when(planOfCareActivityFactory.parseSubstanceAdministrations(any(), any())).thenReturn(Arrays.<PlanOfCareActivity>asList(new PlanOfCareActivity(Long.valueOf(1))));
        when(planOfCareActivityFactory.parseProcedures(any(), any())).thenReturn(Arrays.<PlanOfCareActivity>asList(new PlanOfCareActivity(Long.valueOf(1))));
        when(planOfCareActivityFactory.parseEncounters(any(), any())).thenReturn(Arrays.<PlanOfCareActivity>asList(new PlanOfCareActivity(Long.valueOf(1))));
        when(planOfCareActivityFactory.parseObservations(any(), any())).thenReturn(Arrays.<PlanOfCareActivity>asList(new PlanOfCareActivity(Long.valueOf(1))));
        when(planOfCareActivityFactory.parseActs(any(), any())).thenReturn(Arrays.<PlanOfCareActivity>asList(new PlanOfCareActivity(Long.valueOf(1))));
        */

        final List<PlanOfCare> result = planOfCareParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<PlanOfCare>asList(new PlanOfCare(Long.valueOf(1))), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme