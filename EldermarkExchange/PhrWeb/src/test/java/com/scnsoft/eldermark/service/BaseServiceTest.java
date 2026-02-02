package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.Gender;
import com.scnsoft.eldermark.shared.ccd.AddressDto;
import com.scnsoft.eldermark.shared.ccd.NameDto;
import com.scnsoft.eldermark.shared.ccd.PersonDto;
import com.scnsoft.eldermark.shared.ccd.TelecomDto;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.shared.web.entity.Token;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.mockito.Mockito.when;

/**
 * This abstract class is a base class for all unit tests. It provides a mock authentication and convenient methods for mocking objects.
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
    protected final Long employeeId = TestDataGenerator.randomId();
    protected final Long residentId = TestDataGenerator.randomId();
    protected final Resident resident = new Resident(residentId);
    protected final Long residentId2 = residentId - 1;
    protected final Long residentId3 = residentId + 1;
    protected final Resident resident2 = new Resident(residentId2);
    protected final Resident resident3 = new Resident(residentId3);
    protected final List<Long> allResidentIds = Arrays.asList(residentId2, residentId, residentId3);
    protected final List<Long> activeResidentIds = Collections.singletonList(residentId);
    protected final List<Resident> allResidents = Arrays.asList(resident, resident2, resident3);
    protected final List<Resident> activeResidents = Collections.singletonList(resident);

    protected static final String BEHAVIORAL_HEALTH = "Behavioral Health";

    @Before
    public void mockTokenAuth() {
        when(authentication.getDetails()).thenReturn(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    protected User setUpMockitoExpectationsForCurrentUser(Long consumerUserId) {
        final boolean isConsumer = Objects.equals(consumerUserId, userId);
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();
        final String tokenEncoded = Token.base64encode(token);
        Employee employee = null;
        Resident r = null;
        if (isConsumer) {
            r = resident;
            r.setFirstName(firstName);
            r.setLastName(lastName);
        } else {
            employee = new Employee();
            employee.setId(employeeId);
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
        }
        final User currentUser = User.Builder.anUser()
                .withId(userId)
                .withTokenEncoded(tokenEncoded)
                .withEmployee(employee)
                .withResident(r)
                .withFirstName(firstName)
                .withLastName(lastName)
                .build();

        return currentUser;
    }

    protected User createConsumer(Long newUserId) {
        final String ssn = TestDataGenerator.randomValidSsn();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        return createConsumer(newUserId, ssn, null, null, firstName, lastName);
    }

    protected User createProvider(Long newUserId) {
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        return createProvider(newUserId, null, null, null, firstName, lastName);
    }

    protected User createConsumer(Long newUserId, String ssn, String phone, String email, String firstName, String lastName) {
        final boolean isCurrent = Objects.equals(newUserId, userId);

        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.HP);

        final Organization facility = new Organization();
        facility.setName("Facility name");
        facility.setDatabase(person.getDatabase());
        facility.setDatabaseId(person.getDatabaseId());

        Resident resident = isCurrent ? this.resident : new Resident(TestDataGenerator.randomIdExceptOf(residentId, residentId2, residentId3));

        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setDatabase(person.getDatabase());
        resident.setDatabaseId(person.getDatabaseId());
        resident.setFacility(facility);
        resident.setSocialSecurity(ssn);
        resident.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        resident.setPerson(person);
        final String genderDisplayName = Gender.MALE.getLabel();
        final CcdCode genderCcdCode = new CcdCode();
        genderCcdCode.setDisplayName(genderDisplayName);
        genderCcdCode.setCode(Gender.MALE.getAdministrativeGenderCode());
        resident.setGender(genderCcdCode);

        final Token token = isCurrent ? this.token : Token.generateToken(newUserId);
        final String tokenEncoded = Token.base64encode(token);
        final User currentUser = User.Builder.anUser()
                .withId(newUserId)
                .withTokenEncoded(tokenEncoded)
                .withEmployee(null)
                .withResident(resident)
                .withSsn(ssn)
                .withPhone(phone)
                .withEmail(email)
                .withPhrPatient(true)
                .withTimeZoneOffset(TestDataGenerator.randomTimeZoneOffset())
                .build();

        return currentUser;
    }

    private static Resident createResident(Long residentId, String ssn, String phone, String email, String firstName, String lastName) {
        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.HP);

        final Organization facility = new Organization();
        facility.setName("Facility name");
        facility.setDatabase(person.getDatabase());
        facility.setDatabaseId(person.getDatabaseId());

        final Resident resident = new Resident(residentId);
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setSocialSecurity(ssn);
        resident.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        resident.setFacility(facility);
        resident.setPerson(person);
        resident.setDatabase(person.getDatabase());
        resident.setDatabaseId(person.getDatabaseId());
        return resident;
    }

    protected User createProvider(Long newUserId, String ssn, String phone, String email, String firstName, String lastName) {
        final boolean isCurrent = Objects.equals(newUserId, userId);
        final boolean isNew = (newUserId == null);
        final boolean isAlsoConsumer = (ssn != null);

        final Person person = createPerson(phone, email, firstName, lastName, PersonTelecomCode.HP);

        final Employee employee = createEmployee(email, firstName, lastName, person);

        final Resident resident = new Resident(TestDataGenerator.randomIdExceptOf(residentId, residentId2, residentId3));
        resident.setFirstName(firstName);
        resident.setLastName(lastName);
        resident.setSocialSecurity(ssn);
        resident.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        resident.setPerson(person);
        resident.setDatabase(person.getDatabase());
        resident.setDatabaseId(person.getDatabaseId());

        final Token token = isCurrent ? this.token : Token.generateToken(newUserId);
        final String tokenEncoded = Token.base64encode(token);
        final User currentUser = User.Builder.anUser()
                .withId(newUserId)
                .withTokenEncoded(isNew ? null : tokenEncoded)
                .withEmployee(employee)
                .withResident(isAlsoConsumer ? resident : null)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withPhone(phone)
                .withEmail(email)
                .withSsn(ssn)
                .withPhrPatient(isAlsoConsumer)
                .withDatabase(employee.getDatabase())
                .withTimeZoneOffset(TestDataGenerator.randomTimeZoneOffset())
                .build();

        return currentUser;
    }

    protected Employee createEmployee(String email, String firstName, String lastName, Person person) {
        if (person == null) {
            person = createPerson(null, email, firstName, lastName, null);
        }

        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setId(1L);
        careTeamRole.setDisplayName("Provider");
        careTeamRole.setName(BEHAVIORAL_HEALTH);

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

    protected static Database createDatabase() {
        final Database database = new Database();
        database.setId(TestDataGenerator.randomId());
        database.setName("db name");
        return database;
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

    protected ResidentCareTeamMember createCareTeamMember(Long receiverId, String ssn, String phone, String email, String firstName, String lastName) {
        final ResidentCareTeamMember careTeamMember = new ResidentCareTeamMember();

        careTeamMember.setId(receiverId);
        final Resident resident = createResident(residentId, ssn, phone, email, firstName, lastName);
        careTeamMember.setResident(resident);
        careTeamMember.setResidentId(residentId);
        careTeamMember.setEmergencyContact(false);
        careTeamMember.setCreatedByResidentId(null);
        careTeamMember.setCareTeamRelation(null);
        careTeamMember.setCareTeamRelationship(null);
        careTeamMember.setAccessRights(null);

        return careTeamMember;
    }

    protected ResidentCareTeamMember createCareTeamMember(Long receiverId) {
        final String ssn = TestDataGenerator.randomValidSsn();
        final String firstName = TestDataGenerator.randomName();
        final String lastName = TestDataGenerator.randomName();

        return createCareTeamMember(receiverId, ssn, null, null, firstName, lastName);
    }

}
