package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.document.ccd.Payer;

public interface PayerDao extends JpaRepository<Payer, Long> {
    List<Payer> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
