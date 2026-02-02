package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.TherapUnknownNetworkPlan;

@Repository
public interface TherapUnknownNetworkPlanDao extends JpaRepository<TherapUnknownNetworkPlan, Long> {

    TherapUnknownNetworkPlan findByMedPlanIdAndMedPlanNameAndMedPlanIssuer(String medPlanId, String medPlanName,
            String medPlanIssuer);

}