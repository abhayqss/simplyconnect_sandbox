package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.security.PermissionFilter;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

interface ConversationAllowedBetweenUsersResolver {

    /**
     * Checks if chatting os allowed by permissions only (i.e. we don't check if chats allowed for organization, contact is active, etc)
     *
     * @param toCheckBetweenEmployees should be bidirectional for correct work, i.e. if for x1 key there is x2 in values,
     *                                there also should be present x2 key with x1 in values.
     *                                Bidirectional validation is not performed for optimization
     *
     *
     * @param permissionFilterProvider
     *
     * @return bidirectional map containing allowed chatting between employees
     */
    Map<Long, Set<Long>> resolveConversationsAllowedBetween(Map<Long, Set<Long>> toCheckBetweenEmployees,
                                                            Function<Long, PermissionFilter> permissionFilterProvider);
}
