package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ClientCareTeamMemberDao extends AppJpaRepository<ClientCareTeamMember, Long>, CustomClientCareTeamMemberDao {

    List<ClientCareTeamMember> findByEmployee_Id(Long employeeId);

    List<ClientCareTeamMember> findByClient_IdInAndEmergencyContactIsTrueAndOnHoldIsFalse(List<Long> clientId);

    boolean existsByEmployee_IdAndCareTeamRole_IdAndClient_Id(Long employeeId, Long careTeamRoleId, Long clientId);

    boolean existsByEmployee_IdAndCareTeamRole_IdAndClient_IdAndIdNot(Long employeeId, Long careTeamRoleId, Long clientId, Long id);

    List<ClientCareTeamMember> findByClient_IdInAndIncludeInFaceSheetIsTrue(Collection<Long> clientIds);

    ClientCareTeamMember findByEmployeeIdAndClientId(Long employeeId, Long clientId);

    void deleteAllByIdIn(Collection<Long> ids);

    @Modifying
    @Query("update ClientCareTeamMember set onHold = :onHold where id in :ids")
    void updateOnHoldValue(@Param("onHold") boolean onHold, @Param("ids") Collection<Long> ids);
}
