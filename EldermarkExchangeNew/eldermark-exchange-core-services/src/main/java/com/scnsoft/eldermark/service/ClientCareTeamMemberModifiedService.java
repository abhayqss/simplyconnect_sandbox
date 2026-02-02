package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModificationType;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;

import java.util.Collection;

public interface ClientCareTeamMemberModifiedService {

    void clientCareTeamMemberModified(
            ClientCareTeamMember ctm,
            Long performedById,
            CareTeamMemberModificationType modificationType);

    void clientCareTeamMemberModified(Long ctmId, Long ctmEmployeeId, Long clientId, Long performedById, CareTeamMemberModificationType modificationType);

    void careTeamMemberViewed(Long careTeamMemberId, Long currentEmployeeId);

    void careTeamMemberListViewed(Long currentEmployeeId, Long clientId);

    void setCurrent(Collection<Long> clientCareTeamMemberIds);
}
