package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.Allergy;
import com.scnsoft.eldermark.entity.AllergyObservation;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.AllergyObservationFactory;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.consol.templates.sections.AllergiesParser;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.AllergiesSection;
import org.openhealthtools.mdht.uml.cda.consol.AllergyProblemAct;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class AllergiesParserTest {

    @Mock
    private AllergyObservationFactory allergyObservationFactory;
    @InjectMocks
    private AllergiesParser allergiesParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        // set up mocks
        final Allergy allergy = new Allergy();
        allergy.setResident(resident);
        allergy.setDatabase(database);
        allergy.setDatabaseId(database.getId());
        allergy.setLegacyId(random.nextLong());
        allergy.setStatusCode("active");
        allergy.setTimeLow(new Date());
        allergy.setTimeHigh(new Date());
        final Allergy allergy2 = new Allergy();
        allergy2.setResident(resident);
        allergy2.setDatabase(database);
        allergy2.setDatabaseId(database.getId());
        allergy2.setLegacyId(random.nextLong());
        allergy2.setStatusCode("allergyStatusCode");
        allergy2.setTimeLow(new Date());
        allergy2.setTimeHigh(new Date());

        final AllergyObservation allergyObservation = new AllergyObservation();
        allergyObservation.setId(random.nextLong());
        allergyObservation.setDatabase(database);
        allergyObservation.setAllergy(allergy);
        allergy.setAllergyObservations(Collections.singleton(allergyObservation));
        final AllergyObservation allergyObservation2 = new AllergyObservation();
        allergyObservation2.setId(random.nextLong());
        allergyObservation2.setDatabase(database);
        allergyObservation2.setAllergy(allergy2);
        allergy2.setAllergyObservations(Collections.singleton(allergyObservation2));

        final AllergiesSection section = ConsolFactory.eINSTANCE.createAllergiesSection();
        final AllergyProblemAct allergyProblemAct = ConsolFactory.eINSTANCE.createAllergyProblemAct();
        allergyProblemAct.getIds().add(DatatypesFactory.eINSTANCE.createII("allergy problem act OID", String.valueOf(allergy.getLegacyId())));
        allergyProblemAct.setEffectiveTime(CcdUtils.convertEffectiveTime(allergy.getTimeLow(), allergy.getTimeHigh()));
        final AllergyProblemAct allergyProblemAct2 = ConsolFactory.eINSTANCE.createAllergyProblemAct();
        allergyProblemAct2.getIds().add(DatatypesFactory.eINSTANCE.createII("allergy problem act OID", String.valueOf(allergy2.getLegacyId())));
        allergyProblemAct2.setEffectiveTime(CcdUtils.convertEffectiveTime(allergy2.getTimeLow(), allergy2.getTimeHigh()));
        final org.openhealthtools.mdht.uml.cda.consol.AllergyObservation observation = ConsolFactory.eINSTANCE.createAllergyObservation();
        final org.openhealthtools.mdht.uml.cda.consol.AllergyObservation observation2 = ConsolFactory.eINSTANCE.createAllergyObservation();
        allergyProblemAct.addObservation(observation);
        allergyProblemAct2.addObservation(observation2);
        section.addAct(allergyProblemAct);
        section.addAct(allergyProblemAct2);

        when(allergyObservationFactory.parse(eq(observation), any(Allergy.class), eq(resident), anyString())).thenReturn(allergyObservation);
        when(allergyObservationFactory.parse(eq(observation2), any(Allergy.class), eq(resident), anyString())).thenReturn(allergyObservation2);

        final List<Allergy> result = allergiesParser.parseSection(resident, section);

        // validation
        assertThat(result, Matchers.hasSize(2));
        // FIXME fix validation
        //assertThat(result, containsInAnyOrder(sameBeanAs(allergy), sameBeanAs(allergy2)));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme