package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;

import java.util.Collection;
import java.util.List;
import java.util.Map;

//Intentionally package-private. Facades should use com.scnsoft.eldermark.service.HieConsentPolicyUpdateService
//to update consent
interface CommunityCareTeamHieConsentPolicyUpdateService {

    /**
     * true - will remain current
     * false - will become on hold
     *
     * @param communityId
     * @return
     */
    Map<Boolean, List<EmployeeIdAware>> communityCareTeamCurrentOnHoldCandidatesGrouped(Long communityId);

    Map<Boolean, List<EmployeeIdAware>> communityCareTeamCurrentOnHoldCandidatesAmongEmployeeIdsGrouped(Long communityId,
                                                                                                        Collection<Long> employeeIds);

    <T> List<T> findCareTeamMembersOfCommunities(Collection<Long> communityIds, Long employeeIdToExclude, Class<T> projectionClass);

    <T> List<T> findCareTeamMembersOfCommunitiesWhereCanBeCurrent(Collection<Long> communityIds, Long employeeIdToExclude, Class<T> projectionClass);

    <T> List<T> findCareTeamMembersOfCommunitiesWhereCanBeOnHold(Collection<Long> communityIds, Long employeeIdToExclude, Class<T> projectionClass);

    boolean isOnHoldCandidate(Long communityOrganizationId, Long employeeOrganizationId);

    <T> List<T> communityCareTeamMemberEntries(Long employeeId, Collection<Long> communityIds, Class<T> projectionClass);

    boolean isUserCommunityCareTeamMember(Long employeeId, Long communityId);

    <T> List<T> userIsOnHoldCommunityCareTeamMember(Long employeeId, Class<T> projectionClass);
}
