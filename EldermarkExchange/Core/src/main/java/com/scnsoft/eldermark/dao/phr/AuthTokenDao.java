package com.scnsoft.eldermark.dao.phr;

import com.scnsoft.eldermark.entity.phr.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author phomal
 * Created on 1/18/2018.
 */
@Repository
public interface AuthTokenDao extends JpaRepository<AuthToken, Long> {

    @Query("SELECT CASE WHEN count(at.id) > 0 THEN true ELSE false END " +
            "FROM AuthToken at " +
            "WHERE at.userMobile.id = :userId AND at.tokenEncoded = :token AND (at.expirationTime IS NULL OR at.expirationTime > GETDATE())")
    boolean validateTokenMobile(@Param("userId") Long userId, @Param("token") String token);

    @Query("SELECT CASE WHEN count(at.id) > 0 THEN true ELSE false END " +
            "FROM AuthToken at " +
            "WHERE at.userApplication.id = :userAppId AND at.tokenEncoded = :token AND (at.expirationTime IS NULL OR at.expirationTime > GETDATE())")
    boolean validateTokenThirdPartyApp(@Param("userAppId") Long userAppId, @Param("token") String token);

    @Query("SELECT at FROM AuthToken at " +
            "WHERE at.userMobile.id = :userId AND (at.expirationTime IS NULL OR at.expirationTime > GETDATE())")
    List<AuthToken> findActiveByUserMobileId(@Param("userId") Long id);

    @Query("SELECT CASE WHEN count(at.id) > 0 THEN true ELSE false END " +
                  "FROM AuthToken at " +
                  "WHERE at.userMobile.id = :userId AND (at.expirationTime IS NULL OR at.expirationTime > GETDATE())")
    boolean hasActiveByUserMobileId(@Param("userId") Long userId);

    @Query("SELECT at FROM AuthToken at " +
            "WHERE at.userApplication.id = :userAppId AND (at.expirationTime IS NULL OR at.expirationTime > GETDATE())")
    List<AuthToken> findActiveByThirdPartyAppId(@Param("userAppId") Long id);

}
