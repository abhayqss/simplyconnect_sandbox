package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.RoleDao;
import com.scnsoft.eldermark.dao.carecoordination.CareTeamRoleDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.OrganizationService;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.AddressEditDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 6/28/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ContactServiceTest extends BaseServiceTest {

    @Mock
    private EmployeeDao employeeDao;
    @Mock
    private EmployeeRequestService employeeRequestService;
    @Mock
    private CareTeamRoleDao careTeamRoleDao;
    @Mock
    private RoleDao roleDao;
    @Mock
    private AddressService addressService;
    @Mock
    private DatabasesService databasesService;
    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private ContactService contactService;

    private final String phone = TestDataGenerator.randomPhone();
    private final String email = TestDataGenerator.randomEmail();
    private final String firstName = TestDataGenerator.randomName();
    private final String lastName = TestDataGenerator.randomName();

    @Test
    public void testCreateEmployeeForInvitedFriend() {
        // Expected objects
        final Person expectedPerson = createPerson(phone, email, firstName, lastName, PersonTelecomCode.WP);
        expectedPerson.getNames().get(0).setFullName(null);
        expectedPerson.setAddresses(Collections.<PersonAddress>emptyList());
        expectedPerson.getNames().get(0).setMiddle(null);
        final Database database = expectedPerson.getDatabase();
        final Long databaseId = expectedPerson.getDatabaseId();

        final SystemSetup systemSetup = new SystemSetup();
        systemSetup.setLoginCompanyId("TEST3");
        database.setSystemSetup(systemSetup);
        systemSetup.setDatabase(database);

        final Long organizationId = TestDataGenerator.randomId();
        final Organization facility = new Organization();
        facility.setId(organizationId);
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);
        resident.setDatabase(database);
        resident.setFacility(facility);

        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setId(TestDataGenerator.randomId());

        final Role role = new Role();
        role.setId(TestDataGenerator.randomId());
        role.setCode(RoleCode.ROLE_ELDERMARK_USER);

        final Employee expectedEmployee = new Employee();
        expectedEmployee.setDatabase(database);
        expectedEmployee.setDatabaseId(databaseId);
        expectedEmployee.setCommunityId(organizationId);
        expectedEmployee.setLoginName(email);
        expectedEmployee.setStatus(EmployeeStatus.PENDING);
        expectedEmployee.setPassword("passw0rd");
        expectedEmployee.setCareTeamRole(careTeamRole);
        expectedEmployee.setFirstName(firstName);
        expectedEmployee.setLastName(lastName);
        expectedEmployee.setSecureMessagingActive(false);
        expectedEmployee.setCreatedAutomatically(false);
        expectedEmployee.setContact4d(Boolean.FALSE);
        expectedEmployee.setPerson(expectedPerson);
        expectedEmployee.setCompany("TEST3");
        expectedEmployee.setRoles(Collections.singleton(role));

        // Mockito expectations
        when(employeeDao.isEmployeeLoginTaken(databaseId, email)).thenReturn(false);
        when(addressService.createAddressForPhrUser((AddressEditDto) isNull(), any(Person.class))).thenReturn(null);
        when(employeeDao.create(any(Employee.class))).then(returnsFirstArg());
        when(employeeDao.merge(any(Employee.class))).then(returnsFirstArg());
        when(roleDao.findByCode(RoleCode.ROLE_ELDERMARK_USER)).thenReturn(role);
        when(databasesService.getUnaffiliatedDatabase()).thenReturn(database);
        when(organizationService.getUnaffiliatedOrganization(database.getId())).thenReturn(facility);

        // Execute the method being tested
        Employee result = contactService.createEmployeeForInvitedFriend(resident, email, phone, firstName, lastName, careTeamRole);

        // Validation
        assertThat(result, sameBeanAs(expectedEmployee)
                .ignoring("legacyId")
                .ignoring("person.0x1.legacyId").ignoring("person.0x1.legacyTable").ignoring("person.0x1.id")
                .ignoring("person.0x1.telecoms.legacyId").ignoring("person.0x1.telecoms.legacyTable")
                .ignoring("person.0x1.names.legacyId").ignoring("person.0x1.names.legacyTable"));
        verify(employeeDao).create(any(Employee.class));
    }

    @Test(expected = PhrException.class)
    public void testCreateEmployeeForInvitedFriendThrowsEmailInUse() {
        // Expected objects
        final Database database = createDatabase();
        final Long databaseId = database.getId();
        final Long organizationId = TestDataGenerator.randomId();
        final Organization facility = new Organization();
        facility.setId(organizationId);
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);
        resident.setDatabase(database);
        resident.setFacility(facility);

        // Mockito expectations
        when(employeeDao.isEmployeeLoginTaken(databaseId, email)).thenReturn(true);
        when(databasesService.getUnaffiliatedDatabase()).thenReturn(database);
        when(organizationService.getUnaffiliatedOrganization(database.getId())).thenReturn(facility);

        // Execute the method being tested
        contactService.createEmployeeForInvitedFriend(resident, email, phone, firstName, lastName, new CareTeamRole());

        // Validation
        verify(employeeDao, never()).create(any(Employee.class));
    }
