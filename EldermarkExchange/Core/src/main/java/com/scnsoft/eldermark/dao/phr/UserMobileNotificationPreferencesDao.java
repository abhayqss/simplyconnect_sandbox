package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.EventType;
import com.scnsoft.eldermark.entity.phr.User;
import com.scnsoft.eldermark.entity.phr.UserMobileNotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author phomal
 */
public interface UserMobileNotificationPreferencesDao extends JpaRepository<UserMobileNotificationPreferences, Long> {

    List<UserMobileNotificationPreferences> getByUserAndEventType(User user, EventType eventType);

    List<UserMobileNotificationPreferences> getByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM UserMobileNotificationPreferences u where u.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

}
