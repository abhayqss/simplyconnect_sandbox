package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//Intentionally package-private. Facades should use com.scnsoft.eldermark.service.HieConsentPolicyUpdateService
//to update consent
interface ClientCareTeamHieConsentPolicyUpdateService {

    <T extends IdAware & CommunityIdAware> List<ClientCareTeamMemberIdsAware> onHoldCandidates(T client);

    List<ClientCareTeamMemberIdsAware> onHoldCandidatesByClientCommunityId(Long communityId);

    List<EmployeeIdAware> onHoldAmongEmployeeIds(Long clientId, Collection<Long> employeeIds);

    <T> List<T> onHoldAmongClientIds(Collection<Long> clientIds, Long employeeId, Class<T> projectionClass);

    <CTM extends IdAware> Map<Boolean, List<CTM>> calculateCurrentOnHoldCandidate(Long employeeId, Class<CTM> ctmProjection);

    <T> List<T> findCareTeamMembersOfClients(Collection<Long> clientIds, Long employeeIdToExclude, Class<T> projectionClass);

    <T> List<T> findCurrentCareTeamMembersOfClients(Collection<Long> clientIds, Long employeeIdToExclude, Class<T> projectionClass);

    <T> List<T> findOnHoldCareTeamMembersOfClients(Collection<Long> clientIds, Long employeeIdToExclude, Class<T> projectionClass);

    <T> List<T> findClientCareTeamEntries(Long employeeId, Long clientId, Class<T> projectionClass);

    boolean isOnHoldForAnyClientInCommunity(Long employeeId, Long communityId);

    <T extends IdAware & ClientIdAware & EmployeeIdAware & ClientAssociatedEmployeeIdAware> void setOnHold(Collection<T> clientCareTeamMembers,
                                                                         Long performedById);

    void setCurrent(Collection<Long> clientCareTeamMemberIds);
}
