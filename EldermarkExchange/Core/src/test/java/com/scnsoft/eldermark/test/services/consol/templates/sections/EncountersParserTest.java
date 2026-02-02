package com.scnsoft.eldermark.test.services.consol.templates.sections;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Participant2;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.vocab.ParticipationType;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.EncounterActivities;
import org.openhealthtools.mdht.uml.cda.consol.EncounterDiagnosis;
import org.openhealthtools.mdht.uml.cda.consol.EncountersSection;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Encounter;
import com.scnsoft.eldermark.entity.Problem;
import com.scnsoft.eldermark.entity.ProblemObservation;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.entity.ServiceDeliveryLocation;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.IndicationFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ObservationFactory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.ParticipantRoleFactory;
import com.scnsoft.eldermark.services.consol.templates.sections.EncountersParser;
import com.scnsoft.eldermark.test.util.MockitoAnswers;
import com.scnsoft.eldermark.test.util.TestUtil;

/**
 * @author phomal Created on 5/3/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class EncountersParserTest {
    @Mock
    private CcdCodeFactory ccdCodeFactory;
    @Mock
    private ObservationFactory observationFactory;
    @Mock
    private ParticipantRoleFactory participantRoleFactory;
    @Mock
    private IndicationFactory indicationFactory;
    @InjectMocks
    private EncountersParser encountersParser;

    private final Random random = new Random();
    private final Database database = new Database();
    private final Resident resident = new Resident(random.nextLong());
    {
        database.setId(random.nextLong());
        resident.setDatabase(database);
    }

    @Test
    public void testParseSection() throws Exception {
        final Encounter encounterMock = new Encounter();
        encounterMock.setId(random.nextLong());
        encounterMock.setResident(resident);
        encounterMock.setDatabase(database);
        encounterMock.setDispositionCode(TestUtil.createCcdCodeMock());
        encounterMock.setEffectiveTime(new Date());
        encounterMock.setEncounterType(TestUtil.createCcdCodeMock());
        encounterMock.setEncounterTypeText("encounterTypeText");

        final Encounter encounterMock2 = new Encounter();
        encounterMock2.setId(random.nextLong());
        encounterMock2.setResident(resident);
        encounterMock2.setDatabase(database);
        encounterMock2.setDispositionCode(TestUtil.createCcdCodeMock());
        encounterMock2.setEffectiveTime(new Date());
        encounterMock2.setEncounterType(TestUtil.createCcdCodeMock());
        encounterMock2.setEncounterTypeText("encounterTypeText");

        final ProblemObservation problemObservationMock = new ProblemObservation();
        problemObservationMock.setId(random.nextLong());
        encounterMock.setProblemObservation(problemObservationMock);
        encounterMock.setResident(resident);
        encounterMock.setDatabase(database);

        final ProblemObservation problemObservationMock2 = new ProblemObservation();
        problemObservationMock2.setId(random.nextLong());
        encounterMock2.setProblemObservation(problemObservationMock2);
        encounterMock2.setResident(resident);
        encounterMock2.setDatabase(database);

        final ServiceDeliveryLocation serviceDeliveryLocationMock = new ServiceDeliveryLocation();
        serviceDeliveryLocationMock.setId(random.nextLong());
        serviceDeliveryLocationMock.setDatabase(database);
        serviceDeliveryLocationMock.setName("Test1");
        encounterMock.setServiceDeliveryLocations(Collections.singletonList(serviceDeliveryLocationMock));

        final ServiceDeliveryLocation serviceDeliveryLocationMock2 = new ServiceDeliveryLocation();
        serviceDeliveryLocationMock2.setId(random.nextLong());
        serviceDeliveryLocationMock2.setDatabase(database);
        serviceDeliveryLocationMock2.setName("Test2");
        encounterMock2.setServiceDeliveryLocations(Collections.singletonList(serviceDeliveryLocationMock2));

        final EncountersSection section = ConsolFactory.eINSTANCE.createEncountersSection();
        final EncounterActivities encounterActivities = ConsolFactory.eINSTANCE.createEncounterActivities();
        encounterActivities.setCode(mockCD());
        encounterActivities.setText(DatatypesFactory.eINSTANCE.createED("Test1"));
        section.addEncounter(encounterActivities);
        final EncounterActivities encounterActivities2 = ConsolFactory.eINSTANCE.createEncounterActivities();
        encounterActivities2.setCode(mockCD());
        encounterActivities2.setText(DatatypesFactory.eINSTANCE.createED("Test2"));
        section.addEncounter(encounterActivities2);
        final org.openhealthtools.mdht.uml.cda.consol.ServiceDeliveryLocation serviceDeliveryLocation = ConsolFactory.eINSTANCE
                .createServiceDeliveryLocation(),
                serviceDeliveryLocation2 = ConsolFactory.eINSTANCE.createServiceDeliveryLocation();
        encounterActivities.getParticipants().add(wrapAsParticipant(serviceDeliveryLocation));
        encounterActivities2.getParticipants().add(wrapAsParticipant(serviceDeliveryLocation2));
        final org.openhealthtools.mdht.uml.cda.consol.ProblemObservation problemObservation = ConsolFactory.eINSTANCE
                .createProblemObservation();
        final org.openhealthtools.mdht.uml.cda.consol.ProblemObservation problemObservation2 = ConsolFactory.eINSTANCE
                .createProblemObservation();
        final EncounterDiagnosis encounterDiagnosis = ConsolFactory.eINSTANCE.createEncounterDiagnosis();
        final EncounterDiagnosis encounterDiagnosis2 = ConsolFactory.eINSTANCE.createEncounterDiagnosis();
        encounterDiagnosis.addObservation(problemObservation);
        encounterDiagnosis2.addObservation(problemObservation2);
        encounterActivities.getEntryRelationships().add(wrapAsEntryRelationship(encounterDiagnosis));
        encounterActivities2.getEntryRelationships().add(wrapAsEntryRelationship(encounterDiagnosis2));

        when(ccdCodeFactory.convert(any(CD.class))).then(MockitoAnswers.returnConvertedCode());
        when(observationFactory.parseProblemObservation(eq(encounterDiagnosis.getProblemObservations().get(0)),
                eq(resident), any(Problem.class))).thenReturn(problemObservationMock);
        when(observationFactory.parseProblemObservation(eq(encounterDiagnosis2.getProblemObservations().get(0)),
                eq(resident), any(Problem.class))).thenReturn(problemObservationMock2);
        when(participantRoleFactory.parseServiceDeliveryLocation(
                eq(encounterActivities.getParticipants().get(0).getParticipantRole()), eq(resident), anyString()))
                        .thenReturn(serviceDeliveryLocationMock);
        when(participantRoleFactory.parseServiceDeliveryLocation(
                eq(encounterActivities2.getParticipants().get(0).getParticipantRole()), eq(resident), anyString()))
                        .thenReturn(serviceDeliveryLocationMock2);
        // when(indicationFactory.parseIndication(any(Observation.class), eq(resident),
        // anyString())).thenReturn(null);

        final List<Encounter> result = encountersParser.parseSection(resident, section);

        // validation
        assertThat(result, Matchers.hasSize(2));
        // TODO more validations
        // assertThat(result, containsInAnyOrder(encounterMock, encounterMock2));
    }

    private static CD mockCD() {
        return DatatypesFactory.eINSTANCE.createCD("code", "codeSystem");
    }

    private static EntryRelationship wrapAsEntryRelationship(EncounterDiagnosis encounterDiagnosis) {
        checkNotNull(encounterDiagnosis);
        final EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
        entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
        entryRelationship.setAct(encounterDiagnosis);
        return entryRelationship;
    }

    private static Participant2 wrapAsParticipant(
            org.openhealthtools.mdht.uml.cda.consol.ServiceDeliveryLocation serviceDeliveryLocation) {
        checkNotNull(serviceDeliveryLocation);
        final Participant2 participant2 = CDAFactory.eINSTANCE.createParticipant2();
        participant2.setTypeCode(ParticipationType.LOC);
        participant2.setParticipantRole(serviceDeliveryLocation);
        return participant2;
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
