package com.scnsoft.eldermark.dao.serviceplan;

import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePlanNeedDao extends JpaRepository<ServicePlanNeed, Long> {

    @Query("select chainId from ServicePlanNeed where id = :id")
    Long findChainId(@Param("id") Long id);
}
