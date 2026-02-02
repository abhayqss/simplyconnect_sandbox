package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.serviceplan.ServicePlanNeed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicePlanNeedDao extends AppJpaRepository<ServicePlanNeed, Long> {

    @Query("select chainId from ServicePlanNeed where id = :id")
    Long findChainId(@Param("id") Long id);

}
