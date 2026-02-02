package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ConversationType;
import com.scnsoft.eldermark.beans.projection.AssociatedEmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.entity.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

//Intentionally package-private. Facades should use com.scnsoft.eldermark.service.HieConsentPolicyUpdateService
//to update consent
interface ChatHieConsentPolicyUpdateService {
    <T extends IdAware & AssociatedEmployeeIdAware> Map<ConversationType, Set<String>> allowedChatsBeforeOnHoldUpdate(List<T> clients,
                                                                                                                      Map<Long, Set<Long>> onHoldCandidatesEmployeeIds,
                                                                                                                      Function<Long, PermissionFilter> permissionFilterProvider);


    <T extends IdAware & AssociatedEmployeeIdAware> void disconnectConversations(List<T> clients,
                                                                                 Map<Long, Set<Long>> onHoldEmployeeIds,
                                                                                 Function<Long, PermissionFilter> permissionFilterProvider,
                                                                                 Map<ConversationType, Set<String>> allowedChatsBeforeOnHoldUpdate);

    <T extends IdAware & AssociatedEmployeeIdAware> void reconnectConversations(List<T> clients,
                                                                                Map<Long, Set<Long>> onHoldEmployeeIds,
                                                                                Function<Long, PermissionFilter> permissionFilterProvider);

    void contactChanged(Employee employee,
                        Collection<Long> optOutClientContactWhereChangedUserCtmBecameCurrent,
                        Collection<Long> optOutClientContactWhereChangedUserCtmIsOnHold,
                        Collection<Long> contactsSharingOptOutClientCtmWhereBecameCurrent,
                        Collection<Long> contactsSharingOptOutClientCtmWhereIsOnHold,
                        Function<Long, PermissionFilter> permissionFilterProvider);

    void contactChangedAsync(Employee employee,
                        Collection<Long> optOutClientContactWhereChangedUserCtmBecameCurrent,
                        Collection<Long> optOutClientContactWhereChangedUserCtmIsOnHold,
                        Collection<Long> contactsSharingOptOutClientCtmWhereBecameCurrent,
                        Collection<Long> contactsSharingOptOutClientCtmWhereIsOnHold,
                        Function<Long, PermissionFilter> permissionFilterProvider);
}
