package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientCareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.ClientCareTeamMemberDao;
import com.scnsoft.eldermark.dao.specification.ClientCareTeamMemberSpecificationGenerator;
import com.scnsoft.eldermark.entity.CareTeamMemberIdNameRoleAvatarAware;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModificationType;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientCareTeamMemberServiceImpl implements ClientCareTeamMemberService, ClientCareTeamHieConsentPolicyUpdateService {

    @Autowired
    private ClientCareTeamMemberDao clientCareTeamMemberDao;

    @Autowired
    private ClientCareTeamMemberSpecificationGenerator clientCTMSpecifications;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientCareTeamMemberModifiedService clientCareTeamMemberModifiedService;

    @Override
    @Transactional(readOnly = true)
    public List<ClientCareTeamMember> findEmergencyContacts(Long clientId) {
        return clientCareTeamMemberDao.findByClient_IdInAndEmergencyContactIsTrueAndOnHoldIsFalse(clientService.findAllMergedClientsIds(clientId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyEmployeeInClientCareTeam(Collection<Employee> employees, Long clientId,
                                                 AffiliatedCareTeamType type,
                                                 HieConsentCareTeamType consentType) {
        return isAnyEmployeeInAnyClientCareTeam(employees, Collections.singletonList(clientId), type, consentType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientCareTeamMember> findClientsCareTeamMembersAmongEmployees(Collection<Employee> employees,
                                                                               Collection<Long> clientIds,
                                                                               AffiliatedCareTeamType type,
                                                                               HieConsentCareTeamType consentType) {
        var employeeIn = clientCTMSpecifications.employeeIn(employees);
        var byClientIdIn = clientCTMSpecifications.byClientIdIn(clientIds);
        var ofAffiliationType = clientCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return clientCareTeamMemberDao.findAll(employeeIn.and(byClientIdIn).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    public List<ClientCareTeamMember> findClientCareTeamMembersAmongEmployeesAndClientCommunity(Collection<Employee> employees,
                                                                                                Long communityId,
                                                                                                AffiliatedCareTeamType type,
                                                                                                HieConsentCareTeamType consentType) {
        var employeeIn = clientCTMSpecifications.employeeIn(employees);
        var byClientCommunityId = clientCTMSpecifications.byClientCommunityId(communityId);
        var ofAffiliationType = clientCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return clientCareTeamMemberDao.findAll(employeeIn.and(byClientCommunityId).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyEmployeeInAnyClientCareTeam(Collection<Employee> employees, Collection<Long> clientIds,
                                                    AffiliatedCareTeamType type,
                                                    HieConsentCareTeamType consentType) {
        var employeeIn = clientCTMSpecifications.employeeIn(employees);
        var byClientIdIn = clientCTMSpecifications.byClientIdIn(clientIds);
        var ofAffiliationType = clientCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return clientCareTeamMemberDao.exists(employeeIn.and(byClientIdIn).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyEmployeeIdInAnyClientCareTeam(Collection<Long> employeeIds, Collection<Long> clientIds,
                                                      AffiliatedCareTeamType type,
                                                      HieConsentCareTeamType consentType) {
        var employeeIdIn = clientCTMSpecifications.byEmployeeIdIn(employeeIds);
        var byClientIdIn = clientCTMSpecifications.byClientIdIn(clientIds);
        var ofAffiliationType = clientCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return clientCareTeamMemberDao.exists(employeeIdIn.and(byClientIdIn).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyEmployeeInAnyClientCareTeamOfOrganization(Collection<Employee> employees, Long organizationId,
                                                                  AffiliatedCareTeamType type,
                                                                  HieConsentCareTeamType consentType) {
        var employeeIn = clientCTMSpecifications.employeeIn(employees);
        var byClientOrganizationId = clientCTMSpecifications.byClientOrganizationId(organizationId);
        var ofAffiliationType = clientCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return clientCareTeamMemberDao.exists(employeeIn.and(byClientOrganizationId).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyEmployeeInAnyClientCareTeamOfCommunity(Collection<Employee> employees,
                                                               Long communityId,
                                                               AffiliatedCareTeamType type,
                                                               HieConsentCareTeamType consentType) {
        return isAnyEmployeeInAnyClientCareTeamOfAnyCommunity(
                employees,
                List.of(communityId),
                type,
                consentType
        );
    }

    @Override
    public boolean isAnyEmployeeInAnyClientCareTeamOfAnyCommunity(
            Collection<Employee> employees,
            Collection<Long> communityIds,
            AffiliatedCareTeamType type,
            HieConsentCareTeamType consentType
    ) {
        var employeeIn = clientCTMSpecifications.employeeIn(employees);
        var byClientCommunityId = clientCTMSpecifications.byClientCommunityIds(communityIds);
        var ofAffiliationType = clientCTMSpecifications.ofAffiliationType(type);

        return clientCareTeamMemberDao.exists(employeeIn.and(byClientCommunityId).and(ofAffiliationType).and(clientCTMSpecifications.ofConsentType(consentType)));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCareTeamVisibleForAny(Collection<Employee> employees, Long clientId) {
        var notViewableCareTeam = clientCTMSpecifications.notViewableCareTeamForAll(
                CareCoordinationUtils.toIdsSet(employees)
        );
        var byClientId = clientCTMSpecifications.byClientId(clientId);
        //todo notOnHold?

        return !clientCareTeamMemberDao.exists(notViewableCareTeam.and(byClientId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClientCareTeamMember> findById(Long id) {
        return clientCareTeamMemberDao.findById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Set<Long> findClientCareTeamMemberIds(Collection<Long> clientCommunityIds, Long employeeOrganizationId,
                                                 HieConsentCareTeamType consentType) {
        var byClientCommunityIds = clientCTMSpecifications.byClientCommunityIds(clientCommunityIds);
        var byEmployeeOrganizationId = clientCTMSpecifications.byEmployeeOrganizationId(employeeOrganizationId);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return CareCoordinationUtils.toIdsSet(clientCareTeamMemberDao.findAll(
                        byClientCommunityIds.and(byEmployeeOrganizationId).and(ofConsentType),
                        IdAware.class
                ).stream()
        );
    }

    @Override
    @Transactional
    public void deleteByIds(Collection<Long> ids) {
        clientCareTeamMemberDao.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter) {
        var chatAccessible = clientCTMSpecifications.chatAccessible(
                permissionFilter,
                filter.getExcludedEmployeeId()
        );
        if (!filter.getIncludeNonAssociatedClients()) {
            chatAccessible = chatAccessible.and(clientCTMSpecifications.clientHasActiveAssociatedEmployee());
        }

        return clientCareTeamMemberDao.exists(chatAccessible);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareTeamMemberIdNameRoleAvatarAware> findChatAccessibleClientCareTeamMembers(
            PermissionFilter permissionFilter, AccessibleChatClientCareTeamFilter filter) {
        var chatAccessible = clientCTMSpecifications.chatAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byClientId = clientCTMSpecifications.ofMergedByClientId(filter.getClientId());
        var sortByEmployeeName = Sort.by(CareTeamMember_.EMPLOYEE + "." + Employee_.FIRST_NAME,
                CareTeamMember_.EMPLOYEE + "." + Employee_.LAST_NAME);
        return clientCareTeamMemberDao.findAll(chatAccessible.and(byClientId), CareTeamMemberIdNameRoleAvatarAware.class,
                sortByEmployeeName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareTeamMemberIdNameRoleAvatarAware> findVideoCallAccessibleClientCareTeamMembers(PermissionFilter permissionFilter, AccessibleChatClientCareTeamFilter filter) {
        var videoCallAccessible = clientCTMSpecifications.videoCallAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byClientId = clientCTMSpecifications.ofMergedByClientId(filter.getClientId());
        var sortByEmployeeName = Sort.by(CareTeamMember_.EMPLOYEE + "." + Employee_.FIRST_NAME,
                CareTeamMember_.EMPLOYEE + "." + Employee_.LAST_NAME);
        return clientCareTeamMemberDao.findAll(videoCallAccessible.and(byClientId), CareTeamMemberIdNameRoleAvatarAware.class,
                sortByEmployeeName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasChatAccessibleClientCareTeamMembers(PermissionFilter permissionFilter, AccessibleChatClientCareTeamFilter filter) {
        var chatAccessible = clientCTMSpecifications.chatAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byClientId = clientCTMSpecifications.ofMergedByClientId(filter.getClientId());
        return clientCareTeamMemberDao.exists(chatAccessible.and(byClientId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVideoCallAccessibleClientCareTeamMembers(PermissionFilter permissionFilter, AccessibleChatClientCareTeamFilter filter) {
        var videoCallAccessible = clientCTMSpecifications.videoCallAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byClientId = clientCTMSpecifications.ofMergedByClientId(filter.getClientId());
        return clientCareTeamMemberDao.exists(videoCallAccessible.and(byClientId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existAccessibleCareTeamMemberInCommunity(PermissionFilter permissionFilter, Long communityId,
                                                            HieConsentCareTeamType consentType) {
        var hasAccess = clientCTMSpecifications.hasAccess(permissionFilter);
        var byEmployeeCommunity = clientCTMSpecifications.byEmployeeCommunityId(communityId);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return clientCareTeamMemberDao.exists(hasAccess.and(byEmployeeCommunity).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existAccessibleCareTeamMemberInOrganization(PermissionFilter permissionFilter, Long organizationId,
                                                               HieConsentCareTeamType consentType) {
        var hasAccess = clientCTMSpecifications.hasAccess(permissionFilter);
        var byEmployeeOrganization = clientCTMSpecifications.byEmployeeOrganizationId(organizationId);
        var ofConsentType = clientCTMSpecifications.ofConsentType(consentType);

        return clientCareTeamMemberDao.exists(hasAccess.and(byEmployeeOrganization).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientCareTeamMember getById(Long id) {
        return clientCareTeamMemberDao.getOne(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientCareTeamMember> findProspectivePrimaryContacts(PermissionFilter permissionFilter, Long clientId) {
        var hasAccess = clientCTMSpecifications.hasAccess(permissionFilter);
        var byClient = clientCTMSpecifications.byClientId(clientId);
        var active = clientCTMSpecifications.isContactActive();
        var byRole = clientCTMSpecifications.byContactRoleIn(Set.of(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES, CareTeamRoleCode.ROLE_PARENT_GUARDIAN));
        var notOnHold = clientCTMSpecifications.ofConsentType(HieConsentCareTeamType.current(clientId));

        return clientCareTeamMemberDao.findAll(hasAccess.and(byClient).and(active).and(byRole).and(notOnHold));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientCareTeamMember> findByEmployeeId(Long employeeId) {
        var byEmployeeId = clientCTMSpecifications.byEmployeeId(employeeId);
        return clientCareTeamMemberDao.findAll(byEmployeeId);
    }

    @Override
    public <P> List<P> find(Specification<ClientCareTeamMember> specification, Class<P> projectionClass) {
        return clientCareTeamMemberDao.findAll(specification, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends IdAware & CommunityIdAware> List<ClientCareTeamMemberIdsAware> onHoldCandidates(T client) {
        return clientCareTeamMemberDao.findAll(clientCTMSpecifications.onHoldCandidates(client), ClientCareTeamMemberIdsAware.class);
    }

    @Override
    public List<ClientCareTeamMemberIdsAware> onHoldCandidatesByClientCommunityId(Long communityId) {
        return clientCareTeamMemberDao.findAll(
                clientCTMSpecifications.onHoldCandidates()
                        .and(clientCTMSpecifications.byClientCommunityId(communityId)),
                ClientCareTeamMemberIdsAware.class
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeIdAware> onHoldAmongEmployeeIds(Long clientId, Collection<Long> employeeIds) {
        if (CollectionUtils.isEmpty(employeeIds)) {
            return List.of();
        }
        var onHold = clientCTMSpecifications.ofConsentType(
                HieConsentCareTeamType.onHold(clientId)
        );
        var byClientId = clientCTMSpecifications.byClientId(clientId);
        var byEmployeeIdIn = clientCTMSpecifications.byEmployeeIdIn(employeeIds);

        return clientCareTeamMemberDao.findAll(onHold.and(byEmployeeIdIn).and(byClientId), EmployeeIdAware.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> onHoldAmongClientIds(Collection<Long> clientIds, Long employeeId, Class<T> projectionClass) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return List.of();
        }
        var onHold = clientCTMSpecifications.ofConsentType(
                HieConsentCareTeamType.onHold(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID)
        );
        var byEmployeeId = clientCTMSpecifications.byEmployeeId(employeeId);
        var byClientIds = clientCTMSpecifications.byClientIdIn(clientIds);

        return clientCareTeamMemberDao.findAll(onHold.and(byEmployeeId).and(byClientIds), projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <CTM extends IdAware> Map<Boolean, List<CTM>> calculateCurrentOnHoldCandidate(Long employeeId, Class<CTM> ctmProjection) {
        var byEmployeeId = clientCTMSpecifications.byEmployeeId(employeeId);
        var clientOptOut = clientCTMSpecifications.clientOptOutPolicy();

        var groupedIds = clientCareTeamMemberDao.calculateCurrentOnHoldCandidate(
                byEmployeeId.and(clientOptOut)
        );

        if (MapUtils.isEmpty(groupedIds)) {
            return Map.of();
        }

        var allIds = groupedIds.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        var allCtm = clientCareTeamMemberDao.findByIdIn(allIds, ctmProjection);
        var allCtmMap = allCtm.stream().collect(StreamUtils.toMapOfUniqueKeys(IdAware::getId));

        var result = new HashMap<Boolean, List<CTM>>();
        groupedIds.forEach((isCurrent, ids) -> {
            result.put(isCurrent, ids.stream().map(allCtmMap::get).collect(Collectors.toList()));
        });

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findCareTeamMembersOfClients(Collection<Long> clientIds, Long employeeIdToExclude, Class<T> projectionClass) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return List.of();
        }
        var spec = clientCTMSpecifications.byClientIdIn(clientIds);
        if (employeeIdToExclude != null) {
            spec = spec.and(clientCTMSpecifications.byEmployeeIdNot(employeeIdToExclude));
        }
        return clientCareTeamMemberDao.findAll(spec, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findCurrentCareTeamMembersOfClients(Collection<Long> clientIds, Long employeeIdToExclude, Class<T> projectionClass) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return List.of();
        }
        var spec = clientCTMSpecifications.byClientIdIn(clientIds);
        spec = spec.and(clientCTMSpecifications.ofConsentType(HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID)));
        if (employeeIdToExclude != null) {
            spec = spec.and(clientCTMSpecifications.byEmployeeIdNot(employeeIdToExclude));
        }
        return clientCareTeamMemberDao.findAll(spec, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findOnHoldCareTeamMembersOfClients(Collection<Long> clientIds, Long employeeIdToExclude, Class<T> projectionClass) {
        if (CollectionUtils.isEmpty(clientIds)) {
            return List.of();
        }
        var spec = clientCTMSpecifications.byClientIdIn(clientIds);
        spec = spec.and(clientCTMSpecifications.ofConsentType(HieConsentCareTeamType.onHold(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID)));
        if (employeeIdToExclude != null) {
            spec = spec.and(clientCTMSpecifications.byEmployeeIdNot(employeeIdToExclude));
        }
        return clientCareTeamMemberDao.findAll(spec, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findClientCareTeamEntries(Long employeeId, Long clientId, Class<T> projectionClass) {
        var byClientId = clientCTMSpecifications.byClientId(clientId);
        var byEmployeeId = clientCTMSpecifications.byEmployeeId(employeeId);

        return clientCareTeamMemberDao.findAll(byClientId.and(byEmployeeId), projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOnHoldForAnyClientInCommunity(Long employeeId, Long communityId) {
        var byEmployeeId = clientCTMSpecifications.byEmployeeId(employeeId);
        var byClientCommunityId = clientCTMSpecifications.byClientCommunityId(communityId);
        var onHold = clientCTMSpecifications.ofConsentType(HieConsentCareTeamType.onHold(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID));

        return clientCareTeamMemberDao.exists(byEmployeeId.and(byClientCommunityId).and(onHold));
    }

    @Override
    @Transactional
    public <T extends IdAware & ClientIdAware & EmployeeIdAware & ClientAssociatedEmployeeIdAware> void setOnHold(Collection<T> clientCareTeamMembers,
                                                                                                                  Long performedById) {
        if (CollectionUtils.isNotEmpty(clientCareTeamMembers)) {
            clientCareTeamMemberDao.updateOnHoldValue(true, clientCareTeamMembers.stream().map(IdAware::getId).collect(Collectors.toList()));
        }
        clientCareTeamMembers.stream()
                .filter(it -> !Objects.equals(it.getClientAssociatedEmployeeId(), it.getEmployeeId()))
                .forEach(ctm ->
                        clientCareTeamMemberModifiedService.clientCareTeamMemberModified(ctm.getId(),
                                ctm.getEmployeeId(),
                                ctm.getClientId(),
                                performedById,
                                CareTeamMemberModificationType.ON_HOLD)
                );
    }

    @Override
    @Transactional
    public void setCurrent(Collection<Long> clientCareTeamMemberIds) {
        if (CollectionUtils.isNotEmpty(clientCareTeamMemberIds)) {
            clientCareTeamMemberDao.updateOnHoldValue(false, clientCareTeamMemberIds);
        }
        clientCareTeamMemberModifiedService.setCurrent(clientCareTeamMemberIds);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return findByClientCtmId(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> Optional<P> findByClientCtmId(Long id, Class<P> projection) {
        return clientCareTeamMemberDao.findById(id, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientCareTeamMemberDao.findByIdIn(ids, projection);
    }
}
