package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatCommunityCareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.CareTeamMemberIdNameRoleAvatarAware;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CommunityCareTeamMemberService extends ProjectingService<Long> {

    boolean isAnyEmployeeInCommunityCareTeam(Collection<Employee> employees, Long communityId, AffiliatedCareTeamType type,
                                             HieConsentCareTeamType consentType);

    boolean isAnyEmployeeInAnyCommunityCareTeam(Collection<Employee> employees, Collection<Long> communities, AffiliatedCareTeamType type,
                                                HieConsentCareTeamType consentType);

    boolean isAnyEmployeeInAnyCommunityCareTeamOfOrganization(Collection<Employee> employees, Long organizationId, AffiliatedCareTeamType type,
                                                              HieConsentCareTeamType consentType);

    List<CommunityCareTeamMember> findCommunitiesCareTeamMembersAmongEmployees(Collection<Employee> employees, Collection<Long> communityIds,
                                                                               AffiliatedCareTeamType type,
                                                                               HieConsentCareTeamType consentType);

    <T> List<T> findByEmployeeId(Long employeeId, AffiliatedCareTeamType type,
                                 HieConsentCareTeamType consentType,
                                 Class<T> projectionClass);

    Set<Long> findCommunityCareTeamMemberIds(Collection<Long> communityIds, Long employeeOrganizationId,
                                             HieConsentCareTeamType consentType);

    void deleteByIds(Collection<Long> ids);

    boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter);

    List<CareTeamMemberIdNameRoleAvatarAware> findChatAccessibleCommunityCareTeamMembers(PermissionFilter permissionFilter,
                                                                                         AccessibleChatCommunityCareTeamFilter filter);

    List<CareTeamMemberIdNameRoleAvatarAware> findVideoCallAccessibleCommunityCareTeamMembers(PermissionFilter permissionFilter,
                                                                                              AccessibleChatCommunityCareTeamFilter filter);

    boolean hasChatAccessibleCommunityCareTeamMember(PermissionFilter permissionFilter, AccessibleChatCommunityCareTeamFilter filter);

    boolean hasVideoCallAccessibleCommunityCareTeamMember(PermissionFilter permissionFilter, AccessibleChatCommunityCareTeamFilter filter);

    boolean existAccessibleCareTeamMemberInCommunity(PermissionFilter permissionFilter, Long communityId,
                                                     HieConsentCareTeamType consentType);

    boolean existAccessibleCareTeamMemberInOrganization(PermissionFilter permissionFilter, Long organizationId,
                                                        HieConsentCareTeamType consentType);

    <T> List<T> findCareTeamMembers(Long communityId, Class<T> projectionClass, HieConsentCareTeamType consentType);

    <P> List<P> find(Specification<CommunityCareTeamMember> specification, Class<P> projectionClass);

    boolean isOnHoldCandidate(Long communityOrganizationId, Long employeeOrganizationId);

    CommunityCareTeamMember findById(Long careTeamMemberId);
}
