package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.PlanOfCare;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.InstructionsFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PlanOfCareActivityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.PlanOfCareSection;

import java.util.List;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class PlanOfCareParserTest {
    @Mock
    private InstructionsFactory instructionsFactory;
    @Mock
    private PlanOfCareActivityFactory planOfCareActivityFactory;
    @InjectMocks
    private PlanOfCareParser planOfCareParser;

    private final Random random = new Random();
    private final Organization organization = new Organization();
    private final Client resident = new Client(random.nextLong());
    {
        organization.setId(random.nextLong());
        resident.setOrganization(organization);
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