package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.EmployeeSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.ProspectSecurityAwareEntity;
import com.scnsoft.eldermark.dao.AffiliatedRelationshipDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Marketplace;
import com.scnsoft.eldermark.entity.MarketplacePartnerNetwork;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.ApplicationException;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class BaseSecurityService {

    @Autowired
    protected OrganizationService organizationService;

    @Autowired
    protected PermissionFilterService permissionFilterService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    protected ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    protected CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    protected ClientService clientService;

    @Autowired
    protected CommunityService communityService;

    @Autowired
    protected MarketplaceService marketplaceService;

    @Autowired
    private FeaturedServiceProviderService featuredServiceProviderService;

    @Autowired
    protected AffiliatedRelationshipDao affiliatedRelationshipDao;

    protected boolean isAnyCreatedUnderAnyOrganization(Collection<Employee> employees, Collection<Long> organizationIds) {
        return findEmployeesInOrganizations(employees, organizationIds).findFirst().isPresent();
    }

    protected boolean isAnyCreatedUnderOrganization(Collection<Employee> employees, Long organizationId) {
        return findEmployeeInOrganization(employees, organizationId).isPresent();
    }

    protected Optional<Employee> findEmployeeInOrganization(Collection<Employee> employees, Long organizationId) {
        return employees.stream().filter(employee -> organizationId.equals(employee.getOrganizationId())).findFirst();
    }

    protected Stream<Employee> findEmployeesInOrganizations(Collection<Employee> employees, Collection<Long> organizationIds) {
        return employees.stream().filter(employee -> organizationIds.contains(employee.getOrganizationId()));
    }

    protected boolean isAnyCreatedUnderAnyCommunity(Collection<Employee> employees, Collection<Long> communities) {
        return findEmployeesInCommunities(employees, communities).findFirst().isPresent();
    }

    protected boolean isAnyCreatedUnderCommunity(Collection<Employee> employees, Long communityId) {
        return findEmployeeInCommunity(employees, communityId).isPresent();
    }

    protected Optional<Employee> findEmployeeInCommunity(Collection<Employee> employees, Long communityId) {
        return employees.stream().filter(employee -> communityId.equals(employee.getCommunityId())).findFirst();
    }

    protected Stream<Employee> findEmployeesInCommunities(Collection<Employee> employees, Collection<Long> communityIds) {
        return employees.stream().filter(employee -> communityIds.contains(employee.getCommunityId()));
    }

    protected <C extends IdAware> boolean isAnyInClientCareTeam(
            Collection<Employee> employees,
            C client,
            AffiliatedCareTeamType type,
            HieConsentCareTeamType consentType
    ) {
        return isAnyInClientCareTeam(employees, client.getId(), type, consentType);
    }

    protected boolean isAnyInClientCareTeam(Collection<Employee> employees, Long clientId, AffiliatedCareTeamType type,
                                            HieConsentCareTeamType consentType) {
        return clientCareTeamMemberService.isAnyEmployeeInClientCareTeam(employees, clientId, type, consentType);
    }

    protected <C extends ClientSecurityAwareEntity> boolean isAnyInAnyClientCareTeam(Collection<Employee> employees, Collection<C> clients,
                                                                                     AffiliatedCareTeamType type,
                                                                                     HieConsentCareTeamType consentType) {
        return clientCareTeamMemberService.isAnyEmployeeInAnyClientCareTeam(employees, CareCoordinationUtils.toIdsSet(clients), type, consentType);
    }

    protected boolean isAnyInAnyClientCareTeamOfOrganization(List<Employee> employees, Long organizationId,
                                                             AffiliatedCareTeamType type,
                                                             HieConsentCareTeamType consentType) {
        return clientCareTeamMemberService.isAnyEmployeeInAnyClientCareTeamOfOrganization(employees, organizationId, type, consentType);
    }

    protected boolean isAnyInAnyClientCareTeamOfCommunity(List<Employee> employees, Long communityId,
                                                          AffiliatedCareTeamType type,
                                                          HieConsentCareTeamType consentType) {
        return clientCareTeamMemberService.isAnyEmployeeInAnyClientCareTeamOfCommunity(employees, communityId, type, consentType);
    }

    protected boolean isAnyInAnyClientCareTeamOfAnyCommunity(
            List<Employee> employees,
            Collection<Long> communityIds,
            AffiliatedCareTeamType type,
            HieConsentCareTeamType consentType
    ) {
        return clientCareTeamMemberService.isAnyEmployeeInAnyClientCareTeamOfAnyCommunity(employees, communityIds, type, consentType);
    }

    protected Stream<Employee> findClientsCareTeamMembers(Collection<Employee> employees,
                                                          Collection<ClientSecurityAwareEntity> clients,
                                                          AffiliatedCareTeamType type,
                                                          HieConsentCareTeamType consentType) {
        return clientCareTeamMemberService.findClientsCareTeamMembersAmongEmployees(
                        employees,
                        CareCoordinationUtils.toIdsSet(clients),
                        type,
                        consentType
                )
                .stream()
                .map(CareTeamMember::getEmployee);
    }

    protected Stream<Employee> findClientsCareTeamMembersInClientCommunity(Collection<Employee> employees, Long communityId,
                                                                           AffiliatedCareTeamType type,
                                                                           HieConsentCareTeamType consentType) {
        return clientCareTeamMemberService.findClientCareTeamMembersAmongEmployeesAndClientCommunity(employees, communityId, type, consentType)
                .stream()
                .map(CareTeamMember::getEmployee);
    }

    protected boolean isAnyInCommunityCareTeam(Collection<Employee> employees, Long communityId, AffiliatedCareTeamType type,
                                               HieConsentCareTeamType consentType) {
        return communityCareTeamMemberService.isAnyEmployeeInCommunityCareTeam(employees, communityId, type, consentType);
    }

    protected boolean isAnyInCommunityCareTeam(Collection<Employee> employees, CommunitySecurityAwareEntity community,
                                               AffiliatedCareTeamType type,
                                               HieConsentCareTeamType consentType) {
        return communityCareTeamMemberService.isAnyEmployeeInCommunityCareTeam(employees, community.getId(), type, consentType);
    }

    protected boolean isAnyInAnyCommunityCareTeam(Collection<Employee> employees, Collection<Long> communities, AffiliatedCareTeamType type,
                                                  HieConsentCareTeamType consentType) {
        return communityCareTeamMemberService.isAnyEmployeeInAnyCommunityCareTeam(employees, communities, type, consentType);
    }

    protected boolean isAnyInAnyCommunityCareTeamOfOrganization(Collection<Employee> employees, Long organizationId, AffiliatedCareTeamType type,
                                                                HieConsentCareTeamType consentType) {
        return communityCareTeamMemberService.isAnyEmployeeInAnyCommunityCareTeamOfOrganization(employees, organizationId, type, consentType);
    }

    protected Stream<Employee> findCommunitiesCareTeamMembers(Collection<Employee> employees, Collection<Long> communityIds,
                                                              AffiliatedCareTeamType type,
                                                              HieConsentCareTeamType consentType) {
        return communityCareTeamMemberService.findCommunitiesCareTeamMembersAmongEmployees(employees, communityIds, type, consentType)
                .stream()
                .map(CareTeamMember::getEmployee);
    }

    protected Stream<Employee> findCommunityCareTeamMembers(Collection<Employee> employees,
                                                            Long communityId,
                                                            AffiliatedCareTeamType type,
                                                            HieConsentCareTeamType consentType) {
        return findCommunitiesCareTeamMembers(employees, Collections.singletonList(communityId), type, consentType);
    }

    protected boolean isSelfEmployeeRecord(Collection<Employee> employees, Employee employee) {
        return isSelfEmployeeRecord(employees, employee.getId());
    }

    protected boolean isSelfEmployeeRecord(Collection<Employee> employees, Long employeeId) {
        return findSelfEmployeeRecord(employees, employeeId).isPresent();
    }

    protected boolean isSelfEmployeeRecordOfAny(Collection<Employee> employees, Stream<Employee> employeeStream) {
        return employeeStream.anyMatch(emp -> findSelfEmployeeRecord(employees, emp.getId()).isPresent());
    }

    protected Optional<Employee> findSelfEmployeeRecord(Collection<Employee> employees, Long employeeId) {
        return employees.stream().filter(employee -> isSelfEmployeeRecord(employee, employeeId)).findFirst();
    }

    protected boolean isSelfEmployeeRecord(Employee employee, Collection<Long> employeeIds) {
        return employeeIds.stream().anyMatch(id -> isSelfEmployeeRecord(employee, id));
    }

    protected boolean isSelfEmployeeRecord(Employee employee, Long employeeId) {
        return employee.getId().equals(employeeId);
    }

    protected Stream<Employee> filterEmployeesSelfClientRecord(Collection<Employee> employees, Collection<ClientSecurityAwareEntity> clients) {
        return employees.stream().filter(e -> clients.stream().anyMatch(c -> isSelfClientRecord(e, c)));
    }

    protected Stream<Employee> findSelfClientRecord(Collection<Employee> employees, ClientSecurityAwareEntity client) {
        return employees.stream().filter(e -> isSelfClientRecord(e, client));
    }

    protected <C extends ClientSecurityAwareEntity> boolean isAnySelfClientRecord(Collection<Employee> employees, Collection<C> clients) {
        return clients.stream().anyMatch(c -> isSelfClientRecord(employees, c));
    }

    protected <T extends ClientSecurityAwareEntity> boolean isSelfClientRecord(Collection<Employee> employees, Long clientId) {
        return CareCoordinationUtils.getAssociatedClientIds(employees).contains(clientId);
    }

    protected <T extends IdAware> boolean isSelfClientRecord(Collection<Employee> employees, T client) {
        return isSelfClientRecord(employees, client.getId());
    }

    protected <T extends ClientSecurityAwareEntity> boolean isSelfClientRecord(Employee e, T client) {
        return e.getAssociatedClientIds().contains(client.getId());
    }

    protected boolean existsSelfClientRecordInCommunity(Collection<Employee> employees, Long communityId) {
        return employees.stream().anyMatch(employee -> employeeService.existsSelfClientRecordInCommunity(employee, communityId));
    }

    protected Stream<Employee> findSelfClientRecordInCommunity(Collection<Employee> employees, Long communityId) {
        return employees.stream().filter(employee -> employeeService.existsSelfClientRecordInCommunity(employee, communityId));
    }

    protected boolean hasAnyPermission(Collection<Permission> permissions) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return permissionFilter.hasAnyPermission(permissions);
    }

    protected List<ClientSecurityAwareEntity> mergedClients(Long clientId) {
        return clientService.findAllMergedClients(Collections.singletonList(clientId), ClientSecurityAwareEntity.class);
    }

    protected List<ClientSecurityAwareEntity> mergedClientsEligibleForDiscovery(Long clientId) {
        return clientService.findAllMergedClientsEligibleForDiscovery(Collections.singletonList(clientId),
                ClientSecurityAwareEntity.class);
    }

    protected List<ClientSecurityAwareEntity> mergedClientsEligibleForDiscovery(Collection<ClientSecurityAwareEntity> clients) {
        return clientService.findAllMergedClientsEligibleForDiscovery(CareCoordinationUtils.toIdsSet(clients), ClientSecurityAwareEntity.class);
    }

    protected List<Long> mergedClientIds(Long clientId) {
        return clientService.findAllMergedClientsIds(clientId);
    }

    protected List<ClientSecurityAwareEntity> mergedClientsByIds(Collection<Long> clientIds) {
        return clientService.findAllMergedClients(clientIds, ClientSecurityAwareEntity.class);
    }

    protected <T extends ClientSecurityAwareEntity> List<ClientSecurityAwareEntity> mergedClients(Collection<T> clients) {
        return mergedClientsByIds(CareCoordinationUtils.toIdsSet(clients));
    }

    protected PermissionFilter currentUserFilter() {
        return permissionFilterService.createPermissionFilterForCurrentUser();
    }

    /**
     * There exist at least one community in user's organization which is affiliated
     * to any community in target organization
     *
     * @param employees
     * @param primaryOrganizationId
     * @return
     */
    protected boolean isAnyInAffiliatedOrganizationOfOrganization(Collection<Employee> employees, Long primaryOrganizationId) {
        var affiliatedOrganizationIds = CareCoordinationUtils.getOrganizationIdsSet(employees);
        return affiliatedRelationshipDao.existsByAffiliatedOrganizationIdInAndPrimaryOrganizationId(
                affiliatedOrganizationIds, primaryOrganizationId);

    }

    protected boolean isAnyInAffiliatedOrganizationOfCommunity(Collection<Employee> employees, Long primaryCommunityId) {
        return affiliatedRelationshipDao.existsByAffiliatedOrganizationIdInAndPrimaryCommunityId(
                CareCoordinationUtils.getOrganizationIdsSet(employees), primaryCommunityId);
    }

    protected boolean isAnyInPrimaryOrganizationOfCommunity(Collection<Employee> employees, Long affiliatedCommunityId) {
        return affiliatedRelationshipDao.existsByPrimaryOrganizationIdInAndAffiliatedCommunityId(
                CareCoordinationUtils.getOrganizationIdsSet(employees), affiliatedCommunityId);
    }

    protected boolean isAnyInAffiliatedOrganizationOfAnyCommunity(Collection<Employee> employees, Iterable<Long> primaryCommunityIds) {
        return affiliatedRelationshipDao.existsByAffiliatedOrganizationIdInAndPrimaryCommunityIdIn(
                CareCoordinationUtils.getOrganizationIdsSet(employees), primaryCommunityIds);
    }

    protected boolean isAnyInAffiliatedCommunityOfOrganization(Collection<Employee> employees, Long primaryOrganizationId) {
        return affiliatedRelationshipDao.existsByAffiliatedCommunityIdInAndPrimaryOrganizationId(
                CareCoordinationUtils.getCommunityIdsSet(employees), primaryOrganizationId);
    }

    protected boolean isAnyInAffiliatedCommunity(Collection<Employee> employees, Long primaryCommunityId) {
        return affiliatedRelationshipDao.existsByAffiliatedCommunityIdInAndPrimaryCommunityId(
                CareCoordinationUtils.getCommunityIdsSet(employees), primaryCommunityId);

    }

    protected boolean isAnyInPrimaryCommunity(Collection<Employee> employees, Long affiliatedCommunityId) {
        return affiliatedRelationshipDao.existsByPrimaryCommunityIdInAndAffiliatedCommunityId(
                CareCoordinationUtils.getCommunityIdsSet(employees), affiliatedCommunityId);
    }

    protected boolean isAnyInAffiliatedCommunityOfAny(Collection<Employee> employees, Iterable<Long> primaryCommunityIds) {
        return affiliatedRelationshipDao.existsByAffiliatedCommunityIdInAndPrimaryCommunityIdIn(
                CareCoordinationUtils.getCommunityIdsSet(employees), primaryCommunityIds);

    }

    protected Stream<Employee> findInAffiliatedOrganizationOfCommunity(Collection<Employee> employees, Long primaryCommunityId) {
        return findInAffiliatedOrganizationOfAnyCommunity(employees, Collections.singletonList(primaryCommunityId));
    }

    protected Stream<Employee> findInAffiliatedOrganizationOfAnyCommunity(Collection<Employee> employees, Collection<Long> primaryCommunityIds) {
        return employees.stream().filter(e -> affiliatedRelationshipDao
                .existsByAffiliatedOrganizationIdAndPrimaryCommunityIdIn(e.getOrganizationId(), primaryCommunityIds));
    }


    protected Stream<Employee> findInAffiliatedCommunity(Collection<Employee> employees, Long primaryCommunityId) {
        return findInAffiliatedCommunityOfAny(employees, Collections.singletonList(primaryCommunityId));
    }

    protected Stream<Employee> findInAffiliatedCommunityOfAny(Collection<Employee> employees, Collection<Long> primaryCommunityIds) {
        return employees.stream().filter(e -> affiliatedRelationshipDao
                .existsByAffiliatedCommunityIdAndPrimaryCommunityIdIn(e.getCommunityId(), primaryCommunityIds));
    }

    protected boolean isClientAddedBySelf(Collection<Employee> employees, ClientSecurityAwareEntity client) {
        return isAnyClientAddedBySelf(employees, Optional.ofNullable(client).stream());
    }

    protected boolean isClientOptedInAndAddedBySelf(Collection<Employee> employees, ClientSecurityAwareEntity client) {
        return isAnyClientAddedBySelf(employees, Optional.ofNullable(client).stream()) && isClientOptedIn(client);
    }

    protected <C extends ClientSecurityAwareEntity> boolean isAnyClientAddedBySelf(Collection<Employee> employees, Collection<C> clients) {
        return isAnyClientAddedBySelf(employees, clients.stream());
    }

    private <C extends ClientSecurityAwareEntity> boolean isAnyClientAddedBySelf(Collection<Employee> employees, Stream<C> clients) {
        var creators = getClientsCreatorIds(clients);

        return creators.unordered().distinct().anyMatch(creatorId -> isSelfEmployeeRecord(employees, creatorId));
    }

    protected boolean isAnyClientAddedBySelfInCommunity(Collection<Employee> employees, Long communityId) {
        return clientService.existsCreatedByAnyInCommunityId(employees, communityId);
    }

    protected boolean isAnyClientOptedInAndAddedBySelfInCommunity(Collection<Employee> employees, Long communityId) {
        return clientService.existsOptedInAndCreatedByAnyInCommunityId(employees, communityId);
    }

    protected boolean isAnyClientOptedInAndAddedBySelfInOrganization(Collection<Employee> employees, Long organizationId) {
        return clientService.existsOptedInAndCreatedByAnyInOrganizationId(employees, organizationId);
    }

    protected List<Employee> findClientCreatorsInCommunity(Collection<Employee> employees, Long communityId) {
        return clientService.findCreatedByAnyInCommunityId(employees, communityId);
    }

    protected <T extends ClientSecurityAwareEntity> Stream<Long> getClientsCreatorIds(Stream<T> clients) {
        return clients
                .map(ClientSecurityAwareEntity::getCreatedById)
                .filter(Objects::nonNull);
    }

    protected <C extends ClientSecurityAwareEntity> Stream<Employee> findClientsCreators(Collection<Employee> employees,
                                                                                         Collection<C> clients) {
        var creators = getClientsCreatorIds(clients.stream()).collect(Collectors.toSet());
        return employees.stream().filter(e -> isSelfEmployeeRecord(e, creators));
    }

    protected boolean isAnyClientAddedBySelfInOrganization(Collection<Employee> employees, Long organizationId) {
        return clientService.existsCreatedByAnyInOrganization(employees, organizationId);
    }

    protected Lazy<ClientSecurityAwareEntity> lazyClient(Long id) {
        return Lazy.of(() -> clientService.findSecurityAwareEntity(id));
    }

    protected <T extends ClientIdAware> Lazy<ClientSecurityAwareEntity> lazyClient(Supplier<T> clientIdAware) {
        return Lazy.of(() -> clientService.findSecurityAwareEntity(clientIdAware.get().getClientId()));
    }

    protected <T extends ClientIdAware> Lazy<List<ClientSecurityAwareEntity>> lazyMergedSecurityClients(Supplier<T> clientIdAwareSupplier) {
        return lazyMergedSecurityClients(clientIdAwareSupplier.get());
    }

    protected Lazy<List<ClientSecurityAwareEntity>> lazyMergedSecurityClients(ClientIdAware clientIdAware) {
        return Lazy.of(() -> mergedClients(clientIdAware.getClientId()));
    }

    protected Lazy<List<ClientSecurityAwareEntity>> lazyMergedSecurityClientsEligibleForDiscovery(ClientIdAware clientIdAware) {
        return Lazy.of(() -> mergedClientsEligibleForDiscovery(clientIdAware.getClientId()));
    }

    protected Lazy<List<ClientSecurityAwareEntity>> lazyMergedSecurityClientsEligibleForDiscovery(Collection<ClientSecurityAwareEntity> clients) {
        return Lazy.of(() -> mergedClientsEligibleForDiscovery(clients));
    }

    protected <A extends ClientSecurityAwareEntity, C extends Collection<A>>
    Lazy<List<ClientSecurityAwareEntity>> lazyMergedSecurityClientsOfClientIds(Collection<Long> clientsSupplier) {
        return Lazy.of(() -> mergedClientsByIds(clientsSupplier));
    }

    protected <T extends OrganizationIdAware, C extends Collection<T>> Lazy<Set<Long>> lazyOrganizationIdsSet(Supplier<C> orgIdAwares) {
        return Lazy.of(() -> CareCoordinationUtils.getOrganizationIdsSet(orgIdAwares.get()));
    }

    protected <T extends CommunityIdAware, C extends Collection<T>> Lazy<Set<Long>> lazyCommunityIdsSet(Supplier<C> commIdAwares) {
        return Lazy.of(() -> CareCoordinationUtils.getCommunityIdsSet(commIdAwares.get()));
    }

    protected boolean areInDifferentOrganizations(Employee employee, OrganizationIdAware organizationIdAware) {
        return !employee.getOrganizationId().equals(organizationIdAware.getOrganizationId());
    }

    protected Optional<Employee> findEmployeeCreatedBySelf(Collection<Employee> employees, EmployeeSecurityAwareEntity employee) {
        return employees.stream().filter(e -> e.getId().equals(employee.getCreatorId())).findFirst();
    }

    protected Long resolveAndValidateOrganizationId(Long communityId, Long organizationId, Long anyTargetCommunityValue) {
        if (organizationId == null) {
            if (anyTargetCommunityValue.equals(communityId)) {
                throw new ApplicationException("Organization id for ANY_TARGET_COMMUNITY should be specified");
            }
            return communityService.findSecurityAwareEntity(communityId).getOrganizationId();
        } else {
            if (!anyTargetCommunityValue.equals(communityId)) {
                var community = communityService.findSecurityAwareEntity(communityId);
                if (!community.getOrganizationId().equals(organizationId)) {
                    throw new ValidationException("Community is not in Organization");
                }
            }
            return organizationId;
        }
    }

    protected boolean isInEligibleForDiscoveryCommunity(CommunityIdAware communityIdAware) {
        return isEligibleForDiscoveryCommunity(communityIdAware.getCommunityId());
    }

    protected boolean isInEligibleForDiscoveryClientCommunity(ClientCommunityIdAware clientCommunityIdAware) {
        return isEligibleForDiscoveryCommunity(clientCommunityIdAware.getClientCommunityId());
    }

    protected boolean isEligibleForDiscoveryCommunity(Long communityId) {
        return communityService.isEligibleForDiscovery(communityId);
    }

    protected boolean isEligibleForDiscoveryCommunity(EligibleForDiscoveryAware communityId) {
        return communityService.isEligibleForDiscovery(communityId);
    }

    protected boolean isEligibleForDiscovery(Long communityId, Long organizationId, Long anyTargetCommunity) {
        if (anyTargetCommunity.equals(communityId)) {
            if (!organizationService.hasEligibleForDiscoveryCommunities(organizationId)) {
                return false;
            }
        } else {
            if (!communityService.isEligibleForDiscovery(communityId)) {
                return false;
            }
        }
        return true;
    }

    protected boolean isFeaturedServiceProviderOfAccessibleCommunity(Long communityId, PermissionFilter permissionFilter) {
        return featuredServiceProviderService.isFeaturedServiceProviderOfAccessibleCommunity(communityId, permissionFilter);
    }

    protected boolean isAnyCreatedInPartnerCommunityOrganization(Collection<Employee> employees, Long communityId) {
        var networkIds = Optional.ofNullable(marketplaceService.findByCommunityId(communityId))
                .map(Marketplace::getMarketplacePartnerNetworks)
                .stream()
                .flatMap(Collection::stream)
                .map(MarketplacePartnerNetwork::getPartnerNetworkId)
                .collect(Collectors.toSet());

        return isAnyCreatedInAnyOrganizationInPartnerNetwork(employees, networkIds);
    }

    protected boolean isAnyCreatedInPartnerCommunityOrganizationOfOrganization(Collection<Employee> employees, Long organizationId) {
        var networkIds = Optional.ofNullable(marketplaceService.findByOrgIdWithCommunities(organizationId))
                .stream()
                .flatMap(Collection::stream)
                .map(Marketplace::getMarketplacePartnerNetworks)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .map(MarketplacePartnerNetwork::getPartnerNetworkId)
                .collect(Collectors.toSet());

        return isAnyCreatedInAnyOrganizationInPartnerNetwork(employees, networkIds);
    }

    protected boolean isAnyCreatedInAnyOrganizationInPartnerNetwork(Collection<Employee> employees, Set<Long> networkIds) {

        if (networkIds.isEmpty()) return false;

        var organizationIds = marketplaceService.findAllByPartnerNetworkIds(networkIds, OrganizationIdAware.class)
                .stream()
                .map(OrganizationIdAware::getOrganizationId)
                .collect(Collectors.toSet());

        return isAnyCreatedUnderAnyOrganization(employees, organizationIds);
    }

    protected boolean isAnySelfClientRecordOfOrganization(Collection<Employee> employees, Long organizationId) {
        var associatedClientIds = CareCoordinationUtils.getAssociatedClientIds(employees);
        return clientService.existsByIdsInOrganization(associatedClientIds, organizationId);
    }

    protected boolean isOrganizationHasCommunities(PermissionFilter filter, Long organizationId) {
        return communityService.hasVisibleCommunities(filter, organizationId);
    }

    protected boolean isSignatureEnabledForOrganization(Long organizationId) {
        var organization = organizationService.findById(organizationId, OrganizationIsSignatureEnabledAware.class);
        return organization != null && organization.getIsSignatureEnabled();
    }

    protected boolean hasNoAssociatedClientsOrAnyAssociatedClientOptedIn(AssociatedClientIdsAware employee) {
        if (employee.getAssociatedClientIds().isEmpty()) {
            return true;
        }
        var associatedClients = clientService.findAllById(employee.getAssociatedClientIds(), HieConsentPolicyTypeAware.class);
        return isAnyClientOptedIn(associatedClients);
    }

    protected <T extends HieConsentPolicyTypeAware> boolean areAllClientsOptedIn(Collection<T> clients) {
        return clients.stream().allMatch(this::isClientOptedIn);
    }

    protected boolean isClientOptedIn(HieConsentPolicyTypeAware client) {
        return !clientService.isOptOutPolicy(client);
    }

    protected boolean isClientOptedIn(ClientHieConsentPolicyTypeAware client) {
        return !clientService.isOptOutPolicy(client::getClientHieConsentPolicyType);
    }

    protected boolean isClientOptedOut(HieConsentPolicyTypeAware client) {
        return clientService.isOptOutPolicy(client);
    }

    protected <T extends HieConsentPolicyTypeAware> boolean isAnyClientOptedOut(Collection<T> clients) {
        return clients.stream().anyMatch(this::isClientOptedOut);
    }

    protected <T extends HieConsentPolicyTypeAware> boolean isAnyClientOptedIn(Collection<T> clients) {
        return clients.stream().anyMatch(this::isClientOptedIn);
    }

    protected boolean isProspectAddedBySelf(Collection<Employee> employees, ProspectSecurityAwareEntity prospect) {
        return employees.stream()
                .map(BasicEntity::getId)
                        .anyMatch(employeeId -> Objects.equals(prospect.getCreatedById(), employeeId));
    }
}
