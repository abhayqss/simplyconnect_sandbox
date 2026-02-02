package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.*;
import com.scnsoft.eldermark.services.consol.templates.sections.ImmunizationsParser;
import com.scnsoft.eldermark.test.util.MockitoAnswers;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ImmunizationsSection;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ImmunizationsParserTest {

    @Mock
    private CcdCodeFactory ccdCodeFactory;
    @Mock
    private SubstanceAdministrationFactory substanceAdministrationFactory;
    @Mock
    private ObservationFactory observationFactory;
    @Mock
    private IndicationFactory indicationFactory;
    @Mock
    private InstructionsFactory instructionsFactory;
    @Mock
    private ParticipantRoleFactory participantRoleFactory;
    @InjectMocks
    private ImmunizationsParser immunizationsParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }


    @Test
    public void testParseSection() throws Exception {
        final ImmunizationsSection section = ConsolFactory.eINSTANCE.createImmunizationsSection();

        when(ccdCodeFactory.convert(any(CD.class))).then(MockitoAnswers.returnConvertedCode());
        /*
        when(substanceAdministrationFactory.parseMedicationSupplyOrder(any(), any(), any())).thenReturn(new MedicationSupplyOrder(Long.valueOf(1)));
        when(substanceAdministrationFactory.parseMedicationDispense(any(), any(), any())).thenReturn(new MedicationDispense(Long.valueOf(1)));
        when(substanceAdministrationFactory.parseImmunizationMedicationInformation(any(), any())).thenReturn(new ImmunizationMedicationInformation(Long.valueOf(1)));
        when(substanceAdministrationFactory.parsePreconditions(any(), any())).thenReturn(Arrays.<MedicationPrecondition>asList(new MedicationPrecondition(Long.valueOf(1))));
        when(observationFactory.parseReactionObservation(any(), any(), any())).thenReturn(new ReactionObservation(Long.valueOf(1)));
        when(indicationFactory.parseIndication(any(), any(), any())).thenReturn(new Indication(Long.valueOf(1)));
        when(instructionsFactory.parseInstructions(any(), any())).thenReturn(new Instructions(Long.valueOf(1)));
        when(participantRoleFactory.parseDrugVehicles(any(), any())).thenReturn(Arrays.<DrugVehicle>asList(new DrugVehicle(Long.valueOf(1))));
        */

        final List<Immunization> result = immunizationsParser.parseSection(resident, section);

        // TODO more validations
        //Assert.assertEquals(Arrays.<Immunization>asList(new Immunization(Long.valueOf(1))), result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme