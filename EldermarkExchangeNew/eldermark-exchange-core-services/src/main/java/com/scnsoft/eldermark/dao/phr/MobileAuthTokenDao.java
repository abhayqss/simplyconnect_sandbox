package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.MobileAuthToken;
import com.scnsoft.eldermark.entity.phr.MobileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MobileAuthTokenDao extends JpaRepository<MobileAuthToken, Long> {

    @Query("SELECT CASE WHEN count(at.id) > 0 THEN true ELSE false END " +
            "FROM MobileAuthToken at " +
            "WHERE at.mobileUser = :mobileUser AND (at.expirationTime IS NULL OR at.expirationTime > GETDATE())")
    boolean hasActiveByUserMobileId(@Param("mobileUser") MobileUser mobileUser);
}
