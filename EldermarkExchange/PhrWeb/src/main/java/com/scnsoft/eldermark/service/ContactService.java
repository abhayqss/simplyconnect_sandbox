package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.RoleDao;
import com.scnsoft.eldermark.dao.carecoordination.CareTeamRoleDao;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.service.validation.UserValidator;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.OrganizationService;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.web.entity.AddressEditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.scnsoft.eldermark.services.carecoordination.CareCoordinationConstants.SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX;

/**
 * PHR service for create and update operations with {@link Employee} entity.
 *
 * @author phomal
 * Created on 5/11/2017.
 */
@Service
@Transactional
public class ContactService {

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    EmployeeRequestService employeeRequestService;

    @Autowired
    CareTeamRoleDao careTeamRoleDao;

    @Autowired
    RoleDao roleDao;

    @Autowired
    private AddressService addressService;

    @Autowired
    DatabasesService databasesService;

    @Autowired
    OrganizationService organizationService;

    public Employee createEmployeeForInvitedFriend(final Resident patient, String email, String phone, String firstName, String lastName,
                                                   CareTeamRole careTeamRole) {
        // validate missing patient
        if (patient == null) {
            throw new PhrException(PhrExceptionType.NOT_FOUND_PATIENT_INFO_DURING_INVITATION);
        }

        // TODO in which database/organization should we create employee for friend/family CTM ?
        // 1:
//        Database database = patient.getDatabase();
//        Organization organization = patient.getFacility();
        // 2:
        Database database = databasesService.getUnaffiliatedDatabase();
        Organization organization = organizationService.getUnaffiliatedOrganization(database.getId());

        Employee employee = createEmployee(database, organization, email, phone, firstName, lastName, careTeamRole, null, null, null);

        return employee;
    }

    public Employee createEmployeeForPhysician(String email, String phone, String firstName, String lastName,
                                               AddressEditDto address, Person person) {
        Database database = databasesService.getUnaffiliatedDatabase();
        Organization organization = organizationService.getUnaffiliatedOrganization(database.getId());
        // TODO : what Care Team Role should be assigned to self-registered provider (aka physician) ?
        final CareTeamRole careTeamRole = careTeamRoleDao.getByCode(CareTeamRoleCode.ROLE_BEHAVIORAL_HEALTH);

        Employee employee = createEmployee(database, organization, email, phone, firstName, lastName, careTeamRole, address, null, person);

        return employee;
    }

    public Employee createEmployeeForConsumer(String email, String phone, String firstName, String lastName, String encodedPassword) {
        Database database = databasesService.getUnaffiliatedDatabase();
        Organization organization = organizationService.getUnaffiliatedOrganization(database.getId());
        final CareTeamRoleCode code = CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES;
        final CareTeamRole careTeamRole = careTeamRoleDao.getByCode(code);

        return createEmployee(database, organization, email, phone, firstName, lastName, careTeamRole, null, encodedPassword, null);
    }

    private Employee createEmployee(Database database, Organization community, String email, String phone, String firstName, String lastName,
                                    CareTeamRole careTeamRole, AddressEditDto address, String encodedPassword, Person person) {
        // validate possible duplicate
        if (employeeDao.isEmployeeLoginTaken(database.getId(), email)) {
            throw new PhrException("Employee with email: " + email + " already exists.");
        }

        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setLoginName(email);
        if (encodedPassword == null) {
            employee.setPassword("passw0rd"); // just default password user can't login;
            employee.setStatus(EmployeeStatus.PENDING);       // InvitationStatus would be PENDING until this Employee is activated
        } else {
            employee.setPassword(encodedPassword);
            employee.setStatus(EmployeeStatus.ACTIVE);
        }
        employee.setDatabase(database);
        employee.setDatabaseId(database.getId());
        CareCoordinationConstants.setLegacyId(SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX, employee);

        employee.setSecureMessagingActive(false);
        employee.setCareTeamRole(careTeamRole);
        setExchangeRoles(employee, true);
        Long communityId = community.getId();
        employee.setCommunityId(communityId);
        employee.setCreatedAutomatically(false);

        if (person == null) {
            person = createPerson(employee.getDatabase(), email, phone, PersonTelecomCode.WP, firstName, lastName, address);
        }
        employee.setPerson(person);
        employee.setContact4d(Boolean.FALSE);
        // TODO what if CCN company name is null? this employee won't be able to login
        String ccnCompany = database.getSystemSetup() != null ? database.getSystemSetup().getLoginCompanyId() : null;
        employee.setCompany(ccnCompany);
        employee = employeeDao.create(employee);

        employeeDao.flush();

        updateLegacyIds(employee);
        employee = employeeDao.merge(employee);
        employeeDao.flush();
        return employee;
    }

