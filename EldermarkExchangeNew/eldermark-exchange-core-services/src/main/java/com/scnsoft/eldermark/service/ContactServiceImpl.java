package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AppointmentContactFilter;
import com.scnsoft.eldermark.beans.ContactFilter;
import com.scnsoft.eldermark.beans.ContactNameFilter;
import com.scnsoft.eldermark.beans.OrganizationFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatContactFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.beans.projection.IdNamesCareTeamRoleNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationFilterListItemAwareEntity;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.ClientPrimaryContactDao;
import com.scnsoft.eldermark.dao.EmployeeBasicDao;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.basic.evaluated.params.EvaluatedPropertyParams;
import com.scnsoft.eldermark.dao.basic.evaluated.params.FavouritePropertyParams;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.dto.AvatarUpdateData;
import com.scnsoft.eldermark.dto.employee.EmployeeUpdates;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.projection.EmployeeRoleNameStatusCommunityAware;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.projection.EmployeeIdNameFavouriteOrgDetails;
import com.scnsoft.eldermark.projection.IsFavouriteEvaluatedAware;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.PersonUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    private static Set<CareTeamRoleCode> QA_UNAVAILABLE_ROLES = EnumSet.of(
            CareTeamRoleCode.ROLE_PARENT_GUARDIAN,
            CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES,
            CareTeamRoleCode.ROLE_CONTENT_CREATOR
    );

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private PersonService personService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private EmployeeBasicDao employeeBasicDao;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientPrimaryContactDao clientPrimaryContactDao;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeBasic> find(ContactFilter filter, PermissionFilter permissionFilter, Pageable pageRequest) {
        var byFilter = employeeSpecificationGenerator.byFilter(filter, EmployeeBasic.class);
        var hasAccess = employeeSpecificationGenerator.hasAccess(permissionFilter, EmployeeBasic.class);
        return employeeBasicDao.findAll(byFilter.and(hasAccess), pageRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee findById(Long contactId) {
        return employeeDao.findById(contactId).orElseThrow();
    }

    @Override
    @Transactional
    public Employee save(Employee employee, Long performedById) {
        if (employee.getId() == null && employeeDao.existsByLoginNameAndOrganizationIdAndStatusNot(employee.getLoginName(),
                employee.getOrganizationId(), EmployeeStatus.DECLINED))
            throw new BusinessException(employee.getLoginName() + " already exists");

        if (QA_UNAVAILABLE_ROLES.contains(employee.getCareTeamRole().getCode())) {
            employee.setQaIncidentReports(false);
        }

        avatarService.update(new AvatarUpdateData(employee, employee.getMultipartFile(), employee.getShouldRemoveAvatar()));
        var isNew = employee.getId() == null;
        var contactEntity = employeeDao.save(employee);
        updateLegacyIds(contactEntity);
        if (employee.getId() != null && BooleanUtils.isTrue(employee.getShouldRemovePrimaryContacts())) {
            removePrimaryContacts(employee.getId());
        }
        if (!isNew) {
            hieConsentPolicyUpdateService.contactChanged(employee, performedById);
        }
        return employee;
    }

    private void removePrimaryContacts(Long id) {
        var clientCtm = clientCareTeamMemberService.findByEmployeeId(id);
        clientCtm.stream()
                .filter(clientCareTeamMember -> CollectionUtils.isNotEmpty(clientCareTeamMember.getPrimaryContacts()))
                .map(ClientCareTeamMember::getPrimaryContacts)
                .flatMap(List::stream)
                .forEach(clientPrimaryContact -> {
                    clientDao.deletePrimaryContact(clientPrimaryContact.getId());
                    clientPrimaryContactDao.deleteById(clientPrimaryContact.getId());
                });
    }

    @Override
    @Transactional
    public Employee update(EmployeeUpdates updates, Long performedById) {
        var employee = Objects.requireNonNull(updates.getEmployee());

        if (updates.getStatus() != null) {
            employee.setStatus(updates.getStatus());
        }

        var name = Lazy.of(() -> {
            if (CollectionUtils.isNotEmpty(employee.getPerson().getNames())) {
                return employee.getPerson().getNames().get(0);
            }
            return CareCoordinationUtils.createAndAddName(employee.getPerson(), null, null);
        });

        if (StringUtils.isNotEmpty(updates.getFirstName())) {
            employee.setFirstName(updates.getFirstName());
            name.get().setGiven(updates.getFirstName());
        }

        if (StringUtils.isNotEmpty(updates.getLastName())) {
            employee.setLastName(updates.getLastName());
            name.get().setFamily(updates.getLastName());
        }

        if (updates.isShouldDeleteAvatar() && employee.getAvatar() != null) {
            avatarService.deleteById(employee.getAvatarId());
        } else {
            if (updates.getAvatarData() != null && StringUtils.isEmpty(updates.getAvatarMimeType())) {
                throw new BusinessException("Missing avatarMimeType");
            }

            if (updates.getAvatarData() == null && StringUtils.isNotEmpty(updates.getAvatarMimeType())) {
                throw new BusinessException("Missing avatarData");
            }

            if (updates.getAvatarData() != null && StringUtils.isNotEmpty(updates.getAvatarMimeType())) {
                avatarService.update(new AvatarUpdateData(employee, updates.getAvatarData(), updates.getAvatarMimeType()));
            }
        }

        var address = Lazy.of(() -> {
            if (CollectionUtils.isNotEmpty(employee.getPerson().getAddresses())) {
                return employee.getPerson().getAddresses().get(0);
            }
            return CareCoordinationUtils.createAndAddAddress(employee.getPerson());
        });

        if (StringUtils.isNotEmpty(updates.getStreet())) {
            address.get().setStreetAddress(updates.getStreet());
        }

        if (StringUtils.isNotEmpty(updates.getCity())) {
            address.get().setCity(updates.getCity());
        }

        if (updates.getState() != null) {
            address.get().setState(updates.getState().getAbbr());
        }

        if (StringUtils.isNotEmpty(updates.getZipCode())) {
            address.get().setPostalCode(updates.getZipCode());
        }

        if (StringUtils.isNotEmpty(updates.getCellPhone())) {
            PersonUtils.updateOrCreateTelecom(
                    employee.getPerson(),
                    PersonTelecomCode.MC,
                    updates.getCellPhone(),
                    CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE
            );
        }

        if (updates.getHomePhone() != null) {
            if (updates.getHomePhone().isEmpty()) {
                PersonTelecomUtils.find(employee.getPerson(), PersonTelecomCode.HP)
                        .ifPresent(homePhone -> {
                            employee.getPerson().getTelecoms().remove(homePhone);
                            homePhone.setPerson(null);
                        });
            } else {
                PersonUtils.updateOrCreateTelecom(
                        employee.getPerson(),
                        PersonTelecomCode.HP,
                        updates.getHomePhone(),
                        CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE
                );
            }
        }

        if (StringUtils.isNotEmpty(updates.getEmail())) {
            employee.setLoginName(updates.getEmail());
            PersonUtils.updateOrCreateTelecom(
                    employee.getPerson(),
                    PersonTelecomCode.EMAIL,
                    updates.getEmail(),
                    CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE
            );
        }

        if (updates.getBirthDate() != null) {
            employee.setBirthDate(updates.getBirthDate());
        }

        employee.setModifiedTimestamp(Instant.now().toEpochMilli());

        if (employee.getId() != null) {
            hieConsentPolicyUpdateService.contactChanged(employee, performedById);
        }

        return updateLegacyIds(employeeDao.save(employee));
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(ContactFilter filter, PermissionFilter permissionFilter) {
        var byFilter = employeeSpecificationGenerator.byFilter(filter, Employee.class);
        var hasAccess = employeeSpecificationGenerator.hasAccess(permissionFilter, Employee.class);

        return employeeDao.count(byFilter.and(hasAccess));
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByLoginInOrganization(String login, Long organizationId) {
        return employeeDao.existsByLoginNameAndOrganizationIdAndStatusNot(login, organizationId, EmployeeStatus.DECLINED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesAware> findNames(ContactNameFilter filter) {
        return findNames(filter, IdNamesAware.class);
    }

    @Override
    @Transactional
    public void updateTwilioSid(Long id, String twilioUserSid) {
        employeeDao.updateTwilioUserSid(id, twilioUserSid);
    }

    @Override
    @Transactional
    public void setTwilioServiceConversation(Long id, String twilioServiceConversationSid) {
        employeeDao.updateTwilioServiceConversationSid(id, twilioServiceConversationSid);
    }

    private Employee updateLegacyIds(Employee employee) {
        boolean updateNeeded = CareCoordinationConstants.updateLegacyId(employee);
        updateNeeded |= personService.updateLegacyId(employee.getPerson());

        return updateNeeded ? employeeDao.save(employee) : employee;
    }

    @Override
    public <P> P findById(Long id, Class<P> projection) {
        return employeeDao.findById(id, projection).orElseThrow();
    }

    @Override
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return employeeDao.findByIdIn(ids, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter) {
        var chatAccessibleEmployees = employeeSpecificationGenerator.chatAccessibleEmployees(permissionFilter,
                filter.getExcludedEmployeeId());
        if (filter.getExcludeOneToOneParticipants()) {
            chatAccessibleEmployees = chatAccessibleEmployees.and(employeeSpecificationGenerator
                    .excludeParticipatingInOneToOneChatWithAny(permissionFilter.getAllEmployeeIds()));
        }
        return employeeDao.exists(chatAccessibleEmployees);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesAware> findChatAccessibleNames(PermissionFilter permissionFilter, AccessibleChatContactFilter filter) {
        var chatAccessibleEmployees = employeeSpecificationGenerator.chatAccessibleEmployees(permissionFilter,
                filter.getExcludedEmployeeId());

        var byOrgIds = employeeSpecificationGenerator.byOrganizationIds(filter.getOrganizationIds());
        var byCommunityIds = employeeSpecificationGenerator.byCommunityIds(filter.getCommunityIds());
        if (filter.getExcludeParticipatingInOneToOne()) {
            chatAccessibleEmployees = chatAccessibleEmployees.and(employeeSpecificationGenerator
                    .excludeParticipatingInOneToOneChatWithAny(permissionFilter.getAllEmployeeIds()));
        }

        return employeeDao.findAll(chatAccessibleEmployees.and(byOrgIds.and(byCommunityIds)),
                IdNamesAware.class,
                Sort.by(Employee_.FIRST_NAME, Employee_.LAST_NAME)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeIdNameFavouriteOrgDetails> findChatAccessible(PermissionFilter permissionFilter,
                                                                      EmployeeSearchWithFavouriteFilter filter,
                                                                      Pageable pageRequest) {
        return staffListSpecification(permissionFilter, filter).map(
                        spec -> {
                            var favouriteEvaluatedParamMap = buildStaffFavouriteEvaluatedParamMap(filter.getFavouriteOfEmployeeIdHint());
                            return employeeDao.findAll(spec,
                                    EmployeeIdNameFavouriteOrgDetails.class,
                                    favouriteEvaluatedParamMap,
                                    pageRequest
                            );
                        })
                .orElseGet(() -> Page.empty(pageRequest));
    }

    private Map<String, EvaluatedPropertyParams> buildStaffFavouriteEvaluatedParamMap(Long favouriteOfEmployeeIdHint) {
        if (favouriteOfEmployeeIdHint == null) {
            return Collections.emptyMap();
        } else {
            if (!IsFavouriteEvaluatedAware.class.isAssignableFrom(EmployeeIdNameFavouriteOrgDetails.class)) {
                throw new ValidationException("Projection class should implement " + IsFavouriteEvaluatedAware.class.getSimpleName());
            }
            var favouriteParams = new FavouritePropertyParams(
                    favouriteOfEmployeeIdHint,
                    Employee.class,
                    Employee_.ADDED_AS_FAVOURITE_TO_EMPLOYEE_IDS
            );

            return Map.of(IsFavouriteEvaluatedAware.IS_FAVOURITE_PROPERTY_NAME, favouriteParams);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsChatAccessible(PermissionFilter permissionFilter, EmployeeSearchWithFavouriteFilter filter) {
        //todo check if possible to unify with another existsChatAccessibleMobile with different filter
        var spec = staffListSpecification(permissionFilter, filter);
        return spec.map(employeeDao::exists).orElse(false);
    }

    private Optional<Specification<Employee>> staffListSpecification(PermissionFilter permissionFilter,
                                                                     EmployeeSearchWithFavouriteFilter filter) {
        var accessibleOrganizationIds = getAccessibleChatEnabledOrganizations(permissionFilter);

        if (filter.getOrganizationId() != null) {
            if (accessibleOrganizationIds.contains(filter.getOrganizationId())) {
                accessibleOrganizationIds = Collections.singletonList(filter.getOrganizationId());
            } else {
                return Optional.empty();
            }
        }

        Specification<Employee> chatAccessibleEmployees;
        if (filter.getExcludeCanNotCall()) {
            chatAccessibleEmployees = employeeSpecificationGenerator.videoCallAccessibleEmployeesByOrganizationIds(
                    permissionFilter,
                    filter.getExcludedEmployeeId(),
                    accessibleOrganizationIds
            );
        } else {
            chatAccessibleEmployees = employeeSpecificationGenerator.chatAccessibleEmployeesByOrganizationIds(
                    permissionFilter,
                    filter.getExcludedEmployeeId(),
                    accessibleOrganizationIds
            );
        }

        if (filter.getExcludeParticipatingInOneToOne()) {
            chatAccessibleEmployees = chatAccessibleEmployees.and(employeeSpecificationGenerator
                    .excludeParticipatingInOneToOneChatWithAny(permissionFilter.getAllEmployeeIds()));
        }
        if (filter.getExcludeSystemRole() != null) {
            chatAccessibleEmployees = chatAccessibleEmployees.and(
                    employeeSpecificationGenerator.systemRoleNotIn(List.of(filter.getExcludeSystemRole())));
        }
        if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
            chatAccessibleEmployees = chatAccessibleEmployees.and(
                    employeeSpecificationGenerator.byCommunityIds(filter.getCommunityIds())
            );
        }

        var byNameLike = employeeSpecificationGenerator.byNameLike(filter.getSearchText());

        return Optional.of(chatAccessibleEmployees.and(byNameLike));
    }

    private List<Long> getAccessibleChatEnabledOrganizations(PermissionFilter permissionFilter) {
        var orgFilter = new OrganizationFilter();
        orgFilter.setIsChatEnabled(true);
        return organizationService.findForFilter(orgFilter, permissionFilter).stream()
                .map(OrganizationFilterListItemAwareEntity::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void setFavourite(Long id, boolean favourite, Long addedById) {
        var employee = employeeDao.findById(id).orElseThrow();
        var alreadyAddedAsFavourite = employee.getAddedAsFavouriteToEmployeeIds().contains(addedById);
        if (alreadyAddedAsFavourite != favourite) {
            if (favourite) {
                employee.getAddedAsFavouriteToEmployeeIds().add(addedById);
            } else {
                employee.getAddedAsFavouriteToEmployeeIds().remove(addedById);
            }
            employeeDao.save(employee);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesCareTeamRoleNameAware> findNamesWithRoles(ContactNameFilter filter) {
        return findNames(filter, IdNamesCareTeamRoleNameAware.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeRoleNameStatusCommunityAware> findAppointmentContacts(AppointmentContactFilter filter) {
        var statuses = CollectionUtils.isEmpty(filter.getStatuses())
                ? employeeSpecificationGenerator.active()
                : employeeSpecificationGenerator.byStatusIn(filter.getStatuses());
        var inEligibleForDiscoveryCommunity = employeeSpecificationGenerator.inEligibleForDiscoveryCommunity();
        var resultSpecification = statuses.and(inEligibleForDiscoveryCommunity);
        if (CollectionUtils.isNotEmpty(filter.getRoles())) {
            resultSpecification = employeeSpecificationGenerator.bySystemRoleIn(filter.getRoles())
                    .and(resultSpecification);
        }
        if (BooleanUtils.isTrue(filter.getWithAppointmentsCreated())) {
            var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
            if (BooleanUtils.isTrue(filter.getWithAppointmentsCreated())) {
                resultSpecification = employeeSpecificationGenerator.appointmentCreatorInOrganizationWithAccessToAppointment(permissionFilter, filter.getOrganizationId())
                        .and(resultSpecification);
            }
        } else {
            resultSpecification = employeeSpecificationGenerator.byOrganizationId(filter.getOrganizationId())
                    .and(resultSpecification);
        }
        if (BooleanUtils.isTrue(filter.getWithAppointmentsScheduled())) {
            var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
            resultSpecification = employeeSpecificationGenerator.appointmentServiceProviderWithAccessToAppointment(permissionFilter)
                    .and(resultSpecification);
        }
        if (filter.getAccessibleClientId() != null) {
            resultSpecification = Specification.not(employeeSpecificationGenerator.isOnHoldCtmForClient(filter.getAccessibleClientId()))
                    .and(resultSpecification);
        }
        return employeeDao.findAll(resultSpecification,
                EmployeeRoleNameStatusCommunityAware.class,
                Sort.by(Employee_.FIRST_NAME, Employee_.LAST_NAME)
        );
    }

    public <T extends IdNamesAware> List<T> findNames(ContactNameFilter filter, Class<T> projectionClass) {
        var statuses = CollectionUtils.isEmpty(filter.getStatuses())
                ? employeeSpecificationGenerator.active()
                : employeeSpecificationGenerator.byStatusIn(filter.getStatuses());
        var byName = employeeSpecificationGenerator.byNameLike(filter.getName());
        var byOrgId = employeeSpecificationGenerator.byOrganizationIds(filter.getOrganizationIds());
        var inEligibleForDiscoveryCommunity = employeeSpecificationGenerator.inEligibleForDiscoveryCommunity();
        var resultSpecification = byOrgId.and(statuses.and(byName.and(inEligibleForDiscoveryCommunity)));
        if (CollectionUtils.isNotEmpty(filter.getCommunityIds())) {
            resultSpecification = employeeSpecificationGenerator.byCommunityIds(filter.getCommunityIds())
                    .and(resultSpecification);
        }
        if (CollectionUtils.isNotEmpty(filter.getRoles())) {
            resultSpecification = employeeSpecificationGenerator.bySystemRoleIn(filter.getRoles())
                    .and(resultSpecification);
        }
        return employeeDao.findAll(resultSpecification,
                projectionClass,
                Sort.by(Employee_.FIRST_NAME, Employee_.LAST_NAME)
        );
    }

    /**
     * For now, returns cached contact's community address to reduce number of paid requests to google.
     */
    @Override
    @Transactional(readOnly = true)
    public Pair<Double, Double> findAddressCoordinatesById(Long contactId) {
        return employeeDao.findById(contactId)
                .map(BaseEmployeeSecurityEntity::getCommunity)
                .map(Community::getAddresses)
                .map(addresses -> addresses.get(0))
                .map(address -> Pair.of(address.getLatitude(), address.getLongitude()))
                .orElse(new Pair<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareTeamRole> getQaUnavailableRoles() {
        return QA_UNAVAILABLE_ROLES.stream()
                .map(careTeamRoleService::get)
                .collect(Collectors.toList());
    }
}
