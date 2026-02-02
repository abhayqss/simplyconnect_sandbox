package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ParticipantRoleFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.FunctionalStatusParser;
import com.scnsoft.eldermark.test.util.MockitoAnswers;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.FunctionalStatusSection;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class FunctionalStatusParserTest {

    @Mock
    private CcdCodeFactory ccdCodeFactory;
    @Mock
    private ParticipantRoleFactory participantRoleFactory;
    @InjectMocks
    private FunctionalStatusParser functionalStatusParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final FunctionalStatusSection section = ConsolFactory.eINSTANCE.createFunctionalStatusSection();

        when(ccdCodeFactory.convert(any(CD.class))).then(MockitoAnswers.returnConvertedCode());
        /*
        when(ccdCodeFactory.convertInterpretationCodes(any())).thenReturn(Arrays.<CcdCode>asList(new CcdCode()));
        when(participantRoleFactory.parseProductInstance(any(), any())).thenReturn(new ProductInstance(Long.valueOf(1)));
        */

        final List<FunctionalStatus> result = functionalStatusParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<FunctionalStatus>asList(new FunctionalStatus(Long.valueOf(1))), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme