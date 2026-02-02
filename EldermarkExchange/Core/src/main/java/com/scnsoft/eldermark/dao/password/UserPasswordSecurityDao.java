package com.scnsoft.eldermark.dao.password;

import com.scnsoft.eldermark.entity.password.UserPasswordSecurity;
import com.scnsoft.eldermark.entity.phr.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * @author phomal
 * Created on 11/13/2017.
 */
@Repository
public interface UserPasswordSecurityDao extends JpaRepository<UserPasswordSecurity, Long> {

    @Query("UPDATE UserPasswordSecurity ups SET ups.failedLogonsCount = 0 WHERE ups.user.id = :userId")
    @Modifying(clearAutomatically = true)
    void resetFailAttempts(@Param("userId") Long userId);

    @Query("UPDATE UserPasswordSecurity ups SET ups.failedLogonsCount = ups.failedLogonsCount + 1 WHERE ups.user = :user")
    @Modifying(clearAutomatically = true)
    void increaseFailAttemptsCounter(@Param("user") User user);

    @Query("UPDATE UserPasswordSecurity ups SET ups.lockedTime = :lockedTime, ups.locked = true WHERE ups.user = :user")
    @Modifying(clearAutomatically = true)
    void lockAccount(@Param("user") User user, @Param("lockedTime") Date lockedTime);

    @Query("UPDATE UserPasswordSecurity ups SET ups.lockedTime = null, ups.failedLogonsCount = 0, ups.locked = false WHERE ups.user.id = :userId")
    @Modifying(clearAutomatically = false)
    void unlockAccount(@Param("userId") Long userId);

    UserPasswordSecurity findByUser(User user);

}
