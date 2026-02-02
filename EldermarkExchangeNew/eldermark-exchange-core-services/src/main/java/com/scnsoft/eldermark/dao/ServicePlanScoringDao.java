package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanScoring;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePlanScoringDao extends AppJpaRepository<ServicePlanScoring, Long> {
}
