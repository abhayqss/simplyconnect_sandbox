package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.document.ccd.Result;

public interface ResultDao extends JpaRepository<Result, Long> {
    List<Result> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
