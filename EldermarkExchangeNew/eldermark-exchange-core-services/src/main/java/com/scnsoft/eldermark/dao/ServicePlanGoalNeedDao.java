package com.scnsoft.eldermark.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoalNeed;

public interface ServicePlanGoalNeedDao extends JpaRepository<ServicePlanGoalNeed, Long> {

}
