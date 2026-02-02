package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.document.ccd.DocumentationOf;

public interface DocumentationOfDao extends JpaRepository<DocumentationOf, Long> {
    List<DocumentationOf> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
