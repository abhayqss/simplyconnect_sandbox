package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Medication;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.SubstanceAdministrationFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.MedicationsParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.MedicationsSection;

import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class MedicationsParserTest {
    @Mock
    private SubstanceAdministrationFactory substanceAdministrationFactory;
    @InjectMocks
    private MedicationsParser medicationsParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final MedicationsSection section = ConsolFactory.eINSTANCE.createMedicationsSection();

        /*when(substanceAdministrationFactory.parseMedicationActivity(any(), any(), any())).thenReturn(new Medication(Long.valueOf(1)));
        */

        final List<Medication> result = medicationsParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<Medication>asList(new Medication(Long.valueOf(1))), result);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme