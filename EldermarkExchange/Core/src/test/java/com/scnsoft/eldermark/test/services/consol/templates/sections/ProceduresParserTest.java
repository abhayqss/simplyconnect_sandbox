package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Procedure;
import com.scnsoft.eldermark.entity.ProcedureActivity;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ProcedureActivityFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.ProceduresParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ProceduresSection;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProceduresParserTest {
    @Mock
    private ProcedureActivityFactory procedureActivityFactory;
    @InjectMocks
    private ProceduresParser proceduresParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final ProceduresSection section = ConsolFactory.eINSTANCE.createProceduresSection();

        /*when(procedureActivityFactory.parseProcedureObservation(any(), any(), any())).thenReturn(new ProcedureActivity(Long.valueOf(1)));
        when(procedureActivityFactory.parseProcedureAct(any(), any(), any())).thenReturn(new ProcedureActivity(Long.valueOf(1)));
        when(procedureActivityFactory.parseProcedureActivity(any(), any(), any())).thenReturn(new ProcedureActivity(Long.valueOf(1)));
        */

        final List<Procedure> result = proceduresParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<Procedure>asList(new Procedure(Long.valueOf(1))), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme