package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.LinkedEmployeesDao;
import com.scnsoft.eldermark.dao.RoleDao;
import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationCommunityDao;
import com.scnsoft.eldermark.dao.carecoordination.CareTeamRoleDao;
import com.scnsoft.eldermark.dao.exceptions.EmailAlreadyExistsException;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Created by pzhurba on 29-Oct-15.
 */
@Service
public class ContactServiceImpl implements ContactService {
    private static final Logger logger = LoggerFactory.getLogger(ContactServiceImpl.class);

    @Autowired
    private EmployeeDao employeeDao;
    @Autowired
    private CareTeamRoleDao careTeamRoleDao;
    @Autowired
    private AddressService addressService;
    @Autowired
    private EmployeeRequestService employeeRequestService;
    @Autowired
    CareCoordinationResidentService careCoordinationResidentService;
    @Autowired
    private DatabasesDao databasesDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PersonService personService;
    @Autowired
    private CareCoordinationCommunityDao careCoordinationCommunityDao;
    @Autowired
    LinkedEmployeesDao linkedEmployeesDao;
    @Autowired
    private CommunityCrudService communityCrudService;
    @Autowired
    private CareTeamRoleService careTeamRoleService;
    @Autowired
    private OrganizationService organizationService;

    private Map<CareTeamRoleCode, List<RoleCode>> CC_TO_EEX_ROLES_MAPPING;
    private @Value("${secure.email.domain}") String ELDERMARK_DOMAIN;

    @PostConstruct
    public void initRolesMapping() {
        CC_TO_EEX_ROLES_MAPPING = new HashMap<CareTeamRoleCode, List<RoleCode>>();
        CC_TO_EEX_ROLES_MAPPING.put(CareTeamRoleCode.ROLE_ADMINISTRATOR,
                Arrays.asList(
                        RoleCode.ROLE_MANAGER,
                        RoleCode.ROLE_DIRECT_MANAGER));
        CC_TO_EEX_ROLES_MAPPING.put(CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR,
                Arrays.asList(
                        RoleCode.ROLE_MANAGER,
                        RoleCode.ROLE_DIRECT_MANAGER,
                        RoleCode.ROLE_SUPER_MANAGER));
    }


    @Override
    public Page<ContactListItemDto> list(ContactFilterDto contactFilter, Pageable pageRequest) {
        final List<ContactListItemDto> result = new ArrayList<ContactListItemDto>();
        final Database database = databasesDao.getDatabaseById(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId());

        boolean canAddEditAllContacts = SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_ALL_CONTACTS);
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        Set<Long> employeeIds = userDetails.getEmployeeAndLinkedEmployeeIds();
//        Long communityId = null;
//        if (SecurityUtils.hasRole(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)){
//            communityId = SecurityUtils.getAuthenticatedUser().getCommunityId();
//        }

        Map<Long, Set<CareTeamRoleCode>> employeeRolesAbleToEdit = new HashMap<Long, Set<CareTeamRoleCode>>();
        Set<Long> affiliatedOrgIds = null;
        if (SecurityUtils.isAffiliatedView()) {
            affiliatedOrgIds = organizationService.getAffiliatedOrgIds(userDetails.getCurrentDatabaseId());
        }

        final List<Employee> employees = employeeDao.getEmployeeForDatabase(database, contactFilter, pageRequest, userDetails.getEmployeeId());
        for (Employee employee : employees) {
            ContactListItemDto item = createContactListItemDto(employee);
            if (!SecurityUtils.isAffiliatedView()) {
                boolean canEditItem = false;
                Set<Long> databaseEmployeeIds = userDetails.getEmployeeIdsForCurrentDatabase();
                for (Long employeeId : databaseEmployeeIds) {
                    Long employeeCommunityId = userDetails.getLinkedEmployeeById(employeeId).getCommunityId();
                    Set<CareTeamRoleCode> roleListAbleToEdit;
                    if (employeeRolesAbleToEdit.containsKey(employeeId)) {
                        roleListAbleToEdit =employeeRolesAbleToEdit.get(employeeId);
                    } else {
                        roleListAbleToEdit = new HashSet<CareTeamRoleCode>();
                        Set<GrantedAuthority> currentEmployeeAuthorities = userDetails.getEmployeeAuthoritiesMap().get(employeeId);
                        Set<CareTeamRoleCode> currentEmployeeRoleCodes = SecurityUtils.getCareTeamRoleCodes(currentEmployeeAuthorities);
                        for (CareTeamRoleCode currentEmployeeRoleCode : currentEmployeeRoleCodes) {
                            roleListAbleToEdit.addAll(CareTeamRoleCode.getRoleListAbleForEditing(currentEmployeeRoleCode));
                        }
                    }
                    canEditItem = canEditItem || ((employee.getCareTeamRole() == null || roleListAbleToEdit.contains(employee.getCareTeamRole().getCode())) &&
                            (canAddEditAllContacts ||
                                    (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_EDIT_COMMUNITY_CONTACTS) && employeeCommunityId != null && employeeCommunityId.equals(employee.getCommunityId()))));
                }
                if (!canEditItem) {
                    canEditItem = canEditItem || employeeIds.contains(employee.getId());
                    canEditItem = canEditItem || canAddEditAllContacts;
                }
                item.setEditable(canEditItem);
                if (!SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) && (employee.getCareTeamRole() != null && employee.getCareTeamRole().getCode() == CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR)) {
                    item.setEditable(false);
                }
            }
            if (!item.isEditable()) {
                if (!SecurityUtils.isAffiliatedView() && SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(userDetails.getCurrentDatabaseId()), CareTeamRoleCode.ROLES_CAN_VIEW_CONTACTS)) {
                    item.setViewOnly(true);
                }
                if (SecurityUtils.isAffiliatedView()) {
                    if (CollectionUtils.isNotEmpty(affiliatedOrgIds)) {
                        for (Long affiliatedOrgId : affiliatedOrgIds) {
                            if (SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(affiliatedOrgId), CareTeamRoleCode.ROLES_CAN_VIEW_CONTACTS)) {
                                item.setViewOnly(true);
                                break;
                            }
                        }
                    }
                }
            }
            result.add(item);
        }
        return new PageImpl<>(result, pageRequest, employeeDao.getEmployeeForDatabaseCount(database, contactFilter));
    }

    @Override
    public List<CareTeamRoleDto> getCareTeamRolesToEdit(ContactDto contact) {
        boolean canAddEditAllContacts = SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_ALL_CONTACTS);
        boolean canEditItem = false;
        Set<CareTeamRoleCode> result  = new HashSet<CareTeamRoleCode>();
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            result.addAll(CareTeamRoleCode.getRoleListAbleForEditing(CareTeamRoleCode.ROLE_SUPER_ADMINISTRATOR));
        }
        Set<Long> databaseEmployeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeIdsForCurrentDatabase();
        for (Long employeeId : databaseEmployeeIds) {
            Set<CareTeamRoleCode> roleListAbleToEdit  = new HashSet<CareTeamRoleCode>();
            Long employeeCommunityId = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId).getCommunityId();
            Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
            Set<CareTeamRoleCode> currentEmployeeRoleCodes = SecurityUtils.getCareTeamRoleCodes(currentEmployeeAuthorities);
            for (CareTeamRoleCode currentEmployeeRoleCode : currentEmployeeRoleCodes) {
                roleListAbleToEdit.addAll(CareTeamRoleCode.getRoleListAbleForEditing(currentEmployeeRoleCode));
            }
            canEditItem = (contact.getRoleCode() == null || roleListAbleToEdit.contains(contact.getRoleCode())) &&
                    (canAddEditAllContacts ||
                            (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_EDIT_COMMUNITY_CONTACTS) && employeeCommunityId != null && employeeCommunityId.equals(contact.getCommunityId())));
            if (!canEditItem) {
                canEditItem = employeeId.equals(contact.getId());
            }
            if (canEditItem) {
                result.addAll(roleListAbleToEdit);
            }
        }
        List<CareTeamRoleDto> arrayResult = new ArrayList<CareTeamRoleDto>();
        arrayResult.add(new CareTeamRoleDto(null, "-- Select Role --", null));
        for (CareTeamRoleDto role : careTeamRoleService.getAllCareTeamRoles()) {
            if (result.contains(role.getCode())) {
                arrayResult.add(role);
            }
        }
        return arrayResult;
    }

    @Override
    public List<CareTeamRoleDto> getAllCareTeamRolesToEdit() {
        final Set<CareTeamRoleDto> result = new HashSet<CareTeamRoleDto>();
        for (CareTeamRoleDto role : careTeamRoleService.getAllCareTeamRoles()) {
            if (SecurityUtils.hasRole(role.getCode().getCode())) {
                for (CareTeamRoleDto code : careTeamRoleService.getAllCareTeamRoles()) {
                    if (canAdd(role.getCode(), code.getCode())) {
                        result.add(code);
                    }
                }
            }
        }
        List<CareTeamRoleDto> arrayResult = new ArrayList<CareTeamRoleDto>();
        arrayResult.add(new CareTeamRoleDto(null, "-- Select Role --", null));
        for (CareTeamRoleDto role : careTeamRoleService.getAllCareTeamRoles()) {
            if (result.contains(role)) {
                arrayResult.add(role);
            }
        }
        return arrayResult;
    }


    public  boolean isValidContact(ContactDto dto) {
        return (StringUtils.isNotBlank(dto.getFirstName()) && StringUtils.isNotBlank(dto.getLastName()) &&
                StringUtils.isNotBlank(dto.getEmail()) && dto.getCommunityId()!=null && dto.getOrganization().getId()!=null &&
                dto.getAddress()!=null && StringUtils.isNotBlank(dto.getAddress().getStreet()) &&
                        StringUtils.isNotBlank(dto.getAddress().getCity()) && dto.getAddress().getState()!=null &&
                        StringUtils.isNotBlank(dto.getAddress().getZip()) &&  StringUtils.isNotBlank(dto.getPhone()));
    }

    public  boolean isValidContact(Employee targetEmployee) {
        return isValidContact(createContactDto(targetEmployee, null));
    }

    private boolean canAdd(final CareTeamRoleCode loggedRole, final CareTeamRoleCode roleToAdd) {
        return (CareTeamRoleCode.getRoleListAbleForEditing(loggedRole).contains(roleToAdd)) ;
    }

    /*@Override
    public Page<ContactListItemDto> getContactListItemDto(final Set<Long> employeeIds, final ContactFilterDto contactFilter, final Pageable pageRequest) {
        List<ContactListItemDto> result = new ArrayList<ContactListItemDto>();
        if (!CollectionUtils.isEmpty(employeeIds)) {
            for (Long employeeId: employeeIds) {
                ContactListItemDto contactListItemDto = createContactListItemDto(employeeDao.get(employeeId));
                contactListItemDto.setEditable(true);
                result.add(contactListItemDto);
            }
        }

        return new PageImpl<ContactListItemDto>(result, pageRequest, result.size());
    }*/

    @Override
    public ContactDto createOrUpdate(final Employee creator, final ContactDto contact, final Database database, Boolean createdAutomatically) {
        CareTeamRoleCode oldRole = null;
        String oldSecureEmail = null;
        final boolean isNew = contact.getId() == null;
        Employee employee;

        if (isNew) {
            if (employeeDao.getEmployeeByLogin(database.getId(), contact.getEmail()) != null) {
                throw new EmailAlreadyExistsException(contact.getEmail());
            }

            employee = new Employee();
            employee.setFirstName(contact.getFirstName());
            employee.setLastName(contact.getLastName());
            employee.setLoginName(contact.getEmail());
            employee.setPassword("password"); // just default password user can't login;
            employee.setStatus(EmployeeStatus.PENDING);
            employee.setDatabase(database);
            CareCoordinationConstants.setLegacyId(employee);
            if (StringUtils.isNotBlank(contact.getSecureMessaging())) {
                String newSecureEmail = contact.getSecureMessaging();
                if (employeeDao.isSecureEmailExist(newSecureEmail)) {
                    throw new EmailAlreadyExistsException(newSecureEmail);
                }
                employee.setSecureMessaging(newSecureEmail);
            }
            employee.setSecureMessagingActive(false);
            CareTeamRole careTeamRole = careTeamRoleDao.findOne(contact.getRole().getId());
            employee.setCareTeamRole(careTeamRole);
            setExchangeRoles(employee, careTeamRole, contact.getEnabledExchange());
            Long communityId = createdAutomatically||SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_ALL_CONTACTS) ? contact.getCommunityId() : creator.getCommunityId();
            employee.setCommunityId(communityId);
            employee.setCreatedAutomatically(createdAutomatically);

            final Person person = new Person();
            CareCoordinationConstants.setLegacyId(person);
            person.setDatabase(employee.getDatabase());
            person.setLegacyTable(CareCoordinationConstants.RBA_PERSON_LEGACY_TABLE);

            person.setTelecoms(new ArrayList<PersonTelecom>());
            person.setAddresses(new ArrayList<PersonAddress>());
            person.setNames(new ArrayList<Name>());

            person.getAddresses().add(addressService.createPersonAddress(database, person, contact.getAddress()));

            person.getTelecoms().add(PersonService.createPersonTelecom(person, PersonTelecomCode.EMAIL, contact.getEmail(),
                    CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE));
            person.getTelecoms().add(PersonService.createPersonTelecom(person, PersonTelecomCode.WP, contact.getPhone(),
                    CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE));
            if (StringUtils.isNotBlank(contact.getFax())) {
                person.getTelecoms().add(PersonService.createPersonTelecom(person, PersonTelecomCode.FAX, contact.getPhone(),
                        CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE));
            }

            person.getNames().add(PersonService.createPersonName(person, contact.getFirstName(), contact.getLastName(),
                    CareCoordinationConstants.RBA_NAME_LEGACY_TABLE));

            employee.setPerson(person);
            employee.setContact4d(Boolean.FALSE);
            employee.setCompany(contact.getCompany());
            employee.setQaIncidentReports(contact.getQaIncidentReports());
            employee = employeeDao.create(employee);

        } else {
            employee = employeeDao.getEmployee(contact.getId());
            oldRole = employee.getCareTeamRole() == null ? null : employee.getCareTeamRole().getCode();

            CareTeamRole careTeamRole = careTeamRoleDao.findOne(contact.getRole().getId());
            employee.setCareTeamRole(careTeamRole);
            Long communityId = contact.getCommunityId() != null ?
                    contact.getCommunityId() : (employee.getCommunityId() != null ? employee.getCommunityId() : creator.getCommunityId());
            employee.setCommunityId(communityId);
            if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
                employee.setDatabase(database);
            }

            if (SecurityUtils.hasRole(RoleCode.ROLE_DIRECT_MANAGER) || SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_EDIT_SECURE_MESSAGING)) {
                setExchangeRoles(employee, careTeamRole, contact.getEnabledExchange());
                oldSecureEmail = employee.getSecureMessaging();
                String newSecureEmail = contact.getSecureMessaging();
                boolean emailChanged = !(oldSecureEmail == null ? newSecureEmail == null : oldSecureEmail.equals(newSecureEmail));
                if (emailChanged) {
                    if (StringUtils.isNotBlank(newSecureEmail) && employeeDao.isSecureEmailExist(newSecureEmail)) {
                        throw new EmailAlreadyExistsException(newSecureEmail);
                    }
                    employee.setSecureMessaging(newSecureEmail);
                    employee.setSecureMessagingActive(false);
                }
            }

            // NPE check can be removed after full sync of employees
            if (employee.getPerson() != null) {

                if (CollectionUtils.isEmpty(employee.getPerson().getAddresses())) {
                    employee.getPerson().getAddresses().add(addressService.createPersonAddress(database, employee.getPerson(), contact.getAddress()));
                } else {
                    PersonAddress personAddress = employee.getPerson().getAddresses().get(0); // TODO Clarify
                    addressService.updatePersonAddress(personAddress, contact.getAddress());
                }

                personService.updateOrCreatePersonTelecom(employee, PersonTelecomCode.WP, contact.getPhone(),
                        CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE);
                if (employee.getContact4d()) {
                    personService.updateOrCreatePersonTelecom(employee, PersonTelecomCode.EMAIL, contact.getEmail(),
                            CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE);
                }
                personService.updateOrCreatePersonTelecom(employee, PersonTelecomCode.FAX, contact.getFax(),
                        CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE);

                if (StringUtils.isBlank(employee.getFirstName()) || StringUtils.isBlank(employee.getLastName())) {
                    PersonService.updateOrCreatePersonName(employee.getPerson(), contact.getFirstName(), contact.getLastName(),
                            CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);
                }
            }
            if (StringUtils.isBlank(employee.getFirstName())) {
                employee.setFirstName(contact.getFirstName());
            }
            if (StringUtils.isBlank(employee.getLastName())) {
                employee.setLastName(contact.getLastName());
            }
            employee.setCompany(contact.getCompany());
            employee.setQaIncidentReports(contact.getQaIncidentReports());
            employee = employeeDao.merge(employee);
        }
        employeeDao.flush();

        updateLegacyIds(employee);
        employee = employeeDao.merge(employee);
        employeeDao.flush();

        if (isNew) {
            if (createdAutomatically) {
                employeeRequestService.createInvitationTokenForAutoCreated(database, employee);
            } else {
                employeeRequestService.createInvitationToken(creator, employee);
            }
        }

        return createContactDto(employee, oldRole, oldSecureEmail);
    }

    private void setExchangeRoles(Employee employee, CareTeamRole careTeamRole, Boolean isExchangeEnabled) {
        Set<Role> exchangeRoles = new HashSet<Role>();
        if (Boolean.TRUE.equals(isExchangeEnabled)) {
            exchangeRoles.add(roleDao.findByCode(RoleCode.ROLE_ELDERMARK_USER));

            if(CC_TO_EEX_ROLES_MAPPING.containsKey(careTeamRole.getCode())) {
                for(RoleCode roleCode : CC_TO_EEX_ROLES_MAPPING.get(careTeamRole.getCode()))
                    exchangeRoles.add(roleDao.findByCode(roleCode));
            }
        }
        employee.setRoles(exchangeRoles);
    }

    private void updateLegacyIds(Employee employee) {
        CareCoordinationConstants.setLegacyId(employee);

        Person person = employee.getPerson();
        // NPE check can be removed after full sync of employees
        if (person != null) {
            CareCoordinationConstants.setLegacyId(person);

            for (PersonAddress personAddress : person.getAddresses()) {
                CareCoordinationConstants.setLegacyId(personAddress);
            }

            for (PersonTelecom telecom : person.getTelecoms()) {
                CareCoordinationConstants.setLegacyId(telecom);
            }
        }
    }

    @Override
    public ContactDto getContact(Long contactId) {
        Employee employee = employeeDao.get(contactId);
        return createContactDto(employee, null);
    }

    @Override
    public void createResetPasswordToken(final String login, final String databaseCode) {
        Database database = databasesDao.getDatabaseByCompanyId(databaseCode);
        try {
            final Employee employee = employeeDao.getEmployeeByLogin(database.getId(), login);
            if (employee != null && employee.getStatus().equals(EmployeeStatus.ACTIVE)) {
                employeeRequestService.createResetPasswordToken(employee);
            }
        } catch (Exception e) {
            logger.error("Can't found rba user with login " + login, e);
        }
    }

    public List<KeyValueDto> searchEmployee(Long databaseId, Long communityId, String searchString) {
        final List<KeyValueDto> result = new ArrayList<KeyValueDto>();

        for (SimpleEmployee employee : employeeDao.getEmployeeList(databaseId, communityId, searchString)) {
//            if (checkAccessToEmployee(employee)) {
                result.add(convertEmployee(employee));
//            }
        }
        return result;
    }

    @Override
    public List<KeyValueDto> getEmployeeSelectList(Long communityId, Long patientId, boolean affiliated, Set<Long> employeeIdsAvailableForPatient, Long careTeamMemberEmployeeId) {
        Set<KeyValueDto> result = new HashSet<KeyValueDto>();
        if (affiliated) {
            if(SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
                if (communityId == null){
                    communityId = careCoordinationResidentService.getCommunityId(patientId);
                }
                result.addAll(getAffiliatedEmployees(communityId));
            } else {
                Set<Long> employeeIds = CollectionUtils.isEmpty(employeeIdsAvailableForPatient) ? SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds() : employeeIdsAvailableForPatient;
                if (communityId == null){
                    communityId = careCoordinationResidentService.getCommunityId(patientId);
                }
                if (CollectionUtils.isNotEmpty(employeeIds)) {
                    for (Long employeeId :  employeeIds) {
                        Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                        LinkedContactDto employee = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId);
                        if(SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                            List<Long> communityIds = communityCrudService.getUserCommunityIds(false, employeeId, false);
                            //communityIds == null in case when affiliated community admin have access to all communities from primary database
                            if (communityIds == null || (communityIds != null && communityIds.contains(communityId))) {
                                result.addAll(searchEmployee(employee.getDatabaseId(), employee.getCommunityId(), null));
                            }
                        } else if (patientId == null && SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_AFF_COMMUNITY_CARE_TEAM_MEMBERS)) {
                            List<Long> communityIds = communityCrudService.getUserCommunityIds(false, employeeId, false);
                            if (communityIds == null || (communityIds != null && communityIds.contains(communityId))) {
                                result.addAll(searchEmployee(employee.getDatabaseId(),null,null));
                            }
                        } else if (patientId != null && SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_AFF_PATIENT_CARE_TEAM_MEMBERS)) {
                            result.addAll(searchEmployee(employee.getDatabaseId(),null,null));
                        }
                    }
                }
                /*if (CollectionUtils.isEmpty(result)) {
                }*/
            }
        }
        else {
            boolean communityOnly = true;
            Set<Long> employeeIds = CollectionUtils.isEmpty(employeeIdsAvailableForPatient) ? SecurityUtils.getAuthenticatedUser().getEmployeeIdsForCurrentDatabase(): employeeIdsAvailableForPatient;
            for (Long employeeId : employeeIds) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
                if ( (patientId == null && SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITY_CARE_TEAM_MEMBERS))
                        || (patientId != null && SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_PATIENT_CARE_TEAM_MEMBER))) {
                    communityOnly = false;
                    break;
                }
                if ((patientId == null && SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_EDIT_SELF_ONLY_COMMUNITY_CARE_TEAM_MEMBERS))
                        || (patientId != null && SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_EDIT_SELF_ONLY_PATIENT_CARE_TEAM_MEMBER))) {
                    if (careTeamMemberEmployeeId != null && careTeamMemberEmployeeId.equals(employeeId)) {
                        result.add(new KeyValueDto(employeeId, employeeDao.get(employeeId).getFullName()));
                    }
                }
            }
            if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
                communityOnly = false;
            }
            if (communityOnly && communityId == null) {
                communityId = careCoordinationResidentService.getCommunityId(patientId);
            }
            result.addAll(searchEmployee(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(), communityOnly ? communityId : null, null));
        }
        return new ArrayList<KeyValueDto>(result);
    }


    public List<KeyValueDto> getAffiliatedEmployees(Long organizationId) {
        final List<KeyValueDto> result = new ArrayList<KeyValueDto>();

        for (SimpleEmployee employee : employeeDao.getAffiliatedEmployeeList(organizationId, SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId())) {
                result.add(convertEmployee(employee));
        }
        return result;
    }

    /**
     * Get linked contact dto's
     * @param currentEmployeeId used to detect current account and place it at the beginning of list
     * @param employees
     * @return
     */
    private List<LinkedContactDto> getLinkedContacts(Long currentEmployeeId, List<Employee> employees) {
        List<LinkedContactDto> contactDtos = new ArrayList<LinkedContactDto>();
        for (Employee employee :  employees) {
            if (currentEmployeeId.equals(employee.getId())) {
                contactDtos.add(0, createLinkedContactDto(employee));
            } else {
                contactDtos.add(createLinkedContactDto(employee));
            }
        }
        return contactDtos;
    }

    @Override
    public List<ContactDto> getContacts(List<Long> contactIds) {
        List<Employee> employees = employeeDao.getEmployees(contactIds);
        List<ContactDto> contactDtos = new ArrayList<ContactDto>();
        for (Employee employee :  employees) {
            contactDtos.add(createContactDto(employee, null));
        }
        return contactDtos;
    }

    //    public List<KeyValueDto> getAffiliatedEmployeesForResident(Long residentId) {
//        final List<KeyValueDto> result = new ArrayList<KeyValueDto>();
//
//        for (Employee employee : employeeDao.getAffiliatedEmployeesForResident(residentId)) {
//            if (checkAccessToEmployee(employee)) {
//                result.add(convertEmployee(employee));
//            }
//        }
//        return result;
//    }

    private ContactDto createContactDto(final Employee employee, final CareTeamRoleCode oldCode) {
        return createContactDto(employee, oldCode, null);
    }

    private ContactDto createContactDto(final Employee employee, final CareTeamRoleCode oldCode, final String oldSecureEmail) {
        final ContactDto contactDto = new ContactDto();

        contactDto.setId(employee.getId());
        contactDto.setFirstName(employee.getFirstName());
        contactDto.setLastName(employee.getLastName());
        if (employee.getCareTeamRole()!=null) {
            contactDto.setRoleCode(employee.getCareTeamRole().getCode());
        }
        contactDto.setOldRoleCode(oldCode);

        contactDto.setSecureMessaging(employee.getSecureMessaging());
        contactDto.setSecureMessagingActive(employee.isSecureMessagingActive());
        contactDto.setSecureMessagingError(employee.getSecureMessagingError());
        contactDto.setOldSecureMessaging(oldSecureEmail);

        if (employee.getDatabase()!=null) {
            contactDto.setOrganization(new KeyValueDto(employee.getDatabase().getId(), employee.getDatabase().getName()));
        }

        if (employee.getCareTeamRole() != null) {
            contactDto.setRole(new KeyValueDto(employee.getCareTeamRole().getId(), employee.getCareTeamRole().getName()));
        }

        contactDto.setEnabledExchange(roleDao.hasRole(employee.getId(), RoleCode.ROLE_ELDERMARK_USER));

        if (employee.getPerson() != null) {
            contactDto.setEmail(PersonService.getPersonTelecomValue(employee.getPerson(), PersonTelecomCode.EMAIL));
            contactDto.setPhone(PersonService.getPersonTelecomValue(employee.getPerson(), PersonTelecomCode.WP));
            contactDto.setFax(PersonService.getPersonTelecomValue(employee.getPerson(), PersonTelecomCode.FAX));

            if (CollectionUtils.isNotEmpty(employee.getPerson().getAddresses())) {
                final PersonAddress personAddress = employee.getPerson().getAddresses().get(0); // TODO
                contactDto.setAddress(addressService.createAddressDto(personAddress));
            }
        }
        contactDto.setContact4d(employee.getContact4d());
        contactDto.setLogin4d(employee.getLoginName());
        contactDto.setCompany(employee.getCompany());
        contactDto.setCommunityId(employee.getCommunityId());
        if (employee.getDatabase().getSystemSetup()!=null) {
            contactDto.setCompanyId(employee.getDatabase().getSystemSetup().getLoginCompanyId());
        }
        if (employee.getCommunityId() != null) {
            String communityName = careCoordinationCommunityDao.getCommunityName(employee.getCommunityId());
            contactDto.setCommunityName(communityName);
        }
        contactDto.setExpired(employee.getStatus().equals(EmployeeStatus.EXPIRED));
        contactDto.setQaIncidentReports(employee.getQaIncidentReports());
        return contactDto;
    }


    KeyValueDto convertEmployee(final Employee employee) {
        return new KeyValueDto(employee.getId(), employee.getFullName());
    }

    KeyValueDto convertEmployee(final SimpleEmployee employee) {
        return new KeyValueDto(employee.getId(), employee.getFullName());
    }

    private ContactListItemDto createContactListItemDto(Employee employee) {
        final ContactListItemDto dto = new ContactListItemDto();
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setId(employee.getId());
        if (employee.getPerson() != null) {
            dto.setPhone(PersonService.getPersonTelecomValue(employee.getPerson(), PersonTelecomCode.WP));
            dto.setEmail(PersonService.getPersonTelecomValue(employee.getPerson(), PersonTelecomCode.EMAIL));
        }
        if (employee.getCareTeamRole() != null) {
            dto.setRole(employee.getCareTeamRole().getName());
        }
        dto.setStatus(employee.getStatus().toString());
        return dto;
    }

    private LinkedContactDto createLinkedContactDto(Employee employee) {
        LinkedContactDto dto = new LinkedContactDto();
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setId(employee.getId());
        if (employee.getCareTeamRole() != null) {
            dto.setRole(employee.getCareTeamRole().getName());
        }
        dto.setLogin(employee.getLoginName());
        dto.setCompanyId(employee.getDatabase().getSystemSetup().getLoginCompanyId());
        dto.setOrganization(employee.getDatabase().getName());
        if (employee.getCommunityId() != null) {
            String communityName = careCoordinationCommunityDao.getCommunityName(employee.getCommunityId());
            dto.setCommunity(communityName);
            dto.setCommunityId(employee.getCommunityId());
        }
        dto.setDatabaseId(employee.getDatabaseId());
        dto.setCareTeamRoleCodeName(employee.getCareTeamRole() != null ? employee.getCareTeamRole().getCode().name() : null);
        return dto;
    }

    @Override
    public List<LinkedContactDto> getLinkedEmployees(Long employeeId) {
        List<LinkedContactDto> result = null;
        List<Employee> linkedEmployees = getLinkedEmployeeEntities(employeeId);
        if (CollectionUtils.isNotEmpty(linkedEmployees)){
            result = getLinkedContacts(employeeId, linkedEmployees);
        }
        return result;
    }

    @Override
    public List<Employee> getLinkedEmployeeEntities(Long employeeId) {
        List<Long> linkedEmployeeIds = linkedEmployeesDao.getLinkedEmployeeIds(employeeId);
        return employeeDao.getEmployees(linkedEmployeeIds);
    }

    @Override
    public LinkedContactDto createLinkedEmployee(Employee employee, Employee employeeToLink) {
        LinkedEmployees linkedEmployee = new LinkedEmployees();
        linkedEmployee.setFirstEmployeeId(employee.getId());
        linkedEmployee.setSecondEmployeeId(employeeToLink.getId());
        linkedEmployeesDao.create(linkedEmployee);
        return createLinkedContactDto(employeeToLink);
    }

    @Override
    public void deleteLinkedEmployee(Long linkedEmployeeIdToRemove, Long currentEmployeeId) {
        linkedEmployeesDao.deleteLinkedEmployee(linkedEmployeeIdToRemove, currentEmployeeId);
    }

    @Override
    public Long getEmployeeCommunityId(long employeeId) {
        return employeeDao.getEmployeeCommunityId(employeeId);
    }
}
