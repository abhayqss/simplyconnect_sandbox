package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.OrganizationService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.ResidentFilterPhrAppDto;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.AddressEditDto;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 9/5/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PhrResidentServiceTest {

    @Mock
    ResidentService residentService;

    @Mock
    DatabasesService databasesService;

    @Mock
    OrganizationService organizationService;

    @Mock
    ContactService contactService;

    @InjectMocks
    PhrResidentService phrResidentService;

    // shared test data
    protected final Long residentId = TestDataGenerator.randomId();
    private final String ssn = TestDataGenerator.randomValidSsn();
    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String firstName = TestDataGenerator.randomName();
    private final String lastName = TestDataGenerator.randomName();

    @Test
    public void testFindAssociatedResident() {
        // Expected objects
        final Long databaseId = TestDataGenerator.randomId();
        final Database unaffiliated = new Database();
        unaffiliated.setId(databaseId);

        final Resident expectedResident = new Resident(residentId);
        expectedResident.setDatabase(unaffiliated);

        // Mockito expectations
        when(databasesService.getUnaffiliatedDatabase())
                .thenReturn(unaffiliated);
        when(residentService.getResidents(any(ResidentFilterPhrAppDto.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(expectedResident));

        // Execute the method being tested
        Resident result = phrResidentService.findAssociatedResident(ssn, phone, email, firstName, lastName);

        // Validation
        assertEquals(expectedResident, result);
    }

    @Test
    public void testCreateAssociatedResident() {
        // Expected objects
        final Long databaseId = TestDataGenerator.randomId();
        final Database unaffiliated = new Database();
        unaffiliated.setId(databaseId);

        final Long organizationId = TestDataGenerator.randomId();
        final Organization unaffiliatedOrg = new Organization();
        unaffiliatedOrg.setId(organizationId);

        final PersonTelecom emailTelecom = new PersonTelecom();
        emailTelecom.setValue(email);
        emailTelecom.setUseCode("EMAIL");
        final PersonTelecom phoneTelecom = new PersonTelecom();
        phoneTelecom.setValue(phone);
        phoneTelecom.setUseCode("HP");
        final PersonAddress address = new PersonAddress();
        final Name name = new Name();
        name.setGiven(firstName);
        name.setFamily(lastName);
        final Person expectedPerson = Person.Builder.aPerson()
                .withDatabase(unaffiliated)
                .withTelecoms(Arrays.asList(emailTelecom, phoneTelecom))
                .withNames(Arrays.asList(name))
                .withAddresses(Arrays.asList(address))
                .build();
        name.setPerson(expectedPerson);
        address.setPerson(expectedPerson);
        emailTelecom.setPerson(expectedPerson);
        phoneTelecom.setPerson(expectedPerson);
        final Resident expectedResident = new Resident(residentId);
        expectedResident.setDatabase(unaffiliated);
        expectedResident.setDatabaseId(databaseId);
        expectedResident.setProviderOrganization(unaffiliatedOrg);
        expectedResident.setFacility(unaffiliatedOrg);
        expectedResident.setFirstName(firstName);
        expectedResident.setLastName(lastName);
        expectedResident.setSocialSecurity(ssn);
        expectedResident.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        expectedResident.setPerson(expectedPerson);

        // Mockito expectations
        when(databasesService.getUnaffiliatedDatabase())
                .thenReturn(unaffiliated);
        when(organizationService.getUnaffiliatedOrganization(databaseId))
                .thenReturn(unaffiliatedOrg);
        when(contactService.createPerson(eq(unaffiliated), eq(email), eq(phone), any(PersonTelecomCode.class), eq(firstName), eq(lastName), isNull(AddressEditDto.class)))
                .thenReturn(expectedPerson);
        when(residentService.getResidents(any(ResidentFilterPhrAppDto.class), any(Pageable.class)))
                .thenReturn(Collections.<Resident>emptyList());
        when(residentService.createResident(any(Resident.class))).then(returnsFirstArg());

        // Execute the method being tested
        Resident result = phrResidentService.createAssociatedResident(email, phone, ssn, firstName, lastName, "42", null);

        // Validation
        assertThat(result, sameBeanAs(expectedResident)
                .ignoring("id").ignoring("legacyTable").ignoring("legacyId"));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme