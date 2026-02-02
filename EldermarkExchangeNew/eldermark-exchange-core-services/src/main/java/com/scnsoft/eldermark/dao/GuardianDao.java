package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface GuardianDao extends JpaRepository<Guardian, Long> {

    @Query("SELECT g FROM Guardian g WHERE g.client.id IN (:residentIds)")
    Set<Guardian> listByResidentIds(@Param("residentIds") Collection<Long> clientIds);
}
