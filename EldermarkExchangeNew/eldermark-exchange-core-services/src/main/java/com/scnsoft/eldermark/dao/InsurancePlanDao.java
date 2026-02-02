package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.InsurancePlan;

public interface InsurancePlanDao extends JpaRepository<InsurancePlan, Long> {

    List<InsurancePlan> findAllByInNetworkInsuranceId(Long inNetworkInsuranceId);

}
