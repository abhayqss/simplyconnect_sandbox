package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.VitalSign;
import com.scnsoft.eldermark.entity.VitalSignObservation;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.VitalSignObservationFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.VitalSignParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.VitalSignsSection;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class VitalSignParserTest {

    @Mock
    private VitalSignObservationFactory vitalSignObservationFactory;
    @InjectMocks
    private VitalSignParser vitalSignParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
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