package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.ccd.LegalAuthenticator;

@Repository
public interface LegalAuthenticatorDao extends JpaRepository<LegalAuthenticator, Long> {

    @Query("select d from LegalAuthenticator d where d.client.id = :clientId")
    LegalAuthenticator getCcdLegalAuthenticator(@Param("clientId") Long clientId);

    void deleteAllByClientId(Long clientId);
}
