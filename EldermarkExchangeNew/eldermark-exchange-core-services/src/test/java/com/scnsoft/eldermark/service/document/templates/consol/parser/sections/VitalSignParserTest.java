package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.VitalSign;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.VitalSignObservationFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.VitalSignsSection;

import java.util.List;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class VitalSignParserTest {

    @Mock
    private VitalSignObservationFactory vitalSignObservationFactory;
    @InjectMocks
    private VitalSignParser vitalSignParser;

    private final Random random = new Random();
    private final Organization organization = new Organization();
    private final Client resident = new Client(random.nextLong());
    {
        organization.setId(random.nextLong());
        resident.setOrganization(organization);
    }

    @Test
    public void testParseSection() throws Exception {
        final VitalSignsSection section = ConsolFactory.eINSTANCE.createVitalSignsSection();

        /*when(vitalSignObservationFactory.parse(any(), any(), any())).thenReturn(new VitalSignObservation(Long.valueOf(1)));
        */

        final List<VitalSign> result = vitalSignParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<VitalSign>asList(new VitalSign(Long.valueOf(1))), result);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme