package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServicePlanGoalDao extends AppJpaRepository<ServicePlanGoal, Long> {

    @Query("select chainId from ServicePlanGoal where id = :id")
    Long findChainId(@Param("id") Long id);
}
