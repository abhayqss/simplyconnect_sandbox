package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatCommunityCareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.projection.CommunityCtmCurrentOnHoldProjection;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeOrgIdCommunityOrgIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.CommunityCareTeamMemberDao;
import com.scnsoft.eldermark.dao.specification.CommunityCareTeamMemberSpecificationGenerator;
import com.scnsoft.eldermark.entity.CareTeamMemberIdNameRoleAvatarAware;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.Employee_;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember_;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommunityCareTeamMemberServiceImpl implements CommunityCareTeamMemberService, CommunityCareTeamHieConsentPolicyUpdateService {

    @Autowired
    private CommunityCareTeamMemberDao communityCareTeamMemberDao;

    @Autowired
    private CommunityCareTeamMemberSpecificationGenerator communityCTMSpecifications;

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyEmployeeInCommunityCareTeam(Collection<Employee> employees, Long communityId, AffiliatedCareTeamType type,
                                                    HieConsentCareTeamType consentType) {
        return isAnyEmployeeInAnyCommunityCareTeam(employees, Collections.singletonList(communityId), type, consentType);
    }

    @Override
    public boolean isAnyEmployeeInAnyCommunityCareTeam(Collection<Employee> employees, Collection<Long> communityIds,
                                                       AffiliatedCareTeamType type,
                                                       HieConsentCareTeamType consentType) {
        var employeeIn = communityCTMSpecifications.employeeIn(employees);
        var byCommunityIdsIn = communityCTMSpecifications.byCommunityIdIn(communityIds);
        var ofAffiliationType = communityCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return communityCareTeamMemberDao.exists(employeeIn.and(byCommunityIdsIn).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAnyEmployeeInAnyCommunityCareTeamOfOrganization(Collection<Employee> employees, Long organizationId, AffiliatedCareTeamType type,
                                                                     HieConsentCareTeamType consentType) {
        var employeeIn = communityCTMSpecifications.employeeIn(employees);
        var byCommunityOrganizationId = communityCTMSpecifications.byCommunityOrganizationId(organizationId);
        var ofAffiliationType = communityCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return communityCareTeamMemberDao.exists(employeeIn.and(byCommunityOrganizationId).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityCareTeamMember> findCommunitiesCareTeamMembersAmongEmployees(Collection<Employee> employees,
                                                                                      Collection<Long> communityIds,
                                                                                      AffiliatedCareTeamType type,
                                                                                      HieConsentCareTeamType consentType) {
        var employeeIn = communityCTMSpecifications.employeeIn(employees);
        var byCommunityIdsIn = communityCTMSpecifications.byCommunityIdIn(communityIds);
        var ofAffiliationType = communityCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return communityCareTeamMemberDao.findAll(employeeIn.and(byCommunityIdsIn).and(ofAffiliationType).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findByEmployeeId(Long employeeId, AffiliatedCareTeamType type,
                                        HieConsentCareTeamType consentType,
                                        Class<T> projectionClass) {
        var byEmployeeId = communityCTMSpecifications.byEmployeeId(employeeId);
        var ofAffiliationType = communityCTMSpecifications.ofAffiliationType(type);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return communityCareTeamMemberDao.findAll(byEmployeeId.and(ofAffiliationType).and(ofConsentType), projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> findCommunityCareTeamMemberIds(Collection<Long> communityIds, Long employeeOrganizationId,
                                                    HieConsentCareTeamType consentType) {
        var byCommunityIdsIn = communityCTMSpecifications.byCommunityIdIn(communityIds);
        var byEmployeeOrganizationId = communityCTMSpecifications.byEmployeeOrganizationId(employeeOrganizationId);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return CareCoordinationUtils.toIdsSet(communityCareTeamMemberDao.findAll(
                byCommunityIdsIn.and(byEmployeeOrganizationId).and(ofConsentType),
                IdAware.class).stream()
        );
    }

    @Override
    @Transactional
    public void deleteByIds(Collection<Long> ids) {
        communityCareTeamMemberDao.deleteAllByIdIn(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter) {
        var chatAccessible = communityCTMSpecifications.chatAccessible(permissionFilter, filter.getExcludedEmployeeId());
        return communityCareTeamMemberDao.exists(chatAccessible);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareTeamMemberIdNameRoleAvatarAware> findChatAccessibleCommunityCareTeamMembers(
            PermissionFilter permissionFilter, AccessibleChatCommunityCareTeamFilter filter) {
        var chatAccessible = communityCTMSpecifications.chatAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byCommunityIds = communityCTMSpecifications.byCommunityIdIn(filter.getCommunityIds());
        var sortByEmployeeName = Sort.by(CareTeamMember_.EMPLOYEE + "." + Employee_.FIRST_NAME,
                CareTeamMember_.EMPLOYEE + "." + Employee_.LAST_NAME);
        return communityCareTeamMemberDao.findAll(chatAccessible.and(byCommunityIds), CareTeamMemberIdNameRoleAvatarAware.class,
                sortByEmployeeName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CareTeamMemberIdNameRoleAvatarAware> findVideoCallAccessibleCommunityCareTeamMembers(PermissionFilter permissionFilter, AccessibleChatCommunityCareTeamFilter filter) {
        var videoCallAccessible = communityCTMSpecifications.videoCallAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byCommunityIds = communityCTMSpecifications.byCommunityIdIn(filter.getCommunityIds());
        var sortByEmployeeName = Sort.by(CareTeamMember_.EMPLOYEE + "." + Employee_.FIRST_NAME,
                CareTeamMember_.EMPLOYEE + "." + Employee_.LAST_NAME);
        return communityCareTeamMemberDao.findAll(videoCallAccessible.and(byCommunityIds), CareTeamMemberIdNameRoleAvatarAware.class,
                sortByEmployeeName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasChatAccessibleCommunityCareTeamMember(PermissionFilter permissionFilter, AccessibleChatCommunityCareTeamFilter filter) {
        var chatAccessible = communityCTMSpecifications.chatAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byCommunityIds = communityCTMSpecifications.byCommunityIdIn(filter.getCommunityIds());
        return communityCareTeamMemberDao.exists(chatAccessible.and(byCommunityIds));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasVideoCallAccessibleCommunityCareTeamMember(PermissionFilter permissionFilter, AccessibleChatCommunityCareTeamFilter filter) {
        var videoCallAccessible = communityCTMSpecifications.videoCallAccessible(permissionFilter, filter.getExcludedEmployeeId());
        var byCommunityIds = communityCTMSpecifications.byCommunityIdIn(filter.getCommunityIds());
        return communityCareTeamMemberDao.exists(videoCallAccessible.and(byCommunityIds));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existAccessibleCareTeamMemberInCommunity(PermissionFilter permissionFilter, Long communityId,
                                                            HieConsentCareTeamType consentType) {
        var hasAccess = communityCTMSpecifications.hasAccess(permissionFilter);
        var byEmployeeCommunity = communityCTMSpecifications.byEmployeeCommunityId(communityId);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return communityCareTeamMemberDao.exists(hasAccess.and(byEmployeeCommunity).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existAccessibleCareTeamMemberInOrganization(PermissionFilter permissionFilter, Long organizationId,
                                                               HieConsentCareTeamType consentType) {
        var hasAccess = communityCTMSpecifications.hasAccess(permissionFilter);
        var byEmployeeOrganization = communityCTMSpecifications.byEmployeeOrganizationId(organizationId);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return communityCareTeamMemberDao.exists(hasAccess.and(byEmployeeOrganization).and(ofConsentType));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findCareTeamMembers(Long communityId, Class<T> projectionClass, HieConsentCareTeamType consentType) {
        var byCommunityId = communityCTMSpecifications.byCommunityId(communityId);
        var ofConsentType = communityCTMSpecifications.ofConsentType(consentType);

        return communityCareTeamMemberDao.findAll(byCommunityId.and(ofConsentType), projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Boolean, List<EmployeeIdAware>> communityCareTeamCurrentOnHoldCandidatesGrouped(Long communityId) {
        var byCommunityId = communityCTMSpecifications.byCommunityId(communityId);
        var communityCtm = communityCareTeamMemberDao.findAll(
                byCommunityId,
                CommunityCtmCurrentOnHoldProjection.class
        );
        return groupCommunityCareTeamCurrentOnHold(communityCtm);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Boolean, List<EmployeeIdAware>> communityCareTeamCurrentOnHoldCandidatesAmongEmployeeIdsGrouped(Long communityId, Collection<Long> employeeIds) {
        var byCommunityId = communityCTMSpecifications.byCommunityId(communityId);
        var byEmployeeIds = communityCTMSpecifications.byEmployeeIdIn(employeeIds);
        var communityCtm = communityCareTeamMemberDao.findAll(
                byCommunityId.and(byEmployeeIds),
                CommunityCtmCurrentOnHoldProjection.class
        );
        return groupCommunityCareTeamCurrentOnHold(communityCtm);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findCareTeamMembersOfCommunities(Collection<Long> communityIds, Long employeeIdToExclude, Class<T> projectionClass) {
        var spec = communityCTMSpecifications.byCommunityIdIn(communityIds);
        if (employeeIdToExclude != null) {
            spec = spec.and(communityCTMSpecifications.byEmployeeIdNot(employeeIdToExclude));
        }

        return communityCareTeamMemberDao.findAll(spec, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findCareTeamMembersOfCommunitiesWhereCanBeCurrent(Collection<Long> communityIds, Long employeeIdToExclude, Class<T> projectionClass) {
        var spec = communityCTMSpecifications.byCommunityIdIn(communityIds);
        spec = spec.and(communityCTMSpecifications.notOnHoldCandidates());
        if (employeeIdToExclude != null) {
            spec = spec.and(communityCTMSpecifications.byEmployeeIdNot(employeeIdToExclude));
        }

        return communityCareTeamMemberDao.findAll(spec, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findCareTeamMembersOfCommunitiesWhereCanBeOnHold(Collection<Long> communityIds, Long employeeIdToExclude, Class<T> projectionClass) {
        var spec = communityCTMSpecifications.byCommunityIdIn(communityIds);
        spec = spec.and(communityCTMSpecifications.onHoldCandidates());
        if (employeeIdToExclude != null) {
            spec = spec.and(communityCTMSpecifications.byEmployeeIdNot(employeeIdToExclude));
        }

        return communityCareTeamMemberDao.findAll(spec, projectionClass);
    }

    private Map<Boolean, List<EmployeeIdAware>> groupCommunityCareTeamCurrentOnHold(List<CommunityCtmCurrentOnHoldProjection> communityCtm) {
        return communityCtm.stream()
                .collect(Collectors.groupingBy(ctm -> !isOnHoldCandidate(ctm), Collectors.<EmployeeIdAware>toList()));
    }

    private boolean isOnHoldCandidate(EmployeeOrgIdCommunityOrgIdAware ctm) {
        return isOnHoldCandidate(ctm.getCommunityOrganizationId(), ctm.getEmployeeOrganizationId());
    }

    @Override
    public boolean isOnHoldCandidate(Long communityOrganizationId, Long employeeOrganizationId) {
        return !Objects.equals(communityOrganizationId, employeeOrganizationId);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> communityCareTeamMemberEntries(Long employeeId, Collection<Long> communityIds, Class<T> projectionClass) {
        if (CollectionUtils.isEmpty(communityIds)) {
            return List.of();
        }
        var byCommunityIds = communityCTMSpecifications.byCommunityIdIn(communityIds);
        var byEmployeeId = communityCTMSpecifications.byEmployeeId(employeeId);

        return communityCareTeamMemberDao.findAll(
                        byCommunityIds.and(byEmployeeId),
                        projectionClass);
    }

    @Override
    public boolean isUserCommunityCareTeamMember(Long employeeId, Long communityId) {
        var byCommunityId = communityCTMSpecifications.byCommunityId(communityId);
        var byEmployeeId = communityCTMSpecifications.byEmployeeId(employeeId);

        return communityCareTeamMemberDao.exists(byCommunityId.and(byEmployeeId));
    }

    public <T> List<T> userIsOnHoldCommunityCareTeamMember(Long employeeId, Class<T> projectionClass) {
        var byEmployeeId = communityCTMSpecifications.byEmployeeId(employeeId);
        var onHold = communityCTMSpecifications.ofConsentType(HieConsentCareTeamType.onHold(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID));

        return communityCareTeamMemberDao.findAll(byEmployeeId.and(onHold), projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> find(Specification<CommunityCareTeamMember> specification, Class<P> projectionClass) {
        return communityCareTeamMemberDao.findAll(specification, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public CommunityCareTeamMember findById(Long careTeamMemberId) {
        return communityCareTeamMemberDao.findById(careTeamMemberId).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return communityCareTeamMemberDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return communityCareTeamMemberDao.findByIdIn(ids, projection);
    }
}
