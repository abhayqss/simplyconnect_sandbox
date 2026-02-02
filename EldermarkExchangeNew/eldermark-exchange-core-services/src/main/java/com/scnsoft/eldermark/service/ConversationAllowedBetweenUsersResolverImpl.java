package com.scnsoft.eldermark.service;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

@Service
@Transactional(readOnly = true)
public class ConversationAllowedBetweenUsersResolverImpl implements ConversationAllowedBetweenUsersResolver {

    @Autowired
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public Map<Long, Set<Long>> resolveConversationsAllowedBetween(Map<Long, Set<Long>> toCheckBetweenEmployees,
                                                                    Function<Long, PermissionFilter> permissionFilterProvider) {
        var allowedConversationsBetween = new HashMap<Long, Set<Long>>(toCheckBetweenEmployees.size());
        toCheckBetweenEmployees.forEach((employeeId, otherEmployeeIds) -> {
            //exclude ones we already checked and confirmed
            var employeeIdsToCheck = Sets.difference(
                    otherEmployeeIds,
                    allowedConversationsBetween.getOrDefault(employeeId, Set.of())
            );

            var allowed = findChatsAllowedAmongEmployees(employeeId, employeeIdsToCheck, permissionFilterProvider);
            allowed.forEach(allowedEmployeeId -> CareCoordinationUtils.putBidirectionally(allowedConversationsBetween, employeeId, allowedEmployeeId));
        });

        return allowedConversationsBetween;
    }

    private Stream<Long> findChatsAllowedAmongEmployees(Long employeeId, Collection<Long> otherEmployeeIds,
                                                        Function<Long, PermissionFilter> permissionFilterProvider) {
        if (CollectionUtils.isEmpty(otherEmployeeIds)) {
            return Stream.of();
        }
        var filter = permissionFilterProvider.apply(employeeId);
        var spec = employeeSpecificationGenerator.chatAccessibleEmployeesByPermissionsOnly(
                filter, otherEmployeeIds
        );

        return employeeDao.findAll(spec, IdAware.class).stream().map(IdAware::getId);
    }
}
