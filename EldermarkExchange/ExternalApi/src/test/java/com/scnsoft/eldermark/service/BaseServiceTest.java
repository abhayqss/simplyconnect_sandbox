package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import com.scnsoft.eldermark.shared.ccd.TelecomDto;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.entity.Token;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;

/**
 * This abstract class is a base class for unit tests that require a mock authentication.
 *
 * @author phomal
 * Created on 6/15/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public abstract class BaseServiceTest {

    @Mock
    protected Authentication authentication;

    // Shared test data

    /**
     * Current user ID
     */
    protected final Long userId = TestDataGenerator.randomId();
    /**
     * Current user access token
     */
    protected final Token token = Token.generateToken(userId);
    protected final Long residentId = TestDataGenerator.randomId();
    protected final Resident resident = new Resident(residentId);

    @Before
    public void mockTokenAuth() {
        when(authentication.getDetails()).thenReturn(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected static Employee createEmployee(String email, String firstName, String lastName, Person person) {
        final Long employeeId = TestDataGenerator.randomId();
        if (person == null) {
            person = createPerson(null, email, firstName, lastName, null);
        }

        final CareTeamRole careTeamRole = TestDataGenerator.careTeamRole(CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH);

        final Employee employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setCareTeamRole(careTeamRole);
        employee.setPerson(person);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setLoginName(email);
        employee.setDatabase(person.getDatabase());
        employee.setDatabaseId(person.getDatabaseId());

        return employee;
    }

    protected static Resident createResident(String email, String firstName, String lastName, Person person) {
        final Long residentId = TestDataGenerator.randomId();
        if (person == null) {
            person = createPerson(null, email, firstName, lastName, null);
        }

        final Resident resident = new Resident(residentId);
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setPerson(person);
        resident.setDatabase(person.getDatabase());
        resident.setDatabaseId(person.getDatabaseId());

        return resident;
    }

    protected static Person createPerson(String phone, String email, String firstName, String lastName, PersonTelecomCode phoneUseCode) {
        final boolean isAnonymous = (firstName == null && lastName == null);

        final Database database = createDatabase();

        final Person person = new Person();
        person.setId(TestDataGenerator.randomId());
        person.setDatabase(database);
        person.setDatabaseId(database.getId());

        final Name name = new Name();
        name.setDatabase(database);
        name.setDatabaseId(database.getId());
        name.setFamily(lastName);
        name.setFamilyNormalized(StringUtils.lowerCase(lastName));
        name.setGiven(firstName);
        name.setGivenNormalized(StringUtils.lowerCase(firstName));
        if (!isAnonymous) {
            name.setFullName(firstName + " " + lastName);
        }
        name.setMiddle(TestDataGenerator.randomName());
        name.setNameUse("L");
        name.setPerson(person);
        person.setNames(Collections.singletonList(name));
        person.setAddresses(new ArrayList<PersonAddress>());

        if (phone != null && email != null && phoneUseCode != null) {
            final PersonTelecom personTelecomEmail = PersonService.createPersonTelecom(person, PersonTelecomCode.EMAIL, email, null);
            final PersonTelecom personTelecomPhone = PersonService.createPersonTelecom(person, phoneUseCode, phone, null);
            person.setTelecoms(Arrays.asList(personTelecomEmail, personTelecomPhone));
        } else if (email != null) {
            final PersonTelecom personTelecomEmail = PersonService.createPersonTelecom(person, PersonTelecomCode.EMAIL, email, null);
            person.setTelecoms(Collections.singletonList(personTelecomEmail));
        } else if (phone != null && phoneUseCode != null) {
            final PersonTelecom personTelecomPhone = PersonService.createPersonTelecom(person, phoneUseCode, phone, null);
            person.setTelecoms(Collections.singletonList(personTelecomPhone));
        }

        return person;
    }

    protected static PersonDto createExpectedPerson(Person person) {
        String firstName = person.getNames().get(0).getGiven();
        String lastName = person.getNames().get(0).getFamily();
        final String email = PersonService.getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
        final String phone = PersonService.getPersonTelecomValue(person, PersonTelecomCode.HP);
        final boolean isAnonymous = (firstName == null && lastName == null);

        final NameDto nameDto = new NameDto();
        nameDto.setFullName(firstName + " " + lastName);
        final TelecomDto emailDto = new TelecomDto();
        emailDto.setUseCode("EMAIL");
        emailDto.setValue(email);
        final TelecomDto phoneDto = new TelecomDto();
        phoneDto.setUseCode("HP");
        phoneDto.setValue(phone);

        final PersonDto personDto = new PersonDto();
        if (!isAnonymous) {
            personDto.setNames(Collections.singletonList(nameDto));
        }
        personDto.setAddresses(Collections.<AddressDto>emptyList());
        if (phone != null && email != null) {
            personDto.setTelecoms(Arrays.asList(emailDto, phoneDto));
        } else if (email != null) {
            personDto.setTelecoms(Arrays.asList(emailDto));
        } else if (phone != null) {
            personDto.setTelecoms(Arrays.asList(phoneDto));
        }

        return personDto;
    }

    protected static Database createDatabase() {
        final Database database = new Database();
        database.setId(TestDataGenerator.randomId());
        database.setName("db name");
        return database;
    }

}
