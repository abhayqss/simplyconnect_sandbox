package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.Authenticator;

public interface AuthenticatorDao extends JpaRepository<Authenticator, Long> {
    List<Authenticator> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
