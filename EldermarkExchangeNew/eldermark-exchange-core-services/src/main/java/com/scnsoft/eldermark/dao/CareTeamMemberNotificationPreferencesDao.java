package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.scnsoft.eldermark.entity.careteam.CareTeamMemberNotificationPreferences;

@Transactional(propagation = Propagation.MANDATORY)
@Repository
public interface CareTeamMemberNotificationPreferencesDao
        extends JpaRepository<CareTeamMemberNotificationPreferences, Long> {

    @Modifying
    @Query("DELETE FROM CareTeamMemberNotificationPreferences WHERE careTeamMember.id = :careTeamMemberId")
    void deleteNotificationPreferences(@Param("careTeamMemberId") Long careTeamMemberId);

}
