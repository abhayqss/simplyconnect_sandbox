package com.scnsoft.eldermark.dump.dao;

import com.scnsoft.eldermark.dump.entity.serviceplan.ServicePlanGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePlanGoalDao extends JpaRepository<ServicePlanGoal, Long>, JpaSpecificationExecutor<ServicePlanGoal> {
}
