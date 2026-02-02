package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientCareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.CareTeamMemberIdNameRoleAvatarAware;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClientCareTeamMemberService extends ProjectingService<Long> {

    List<ClientCareTeamMember> findEmergencyContacts(Long clientId);

    Optional<ClientCareTeamMember> findById(Long id);

    /**
     * Checks if given client is among client care team of any given employees
     *
     * @param employees
     * @param clientId
     * @param type
     * @return
     */
    boolean isAnyEmployeeInClientCareTeam(Collection<Employee> employees, Long clientId, AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    @Deprecated
    default List<ClientCareTeamMember> findClientsCareTeamMembersAmongEmployees(Collection<Employee> employees, Collection<Long> clientIds,
                                                                        AffiliatedCareTeamType type) {
        return findClientsCareTeamMembersAmongEmployees(employees, clientIds, type, HieConsentCareTeamType.currentAndOnHold());
    }

    List<ClientCareTeamMember> findClientsCareTeamMembersAmongEmployees(Collection<Employee> employees, Collection<Long> clientIds,
                                                                        AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    List<ClientCareTeamMember> findClientCareTeamMembersAmongEmployeesAndClientCommunity(Collection<Employee> employees, Long communityId,
                                                                                         AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    /**
     * Checks if given client is among client care team of any given employees
     *
     * @param employees
     * @param clientIds
     * @param type
     * @return
     */
    boolean isAnyEmployeeInAnyClientCareTeam(Collection<Employee> employees, Collection<Long> clientIds, AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    @Deprecated
    default boolean isAnyEmployeeIdInAnyClientCareTeam(Collection<Long> employeeIds, Collection<Long> clientIds, AffiliatedCareTeamType type) {
        return isAnyEmployeeIdInAnyClientCareTeam(employeeIds, clientIds, type, HieConsentCareTeamType.currentAndOnHold());
    }

    boolean isAnyEmployeeIdInAnyClientCareTeam(Collection<Long> employeeIds, Collection<Long> clientIds, AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    boolean isAnyEmployeeInAnyClientCareTeamOfOrganization(Collection<Employee> employees, Long organizationId, AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    boolean isAnyEmployeeInAnyClientCareTeamOfCommunity(Collection<Employee> employees, Long communityId, AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    boolean isAnyEmployeeInAnyClientCareTeamOfAnyCommunity(Collection<Employee> employees, Collection<Long> communityIds, AffiliatedCareTeamType type, HieConsentCareTeamType consentType);

    boolean isCareTeamVisibleForAny(Collection<Employee> employees, Long clientId);

    Set<Long> findClientCareTeamMemberIds(Collection<Long> clientCommunityIds, Long employeeOrganizationId,
                                          HieConsentCareTeamType consentType);

    void deleteByIds(Collection<Long> ids);

    boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter);

    List<CareTeamMemberIdNameRoleAvatarAware> findChatAccessibleClientCareTeamMembers(PermissionFilter permissionFilter,
                                                                                      AccessibleChatClientCareTeamFilter filter);

    List<CareTeamMemberIdNameRoleAvatarAware> findVideoCallAccessibleClientCareTeamMembers(PermissionFilter permissionFilter,
                                                                                           AccessibleChatClientCareTeamFilter filter);

    boolean hasChatAccessibleClientCareTeamMembers(PermissionFilter permissionFilter, AccessibleChatClientCareTeamFilter filter);

    boolean hasVideoCallAccessibleClientCareTeamMembers(PermissionFilter permissionFilter, AccessibleChatClientCareTeamFilter filter);

    boolean existAccessibleCareTeamMemberInCommunity(PermissionFilter permissionFilter, Long communityId, HieConsentCareTeamType consentType);

    boolean existAccessibleCareTeamMemberInOrganization(PermissionFilter permissionFilter, Long organizationId, HieConsentCareTeamType consentType);

    ClientCareTeamMember getById(Long id);

    List<ClientCareTeamMember> findProspectivePrimaryContacts(PermissionFilter permissionFilter, Long clientId);

    List<ClientCareTeamMember> findByEmployeeId(Long employeeId);

    <P> List<P> find(Specification<ClientCareTeamMember> specification, Class<P> projectionClass);

    <P> Optional<P> findByClientCtmId(Long id, Class<P> projection);
}
