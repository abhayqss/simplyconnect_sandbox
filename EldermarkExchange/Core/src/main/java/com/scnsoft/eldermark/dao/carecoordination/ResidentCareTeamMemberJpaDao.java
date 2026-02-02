package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.ResidentCareTeamMember;
import com.scnsoft.eldermark.shared.carecoordination.careteam.ResidentNotificationEventTypeVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ResidentCareTeamMemberJpaDao extends JpaRepository<ResidentCareTeamMember, Long> {

    @Query("Select new com.scnsoft.eldermark.shared.carecoordination.careteam.ResidentNotificationEventTypeVO(rctm.resident.id, prefs.eventType.id)  from ResidentCareTeamMember rctm " +
            "join rctm.careTeamMemberNotificationPreferencesList prefs " +
            "where rctm.employee.id in (:employeeIds) and prefs.responsibility = com.scnsoft.eldermark.dao.carecoordination.Responsibility.N and rctm.resident.id in (:residentIds) " +
            "group by rctm.resident.id, prefs.eventType.id ")
    List<ResidentNotificationEventTypeVO> getNotViewableByEmployeesEventTypesForResidents(@Param("employeeIds") Set<Long> employeeIds, @Param("residentIds") Set<Long> residentIds);

    @Query("Select new com.scnsoft.eldermark.shared.carecoordination.careteam.ResidentNotificationEventTypeVO(rctm.resident.id, prefs.eventType.id)  from ResidentCareTeamMember rctm " +
            "join rctm.careTeamMemberNotificationPreferencesList prefs " +
            "where rctm.employee.id in (:employeeIds) and prefs.responsibility <> com.scnsoft.eldermark.dao.carecoordination.Responsibility.N and rctm.resident.id in (:residentIds) " +
            "group by rctm.resident.id, prefs.eventType.id ")
    List<ResidentNotificationEventTypeVO> getViewableByEmployeesEventTypesForResidents(@Param("employeeIds") Set<Long> employeeIds, @Param("residentIds") Set<Long> residentIds);

}
