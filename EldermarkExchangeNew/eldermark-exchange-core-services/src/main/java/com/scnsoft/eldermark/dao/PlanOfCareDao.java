package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.ccd.PlanOfCare;

@Repository
public interface PlanOfCareDao extends JpaRepository<PlanOfCare, Long> {

    @Query("select p from PlanOfCare p where p.client.id = :clientId ")
    PlanOfCare getClientPlanOfCare(@Param("clientId") String clientId);

    @Query("select p from PlanOfCare p where p.client.id  in (:clientIds) ")
    List<PlanOfCare> listByClientIds(@Param("clientIds") List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
