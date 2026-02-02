package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.AdvanceDirective;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import org.eclipse.mdht.uml.cda.Observation;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.AdvanceDirectivesSection;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;

import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class AdvanceDirectiveParserTest {

    @Mock
    private ObservationFactory observationFactory;
    @InjectMocks
    private AdvanceDirectiveParser advanceDirectiveParser;

    private final Random random = new Random();
    private final Organization database = new Organization();
    private final Client resident = new Client(random.nextLong());

    {
        database.setId(random.nextLong());
        resident.setOrganization(database);
    }

    @Test
    public void testParseSection() throws Exception {
        final AdvanceDirective mockAdvanceDirective = new AdvanceDirective();
        CcdCode ccdcode1 = new CcdCode();
        ccdcode1.setDisplayName("Test1");
        mockAdvanceDirective.setType(ccdcode1);
        mockAdvanceDirective.setId(random.nextLong());
        final AdvanceDirective mockAdvanceDirective2 = new AdvanceDirective();
        mockAdvanceDirective2.setId(random.nextLong());
        CcdCode ccdcode2 = new CcdCode();
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