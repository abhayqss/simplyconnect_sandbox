package com.scnsoft.eldermark.dao.careteam.invitation;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.careteam.invitation.ClientCareTeamInvitation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientCareTeamInvitationDao extends AppJpaRepository<ClientCareTeamInvitation, Long> {

    Optional<ClientCareTeamInvitation> findByToken(String token);

    @Modifying
    @Query("update ClientCareTeamInvitation set token = null where targetEmployeeId = :targetEmployeeId")
    void deleteInvitationTokens(@Param("targetEmployeeId") Long targetEmployeeId);

}
