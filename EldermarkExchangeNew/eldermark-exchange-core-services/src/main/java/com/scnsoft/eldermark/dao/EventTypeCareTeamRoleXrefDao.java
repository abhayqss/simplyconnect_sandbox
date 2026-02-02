package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.EventTypeCareTeamRoleXref;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventTypeCareTeamRoleXrefDao extends JpaRepository<EventTypeCareTeamRoleXref, Long> {
    
    @Query("Select e from EventTypeCareTeamRoleXref e WHERE e.id.careTeamRoleId = :roleId ORDER BY e.eventType.description")
    List<EventTypeCareTeamRoleXref> getResponsibilityForRole(@Param("roleId") Long roleId);

}