    Person createPersonInUnaffiliated(String email, String phone, String firstName, String lastName, AddressEditDto address) {
        Database database = databasesService.getUnaffiliatedDatabase();
        return createPerson(database, email, phone, PersonTelecomCode.WP, firstName, lastName, address);
    }

    Person createPerson(Database database, String email, String phone, PersonTelecomCode phoneUseCode, String firstName, String lastName, AddressEditDto address) {
        final Person person = new Person();
        CareCoordinationConstants.setLegacyId(SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX, person);
        person.setDatabase(database);
        if (database != null) {
            person.setDatabaseId(database.getId());
        }
        person.setLegacyTable(CareCoordinationConstants.SIMPLYCONNECT_PHR_PERSON_LEGACY_TABLE);

        person.setTelecoms(new ArrayList<PersonTelecom>());
        person.setAddresses(new ArrayList<PersonAddress>());
        PersonAddress addressForPhysician = addressService.createAddressForPhrUser(address, person);
        if (addressForPhysician != null) {
            person.getAddresses().add(addressForPhysician);
        }
        person.setNames(new ArrayList<Name>());
        person.getTelecoms().add(PersonService.createPersonTelecom(person, PersonTelecomCode.EMAIL, email,
                CareCoordinationConstants.SIMPLYCONNECT_PHR_PERSON_TELECOM_LEGACY_TABLE));
        person.getTelecoms().add(PersonService.createPersonTelecom(person, phoneUseCode, phone,
                CareCoordinationConstants.SIMPLYCONNECT_PHR_PERSON_TELECOM_LEGACY_TABLE));
        person.getNames().add(PersonService.createPersonName(person, firstName, lastName,
                CareCoordinationConstants.SIMPLYCONNECT_PHR_NAME_LEGACY_TABLE));
        return person;
    }

    private void setExchangeRoles(Employee employee, Boolean isExchangeEnabled) {
        Set<Role> exchangeRoles = new HashSet<Role>();
        if (Boolean.TRUE.equals(isExchangeEnabled)) {
            exchangeRoles.add(roleDao.findByCode(RoleCode.ROLE_ELDERMARK_USER));
        }
        employee.setRoles(exchangeRoles);
    }

    private static void updateLegacyIds(Employee employee) {
        CareCoordinationConstants.setLegacyId(SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX, employee);
        Person person = employee.getPerson();
        updateLegacyIds(person);
    }

    static void updateLegacyIds(Person person) {
        CareCoordinationConstants.setLegacyId(SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX, person);
        for (PersonTelecom telecom : person.getTelecoms()) {
            CareCoordinationConstants.setLegacyId(telecom);
        }
        for (Name name : person.getNames()) {
            CareCoordinationConstants.setLegacyId(name);
        }
        for (PersonAddress address : person.getAddresses()) {
            CareCoordinationConstants.setLegacyId(SIMPLYCONNECT_PHR_LEGACY_ID_PREFIX, address);
        }
    }

    public void merge(Employee employee) {
        employeeDao.merge(employee);
    }

    public void setPassword(Employee employee, String encodedPassword) {
        employee.setPassword(encodedPassword);
        employee.setStatus(EmployeeStatus.ACTIVE);
        employeeDao.merge(employee);
        employeeRequestService.deleteInvitations(employee);
    }

    @Transactional(readOnly = true)
    public Pair<String, String> getPhoneAndEmail(Long employeeId) {
        final Employee employee = employeeDao.getEmployee(employeeId);
        final Person person = employee.getPerson();
        final String email = PersonService.getPersonTelecomValue(person, PersonTelecomCode.EMAIL);
        final String phone = PersonService.getPersonTelecomValue(person, PersonTelecomCode.WP);
        UserValidator.validatePhoneAndEmailNotEmpty(phone, email);
        return new Pair<>(phone, email);
    }

}
