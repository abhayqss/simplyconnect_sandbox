package com.scnsoft.eldermark.dao.careteam;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModificationType;
import com.scnsoft.eldermark.entity.careteam.CareTeamMemberModified;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface CareTeamMemberModifiedDao extends AppJpaRepository<CareTeamMemberModified, Long> {

    @Modifying
    @Query("update CareTeamMemberModified set removed = true where careTeamMemberId in :clientCareTeamMemberIds and modificationType = :type")
    void markRemovedByCtmIdsAndType(@Param("clientCareTeamMemberIds") Collection<Long> clientCareTeamMemberIds,
                                    @Param("type") CareTeamMemberModificationType type);
}
