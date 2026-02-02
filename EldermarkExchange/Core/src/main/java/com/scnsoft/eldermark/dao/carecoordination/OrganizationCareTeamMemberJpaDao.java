package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.OrganizationCareTeamMember;
import com.scnsoft.eldermark.shared.carecoordination.careteam.CommunityNotificationTypeVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface OrganizationCareTeamMemberJpaDao extends JpaRepository<OrganizationCareTeamMember, Long> {

    @Query("Select new com.scnsoft.eldermark.shared.carecoordination.careteam.CommunityNotificationTypeVO(octm.organization.id, prefs.eventType.id)  from OrganizationCareTeamMember octm " +
            "join octm.careTeamMemberNotificationPreferencesList prefs " +
            "where octm.employee.id in (:employeeIds) and prefs.responsibility = com.scnsoft.eldermark.dao.carecoordination.Responsibility.N and octm.organization.id in (:communityIds) " +
            "group by octm.organization.id, prefs.eventType.id ")
    List<CommunityNotificationTypeVO> getNotViewableByEmployeesEventTypesForCommunities(@Param("employeeIds") Set<Long> employeeIds, @Param("communityIds") Set<Long> communityIds);

    @Query("Select new com.scnsoft.eldermark.shared.carecoordination.careteam.CommunityNotificationTypeVO(octm.organization.id, prefs.eventType.id)  from OrganizationCareTeamMember octm " +
            "join octm.careTeamMemberNotificationPreferencesList prefs " +
            "where octm.employee.id in (:employeeIds) and prefs.responsibility <> com.scnsoft.eldermark.dao.carecoordination.Responsibility.N and octm.organization.id in (:communityIds) " +
            "group by octm.organization.id, prefs.eventType.id ")
    List<CommunityNotificationTypeVO> getViewableByEmployeesEventTypesForCommunities(@Param("employeeIds") Set<Long> employeeIds, @Param("communityIds") Set<Long> communityIds);

    @Query("Select new com.scnsoft.eldermark.shared.carecoordination.careteam.CommunityNotificationTypeVO(octm.organization.id, prefs.eventType.id)  from OrganizationCareTeamMember octm " +
            "join octm.careTeamMemberNotificationPreferencesList prefs " +
            "where octm.employee.id in (:employeeIds) and prefs.responsibility = com.scnsoft.eldermark.dao.carecoordination.Responsibility.N and octm.organization.databaseId=:databaseId " +
            "group by octm.organization.id, prefs.eventType.id ")
    List<CommunityNotificationTypeVO> getNotViewableByEmployeesEventTypesForAllCommunitiesInDatabase(@Param("employeeIds") Set<Long> employeeIds, @Param("databaseId") Long databaseId);

    @Query("Select new com.scnsoft.eldermark.shared.carecoordination.careteam.CommunityNotificationTypeVO(octm.organization.id, prefs.eventType.id)  from OrganizationCareTeamMember octm " +
            "join octm.careTeamMemberNotificationPreferencesList prefs " +
            "where octm.employee.id in (:employeeIds) and prefs.responsibility <> com.scnsoft.eldermark.dao.carecoordination.Responsibility.N and octm.organization.databaseId=:databaseId " +
            "group by octm.organization.id, prefs.eventType.id ")
    List<CommunityNotificationTypeVO> getViewableByEmployeesEventTypesForAllCommunitiesInDatabase(@Param("employeeIds") Set<Long> employeeIds, @Param("databaseId") Long databaseId);

}
