package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.Immunization;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.ImmunizationsSection;

import java.util.List;
import java.util.Random;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
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
    private final Organization organization = new Organization();
    private final Client resident = new Client(random.nextLong());
    {
        organization.setId(random.nextLong());
        resident.setOrganization(organization);
    }


    @Test
    public void testParseSection() throws Exception {
        final ImmunizationsSection section = ConsolFactory.eINSTANCE.createImmunizationsSection();

        /*
        when(ccdCodeFactory.convert(any(CD.class))).then(MockitoAnswers.returnConvertedCode());
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