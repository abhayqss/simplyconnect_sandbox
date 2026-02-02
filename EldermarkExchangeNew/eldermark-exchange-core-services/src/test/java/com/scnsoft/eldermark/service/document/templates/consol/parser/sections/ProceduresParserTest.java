package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.Procedure;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ProcedureActivityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ProceduresSection;

import java.util.List;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class ProceduresParserTest {
    @Mock
    private ProcedureActivityFactory procedureActivityFactory;
    @InjectMocks
    private ProceduresParser proceduresParser;

    private final Random random = new Random();
    private final Organization organization = new Organization();
    private final Client resident = new Client(random.nextLong());
    {
        organization.setId(random.nextLong());
        resident.setOrganization(organization);
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