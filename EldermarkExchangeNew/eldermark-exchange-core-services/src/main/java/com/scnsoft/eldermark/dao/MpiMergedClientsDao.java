package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.MpiMergedClients;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MpiMergedClientsDao extends AppJpaRepository<MpiMergedClients, Long> {

    @Modifying
    @Query("delete from MpiMergedClients where mergedClientId = :clientId or survivingClient = :clientId")
    void unmerge(@Param("clientId") Long clientId);

    @Modifying
    @Query("delete from MpiMergedClients where mergedClient in (select c from Client c where c.communityId = :communityId)" +
            " or survivingClient in (select c from Client c where c.communityId = :communityId)")
    void unmergeByCommunityId(@Param("communityId") Long communityId);
}
