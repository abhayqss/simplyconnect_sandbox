package com.scnsoft.eldermark.service.document.templates.consol.parser.sections;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.ccd.Allergy;
import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.AllergyObservationFactory;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.CDAFactory;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openhealthtools.mdht.uml.cda.consol.AllergiesSection;
import org.openhealthtools.mdht.uml.cda.consol.AllergyProblemAct;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 5/3/2018.
 */
@ExtendWith(MockitoExtension.class)
public class AllergiesParserTest {

    @Mock
    private AllergyObservationFactory allergyObservationFactory;

    @InjectMocks
    private AllergiesParser allergiesParser;

    private final Random random = new Random();
    private final Organization organization = new Organization();
    private final Client resident = new Client(random.nextLong());

    {
        organization.setId(random.nextLong());
        resident.setOrganization(organization);
    }


    @Test
    public void testParseSection() throws Exception {
        // set up mocks
        final Allergy allergy = new Allergy();
        allergy.setClient(resident);
        allergy.setOrganization(organization);
//        allergy.setOrganizationId(organization.getId());
        allergy.setLegacyId(random.nextLong());
        allergy.setStatusCode("active");
        allergy.setTimeLow(TestUtil.atStartOfDay());
        allergy.setTimeHigh(TestUtil.atStartOfDay());


        final Allergy allergy2 = new Allergy();
        allergy2.setClient(resident);
        allergy2.setOrganization(organization);
//        allergy2.setOrganizationId(organization.getId());
        allergy2.setLegacyId(random.nextLong());
        allergy2.setStatusCode("allergyStatusCode");
        allergy2.setTimeLow(TestUtil.atStartOfDay());
        allergy2.setTimeHigh(TestUtil.atStartOfDay());

        final AllergyObservation allergyObservation = new AllergyObservation();
        allergyObservation.setId(random.nextLong());
        allergyObservation.setOrganization(organization);
        allergyObservation.setAllergy(allergy);
        allergyObservation.setProductText("product text 1");
        allergy.setAllergyObservations(Collections.singleton(allergyObservation));
        final AllergyObservation allergyObservation2 = new AllergyObservation();
        allergyObservation2.setId(random.nextLong());
        allergyObservation2.setOrganization(organization);
        allergyObservation2.setAllergy(allergy2);
        allergyObservation2.setProductText("product text 2");
        allergy2.setAllergyObservations(Collections.singleton(allergyObservation2));

        final AllergiesSection section = ConsolFactory.eINSTANCE.createAllergiesSection();
        final AllergyProblemAct allergyProblemAct = ConsolFactory.eINSTANCE.createAllergyProblemAct();
        allergyProblemAct.getIds().add(DatatypesFactory.eINSTANCE.createII("allergy problem act OID", String.valueOf(allergy.getLegacyId())));
        allergyProblemAct.setEffectiveTime(CcdUtils.convertEffectiveTime(allergy.getTimeLow(), allergy.getTimeHigh()));
        var status1 = DatatypesFactory.eINSTANCE.createCS();
        status1.setCode(allergy.getStatusCode());
        allergyProblemAct.setStatusCode(status1);

        final AllergyProblemAct allergyProblemAct2 = ConsolFactory.eINSTANCE.createAllergyProblemAct();
        allergyProblemAct2.getIds().add(DatatypesFactory.eINSTANCE.createII("allergy problem act OID", String.valueOf(allergy2.getLegacyId())));
        allergyProblemAct2.setEffectiveTime(CcdUtils.convertEffectiveTime(allergy2.getTimeLow(), allergy2.getTimeHigh()));
        var status2 = DatatypesFactory.eINSTANCE.createCS();
        status2.setCode(allergy2.getStatusCode());
        allergyProblemAct2.setStatusCode(status2);

        final org.openhealthtools.mdht.uml.cda.consol.AllergyObservation observation = ConsolFactory.eINSTANCE.createAllergyObservation();
        addProductText(observation, allergyObservation.getProductText());
        final org.openhealthtools.mdht.uml.cda.consol.AllergyObservation observation2 = ConsolFactory.eINSTANCE.createAllergyObservation();
        addProductText(observation2, allergyObservation2.getProductText());

        allergyProblemAct.addObservation(observation);
        allergyProblemAct2.addObservation(observation2);
        section.addAct(allergyProblemAct);
        section.addAct(allergyProblemAct2);


        when(allergyObservationFactory.parse(eq(allergyProblemAct.getEntryRelationships()), org.mockito.Matchers.any(Allergy.class), eq(resident), anyString())).thenReturn(Collections.singleton(allergyObservation));
        when(allergyObservationFactory.parse(eq(allergyProblemAct2.getEntryRelationships()), org.mockito.Matchers.any(Allergy.class), eq(resident), anyString())).thenReturn(Collections.singleton(allergyObservation2));

        final List<Allergy> result = allergiesParser.parseSection(resident, section);

        // validation
        assertThat(result, Matchers.hasSize(2));
        assertThat(result, containsInAnyOrder(samePropertyValuesAs(allergy), samePropertyValuesAs(allergy2)));
    }

    private void addProductText(org.openhealthtools.mdht.uml.cda.consol.AllergyObservation observation,
                                String productText) {
        var participant = CDAFactory.eINSTANCE.createParticipant2();
        var role = CDAFactory.eINSTANCE.createParticipantRole();
        var playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
        var name = DatatypesFactory.eINSTANCE.createPN();

        name.addText(productText);
        playingEntity.getNames().add(name);
        role.setPlayingEntity(playingEntity);
        participant.setParticipantRole(role);
        observation.getParticipants().add(participant);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme