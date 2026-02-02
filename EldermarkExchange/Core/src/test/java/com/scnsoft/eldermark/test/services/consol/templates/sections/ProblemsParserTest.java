package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.ProblemsParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ProblemSection;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProblemsParserTest {

    @Mock
    private ObservationFactory observationFactory;
    @InjectMocks
    private ProblemsParser problemsParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final ProblemSection section = ConsolFactory.eINSTANCE.createProblemSection();

        /*when(observationFactory.parseProblemObservation(any(), any(), any())).thenReturn(new ProblemObservation(Long.valueOf(1)));
        */

        final List<Problem> result = problemsParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<Problem>asList(new Problem(Long.valueOf(1))), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme