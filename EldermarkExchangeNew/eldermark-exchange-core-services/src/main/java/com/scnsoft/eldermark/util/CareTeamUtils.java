package com.scnsoft.eldermark.util;

import com.google.common.collect.Sets;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.projection.EmployeeIdAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdEmployeeIdAware;
import com.scnsoft.eldermark.entity.Client;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CareTeamUtils {

    private CareTeamUtils() {
    }

    public static <T extends IdAware & CommunityIdAware, D extends EmployeeIdAware> Map<Long, Set<Long>> resolveOnHoldCareTeamEmployeeIds(
            List<T> clients,
            Map<Long, List<D>> clientCtmOnHoldCandidates,
            Map<Long, Map<Boolean, List<EmployeeIdAware>>> communityCtmMembers) {

        return clients.stream()
                .collect(Collectors.toMap(
                        IdAware::getId,
                        client -> {
                            var clientCtm = clientCtmOnHoldCandidates.getOrDefault(client.getId(), List.of());
                            var communityCtm = communityCtmMembers.getOrDefault(client.getCommunityId(), Map.of());
                            return resolveOnHoldCareTeamEmployeeIds(clientCtm, communityCtm);
                        }));
    }

    public static Set<Long> resolveOnHoldCareTeamEmployeeIds(List<? extends EmployeeIdAware> clientCtmOnHoldCandidates, Map<Boolean, List<EmployeeIdAware>> communityCtmMembers) {
        var clientCtmOnHoldEmployeeIds = clientCtmOnHoldCandidates.stream().map(EmployeeIdAware::getEmployeeId).collect(Collectors.toSet());
        var communityCtmCurrentEmployeeIds = communityCtmMembers.getOrDefault(true, List.of()).stream()
                .map(EmployeeIdAware::getEmployeeId).collect(Collectors.toSet());
        var communityCtmOnHoldEmployeeIds = communityCtmMembers.getOrDefault(false, List.of()).stream()
                .map(EmployeeIdAware::getEmployeeId).collect(Collectors.toSet());

        var onHoldEmployeeIds = resolveOnHoldCareTeamEmployeeIds(
                clientCtmOnHoldEmployeeIds,
                communityCtmCurrentEmployeeIds,
                communityCtmOnHoldEmployeeIds
        );
        return onHoldEmployeeIds;
    }

    public static Set<Long> resolveOnHoldCareTeamEmployeeIds(Set<Long> clientCtmOnHold,
                                                             Set<Long> communityCtmCurrent,
                                                             Set<Long> communityCtmOnHold) {
        var clientCtmOnHoldResolved = resolveOnHoldClientCareTeamEmployeeIds(
                clientCtmOnHold,
                communityCtmCurrent
        );

        return Sets.union(
                clientCtmOnHoldResolved,
                communityCtmOnHold
        );
    }


    private static Set<Long> resolveOnHoldClientCareTeamEmployeeIds(Set<Long> clientCtmOnHold,
                                                                    Set<Long> communityCtmCurrent) {
        //community CTM overrides clientCtm
        return Sets.difference(clientCtmOnHold, communityCtmCurrent);
    }
}
