package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.AddressEditDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/28/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class AddressServiceTest {

    @Mock
    StateService stateService;

    @InjectMocks
    AddressService addressService;

    @Test
    public void testCreateAddressForPhrUser() {
        // Expected objects
        final String stateAbbr = "MN";
        final AddressEditDto dto = new AddressEditDto();
        dto.setCity("test city");
        dto.setState(stateAbbr);
        dto.setPostalCode("54637");
        dto.setStreetAddress("test street");
        final Long databaseId = TestDataGenerator.randomId();
        Database database = new Database();
        database.setId(databaseId);
        final Person person = Person.Builder.aPerson()
                .withId(TestDataGenerator.randomId())
                .withDatabase(database)
                .build();
        final Long stateId = TestDataGenerator.randomId();
        final State state = new State();
        state.setId(stateId);
        state.setAbbr(stateAbbr);

        final PersonAddress expectedAddress = PersonAddress.Builder.aPersonAddress()
                .withCity("test city")
                .withStreetAddress("test street")
                .withPostalCode("54637")
                .withState(stateAbbr)
                .withCountry("US")
                .withPostalAddressUse("WP")
                .withDatabase(database)
                .withPerson(person)
                .build();

        // Mockito expectations
        when(stateService.findByAbbrOrFullName(stateAbbr)).thenReturn(state);

        // Execute the method being tested
        PersonAddress result = addressService.createAddressForPhrUser(dto, person);

        // Validation
        assertThat(result, sameBeanAs(expectedAddress)
                .ignoring("legacyId").ignoring("legacyTable"));
    }

    @Test
    public void testCreateAddressForPhrUserNull() {
        // Expected objects
        final Person person = Person.Builder.aPerson()
                .withId(TestDataGenerator.randomId())
                .build();

        // Execute the method being tested
        PersonAddress result = addressService.createAddressForPhrUser(null, person);

        // Validation
        assertNull(result);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme