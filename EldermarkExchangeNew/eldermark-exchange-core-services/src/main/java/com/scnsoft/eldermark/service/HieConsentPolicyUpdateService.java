package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.IdCommunityIdAssociatedEmployeeIdsAware;
import com.scnsoft.eldermark.dto.hieconsentpolicy.ClientHieConsentPolicyData;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.CommunityCareTeamMember;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.util.List;

public interface HieConsentPolicyUpdateService {

    String OBTAINED_FROM_STATE_POLICY_VALUE = "State Policy";

    void updateHieConsentPolicyByClient(Client client, HieConsentPolicyType type, HieConsentPolicySource source, Employee author);

    void updateHieConsentPolicyByStaff(Client client, ClientHieConsentPolicyData data);

    void updateCommunityDefaultHieConsentPolicy(Community community, HieConsentPolicyType type, HieConsentPolicySource source, Employee author);

    void updateSignatureRequests(Long communityId, HieConsentPolicyType type, Long authorId);

    void contactChanged(Employee employee, Long performedById);

    List<IdCommunityIdAssociatedEmployeeIdsAware> updateOnHoldCareTeamAndChatsConnection(Long communityId, HieConsentPolicyType type, Long authorId);

    void communityCareTeamMemberAdded(CommunityCareTeamMember communityCareTeamMember);

    void clientCareTeamMemberAdded(ClientCareTeamMember communityCareTeamMember);

    void communityCareTeamMemberDeleted(Employee e, Community community);

    void clientCareTeamMemberDeleted(Employee employee, Client client, boolean wasOnHold);

    <T> List<T> findIncomingCareTeamInvitationsForHieConsentChangeInCommunity(Long communityId, Class<T> projectionClass);
}