/*
    @Test
    public void testCreateEmployeeForPhysician() throws Exception {
        // Expected objects
        final Person expectedPerson = createPerson(phone, email, firstName, lastName, PersonTelecomCode.WP);
        expectedPerson.getNames().get(0).setFullName(null);
        expectedPerson.setAddresses(Collections.<PersonAddress>emptyList());
        expectedPerson.getNames().get(0).setMiddle(null);
        final Database database = expectedPerson.getDatabase();
        final Long databaseId = expectedPerson.getDatabaseId();

        final SystemSetup systemSetup = new SystemSetup();
        systemSetup.setLoginCompanyId("TEST3");
        database.setSystemSetup(systemSetup);
        systemSetup.setDatabase(database);

        final Long organizationId = TestDataGenerator.randomId();
        final Organization facility = new Organization();
        facility.setId(organizationId);
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);
        resident.setDatabase(database);
        resident.setFacility(facility);

        // TODO change role as soon as hardcoded care team role will be removed from ContactService
        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH);
        careTeamRole.setId(TestDataGenerator.randomId());

        final Role role = new Role();
        role.setId(TestDataGenerator.randomId());
        role.setCode(RoleCode.ROLE_ELDERMARK_USER);

        final Employee expectedEmployee = new Employee();
        expectedEmployee.setDatabase(database);
        expectedEmployee.setDatabaseId(databaseId);
        expectedEmployee.setCommunityId(organizationId);
        expectedEmployee.setLoginName(email);
        expectedEmployee.setInactive(true);
        expectedEmployee.setPassword("passw0rd");
        expectedEmployee.setCareTeamRole(careTeamRole);
        expectedEmployee.setFirstName(firstName);
        expectedEmployee.setLastName(lastName);
        expectedEmployee.setSecureMessagingActive(false);
        expectedEmployee.setCreatedAutomatically(false);
        expectedEmployee.setContact4d(Boolean.FALSE);
        expectedEmployee.setPerson(expectedPerson);
        expectedEmployee.setCompany("TEST3");
        expectedEmployee.setRoles(Collections.singleton(role));

        // Mockito expectations
        when(databasesService.getUnaffiliatedDatabase()).thenReturn(database);
        when(organizationService.getUnaffiliatedOrganization(database.getId())).thenReturn(facility);
        when(employeeDao.isEmployeeLoginTaken(databaseId, email)).thenReturn(false);
        when(addressService.createAddressForPhrUser((AddressEditDto) isNull(), any(Person.class))).thenReturn(null);
        when(employeeDao.create(any(Employee.class))).then(returnsFirstArg());
        when(employeeDao.merge(any(Employee.class))).then(returnsFirstArg());
        when(roleDao.findByCode(RoleCode.ROLE_ELDERMARK_USER)).thenReturn(role);
        when(careTeamRoleDao.getByCode(CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH)).thenReturn(careTeamRole);

        // Execute the method being tested
        Employee result = contactService.createEmployeeForPhysician(email, phone, firstName, lastName, null);

        // Validation
        assertThat(result, sameBeanAs(expectedEmployee)
                .ignoring("legacyId")
                .ignoring("person.0x1.legacyId").ignoring("person.0x1.legacyTable").ignoring("person.0x1.id")
                .ignoring("person.0x1.telecoms.legacyId").ignoring("person.0x1.telecoms.legacyTable")
                .ignoring("person.0x1.names.legacyId").ignoring("person.0x1.names.legacyTable"));
        verify(employeeDao).create(any(Employee.class));
    }

    @Test
    public void testCreateEmployeeForPhysicianWithAddress() throws Exception {
        // Expected objects
        final AddressEditDto address = new AddressEditDto();
        address.setCity("city");
        address.setState("state");
        address.setStreetAddress("street");
        address.setPostalCode("12345");

        final Person expectedPerson = createPerson(phone, email, firstName, lastName, PersonTelecomCode.WP);
        expectedPerson.getNames().get(0).setFullName(null);
        expectedPerson.getNames().get(0).setMiddle(null);
        final Database database = expectedPerson.getDatabase();
        final Long databaseId = expectedPerson.getDatabaseId();

        final PersonAddress expectedPersonAddress = PersonAddress.Builder.aPersonAddress()
                .withDatabase(database)
                .withStreetAddress("street")
                .withPostalAddressUse("HP")
                .withCity("city")
                .withCountry("US")
                .withPostalCode("12345")
                .build();
        expectedPerson.setAddresses(Collections.singletonList(expectedPersonAddress));

        final SystemSetup systemSetup = new SystemSetup();
        systemSetup.setLoginCompanyId("TEST3");
        database.setSystemSetup(systemSetup);
        systemSetup.setDatabase(database);

        final Long organizationId = TestDataGenerator.randomId();
        final Organization facility = new Organization();
        facility.setId(organizationId);
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);
        resident.setDatabase(database);
        resident.setFacility(facility);

        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH);
        careTeamRole.setId(TestDataGenerator.randomId());

        final Role role = new Role();
        role.setId(TestDataGenerator.randomId());
        role.setCode(RoleCode.ROLE_ELDERMARK_USER);

        final Employee expectedEmployee = new Employee();
        expectedEmployee.setDatabase(database);
        expectedEmployee.setDatabaseId(databaseId);
        expectedEmployee.setCommunityId(organizationId);
        expectedEmployee.setLoginName(email);
        expectedEmployee.setInactive(true);
        expectedEmployee.setPassword("passw0rd");
        expectedEmployee.setCareTeamRole(careTeamRole);
        expectedEmployee.setFirstName(firstName);
        expectedEmployee.setLastName(lastName);
        expectedEmployee.setSecureMessagingActive(false);
        expectedEmployee.setCreatedAutomatically(false);
        expectedEmployee.setContact4d(Boolean.FALSE);
        expectedEmployee.setPerson(expectedPerson);
        expectedEmployee.setCompany("TEST3");
        expectedEmployee.setRoles(Collections.singleton(role));

        // Mockito expectations
        when(databasesService.getUnaffiliatedDatabase()).thenReturn(database);
        when(organizationService.getUnaffiliatedOrganization(database.getId())).thenReturn(facility);
        when(employeeDao.isEmployeeLoginTaken(databaseId, email)).thenReturn(false);
        when(addressService.createAddressForPhrUser(eq(address), any(Person.class))).thenReturn(expectedPersonAddress);
        when(employeeDao.create(any(Employee.class))).then(returnsFirstArg());
        when(employeeDao.merge(any(Employee.class))).then(returnsFirstArg());
        when(roleDao.findByCode(RoleCode.ROLE_ELDERMARK_USER)).thenReturn(role);
        when(careTeamRoleDao.getByCode(CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH)).thenReturn(careTeamRole);

        // Execute the method being tested
        Employee result = contactService.createEmployeeForPhysician(email, phone, firstName, lastName, address);

        // Validation
        assertThat(result, sameBeanAs(expectedEmployee)
                .ignoring("legacyId")
                .ignoring("person.0x1.legacyId").ignoring("person.0x1.legacyTable").ignoring("person.0x1.id")
                .ignoring("person.0x1.telecoms.legacyId").ignoring("person.0x1.telecoms.legacyTable")
                .ignoring("person.0x1.names.legacyId").ignoring("person.0x1.names.legacyTable"));
        verify(employeeDao).create(result);
    }

    @Test
    public void testCreateEmployeeForConsumer() throws Exception {
        // Expected objects
        final String sPassword = "secret";
        final char[] password = sPassword.toCharArray();

        final Person expectedPerson = createPerson(phone, email, firstName, lastName, PersonTelecomCode.WP);
        expectedPerson.getNames().get(0).setFullName(null);
        expectedPerson.setAddresses(Collections.<PersonAddress>emptyList());
        expectedPerson.getNames().get(0).setMiddle(null);
        final Database database = expectedPerson.getDatabase();
        final Long databaseId = expectedPerson.getDatabaseId();

        final SystemSetup systemSetup = new SystemSetup();
        systemSetup.setLoginCompanyId("TEST3");
        database.setSystemSetup(systemSetup);
        systemSetup.setDatabase(database);

        final Long organizationId = TestDataGenerator.randomId();
        final Organization facility = new Organization();
        facility.setId(organizationId);
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);
        resident.setDatabase(database);
        resident.setFacility(facility);

        final CareTeamRole careTeamRole = new CareTeamRole();
        careTeamRole.setCode(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);
        careTeamRole.setId(TestDataGenerator.randomId());

        final Role role = new Role();
        role.setId(TestDataGenerator.randomId());
        role.setCode(RoleCode.ROLE_ELDERMARK_USER);

        final Employee expectedEmployee = new Employee();
        expectedEmployee.setDatabase(database);
        expectedEmployee.setDatabaseId(databaseId);
        expectedEmployee.setCommunityId(organizationId);
        expectedEmployee.setLoginName(email);
        expectedEmployee.setInactive(false);
        expectedEmployee.setPassword("ignored");
        expectedEmployee.setCareTeamRole(careTeamRole);
        expectedEmployee.setFirstName(firstName);
        expectedEmployee.setLastName(lastName);
        expectedEmployee.setSecureMessagingActive(false);
        expectedEmployee.setCreatedAutomatically(false);
        expectedEmployee.setContact4d(Boolean.FALSE);
        expectedEmployee.setPerson(expectedPerson);
        expectedEmployee.setCompany("TEST3");
        expectedEmployee.setRoles(Collections.singleton(role));

        // Mockito expectations
        when(databasesService.getUnaffiliatedDatabase()).thenReturn(database);
        when(organizationService.getUnaffiliatedOrganization(database.getId())).thenReturn(facility);
        when(employeeDao.isEmployeeLoginTaken(databaseId, email)).thenReturn(false);
        when(addressService.createAddressForPhrUser((AddressEditDto) isNull(), any(Person.class))).thenReturn(null);
        when(employeeDao.create(any(Employee.class))).then(returnsFirstArg());
        when(employeeDao.merge(any(Employee.class))).then(returnsFirstArg());
        when(roleDao.findByCode(RoleCode.ROLE_ELDERMARK_USER)).thenReturn(role);
        when(careTeamRoleDao.getByCode(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES)).thenReturn(careTeamRole);

        // Execute the method being tested
        Employee result = contactService.createEmployeeForConsumer(email, phone, firstName, lastName, password);

        // Validation
        assertThat(result, sameBeanAs(expectedEmployee)
                .ignoring("legacyId")
                .ignoring("password")
                .ignoring("person.0x1.legacyId").ignoring("person.0x1.legacyTable").ignoring("person.0x1.id")
                .ignoring("person.0x1.telecoms.legacyId").ignoring("person.0x1.telecoms.legacyTable")
                .ignoring("person.0x1.names.legacyId").ignoring("person.0x1.names.legacyTable"));
        assertNotEquals(result.getPassword(), "passw0rd");
        assertNotEquals(result.getPassword(), "password");
        verify(employeeDao).create(any(Employee.class));
    }
*/
    @Test
    public void testMerge() {
        // Expected objects
        final Employee employee = new Employee();

        // Mockito expectations
        when(employeeDao.merge(any(Employee.class))).then(returnsFirstArg());

        // Execute the method being tested
        contactService.merge(employee);

        // Validation
        verify(employeeDao).merge(employee);
    }
/*
    @Test
    public void testSetPassword() {
        // Expected objects
        final Employee employee = new Employee();
        employee.setInactive(true);
        employee.setPassword("password");
        final char[] password = "new secure password".toCharArray();

        // Mockito expectations
        when(employeeDao.merge(any(Employee.class))).then(returnsFirstArg());

        // Execute the method being tested
        contactService.setPassword(employee, password);

        // Validation
        assertFalse(employee.isInactive());
        assertNotEquals("password", employee.getPassword());

        verify(employeeDao).merge(employee);
        verify(employeeRequestService).deleteInvitations(employee);
    }
*/
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme