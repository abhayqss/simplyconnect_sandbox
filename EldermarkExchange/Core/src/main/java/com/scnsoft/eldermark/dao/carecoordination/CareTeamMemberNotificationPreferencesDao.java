package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.CareTeamMemberNotificationPreferences;
import com.scnsoft.eldermark.entity.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Created by pzhurba on 24-Sep-15.
 * Rewritten by phomal on 21-Mar-18
 */
@Transactional(propagation = Propagation.MANDATORY)
@Repository
public interface CareTeamMemberNotificationPreferencesDao extends JpaRepository<CareTeamMemberNotificationPreferences, Long> {

    @Query("SELECT ctmnp FROM CareTeamMemberNotificationPreferences ctmnp WHERE ctmnp.careTeamMember.id = :careTeamMemberId AND ctmnp.eventType = :eventType")
    List<CareTeamMemberNotificationPreferences> getNotificationPreferences(@Param("careTeamMemberId") Long careTeamMemberId,
                                                                           @Param("eventType") EventType eventType);

    @Modifying
    @Query("DELETE FROM CareTeamMemberNotificationPreferences WHERE careTeamMember.id = :careTeamMemberId")
    void deleteNotificationPreferences(@Param("careTeamMemberId") Long careTeamMemberId);

    @Modifying
    @Query("DELETE FROM CareTeamMemberNotificationPreferences WHERE careTeamMember.id IN (:careTeamMemberIds)")
    void deleteNotificationPreferences(@Param("careTeamMemberIds") Collection<Long> careTeamMemberIds);

}
