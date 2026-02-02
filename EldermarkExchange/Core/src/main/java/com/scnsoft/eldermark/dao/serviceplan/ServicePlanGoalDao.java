package com.scnsoft.eldermark.dao.serviceplan;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePlanGoalDao extends JpaRepository<ServicePlanGoal, Long> {

    @Query("select chainId from ServicePlanGoal where id = :id")
    Long findChainId(@Param("id") Long id);

}
