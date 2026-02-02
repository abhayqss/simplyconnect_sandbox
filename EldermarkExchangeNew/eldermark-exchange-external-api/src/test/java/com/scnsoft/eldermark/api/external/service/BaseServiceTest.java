package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.utils.PersonUtils;
import com.scnsoft.eldermark.api.shared.ccd.dto.NameDto;
import com.scnsoft.eldermark.api.shared.ccd.dto.PersonDto;
import com.scnsoft.eldermark.api.shared.ccd.dto.TelecomDto;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.api.shared.web.dto.Token;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.when;

/**
 * This abstract class is a base class for unit tests that require a mock authentication.
 *
 */
@ExtendWith(MockitoExtension.class)
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
    protected final Client resident = new Client(residentId);

    @BeforeEach
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
        employee.setOrganization(person.getOrganization());
        employee.setOrganizationId(person.getOrganizationId());

        return employee;
    }

    protected static Client createResident(String email, String firstName, String lastName, Person person) {
        final Long residentId = TestDataGenerator.randomId();
        if (person == null) {
            person = createPerson(null, email, firstName, lastName, null);
        }

        final Client resident = new Client(residentId);
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setPerson(person);
        resident.setOrganization(person.getOrganization());
        resident.setOrganizationId(person.getOrganizationId());

        return resident;
    }

    protected static Person createPerson(String phone, String email, String firstName, String lastName, PersonTelecomCode phoneUseCode) {
        final boolean isAnonymous = (firstName == null && lastName == null);

        final Organization organization = createDatabase();

        final Person person = new Person();
        person.setId(TestDataGenerator.randomId());
        person.setOrganization(organization);
        person.setOrganizationId(organization.getId());

        final Name name = new Name();
        name.setOrganization(organization);
        name.setOrganizationId(organization.getId());
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
        person.setAddresses(new ArrayList<>());

        if (phone != null && email != null && phoneUseCode != null) {
            final PersonTelecom personTelecomEmail = PersonUtils.createPersonTelecom(person, PersonTelecomCode.EMAIL, email, null);
            final PersonTelecom personTelecomPhone = PersonUtils.createPersonTelecom(person, phoneUseCode, phone, null);
            person.setTelecoms(Arrays.asList(personTelecomEmail, personTelecomPhone));
        } else if (email != null) {
            final PersonTelecom personTelecomEmail = PersonUtils.createPersonTelecom(person, PersonTelecomCode.EMAIL, email, null);
            person.setTelecoms(Collections.singletonList(personTelecomEmail));
        } else if (phone != null && phoneUseCode != null) {
            final PersonTelecom personTelecomPhone = PersonUtils.createPersonTelecom(person, phoneUseCode, phone, null);
            person.setTelecoms(Collections.singletonList(personTelecomPhone));
        }

        return person;
    }

    protected static PersonDto createExpectedPerson(Person person) {
        String firstName = person.getNames().get(0).getGiven();
        String lastName = person.getNames().get(0).getFamily();
        final String email = PersonUtils.getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
        final String phone = PersonUtils.getPersonTelecomValue(person, PersonTelecomCode.HP);
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

        personDto.setAddresses(Collections.emptyList());

        if (phone != null && email != null) {
            personDto.setTelecoms(Arrays.asList(emailDto, phoneDto));
        } else if (email != null) {
            personDto.setTelecoms(Arrays.asList(emailDto));
        } else if (phone != null) {
            personDto.setTelecoms(Arrays.asList(phoneDto));
        }

        return personDto;
    }

    protected static Organization createDatabase() {
        final Organization database = new Organization();
        database.setId(TestDataGenerator.randomId());
        database.setName("db name");
        return database;
    }

}
