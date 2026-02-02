package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.IdCommunityIdAssociatedEmployeeIdsAware;
import com.scnsoft.eldermark.dto.hieconsentpolicy.ClientHieConsentPolicyData;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;

import java.util.Collection;
import java.util.List;

//Intentionally package-private. Facades should use com.scnsoft.eldermark.service.HieConsentPolicyUpdateService
//to update consent
interface ClientHieConsentPolicyUpdateService extends OptOutPolicyCheckingClientService {

    void updateHieConsentPolicyByClient(Client client, HieConsentPolicyType type, HieConsentPolicySource source, Employee author);

    void updateHieConsentPolicy(Client client, ClientHieConsentPolicyData data);

    void updateHieConsentPolicyWithDefaultCommunityPolicy(Long communityId, ClientHieConsentPolicyData data);

    List<IdCommunityIdAssociatedEmployeeIdsAware> findWithCommunityHieConsentPolicy(Long communityId);

    <T> List<T> findClientsInCommunity(Long communityId, Class<T> projectionClass);

    <T> List<T> findOptOutClientsInCommunities(Collection<Long> communityIds, Class<T> projectionClass);

    boolean existsOptOutClientsInCommunity(Long communityId);

}
