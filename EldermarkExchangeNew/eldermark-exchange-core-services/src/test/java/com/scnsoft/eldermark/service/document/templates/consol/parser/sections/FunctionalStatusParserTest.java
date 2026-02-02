package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ParticipantRoleFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.FunctionalStatusSection;

import java.util.List;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class FunctionalStatusParserTest {

    @Mock
    private CcdCodeFactory ccdCodeFactory;
    @Mock
    private ParticipantRoleFactory participantRoleFactory;
    @InjectMocks
    private FunctionalStatusParser functionalStatusParser;

    private final Random random = new Random();
    private final Organization database = new Organization();
    private final Client resident = new Client(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setOrganization(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final FunctionalStatusSection section = ConsolFactory.eINSTANCE.createFunctionalStatusSection();

        /*
        when(ccdCodeFactory.convert(Matchers.any(CD.class))).then(MockitoAnswers.returnConvertedCode());
        when(ccdCodeFactory.convertInterpretationCodes(any())).thenReturn(Arrays.<CcdCode>asList(new CcdCode()));
        when(participantRoleFactory.parseProductInstance(any(), any())).thenReturn(new ProductInstance(Long.valueOf(1)));
        */

        final List<FunctionalStatus> result = functionalStatusParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<FunctionalStatus>asList(new FunctionalStatus(Long.valueOf(1))), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme