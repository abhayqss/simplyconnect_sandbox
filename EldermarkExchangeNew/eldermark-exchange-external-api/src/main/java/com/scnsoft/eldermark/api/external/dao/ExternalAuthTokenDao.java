package com.scnsoft.eldermark.api.external.dao;

import com.scnsoft.eldermark.api.external.entity.ExternalAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExternalAuthTokenDao extends JpaRepository<ExternalAuthToken, Long> {

    @Query("SELECT at FROM ExternalAuthToken at " +
            "WHERE at.userApplication.id = :userAppId AND (at.expirationTime IS NULL OR at.expirationTime > GETDATE())")
    List<ExternalAuthToken> findActiveByThirdPartyAppId(@Param("userAppId") Long id);

}
