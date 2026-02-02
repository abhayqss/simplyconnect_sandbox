package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.AdvanceDirective;
import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.AdvanceDirectiveParser;
import org.eclipse.mdht.uml.cda.Observation;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.AdvanceDirectivesSection;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class AdvanceDirectiveParserTest {
    @Mock
    private ObservationFactory observationFactory;
    @InjectMocks
    private AdvanceDirectiveParser advanceDirectiveParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }

    @Test
    public void testParseSection() throws Exception {
        final AdvanceDirective mockAdvanceDirective = new AdvanceDirective();
        CcdCode ccdcode1= new CcdCode();
        ccdcode1.setDisplayName("Test1");
        mockAdvanceDirective.setType(ccdcode1);
        mockAdvanceDirective.setId(random.nextLong());
        final AdvanceDirective mockAdvanceDirective2 = new AdvanceDirective();
        mockAdvanceDirective2.setId(random.nextLong());
        CcdCode ccdcode2= new CcdCode();
        ccdcode2.setDisplayName("Test2");
        mockAdvanceDirective2.setType(ccdcode2);

        final AdvanceDirectivesSection section = ConsolFactory.eINSTANCE.createAdvanceDirectivesSection();
        final Observation ccdObservation = ConsolFactory.eINSTANCE.createAdvanceDirectiveObservation();
        final Observation ccdObservation2 = ConsolFactory.eINSTANCE.createAdvanceDirectiveObservation();
        section.addObservation(ccdObservation);
        section.addObservation(ccdObservation2);

        when(observationFactory.parseAdvanceDirective(eq(ccdObservation), eq(resident), anyString())).thenReturn(mockAdvanceDirective);
        when(observationFactory.parseAdvanceDirective(eq(ccdObservation2), eq(resident), anyString())).thenReturn(mockAdvanceDirective2);

        final List<AdvanceDirective> result = advanceDirectiveParser.parseSection(resident, section);

        assertThat(result, Matchers.hasSize(2));
        assertThat(result, containsInAnyOrder(mockAdvanceDirective, mockAdvanceDirective2));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme