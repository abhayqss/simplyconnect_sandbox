package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.specification.*;
import com.scnsoft.eldermark.dto.notification.AddedToCareTeamNotificationDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModificationType;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.jms.producer.ClientUpdateQueueProducer;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class CareTeamMemberServiceImpl implements CareTeamMemberService {

    private static final Set<CareTeamRoleCode> COMMUNITY_CTM_ROLES_TO_EXCLUDE = EnumSet.of(
            CareTeamRoleCode.ROLE_PARENT_GUARDIAN,
            CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES,
            CareTeamRoleCode.ROLE_HCA,
            CareTeamRoleCode.ROLE_CONTENT_CREATOR
    );
    private static final Set<CareTeamRoleCode> CLIENT_CTM_ROLES_TO_EXCLUDE = EnumSet.of(
            CareTeamRoleCode.ROLE_CONTENT_CREATOR
    );
    private static final Set<EmployeeStatus> STATUSES_TO_EXCLUDE = Set.of(EmployeeStatus.INACTIVE, EmployeeStatus.EXPIRED, EmployeeStatus.DECLINED);

    @Autowired
    private ClientCareTeamMemberDao clientCareTeamMemberDao;

    @Autowired
    private CommunityCareTeamMemberDao communityCareTeamMemberDao;

    @Autowired
    private EventTypeCareTeamRoleXrefDao eventTypeCareTeamRoleXrefDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CareTeamMemberDao careTeamMemberDao;

    @Autowired
    private CommunityCareTeamMemberSpecificationGenerator communityCareSpecification;

    @Autowired
    private ClientCareTeamMemberSpecificationGenerator clientCareSpecification;

    @Autowired
    private FullCareTeamSpecificationGenerator fullCareTeamSpecificationGenerator;

    @Autowired
    private CareTeamMemberNotificationPreferencesDao careTeamMemberNotificationPreferencesDao;

    @Autowired
    private AccessRightService accessRightService;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private OrganizationSpecificationGenerator organizationSpecifications;

    @Autowired
    private ClientUpdateQueueProducer clientUpdateQueueProducer;

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private ClientCareTeamMemberModifiedService clientCareTeamMemberModifiedService;

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Autowired
    private ClientService clientService;

    @Override
    public void deleteById(Long careTeamMemberId, Long performedById) {
        var ctm = careTeamMemberDao.findById(careTeamMemberId).orElseThrow();

        validateClientActive(ctm);

        //bulk remove of notification preferences instead of removing one by one in case of removing entity
        careTeamMemberNotificationPreferencesDao.deleteNotificationPreferences(careTeamMemberId);

        var employee = ctm.getEmployee();
        Client client = null;
        Community community = null;
        boolean wasOnHold = false;

        if (ctm instanceof ClientCareTeamMember) {
            var clientCTM = (ClientCareTeamMember) ctm;
            clientCareTeamMemberModifiedService.clientCareTeamMemberModified(
                    clientCTM,
                    performedById,
                    CareTeamMemberModificationType.REMOVED);
            clientCTM.getPrimaryContacts().forEach(clientPrimaryContact -> clientDao.deletePrimaryContact(clientPrimaryContact.getId()));

            client = clientCTM.getClient();
            wasOnHold = clientCTM.getOnHold();
        } else if (ctm instanceof CommunityCareTeamMember) {
            var communityCTM = (CommunityCareTeamMember) ctm;
            community = communityCTM.getCommunity();
        }

        careTeamMemberDao.delete(ctm);

        if (client != null) {
            clientUpdateQueueProducer.putToResidentUpdateQueue(
                    client.getId(),
                    ResidentUpdateType.CARE_TEAM
            );
            hieConsentPolicyUpdateService.clientCareTeamMemberDeleted(employee, client, wasOnHold);
        } else if (community != null) {
            hieConsentPolicyUpdateService.communityCareTeamMemberDeleted(employee, community);
        }
    }

    private void validateClientActive(CareTeamMember careTeamMember) {
        if (careTeamMember instanceof ClientCareTeamMember) {
            clientService.validateActive(((ClientCareTeamMember) careTeamMember).getClient());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CareTeamMember> find(CareTeamFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        if (filter.getClientId() != null) {
            return clientCareTeamMemberDao.findAll(clientCtmListSpecification(filter, permissionFilter), pageable)
                    .map(Function.identity());
        }

        if (filter.getCommunityId() != null) {
           return communityCareTeamMemberDao.findAll(communityCtmListSpecification(filter, permissionFilter), pageable)
                    .map(Function.identity());
        }

        throw new InternalServerException(InternalServerExceptionType.NOT_FOUND);
    }

    @Override
    @Transactional(readOnly = true)
    public List<? extends CareTeamMember> find(CareTeamFilter filter, PermissionFilter permissionFilter) {
        if (filter.getClientId() != null) {
            return clientCareTeamMemberDao.findAll(clientCtmListSpecification(filter, permissionFilter));
        }

        if (filter.getCommunityId() != null) {
            return communityCareTeamMemberDao.findAll(communityCtmListSpecification(filter, permissionFilter));
        }

        throw new InternalServerException(InternalServerExceptionType.NOT_FOUND);
    }

    private Specification<ClientCareTeamMember> clientCtmListSpecification(CareTeamFilter filter, PermissionFilter permissionFilter) {
        var ofMergedByClientId = clientCareSpecification.ofMergedByClientId(filter.getClientId());
        var byEmployeeName = clientCareSpecification.byEmployeeNameLike(filter.getName());
        var ofAffiliationType = clientCareSpecification.ofAffiliationType(filter.getAffiliationType());
        var hasAccess = clientCareSpecification.hasAccess(permissionFilter);

        return ofMergedByClientId.and(byEmployeeName).and(ofAffiliationType).and(hasAccess);
    }

    private Specification<CommunityCareTeamMember> communityCtmListSpecification(CareTeamFilter filter, PermissionFilter permissionFilter) {
        var byCommunityId = communityCareSpecification.byCommunityId(filter.getCommunityId());
        var byEmployeeName = communityCareSpecification.byEmployeeNameLike(filter.getName());
        var ofAffiliationType = communityCareSpecification.ofAffiliationType(filter.getAffiliationType());
        var hasAccess = communityCareSpecification.hasAccess(permissionFilter);

        return byCommunityId.and(byEmployeeName).and(ofAffiliationType).and(hasAccess);
    }

    @Override
    @Transactional(readOnly = true)
    public CareTeamMember findById(Long careTeamMemberId) {
        return careTeamMemberDao.findById(careTeamMemberId).orElseThrow();
    }

    @Override
    public <T extends CareTeamMember> T save(T careTeamMember, Long performedById) {
        validateTargetEmployee(careTeamMember);
        validateCareTeamRelationExists(careTeamMember);
        validateCareTeamMemberNotificationPreferences(careTeamMember);

        fillDefaultClientCareTeamAccessRights(careTeamMember);

        boolean isNew = careTeamMember.getId() == null;
        var saved = careTeamMemberDao.save(careTeamMember);

        if (isNew) {
            PersonTelecomUtils.find(saved.getEmployee().getPerson(), PersonTelecomCode.EMAIL)
                    .ifPresent(t -> {
                        var email = t.getNormalized();
                        exchangeMailService.sendAddedToCareTeamNotification(
                                new AddedToCareTeamNotificationDto(saved.getEmployee().getFullName(), email)
                        );
                    });

            if (careTeamMember instanceof ClientCareTeamMember) {
                var ctm = (ClientCareTeamMember) careTeamMember;
                var clientId = ctm.getClientId();
                clientUpdateQueueProducer.putToResidentUpdateQueue(clientId, ResidentUpdateType.CARE_TEAM);
                clientCareTeamMemberModifiedService.clientCareTeamMemberModified(ctm, performedById, CareTeamMemberModificationType.NEW);
                hieConsentPolicyUpdateService.clientCareTeamMemberAdded((ClientCareTeamMember) careTeamMember);
            }

            if (careTeamMember instanceof CommunityCareTeamMember) {
                hieConsentPolicyUpdateService.communityCareTeamMemberAdded((CommunityCareTeamMember) careTeamMember);
            }
        }

        return saved;
    }

    private <T extends CareTeamMember> void validateTargetEmployee(T careTeamMember) {
        var employee = careTeamMember.getEmployee();

        var allowedCtmRoles = careTeamRoleService.findAllowedCtmRolesForEmployeeRole(employee.getCareTeamRole());
        if (allowedCtmRoles.stream().noneMatch(allowedRole -> Objects.equals(allowedRole.getId(), careTeamMember.getCareTeamRole().getId()))) {
            throw new BusinessException(
                    careTeamMember.getCareTeamRole().getDisplayName() + " role is not available for "
                            + employee.getCareTeamRole().getDisplayName()
            );
        }

        if (STATUSES_TO_EXCLUDE.contains(employee.getStatus())) {
            throw new BusinessException("Employee with " + employee.getStatus().toString().toLowerCase() +
                    " status can't be added as care team member");

        }

        if (careTeamMember instanceof CommunityCareTeamMember) {
            var employeeRole = employee.getCareTeamRole().getCode();
            if (COMMUNITY_CTM_ROLES_TO_EXCLUDE.contains(employeeRole)) {
                throw new BusinessException("This employee can't be added as community care team member");
            }
        }
    }

    private <T extends CareTeamMember> void validateCareTeamRelationExists(T careTeamMember) {
        boolean exists = false;
        if (careTeamMember instanceof ClientCareTeamMember) {
            if (careTeamMember.getId() == null) {
                exists = clientCareTeamMemberDao.existsByEmployee_IdAndCareTeamRole_IdAndClient_Id(
                        careTeamMember.getEmployee().getId(),
                        careTeamMember.getCareTeamRole().getId(),
                        ((ClientCareTeamMember) careTeamMember).getClient().getId()
                );
            } else {
                exists = clientCareTeamMemberDao.existsByEmployee_IdAndCareTeamRole_IdAndClient_IdAndIdNot(careTeamMember.getEmployee().getId(),
                        careTeamMember.getCareTeamRole().getId(), ((ClientCareTeamMember) careTeamMember).getClient().getId(), careTeamMember.getId());
            }
        } else if (careTeamMember instanceof CommunityCareTeamMember) {
            if (careTeamMember.getId() == null) {
                exists = communityCareTeamMemberDao.existsByEmployee_IdAndCareTeamRole_IdAndCommunity_Id(
                        careTeamMember.getEmployee().getId(), careTeamMember.getCareTeamRole().getId(),
                        ((CommunityCareTeamMember) careTeamMember).getCommunity().getId());
            } else {
                exists = communityCareTeamMemberDao.existsByEmployee_IdAndCareTeamRole_IdAndCommunity_IdAndIdNot(
                        careTeamMember.getEmployee().getId(), careTeamMember.getCareTeamRole().getId(),
                        ((CommunityCareTeamMember) careTeamMember).getCommunity().getId(), careTeamMember.getId());
            }
        }
        if (exists) {
            throw new BusinessException("Care Team Member with selected role already exists. Please check entered data");
        }
    }

    private <T extends CareTeamMember> void validateCareTeamMemberNotificationPreferences(T careTeamMember) {
        var employee = careTeamMember.getEmployee();
        validateCtmNotificationImmutablePreferences(careTeamMember);
        careTeamMember.getNotificationPreferences().stream()
                .filter(pref -> pref.getResponsibility() != Responsibility.N && pref.getResponsibility() != Responsibility.V)
                .forEach(x -> {
                    Person person = employee.getPerson();
                    NotificationType channel = x.getNotificationType();
                    switch (channel) {
                        case FAX:
                            var fax = PersonTelecomUtils.findValue(person, PersonTelecomCode.FAX);
                            if (fax.isEmpty()) {
                                throw new BusinessException("This user has no fax. Please select other notification type or add fax number for this user.");
                            }
                            break;

                        case EMAIL:
                            var email = PersonTelecomUtils.findValue(person, PersonTelecomCode.EMAIL);
                            if (email.isEmpty()) {
                                throw new BusinessException("This user has no email. Please select other notification type or add email for this user.");
                            }
                            break;

                        case SMS:
                            var phone = PersonTelecomUtils.findValue(person, PersonTelecomCode.MC)
                                    .or(() -> PersonTelecomUtils.findValue(person, PersonTelecomCode.WP));
                            if (phone.isEmpty()) {
                                throw new BusinessException("This user has no phone. Please select other notification type or add phone number for this user.");
                            }
                            break;

                        case SECURITY_MESSAGE:
                            if (StringUtils.isEmpty(employee.getSecureMessaging())) {
                                throw new BusinessException("This user has no secure email. Please select other notification type or add secure email for this user.");
                            }
                            break;
                        default:
                            break;
                    }
                });
    }

    private <T extends CareTeamMember> void validateCtmNotificationImmutablePreferences(T careTeamMember) {
        eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamMember.getCareTeamRole().getId()).stream()
                .filter(it -> !it.getResponsibility().isChangeable())
                .forEach(defaultPref -> careTeamMember.getNotificationPreferences().stream()
                        .filter(pref -> Objects.equals(pref.getEventType().getId(), defaultPref.getEventType().getId()))
                        .forEach(pref -> {
                            if (pref.getResponsibility() != defaultPref.getResponsibility()) {
                                throw new BusinessException(defaultPref.getEventType().getDescription() + " event responsibility cannot be changed");
                            }
                        }));
    }

    private <T extends CareTeamMember> void fillDefaultClientCareTeamAccessRights(T careTeamMember) {
        if (careTeamMember.getId() == null && careTeamMember instanceof ClientCareTeamMember) {
            ((ClientCareTeamMember) careTeamMember).setAccessRights(accessRightService.getDefaultAccessRights());
        }
    }


    @Override
    @Transactional(readOnly = true)
    public List<EventTypeCareTeamRoleXref> getResponsibilitiesForRole(Long careTeamRoleId) {
        return eventTypeCareTeamRoleXrefDao.getResponsibilityForRole(careTeamRoleId);
    }

    @Override
    public List<NotificationType> defaultNotificationChannels() {
        return List.of(NotificationType.EMAIL, NotificationType.PUSH_NOTIFICATION);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getContactsOrganizationsSuitableForCommunityCareTeam(PermissionFilter filter, Long communityId,
                                                                            AffiliatedCareTeamType type, Sort sort,
                                                                            Class<T> projection) {
        var community = communityDao.findById(communityId, OrganizationIdAware.class).orElseThrow();
        return getContactsOrganizationsSuitableForCareTeam(filter, community.getOrganizationId(), type, sort, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> getContactsOrganizationsSuitableForClientCareTeam(PermissionFilter filter, Long clientId,
                                                                         AffiliatedCareTeamType type, Sort sort,
                                                                         Class<T> projection) {
        var client = clientDao.findById(clientId, OrganizationIdAware.class).orElseThrow();
        return getContactsOrganizationsSuitableForCareTeam(filter, client.getOrganizationId(), type, sort, projection);
    }

    private <T> List<T> getContactsOrganizationsSuitableForCareTeam(PermissionFilter filter, Long memberOrganizationId,
                                                                    AffiliatedCareTeamType type, Sort sort,
                                                                    Class<T> projection) {
        var specifications = new ArrayList<Specification<Organization>>();
        var employeeOrganizations = CareCoordinationUtils.getOrganizationIdsSet(filter.getEmployees());

        if (type.isIncludesRegular()) {
            //users can create care team members from their's organizations only except for super admin
            if (filter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR) || employeeOrganizations.contains(memberOrganizationId)) {
                specifications.add(organizationSpecifications.byId(memberOrganizationId));
            }
        }

        if (type.isIncludesPrimary()) {
            Specification<Organization> orgsOfCurrentUser;

            //users can create care team members from their's organizations only except for super admin
            if (filter.hasPermission(Permission.ROLE_SUPER_ADMINISTRATOR)) {
                orgsOfCurrentUser = (root, query, criteriaBuilder) -> criteriaBuilder.and();
            } else {
                orgsOfCurrentUser = organizationSpecifications.byIds(employeeOrganizations);
            }

            var affiliatedOrgs = organizationSpecifications.affiliatedOrganizations(memberOrganizationId);
            specifications.add(orgsOfCurrentUser.and(affiliatedOrgs));
        }

        var specification = specifications.stream()
                .reduce(Specification::or)
                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.or());

        return organizationDao.findAll(specification, projection, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesAware> getContactsSuitableForCommunityCareTeam(Long organizationId, Sort sort) {
        var byOrganizationId = employeeSpecificationGenerator.byOrganizationId(organizationId);
        var inEligibleForDiscoveryCommunityOrWithoutCommunity = employeeSpecificationGenerator.inEligibleForDiscoveryCommunity()
                .or(employeeSpecificationGenerator.withoutCommunity());
        var byStatusNotIn = employeeSpecificationGenerator.byStatusNotIn(STATUSES_TO_EXCLUDE);
        var systemRoleNotIn = employeeSpecificationGenerator.systemRoleNotIn(COMMUNITY_CTM_ROLES_TO_EXCLUDE);
        var withRole = employeeSpecificationGenerator.withRole();
        return employeeDao.findAll(byOrganizationId.and(inEligibleForDiscoveryCommunityOrWithoutCommunity).and(byStatusNotIn).and(systemRoleNotIn).and(withRole), IdNamesAware.class, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesAware> getContactsSuitableForClientCareTeam(Long organizationId, Long clientId, Sort sort) {
        var specifications = new ArrayList<Specification<Employee>>();

        specifications.add(employeeSpecificationGenerator.byOrganizationId(organizationId));
        specifications.add(
                employeeSpecificationGenerator.inEligibleForDiscoveryCommunity()
                        .or(employeeSpecificationGenerator.withoutCommunity())
        );
        specifications.add(employeeSpecificationGenerator.byStatusNotIn(STATUSES_TO_EXCLUDE));
        specifications.add(employeeSpecificationGenerator.withRole());
        specifications.add(employeeSpecificationGenerator.systemRoleNotIn(CLIENT_CTM_ROLES_TO_EXCLUDE));

        var client = clientDao.findById(clientId, ClientSecurityAwareEntity.class)
                .orElseThrow();
        if (client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT) {
            specifications.add(employeeSpecificationGenerator.canBeCtmForOptedOutClient(client));
        }

        var specification = specifications.stream()
                .reduce(Specification::and)
                .orElse(null);

        return employeeDao.findAll(specification, IdNamesAware.class, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(CareTeamFilter filter, PermissionFilter permissionFilter) {

        if (filter.getClientId() != null) {
            return clientCareTeamMemberDao.count(clientCtmListSpecification(filter, permissionFilter));
        }

        if (filter.getCommunityId() != null) {
            return communityCareTeamMemberDao.count(communityCtmListSpecification(filter, permissionFilter));
        }

        throw new InternalServerException(InternalServerExceptionType.NOT_FOUND);
    }

    @Override
    public boolean exists(CareTeamFilter filter, PermissionFilter permissionFilter) {
        if (filter.getClientId() != null) {
            return clientCareTeamMemberDao.exists(clientCtmListSpecification(filter, permissionFilter));
        }

        if (filter.getCommunityId() != null) {
            return communityCareTeamMemberDao.exists(communityCtmListSpecification(filter, permissionFilter));
        }

        throw new InternalServerException(InternalServerExceptionType.NOT_FOUND);
    }

    /*
            There may be multiple care team  relations with the same employee, but notification settings can be different in this relations
         */
    @Override
    @Transactional(readOnly = true)
    public List<CareTeamMember> getFullCareTeam(List<Client> clients) {
        var clientCtm = loadClientsCareTeam(clients);

        var communities = clients.stream()
                .map(Client::getCommunity)
                .filter(StreamUtils.distinctByKey(Community::getId))
                .collect(Collectors.toList());

        var communityCtm = loadCommunitiesCareTeam(communities);

        var result = new ArrayList<CareTeamMember>();
        result.addAll(clientCtm);
        result.addAll(communityCtm);

        return result;
    }

    private List<CareTeamMember> loadClientsCareTeam(List<Client> clients) {
        var ofMerged = clientCareSpecification.ofMergedByClients(clients);
        return new ArrayList<>(clientCareTeamMemberDao.findAll(ofMerged));
    }

    private List<CareTeamMember> loadCommunitiesCareTeam(List<Community> communities) {
        var inCommunities = communityCareSpecification.byCommunityIn(communities);
        return new ArrayList<>(communityCareTeamMemberDao.findAll(inCommunities));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesAnyShareCtmWithEmployee(Collection<Long> sourceEmployeeIds,
                                               Long targetEmployeeId,
                                               HieConsentCareTeamType consentType) {
        var careTeamMembersFromSameCareTeam = fullCareTeamSpecificationGenerator.careTeamMembersFromSameCareTeam(
                sourceEmployeeIds,
                targetEmployeeId,
                consentType
        );
        return careTeamMemberDao.exists(careTeamMembersFromSameCareTeam);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getContactsSuitableForClientCareTeamCount(Long organizationId) {
        var byOrganizationId = employeeSpecificationGenerator.byOrganizationId(organizationId);
        var inEligibleForDiscoveryCommunityOrWithoutCommunity = employeeSpecificationGenerator.inEligibleForDiscoveryCommunity()
                .or(employeeSpecificationGenerator.withoutCommunity());
        var byStatusNotIn = employeeSpecificationGenerator.byStatusNotIn(STATUSES_TO_EXCLUDE);
        var withRole = employeeSpecificationGenerator.withRole();
        return employeeDao.count(byOrganizationId.and(inEligibleForDiscoveryCommunityOrWithoutCommunity).and(byStatusNotIn).and(withRole));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getContactsSuitableForCommunityCareTeamCount(Long organizationId) {
        var byOrganizationId = employeeSpecificationGenerator.byOrganizationId(organizationId);
        var inEligibleForDiscoveryCommunityOrWithoutCommunity = employeeSpecificationGenerator.inEligibleForDiscoveryCommunity()
                .or(employeeSpecificationGenerator.withoutCommunity());
        var byStatusNotIn = employeeSpecificationGenerator.byStatusNotIn(STATUSES_TO_EXCLUDE);
        var systemRoleNotIn = employeeSpecificationGenerator.systemRoleNotIn(COMMUNITY_CTM_ROLES_TO_EXCLUDE);
        var withRole = employeeSpecificationGenerator.withRole();
        return employeeDao.count(byOrganizationId.and(inEligibleForDiscoveryCommunityOrWithoutCommunity).and(byStatusNotIn).and(systemRoleNotIn).and(withRole));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findByClientIdInAndRoleCodeIn(Collection<Long> clientIds, List<CareTeamRoleCode> codes, Class<T> projectionClass) {
        var byClientIdIn = clientCareSpecification.byClientIdIn(clientIds);
        var isEmployeeActive = clientCareSpecification.isEmployeeActive();
        var byRole = clientCareSpecification.byCareTeamRoleCodeIn(codes);

        var specification = byClientIdIn.and(isEmployeeActive.and(byRole));

        return clientCareTeamMemberDao.findAll(specification, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean doesEmployeeWithSameLoginExistInClientCareTeam(Long clientId, String login) {
        return clientCareTeamMemberDao.exists(
                clientCareSpecification.ofMergedByClientId(clientId)
                        .and(clientCareSpecification.byEmployeeLogin(login))
        );
    }
}
