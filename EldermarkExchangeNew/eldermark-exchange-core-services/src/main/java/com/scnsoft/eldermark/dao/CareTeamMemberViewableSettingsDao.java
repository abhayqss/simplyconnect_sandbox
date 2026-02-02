package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.event.CareTeamMemberViewableSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareTeamMemberViewableSettingsDao extends JpaRepository<CareTeamMemberViewableSettings, CareTeamMemberViewableSettings.Id> {

    @Query("SELECT vs.eventTypeId FROM CareTeamMemberViewableSettings vs where vs.clientId=:clientId and vs.employeeId=:employeeId and vs.canViewEventType=0")
    List<Long> findNotViewableEventTypes(@Param("employeeId") Long employeeId, @Param("clientId") Long clientId);
}
