package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.Encounter;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.document.ccd.ServiceDeliveryLocation;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.IndicationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ObservationFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.ParticipantRoleFactory;
import com.scnsoft.eldermark.util.MockitoAnswers;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.cda.EntryRelationship;
import org.eclipse.mdht.uml.cda.Participant2;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.vocab.ParticipationType;
import org.eclipse.mdht.uml.hl7.vocab.x_ActRelationshipEntryRelationship;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.EncounterActivities;
import org.openhealthtools.mdht.uml.cda.consol.EncounterDiagnosis;
import org.openhealthtools.mdht.uml.cda.consol.EncountersSection;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * @author phomal Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
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
    private final Organization organization = new Organization();
    private final Client resident = new Client(random.nextLong());

    {
        organization.setId(random.nextLong());
        resident.setOrganization(organization);
    }

    @Test
    public void testParseSection() throws Exception {
        final Encounter encounterMock = new Encounter();
//        encounterMock.setId(random.nextLong());
        encounterMock.setClient(resident);
        encounterMock.setOrganization(organization);
//        encounterMock.setDispositionCode(TestUtil.createCcdCodeMock());
        encounterMock.setEncounterPerformers(Collections.emptyList());
        encounterMock.setIndications(Collections.emptyList());
        encounterMock.setEffectiveTime(TestUtil.atStartOfDay());
        encounterMock.setEncounterType(TestUtil.createCcdCodeMock());
        encounterMock.setEncounterTypeText("encounterTypeText");

        final Encounter encounterMock2 = new Encounter();
//        encounterMock2.setId(random.nextLong());
        encounterMock2.setClient(resident);
        encounterMock2.setOrganization(organization);
//        encounterMock2.setDispositionCode(TestUtil.createCcdCodeMock());
        encounterMock2.setEncounterPerformers(Collections.emptyList());
        encounterMock2.setIndications(Collections.emptyList());
        encounterMock2.setEffectiveTime(new Date(TestUtil.atStartOfDay().getTime() - 24 * 60 * 60 * 1000));
        encounterMock2.setEncounterType(TestUtil.createCcdCodeMock());
        encounterMock2.setEncounterTypeText("encounterTypeText2");

        final ProblemObservation problemObservationMock = new ProblemObservation();
        problemObservationMock.setId(random.nextLong());
        problemObservationMock.setOrganization(organization);
        encounterMock.setProblemObservation(problemObservationMock);
        encounterMock.setClient(resident);
        encounterMock.setOrganization(organization);

        final ProblemObservation problemObservationMock2 = new ProblemObservation();
        problemObservationMock2.setId(random.nextLong());
        problemObservationMock2.setOrganization(organization);
        encounterMock2.setProblemObservation(problemObservationMock2);
        encounterMock2.setClient(resident);
        encounterMock2.setOrganization(organization);

        final ServiceDeliveryLocation serviceDeliveryLocationMock = new ServiceDeliveryLocation();
        serviceDeliveryLocationMock.setId(random.nextLong());
        serviceDeliveryLocationMock.setOrganization(organization);
//        serviceDeliveryLocationMock.setName("Test1");
        serviceDeliveryLocationMock.setName("serviceDeliveryLocationMock setName");
        encounterMock.setServiceDeliveryLocations(Collections.singletonList(serviceDeliveryLocationMock));

        final ServiceDeliveryLocation serviceDeliveryLocationMock2 = new ServiceDeliveryLocation();
        serviceDeliveryLocationMock2.setId(random.nextLong());
        serviceDeliveryLocationMock2.setOrganization(organization);
        serviceDeliveryLocationMock2.setName("serviceDeliveryLocationMock2 setName");
//        serviceDeliveryLocationMock2.setName("Test2");
        encounterMock2.setServiceDeliveryLocations(Collections.singletonList(serviceDeliveryLocationMock2));

        final EncountersSection section = ConsolFactory.eINSTANCE.createEncountersSection();
        final EncounterActivities encounterActivities = ConsolFactory.eINSTANCE.createEncounterActivities();
        encounterActivities.setCode(CcdUtils.createCD(encounterMock.getEncounterType()));
        encounterActivities.setText(DatatypesFactory.eINSTANCE.createED(encounterMock.getEncounterTypeText()));
        encounterActivities.setEffectiveTime(CcdUtils.createCenterTime(encounterMock.getEffectiveTime()));
        section.addEncounter(encounterActivities);
        final EncounterActivities encounterActivities2 = ConsolFactory.eINSTANCE.createEncounterActivities();
        encounterActivities2.setCode(CcdUtils.createCD(encounterMock2.getEncounterType()));
        encounterActivities2.setText(DatatypesFactory.eINSTANCE.createED(encounterMock2.getEncounterTypeText()));
        encounterActivities2.setEffectiveTime(CcdUtils.createCenterTime(encounterMock2.getEffectiveTime()));
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
        var observationEr1 = CDAFactory.eINSTANCE.createEntryRelationship();
        observationEr1.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
        observationEr1.setObservation(problemObservation);
        encounterDiagnosis.getEntryRelationships().add(observationEr1);
        encounterActivities.getEntryRelationships().add(wrapAsEntryRelationship(encounterDiagnosis));

        final EncounterDiagnosis encounterDiagnosis2 = ConsolFactory.eINSTANCE.createEncounterDiagnosis();
        var observationEr2 = CDAFactory.eINSTANCE.createEntryRelationship();
        observationEr2.setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
        observationEr2.setObservation(problemObservation2);
        encounterDiagnosis2.getEntryRelationships().add(observationEr2);
        encounterActivities2.getEntryRelationships().add(wrapAsEntryRelationship(encounterDiagnosis2));

        when(ccdCodeFactory.convert(any(CD.class))).then(MockitoAnswers.returnConvertedCode());

        when(observationFactory.parseProblemObservation(
                eq(findEntryRelationshipWithType(
                        findEntryRelationshipWithType(
                                encounterActivities.getEntryRelationships(),
                                x_ActRelationshipEntryRelationship.COMP).getAct().getEntryRelationships(),
                        x_ActRelationshipEntryRelationship.SUBJ
                ).getObservation()),
                eq(resident),
                nullable(Problem.class))).thenReturn(problemObservationMock);

        when(observationFactory.parseProblemObservation(eq(
                findEntryRelationshipWithType(
                        findEntryRelationshipWithType(
                                encounterActivities2.getEntryRelationships(),
                                x_ActRelationshipEntryRelationship.COMP).getAct().getEntryRelationships(),
                        x_ActRelationshipEntryRelationship.SUBJ
                ).getObservation()),
                eq(resident), nullable(Problem.class))).thenReturn(problemObservationMock2);

        when(participantRoleFactory.parseServiceDeliveryLocation(
                eq(encounterActivities.getParticipants().get(0).getParticipantRole()), eq(resident), anyString()))
                .thenReturn(serviceDeliveryLocationMock);
        when(participantRoleFactory.parseServiceDeliveryLocation(
                eq(encounterActivities2.getParticipants().get(0).getParticipantRole()), eq(resident), anyString()))
                .thenReturn(serviceDeliveryLocationMock2);
        // when(indicationFactory.parseIndication(any(Observation.class), eq(resident),
        // anyString())).thenReturn(null);

        final List<Encounter> result = encountersParser.parseSection(resident, section);

//        for (var p: PropertyUtil.propertyDescriptorsFor(encounterMock, Object.class)) {
//            System.out.println("\"" + p.getName()+ "\",");
//        }
        // validation
        assertThat(result, Matchers.hasSize(2));
        // TODO more validations
        assertThat(result, containsInAnyOrder(
                samePropertyValuesAs(encounterMock, "encounterType", "problemObservation"),
                samePropertyValuesAs(encounterMock2, "encounterType", "problemObservation"))
        );

        assertThat(result.get(0).getEncounterType(), samePropertyValuesAs(encounterMock.getEncounterType()));
        assertThat(result.get(1).getEncounterType(), samePropertyValuesAs(encounterMock2.getEncounterType()));

        assertThat(result.get(0).getProblemObservation(), samePropertyValuesAs(encounterMock.getProblemObservation()));
        assertThat(result.get(1).getProblemObservation(), samePropertyValuesAs(encounterMock2.getProblemObservation()));
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

    private static EntryRelationship findEntryRelationshipWithType(Collection<EntryRelationship> entryRelationshipCollection, x_ActRelationshipEntryRelationship type) {
        return entryRelationshipCollection.stream()
                .filter(er -> type.equals(er.getTypeCode()))
                .findFirst()
                .orElseThrow();
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme
